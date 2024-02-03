/**
 *
 */
package com.birlax.indiantrader.overlay;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.domain.Security;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.indicator.util.IndicatorUtil.PriceType;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;
import com.birlax.indiantrader.service.NSEFuturesAndOptionsService;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;


public class MACDTest extends BaseIntegerationTest {

    @Inject
    private MACD macd;

    private NSEFuturesAndOptionsService securityService;

    private HistoricalPriceVolumnService historicalPriceVolumnService;

    public void test(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        LocalDate startDate = BirlaxUtil.getDateFromString("20130101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180909");

        LocalDate resultDate = BirlaxUtil.getDateFromString("20180901");

        IndicatorComputationOptions options12 = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 12, 26,
                9);

        IndicatorResultHolder holder = new IndicatorResultHolder();
        List<PriceVolumnDelivery> priceVolumnDeliveries = macd.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        Double[] data = IndicatorUtil.transform(priceVolumnDeliveries, holder, options12);

        if (priceVolumnDeliveries.size() < 26) {
            return;
        }
        macd.compute(data, holder, options12);

        IndicatorUtil.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);

    }

    @Test
    public void testReportAll() {

        String assetType = "STOCK";
        List<Security> securities = securityService.getFNOSecuritiesByAssetType(assetType);
        boolean printHeader = true;
        for (Security sec : securities) {
            test(sec, printHeader);
            printHeader = false;
            // break;
        }
    }

}
