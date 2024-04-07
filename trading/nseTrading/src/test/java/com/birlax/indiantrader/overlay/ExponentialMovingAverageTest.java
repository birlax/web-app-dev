
package com.birlax.indiantrader.overlay;

import com.birlax.indiantrader.patterndetection.PriceType;
import com.birlax.indiantrader.patterndetection.overlay.ExponentialMovingAverage;
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
import com.birlax.indiantrader.report.DailyBuySellReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ExponentialMovingAverageTest extends BaseIntegerationTest {

    @Autowired
    private ExponentialMovingAverage exponentialMovingAverage;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Autowired
    private DailyBuySellReport dailyBuySellReport;

    public void test(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();
        LocalDate startDate = BirlaxUtil.getDateFromString("20160101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180701");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20180501");

        IndicatorComputationOptions options12 = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 12, 26,
                9);

        IndicatorResultHolder holder = new IndicatorResultHolder();

        List<PriceVolumnDelivery> priceVolumnDeliveries = exponentialMovingAverage.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        Double[] data = IndicatorUtil.transform(priceVolumnDeliveries, holder, options12);

        exponentialMovingAverage.compute(data, holder, options12);

        dailyBuySellReport.printReport(sec, resultDate.atStartOfDay(), printHeader, priceVolumnDeliveries, holder);

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
