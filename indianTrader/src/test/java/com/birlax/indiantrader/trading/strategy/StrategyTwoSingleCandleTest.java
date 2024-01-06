/**
 *
 */
package com.birlax.indiantrader.trading.strategy;

import java.util.Date;
import java.util.Deque;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.candle.OpeningAbovePreviousHigh;
import com.birlax.indiantrader.domain.IndicatorCautionType;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorEventType;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.IndicatorSignalType;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.domain.Security;
import com.birlax.indiantrader.indicator.events.BuySellEvent;
import com.birlax.indiantrader.indicator.events.GenericNotificationEvent;
import com.birlax.indiantrader.indicator.events.SignalRack;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.indicator.util.IndicatorUtil.PriceType;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;
import com.birlax.indiantrader.service.NSEFuturesAndOptionsService;
import com.birlax.indiantrader.service.SecurityService;
import com.birlax.indiantrader.service.backtest.BuySellActionGeneratorService;
import com.birlax.indiantrader.service.backtest.EvaluateBuySellActionService;

/**
 * @author birlax
 */
public class StrategyTwoSingleCandleTest extends BaseIntegerationTest {

    @Inject
    private OpeningAbovePreviousHigh openingAbovePreviousHigh;

    @Inject
    private SecurityService securityService;

    @Inject
    private NSEFuturesAndOptionsService nseFuturesAndOptionsService;

    @Inject
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Inject
    private BuySellActionGeneratorService buySellActionGeneratorService;

    @Inject
    private EvaluateBuySellActionService evaluateBuySellActions;

    public void testSingleCandle(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        Date startDate = BirlaxUtil.getDateFromString("20130101");
        Date endDate = BirlaxUtil.getDateFromString("20180730");
        Date resultDate = BirlaxUtil.getDateFromString("20120520");

        IndicatorComputationOptions options = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 2);

        List<PriceVolumnDelivery> priceVolumnDeliveries = openingAbovePreviousHigh.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] closingPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder, options);

        double percentage = 1.0;

        String resultName = OpeningAbovePreviousHigh.OPENING_ABOVE_PREVIOUS_HIGH;
        openingAbovePreviousHigh.compute(closingPrice, priceVolumnDeliveries, holder, 2, percentage, resultName);

        Double[] rsi = holder.getResultList(resultName);
        // IndicatorUtil.printReport(sec, resultDate, printHeader, priceVolumnDeliveries, holder);

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
