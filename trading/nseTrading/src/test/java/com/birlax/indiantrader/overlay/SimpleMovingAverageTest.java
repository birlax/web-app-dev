
package com.birlax.indiantrader.overlay;

import com.birlax.indiantrader.patterndetection.PriceType;
import com.birlax.indiantrader.patterndetection.overlay.SimpleMovingAverage;
import com.birlax.indiantrader.report.DailyBuySellReport;
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
import com.birlax.indiantrader.capitalmarket.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SimpleMovingAverageTest extends BaseIntegerationTest {

    @Autowired
    private SimpleMovingAverage simpleMovingAverage;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Autowired
    private DailyBuySellReport dailyBuySellReport;

    // @Test
    public void test(Security sec, boolean printHeader) {

        String securitySymbol = sec.getSymbol();// "AVANTIFEED";

        LocalDate startDate = BirlaxUtil.getDateFromString("20180101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180701");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20180501");
        IndicatorComputationOptions optionsClosingPrice50 = new IndicatorComputationOptions(
                PriceType.CLOSING.toString(), 50);
        IndicatorComputationOptions optionsAvgPrice20 = new IndicatorComputationOptions(PriceType.VWAP.toString(), 20);

        IndicatorResultHolder holder = new IndicatorResultHolder();
        List<PriceVolumnDelivery> priceVolumnDeliveries = simpleMovingAverage.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        Double[] dataClosing = IndicatorUtil.transform(priceVolumnDeliveries, holder, optionsClosingPrice50);
        simpleMovingAverage.compute(dataClosing, holder, optionsClosingPrice50);

        Double[] dataAvgPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder, optionsAvgPrice20);
        simpleMovingAverage.compute(dataAvgPrice, holder, optionsAvgPrice20);

        dailyBuySellReport.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);
    }

    @Test
    // @Ignore
    public void testReportAll() {

        List<Security> securities = securityService.getAllSecurities();
        boolean printHeader = true;
        for (Security sec : securities) {
            test(sec, printHeader);
            printHeader = false;
            break;
        }
    }
}
