/**
 *
 */
package com.birlax.indiantrader.oscillator;

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
import com.birlax.indiantrader.service.SecurityService;

/**
 * @author birlax
 */
public class RelativeStrengthIndexTest extends BaseIntegerationTest {

    @Inject
    private RelativeStrengthIndex rsi;

    @Inject
    private SecurityService securityService;

    @Inject
    private NSEFuturesAndOptionsService nseFuturesAndOptionsService;

    @Inject
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    public void test(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        Date startDate = BirlaxUtil.getDateFromString("20130101");
        Date endDate = BirlaxUtil.getDateFromString("20180730");
        Date resultDate = BirlaxUtil.getDateFromString("20180520");

        IndicatorComputationOptions options14 = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 14);

        List<PriceVolumnDelivery> priceVolumnDeliveries = rsi.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        if (priceVolumnDeliveries.size() < 26) {
            return;
        }
        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] rawMoney = IndicatorUtil.transform(priceVolumnDeliveries, holder, options14);

        rsi.compute(priceVolumnDeliveries, rawMoney, holder, options14);

        IndicatorUtil.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);
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