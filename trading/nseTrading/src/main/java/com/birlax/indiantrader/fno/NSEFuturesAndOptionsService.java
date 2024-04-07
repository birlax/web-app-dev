
package com.birlax.indiantrader.fno;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.birlax.indiantrader.capitalmarket.SecurityService;
import lombok.extern.slf4j.Slf4j;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.birlax.indiantrader.capitalmarket.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class NSEFuturesAndOptionsService {

    @Autowired
    private NSEFuturesAndOptionsService nseFuturesAndOptionsService;

    @Autowired
    private SingleTemporalServiceImpl temporalService;

    @Autowired
    private SecurityService securityService;

//    public void syncFuturesAndOptionsFromFile(String fileName) throws IOException {
//
//        List<Map<String, Object>> rawDataFacts = null;
//        try {
//            rawDataFacts = nseFuturesAndOptionsService.getFNOSecuritiesByAssetType(fileName);
//        } catch (IOException e) {
//            log.error("Failed to sync Price/Volumn : {}", e);
//        }
//        List<SecuritiesInFuturesAndOptions> list = enrichedData(rawDataFacts);
//        temporalService.mergeRecords(list);
//    }

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

        SecuritiesInFuturesAndOptions security = SecuritiesInFuturesAndOptions
                .builder()
                .assetType(assetType)
                .build();

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

    public void syncFuturesAndOptionsFromFile(String s) {

    }
}
