/**
 *
 */
package com.birlax.indiantrader.oscillator;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.domain.Security;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.indicator.util.IndicatorUtil.PriceType;
import com.birlax.indiantrader.overlay.SimpleMovingAverage;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;
import com.birlax.indiantrader.service.SecurityService;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author birlax
 */
public class MoneyFlowIndexTest extends BaseIntegerationTest {

    @Inject
    private MoneyFlowIndex moneyFlowIndex;

    @Inject
    private SimpleMovingAverage simpleMovingAverage;

    @Inject
    private SecurityService securityService;

    @Inject
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    public void test(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        LocalDate startDate = BirlaxUtil.getDateFromString("20130101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180730");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20180520");

        IndicatorComputationOptions options14 = new IndicatorComputationOptions(PriceType.RAW_MONEY_FLOW.toString(),
                14);
        IndicatorComputationOptions options50C = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 50);
        IndicatorComputationOptions options50V = new IndicatorComputationOptions(PriceType.TOTAL_TRADED_QTY.toString(),
                50);

        List<PriceVolumnDelivery> priceVolumnDeliveries = moneyFlowIndex.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        if (priceVolumnDeliveries.size() < 80) {
            return;
        }

        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] data = IndicatorUtil.transform(priceVolumnDeliveries, holder, options14);

        Double[] closePriceInput = IndicatorUtil.transform(priceVolumnDeliveries, holder, options50C);
        Double[] tradedQtyInput = IndicatorUtil.transform(priceVolumnDeliveries, holder, options50V);

        simpleMovingAverage.compute(closePriceInput, holder, options50C);

        Double[] dd = holder.getResultList(options50C.getNameForComputation(SimpleMovingAverage.SMA));

        if (dd[dd.length - 1] < 500.0) {
            System.out.println("50 Day SMA Price below 500.0 For : " + securitySymbol + " as of : "
                    + priceVolumnDeliveries.get(dd.length - 1));
            // return;
        }

        simpleMovingAverage.compute(tradedQtyInput, holder, options50V);

        dd = holder.getResultList(options50V.getNameForComputation(SimpleMovingAverage.SMA));
        if (dd[dd.length - 1] < 100_000) {
            System.out.println("50 Day SMA Trade-Qty below 100K For : " + securitySymbol + " as of : "
                    + priceVolumnDeliveries.get(dd.length - 1));

            // return;
        }
        moneyFlowIndex.compute(priceVolumnDeliveries, data, holder, options14);
        IndicatorComputationOptions options = new IndicatorComputationOptions("MFI(RAW_MONEY_FLOW|14)", 50);
        Double[] smaOfMFI = holder.getResultList("MFI(RAW_MONEY_FLOW|14)");
        simpleMovingAverage.compute(smaOfMFI, holder, options);

        IndicatorUtil.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);
    }

    @Test
    // @Ignore
    public void testReportAll() {

        // List<Security> securities = securityService.getAllSecurities();
        Security sec = securityService.getSecurityBySymbol("DHFL");
        boolean printHeader = true;
        // for (Security sec : securities) {
        test(sec, printHeader);
        printHeader = false;
        // break;
        // }
    }

}
