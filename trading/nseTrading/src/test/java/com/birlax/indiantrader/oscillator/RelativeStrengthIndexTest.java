
package com.birlax.indiantrader.oscillator;

import com.birlax.indiantrader.patterndetection.PriceType;
import com.birlax.indiantrader.patterndetection.oscillator.RelativeStrengthIndex;
import java.time.LocalDate;
import java.util.List;


import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorComputationOptions;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorResultHolder;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.capitalmarket.Security;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorUtil;
import com.birlax.indiantrader.capitalmarket.HistoricalPriceVolumnService;
import com.birlax.indiantrader.fno.NSEFuturesAndOptionsService;
import com.birlax.indiantrader.capitalmarket.SecurityService;
import com.birlax.indiantrader.report.DailyBuySellReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/data-setup-native.sql")
public class RelativeStrengthIndexTest extends BaseIntegerationTest {

    @Autowired
    private RelativeStrengthIndex rsi;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private NSEFuturesAndOptionsService nseFuturesAndOptionsService;

    @Autowired
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Autowired
    private DailyBuySellReport dailyBuySellReport;

    public void test(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        LocalDate startDate = BirlaxUtil.getDateFromString("20130101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180730");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20180520");

        IndicatorComputationOptions options14 = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 14);

        List<PriceVolumnDelivery> priceVolumnDeliveries = rsi.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        if (priceVolumnDeliveries.size() < 26) {
            return;
        }
        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] rawMoney = IndicatorUtil.transform(priceVolumnDeliveries, holder, options14);

        rsi.compute(priceVolumnDeliveries, rawMoney, holder, options14);

        dailyBuySellReport.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);
    }

    @Test
    // @Ignore
    public void testReportAll() {

        List<Security> securities = nseFuturesAndOptionsService.getFNOSecuritiesByAssetType("STOCK");
        // securityService.getAllSecurities();
        boolean printHeader = true;
        for (Security sec : securities) {
            test(sec, printHeader);
            printHeader = false;
            // break;
        }
    }

}
