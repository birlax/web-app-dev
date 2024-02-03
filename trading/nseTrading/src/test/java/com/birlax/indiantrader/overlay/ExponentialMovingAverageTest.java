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


public class ExponentialMovingAverageTest extends BaseIntegerationTest {

    @Inject
    private ExponentialMovingAverage exponentialMovingAverage;

    @Inject
    private SecurityService securityService;

    @Inject
    private HistoricalPriceVolumnService historicalPriceVolumnService;

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
