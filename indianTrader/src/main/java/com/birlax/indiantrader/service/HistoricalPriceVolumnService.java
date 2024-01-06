package com.birlax.indiantrader.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.domain.Security;
import com.birlax.indiantrader.feedcapture.NSE24MonthHistoricalPriceVolumnDeliverySource;

@RestController
@RequestMapping(path = "/historicalPriceVolumnService", produces = { "application/json" })
@Named
public class HistoricalPriceVolumnService {

    Logger log = LoggerFactory.getLogger(HistoricalPriceVolumnService.class);

    @Inject
    private SingleTemporalServiceImpl temporalService;

    @Inject
    private SecurityService securityService;

    public void syncPriceVolumnDeliveryForAllSecuritiesSingleDay(String fileName) throws IOException {

        Map<String, Security> enricherSource = enricherSource();

        List<Map<String, Object>> rawDataFacts = null;
        try {
            rawDataFacts = NSE24MonthHistoricalPriceVolumnDeliverySource.getDataFromCSVFileNSEDownloaded(fileName);
        } catch (IOException e) {
            log.error("Failed to sync Price/Volumn : {}", e);
        }
        List<PriceVolumnDelivery> priceVolDelivery = enrichedData(rawDataFacts, enricherSource);
        temporalService.mergeRecords(priceVolDelivery);

    }

    @RequestMapping(path = "/syncAllPriceVolumnDeliveryFor24Month", produces = { "application/json" })
    public void syncPriceVolumnDeliveryFor24Month() throws IOException {

        Set<String> retrieveColumns = new HashSet<>();
        List<Security> securities = securityService.getAllSecurities();
        retrieveColumns = new HashSet<>();
        retrieveColumns.add("spn");
        List<PriceVolumnDelivery> priceVolumnDeliveries = temporalService.getAllRecords(retrieveColumns,
                PriceVolumnDelivery.class);
        log.info("Already synced securities  : {} ", priceVolumnDeliveries.size());
        Set<Integer> spns = priceVolumnDeliveries.stream().map(a -> a.getSpn()).collect(Collectors.toSet());
        for (Security security : securities) {
            if (spns.contains(security.getSpn())) {
                continue;
            }
            syncPriceVolumnDeliveryFor24Month(security);
        }
    }

    @RequestMapping(path = "/syncPriceVolumnDeliveryFor24Month", produces = { "application/json" })
    public void syncPriceVolumnDeliveryFor24Month(Security security) throws IOException {

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.add("spn");
        PriceVolumnDelivery r = new PriceVolumnDelivery();
        r.setSpn(security.getSpn());

        List<PriceVolumnDelivery> priceVolumnDeliveries = temporalService.searchRecords(Arrays.asList(r),
                retrieveColumns, PriceVolumnDelivery.class);

        if (!priceVolumnDeliveries.isEmpty()) {
            log.info("Already populated.. Nothing to do.." + priceVolumnDeliveries.size() + "Security : " + security);
            return;
        }
        log.info("Starting data sync for Security : {}", security);
        try {
            syncDataForSecurity(security.getSymbol());
        } catch (Exception e) {
            log.error("Something wrong happend moving on. : ", e);
        }
        log.info("Completed data sync for Security : {}", security);
    }

    @RequestMapping(path = "/syncDataForSecurity", produces = { "application/json" })
    public boolean syncDataForSecurity(String nseSecurity) throws IOException {

        log.info("Starting the sync for the Security : {}", nseSecurity);
        Map<String, Security> enricherSource = enricherSource(nseSecurity);
        log.info("Download/Parsing done for the Security : {}", nseSecurity);
        List<Map<String, Object>> rawDataFacts = null;
        try {
            rawDataFacts = NSE24MonthHistoricalPriceVolumnDeliverySource.getDataFromNSE(nseSecurity);
        } catch (IOException e) {
            log.error("Failed to sync Price/Volumn for the Security : {}", nseSecurity, e);
        }

        List<PriceVolumnDelivery> priceVolDelivery = enrichedData(rawDataFacts, enricherSource);
        temporalService.mergeRecords(priceVolDelivery);
        log.info("Completed the sync for the Security : {}", nseSecurity);
        return true;
    }

