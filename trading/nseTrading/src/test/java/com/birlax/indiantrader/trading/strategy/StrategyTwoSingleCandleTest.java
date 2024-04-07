
package com.birlax.indiantrader.trading.strategy;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.fno.NSEFuturesAndOptionsService;
import com.birlax.indiantrader.patterndetection.PriceType;
import com.birlax.indiantrader.patterndetection.candle.OpeningAbovePreviousHigh;
import com.birlax.indiantrader.capitalmarket.Security;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorComputationOptions;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorEventType;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorResultHolder;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorSignalType;
import com.birlax.indiantrader.patterndetection.events.BuySellEvent;
import com.birlax.indiantrader.patterndetection.events.GenericNotificationEvent;
import com.birlax.indiantrader.patterndetection.events.SignalRack;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorUtil;
import com.birlax.indiantrader.patterndetection.IndicatorCautionType;
import com.birlax.indiantrader.capitalmarket.HistoricalPriceVolumnService;
import com.birlax.indiantrader.capitalmarket.SecurityService;
import com.birlax.indiantrader.report.BuySellActionGeneratorService;
import com.birlax.indiantrader.report.DailyBuySellReport;
import com.birlax.indiantrader.report.EvaluateBuySellActionService;

import java.time.LocalDate;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/data-setup-native.sql")
public class StrategyTwoSingleCandleTest extends BaseIntegerationTest {

    @Autowired
    private OpeningAbovePreviousHigh openingAbovePreviousHigh;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private NSEFuturesAndOptionsService nseFuturesAndOptionsService;

    @Autowired
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Autowired
    private BuySellActionGeneratorService buySellActionGeneratorService;

    @Autowired
    private EvaluateBuySellActionService evaluateBuySellActions;

    @Autowired
    private DailyBuySellReport dailyBuySellReport;


    public void testSingleCandle(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        LocalDate startDate = BirlaxUtil.getDateFromString("20130101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180730");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20120520");

        IndicatorComputationOptions options = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 2);

        List<PriceVolumnDelivery> priceVolumnDeliveries = openingAbovePreviousHigh.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] closingPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder, options);

        double percentage = 1.0;

        String resultName = OpeningAbovePreviousHigh.OPENING_ABOVE_PREVIOUS_HIGH;
        openingAbovePreviousHigh.compute(closingPrice, priceVolumnDeliveries, holder, 2, percentage, resultName);

        Double[] rsi = holder.getResultList(resultName);
        dailyBuySellReport.printReport(sec, resultDate.atStartOfDay(), printHeader, priceVolumnDeliveries, holder);

        SignalRack signalRack = new SignalRack();
        for (int i = 0; i < rsi.length; i++) {

            if (rsi[i] >= 1) {
                signalRack.addBuyNotificationEvent(
                        new GenericNotificationEvent(i, priceVolumnDeliveries.get(i).getTradeDate(),
                                IndicatorSignalType.BULLISH, IndicatorCautionType.NORMAL,
                                IndicatorEventType.SINGLE_CANDLE_BIG_GAP_UP_OPENING, "Opened above previou HIGH."));
                signalRack.addSellNotificationEvent(
                        new GenericNotificationEvent(i, priceVolumnDeliveries.get(i).getTradeDate(),
                                IndicatorSignalType.BEARISH, IndicatorCautionType.NORMAL, IndicatorEventType.NO_CHANGE,
                                "Opened above previou HIGH. Moved above requested %pct"));
            }
        }
        // System.out.println(signalRack.getBuyNotificationEventsStack());
        // System.out.println(signalRack.getSellNotificationEventsStack());
        Deque<BuySellEvent> events = buySellActionGeneratorService.generateBuySellActions(signalRack);
        evaluateBuySellActions.evaluate(sec, priceVolumnDeliveries, events, false);
    }

    @Test
    // @Ignore
    public void testReportAll() {

        List<Security> securities = nseFuturesAndOptionsService.getFNOSecuritiesByAssetType("STOCK");
        // List<Security> securities = new ArrayList<>();
        // securities.add(securityService.getSecurityBySymbol("BALKRISIND"));
        // List<Security> securities = securityService.getAllSecurities();
        boolean printHeader = true;
        for (Security sec : securities) {
            testSingleCandle(sec, printHeader);
            printHeader = false;
            // break;
        }
    }

}
