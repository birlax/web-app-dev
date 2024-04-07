
package com.birlax.indiantrader.overlay;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.patterndetection.PriceType;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorComputationOptions;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorResultHolder;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.capitalmarket.Security;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorUtil;
import com.birlax.indiantrader.patterndetection.overlay.MACD;
import com.birlax.indiantrader.capitalmarket.HistoricalPriceVolumnService;
import com.birlax.indiantrader.fno.NSEFuturesAndOptionsService;
import com.birlax.indiantrader.report.DailyBuySellReport;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class MACDTest extends BaseIntegerationTest {

    @Autowired
    private MACD macd;

    @Autowired
    private NSEFuturesAndOptionsService securityService;

    @Autowired
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Autowired
    private DailyBuySellReport dailyBuySellReport;

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

        dailyBuySellReport.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);

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
