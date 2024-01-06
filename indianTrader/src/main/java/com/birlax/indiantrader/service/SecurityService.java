/**
 *
 */
package com.birlax.indiantrader.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.indiantrader.domain.Security;

/**
 * @author birlax
 */
@Named
public class SecurityService {

    Logger LOGGER = LoggerFactory.getLogger(HistoricalPriceVolumnService.class);

    @Inject
    private SingleTemporalServiceImpl temporalService;

    @RequestMapping(path = "/getAllSecurities", produces = { "application/json" })
    public List<Security> getAllSecurities() {

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.add("symbol");
        retrieveColumns.add("isin");
        retrieveColumns.add("spn");

        List<Security> securities = temporalService.getAllRecords(retrieveColumns, Security.class);
        securities.sort((a, b) -> {
            if (a.getSpn() < b.getSpn()) {
                return -1;
            }
            if (a.getSpn() > b.getSpn()) {
                return 1;
            }
            return 0;
        });
        LOGGER.info("Found Securities to Sync : {} ", securities.size());
        return securities;
    }

    @RequestMapping(path = "/getSecurityBySymbol", produces = { "application/json" })
    public Security getSecurityBySymbol(String symbol) {

        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Missing security symbol.");
        }

        LOGGER.debug("Search for security with Symbol: {} ", symbol);

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.add("symbol");
        Security security = new Security();
        security.setSymbol(symbol);

        List<Security> securities = temporalService.searchRecords(Arrays.asList(security), retrieveColumns,
                Security.class);

        if (securities.isEmpty()) {
            throw new IllegalArgumentException("Unknow Security with symbol : " + symbol);
        }
        return securities.iterator().next();
    }
}