    @RequestMapping(path = "/syncPriceVolumnDelivery", produces = { "application/json" })
    public boolean syncPriceVolumnDelivery(String nseSecurity, Date startDate, Date endDate) throws IOException {

        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("End Date can't be before Start Date");
        }
        log.info("Starting the sync for the Security : {}", nseSecurity);
        Map<String, Security> enricherSource = enricherSource(nseSecurity);
        log.info("Download/Parsing done for the Security : {}", nseSecurity);
        List<Map<String, Object>> rawDataFacts = null;
        try {
            rawDataFacts = NSE24MonthHistoricalPriceVolumnDeliverySource.getDataFromNSEForDateRange(nseSecurity,
                    startDate, endDate);
        } catch (Exception e) {
            log.error("Failed to sync Price/Volumn for the Security : {}", nseSecurity, e);
            // throw new RuntimeException(e);
            return false;
        }
        List<PriceVolumnDelivery> priceVolDelivery = enrichedData(rawDataFacts, enricherSource);
        if (!priceVolDelivery.isEmpty()) {
            temporalService.mergeRecords(priceVolDelivery);
        } else {
            log.info("Nothing to merge...", nseSecurity);
        }
        log.info("Completed the sync for the Security : {}", nseSecurity);

        return true;
    }

    @RequestMapping(path = "/getPriceVolumnForSecurity", produces = { "application/json" })
    public List<PriceVolumnDelivery> getPriceVolumnForSecurity(String securitySymbol, String series,
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("Missing endDate.");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("StartDate can't be after endDate.");
        }
        Security security = securityService.getSecurityBySymbol(securitySymbol);
        log.debug("Search requested for security : {} for Range :{} to {}", securitySymbol, startDate, endDate);

        PriceVolumnDelivery priceVolumnDelivery = new PriceVolumnDelivery();
        priceVolumnDelivery.setSpn(security.getSpn());

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.addAll(priceVolumnDelivery.getDAOFlatView().keySet());

        Set<String> searchByColumns = new HashSet<>();
        searchByColumns.add("spn");

        if (series != null && !series.isEmpty()) {
            priceVolumnDelivery.setSeries(series);
            searchByColumns.add("series");
        }

        List<PriceVolumnDelivery> priceVolumnDeliveries = temporalService.searchRecordsForDateRange(
                Arrays.asList(priceVolumnDelivery), searchByColumns, retrieveColumns, "trade_date", startDate, endDate,
                PriceVolumnDelivery.class);

        return priceVolumnDeliveries;
    }

    private Map<String, Security> enricherSource(String nseSecurity) {
        Security s = securityService.getSecurityBySymbol(nseSecurity);
        Map<String, Security> enricherSource = new HashMap<>();
        enricherSource.put(s.getSymbol(), s);
        return enricherSource;
    }

    private Map<String, Security> enricherSource() {
        List<Security> securities = securityService.getAllSecurities();
        Map<String, Security> enricherSource = new HashMap<>();
        for (Security security : securities) {
            enricherSource.put(security.getSymbol(), security);
        }
        return enricherSource;
    }

    private List<PriceVolumnDelivery> enrichedData(List<Map<String, Object>> rawDataFacts,
            Map<String, Security> enricherSource) {
        List<PriceVolumnDelivery> priceVolDelivery = new ArrayList<>();
        for (Map<String, Object> map : rawDataFacts) {

            if (enricherSource.get(map.get("symbol")) == null) {
                log.error("Unknown Security .. " + map);
                continue;
            }
            PriceVolumnDelivery pvd;
            try {
                pvd = ReflectionHelper.getDomainObject(map, PriceVolumnDelivery.class);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Error : {}", e);
                throw new RuntimeException(e);
            }
            pvd.setSpn(enricherSource.get(map.get("symbol")).getSpn());
            priceVolDelivery.add(pvd);
        }
        return priceVolDelivery;
    }
}
