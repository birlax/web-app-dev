
package com.birlax.indiantrader.service;

import com.birlax.indiantrader.capitalmarket.HistoricalPriceVolumnService;
import com.birlax.indiantrader.capitalmarket.SecurityService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.capitalmarket.Security;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/data-setup-native.sql")
public class HistoricalPriceVolumnServiceTest extends BaseIntegerationTest {

    @Autowired
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Autowired
    private SecurityService securityService;

    private final String baseFileLocation = "/home/birlax/Desktop/Downloads_Win/nseData/2018/July";

    @Disabled
    @Test
    public void testSyncPriceVolumnDeliveryForAllSecuritiesSingleDay()
            throws IOException, InstantiationException, IllegalAccessException {

        historicalPriceVolumnService.syncPriceVolumnDeliveryForAllSecuritiesSingleDay(
                baseFileLocation + "/2018_Jul_24_sec_bhavdata_full.csv");
    }

    @Test
    @Disabled
    public void test() throws IOException, InstantiationException, IllegalAccessException {
        historicalPriceVolumnService.syncPriceVolumnDeliveryFor24Month();
    }

    // @Ignore
    @Test
    public void testForRange() throws IOException, InstantiationException, IllegalAccessException {

        LocalDate startDate = BirlaxUtil.getDateFromString("20180731");
        LocalDate mid = BirlaxUtil.getDateFromString("20180525");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180909");

        List<Security> securities = securityService.getAllSecurities();
        Set<String> ss = new HashSet<>();

        ss.add("ABMINTLTD");
        ss.add("AICHAMP");
        ss.add("ARENTERP");
        ss.add("AUSTRAL");
        ss.add("BALAXI");
        ss.add("BLS");
        ss.add("BLUEBLENDS");
        ss.add("BLUECHIP");
        ss.add("BLUECOAST");
        ss.add("BLUEDART");
        ss.add("BSLIMITED");
        ss.add("CREATIVEYE");
        ss.add("CURATECH");
        ss.add("DBSTOCKBRO");
        ss.add("ESL");
        ss.add("EUROMULTI");
        ss.add("EXCELCROP");
        ss.add("EXCELINDUS");
        ss.add("EXIDEIND");
        ss.add("FACT");
        ss.add("FAIRCHEM");
        ss.add("FCL");
        ss.add("FCONSUMER");
        ss.add("FCSSOFT");
        ss.add("FDC");
        ss.add("FEDDERELEC");
        ss.add("FEDERALBNK");
        ss.add("FEL");
        ss.add("FELDVR");
        ss.add("FIEMIND");
        ss.add("FILATEX");
        ss.add("FINCABLES");
        ss.add("FINPIPE");
        ss.add("FIRSTWIN");
        ss.add("FORTUNEFIN");
        ss.add("GAMMONIND");
        ss.add("GARWALLROP");
        ss.add("GOLDINFRA");
        ss.add("GREENFIRE");
        ss.add("HINDDORROL");
        ss.add("HINDSYNTEX");
        ss.add("HOTELRUGBY");
        ss.add("IITL");
        ss.add("IMFA");
        ss.add("INFOMEDIA");
        ss.add("LCCINFOTEC");
        ss.add("MASKINVEST");
        ss.add("PAEL");
        ss.add("PINCON");
        ss.add("PITTILAM");
        ss.add("POLARIS");
        ss.add("PRADIP");
        ss.add("PRAKASHSTL");
        ss.add("RAJOIL");
        ss.add("RAJVIR");
        ss.add("REGENCERAM");
        ss.add("SAMTEL");
        ss.add("SERVALL");
        ss.add("SGFL");
        ss.add("SORILHOLD");
        ss.add("STERLINBIO");
        ss.add("SURANACORP");
        ss.add("TCIEXP");
        ss.add("THOMASCOTT");
        ss.add("TODAYS");
        ss.add("UMESLTD");
        ss.add("VALUEIND");
        ss.add("WINSOME");
        ss.add("WIPL");
        ss.add("ZANDUREALT");

        for (Security sec : securities) {
            if (!ss.contains(sec.getSymbol())) {
                continue;
            }
            List<PriceVolumnDelivery> priceVolumnDeliveries = historicalPriceVolumnService
                    .getPriceVolumnForSecurity(sec.getSymbol(), null, startDate, endDate);

            Set<LocalDateTime> tdDates = priceVolumnDeliveries.stream().map(a -> a.getTradeDate()).collect(Collectors.toSet());
            if (tdDates.contains(startDate) && tdDates.contains(mid)) {
                System.out.println("\n --- All good for : " + sec + " -- Nothing to sync.....\n ");
                continue;
            }
            historicalPriceVolumnService.syncPriceVolumnDelivery(sec.getSymbol(), startDate, endDate);
        }
    }

}
