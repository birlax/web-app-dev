/**
 *
 */
package com.birlax.indiantrader.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.birlax.indiantrader.domain.SecuritiesInFuturesAndOptions;
import com.birlax.indiantrader.domain.Security;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class NSEFuturesAndOptionsService {

    Logger LOGGER = LoggerFactory.getLogger(NSEFuturesAndOptionsService.class);

    private SingleTemporalServiceImpl temporalService;

    private SecurityService securityService;

    public void syncFuturesAndOptionsFromFile(String fileName) throws IOException {

        List<Map<String, Object>> rawDataFacts = null;
        try {
            rawDataFacts = NseFAndOListParser.getDataFromCSVFileNSEDownloaded(fileName);
        } catch (IOException e) {
            log.error("Failed to sync Price/Volumn : {}", e);
        }
        List<SecuritiesInFuturesAndOptions> list = enrichedData(rawDataFacts);
        temporalService.mergeRecords(list);
    }

    private List<SecuritiesInFuturesAndOptions> enrichedData(List<Map<String, Object>> rawDataFacts) {
        List<SecuritiesInFuturesAndOptions> list = new ArrayList<>();

        for (Map<String, Object> map : rawDataFacts) {

            SecuritiesInFuturesAndOptions pvd;
            try {
                pvd = ReflectionHelper.getDomainObject(map, SecuritiesInFuturesAndOptions.class);
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Error : {}", e);
                throw new RuntimeException(e);
            }

            list.add(pvd);
        }
        return list;
    }

    public List<Security> getFNOSecuritiesByAssetType(String assetType) {

        if (assetType == null || assetType.isEmpty()) {
            throw new IllegalArgumentException("Missing asset Type.");
        }

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.add("asset_type");

        SecuritiesInFuturesAndOptions security = new SecuritiesInFuturesAndOptions();

        security.setAssetType(assetType);

        List<SecuritiesInFuturesAndOptions> fnoSecurities = temporalService.searchRecords(Arrays.asList(security),
                retrieveColumns, SecuritiesInFuturesAndOptions.class);

        Map<String, SecuritiesInFuturesAndOptions> map = fnoSecurities.stream()
                .collect(Collectors.toMap(SecuritiesInFuturesAndOptions::getUnderlyingSymbol, Function.identity()));

        List<Security> securities = securityService.getAllSecurities();
        List<Security> filtered = new ArrayList<>();
        for (Security sec : securities) {
            if (map.containsKey(sec.getSymbol())) {
                filtered.add(sec);
            }
        }
        log.info("Found F&O Securities  : {} ", filtered.size());
        return filtered;
    }
}
