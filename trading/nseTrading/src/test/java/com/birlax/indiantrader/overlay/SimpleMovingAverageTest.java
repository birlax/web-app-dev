/**
 *
 */
package com.birlax.indiantrader.overlay;

import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.domain.Security;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.indicator.util.IndicatorUtil.PriceType;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;
import com.birlax.indiantrader.service.SecurityService;
import org.junit.jupiter.api.Test;


public class SimpleMovingAverageTest extends BaseIntegerationTest {

    @Inject
    private SimpleMovingAverage simpleMovingAverage;

    @Inject
    private SecurityService securityService;

    @Inject
    private HistoricalPriceVolumnService historicalPriceVolumnService;

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

        IndicatorUtil.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);
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
