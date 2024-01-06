/**
 *
 */
package com.birlax.indiantrader.overlay;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

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

/**
 * @author birlax
 */
public class MACDTest extends BaseIntegerationTest {

    @Inject
    private MACD macd;

    @Inject
    private NSEFuturesAndOptionsService securityService;

    @Inject
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    public void test(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        Date startDate = BirlaxUtil.getDateFromString("20130101");
        Date endDate = BirlaxUtil.getDateFromString("20180909");

        Date resultDate = BirlaxUtil.getDateFromString("20180901");

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
    // @Ignore
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
