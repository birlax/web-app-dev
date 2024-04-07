
package com.birlax.indiantrader.capitalmarket;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.indiantrader.capitalmarket.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SecurityService {


    private SingleTemporalServiceImpl temporalService;

    @RequestMapping(path = "/getAllSecurities", produces = { "application/json" })
    public List<Security> getAllSecurities() {

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.add("symbol");
        retrieveColumns.add("isin");
        retrieveColumns.add("spn");

        List<Security> securities = temporalService.fetchAllRecords(retrieveColumns, Security.class);
        securities.sort((a, b) -> {
            if (a.getSpn() < b.getSpn()) {
                return -1;
            }
            if (a.getSpn() > b.getSpn()) {
                return 1;
            }
            return 0;
        });
        log.info("Found Securities to Sync : {} ", securities.size());
        return securities;
    }

    @RequestMapping(path = "/getSecurityBySymbol", produces = { "application/json" })
    public Security getSecurityBySymbol(String symbol) {

        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Missing security symbol.");
        }

        log.debug("Search for security with Symbol: {} ", symbol);

        Set<String> retrieveColumns = new HashSet<>();
        retrieveColumns.add("symbol");
        Security security = Security.builder()
                .symbol(symbol)
                .build();

        List<Security> securities = temporalService.searchRecords(Arrays.asList(security), retrieveColumns,
                Security.class);

        if (securities.isEmpty()) {
            throw new IllegalArgumentException("Unknow Security with symbol : " + symbol);
        }
        return securities.iterator().next();
    }
}
