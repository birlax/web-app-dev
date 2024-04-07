
package com.birlax.indiantrader.trading.strategy;

import com.birlax.indiantrader.fno.NSEFuturesAndOptionsService;
import com.birlax.indiantrader.patterndetection.PriceType;
import com.birlax.indiantrader.report.DailyBuySellReport;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.Deque;
import java.util.List;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.patterndetection.Direction;
import com.birlax.indiantrader.patterndetection.IndicatorCautionType;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorComputationOptions;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorEventType;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorResultHolder;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorSignalType;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.capitalmarket.Security;
import com.birlax.indiantrader.patterndetection.SmoothCurve;
import com.birlax.indiantrader.patterndetection.events.BuySellEvent;
import com.birlax.indiantrader.patterndetection.events.GenericNotificationEvent;
import com.birlax.indiantrader.patterndetection.events.SignalRack;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorUtil;
import com.birlax.indiantrader.patterndetection.oscillator.RelativeStrengthIndex;
import com.birlax.indiantrader.patterndetection.overlay.ExponentialMovingAverage;
import com.birlax.indiantrader.patterndetection.overlay.MACD;
import com.birlax.indiantrader.patterndetection.overlay.SimpleMovingAverage;
import com.birlax.indiantrader.capitalmarket.HistoricalPriceVolumnService;
import com.birlax.indiantrader.capitalmarket.SecurityService;
import com.birlax.indiantrader.report.BuySellActionGeneratorService;
import com.birlax.indiantrader.report.EvaluateBuySellActionService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/data-setup-native.sql")
public class StrategyOneTest extends BaseIntegerationTest {

    @Autowired
    private RelativeStrengthIndex rsi;

    @Autowired
    private MACD macd;

    @Autowired
    private ExponentialMovingAverage exponentialMovingAverage;

    @Autowired
    private SimpleMovingAverage simpleMovingAverage;

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

    public void testRSIBaseBUY(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        LocalDate startDate = BirlaxUtil.getDateFromString("20130101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180730");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20120520");

        IndicatorComputationOptions optionsRSI = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 14);
        IndicatorComputationOptions optionsEMA = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 9, 12,
                26);
        IndicatorComputationOptions optionsSMA = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 50);

        List<PriceVolumnDelivery> priceVolumnDeliveries = rsi.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        if (priceVolumnDeliveries.size() < 26) {
            return;
        }
        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] closingPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder, optionsRSI);

        rsi.compute(priceVolumnDeliveries, closingPrice, holder, optionsRSI);
        exponentialMovingAverage.compute(closingPrice, holder, optionsEMA);
        simpleMovingAverage.compute(closingPrice, holder, optionsSMA);

        Double[] rsi = holder.getResultList(optionsRSI.getNameForComputation(RelativeStrengthIndex.RSI));
        Double[] sma50 = holder.getResultList(optionsSMA.getNameForComputation(SimpleMovingAverage.SMA));

        Double[] sma9 = holder.getResultList(optionsEMA.getNameForComputation(SimpleMovingAverage.SMA));
        Double[] ema9 = holder.getResultList(optionsEMA.getNameForComputation(ExponentialMovingAverage.EMA));
        Double[] ema12 = holder.getResultList(optionsEMA.getNameForComputation(ExponentialMovingAverage.EMA));
        Double[] ema26 = holder.getResultList(optionsEMA.getNameForComputation(ExponentialMovingAverage.EMA));
        Double[] rsiV = holder.getResultList(optionsRSI.getNameForComputation(RelativeStrengthIndex.RSI));
        dailyBuySellReport.printReport(sec, resultDate.atStartOfDay(), printHeader, priceVolumnDeliveries, holder);

        Direction[] dirOfSMA50 = IndicatorUtil.getDirectionByLookBack(sma50, 20, 1.0, 12);

        SignalRack signalRack = new SignalRack();
        for (int i = 1; i < rsi.length; i++) {
            if (rsiV[i] == null || rsiV[i - 1] == null) {
                continue;
            }
            if (rsiV[i - 1] <= 50.0 && rsiV[i] >= 50.0) {
                // RSI crossing center line very strong signal.
                signalRack.addBuyNotificationEvent(
                        new GenericNotificationEvent(i, priceVolumnDeliveries.get(i).getTradeDate(),
                                IndicatorSignalType.BULLISH, IndicatorCautionType.NORMAL,
                                IndicatorEventType.CENTER_LINE_CROSSOVER, "RSI Crossed above center line."));
            }
            if (sma50[i - 1] != null && sma50[i] != null && ema9[i - 1] <= sma50[i - 1] && ema9[i] >= sma50[i]) {
                signalRack.addBuyNotificationEvent(
                        new GenericNotificationEvent(i, priceVolumnDeliveries.get(i).getTradeDate(),
                                IndicatorSignalType.BULLISH, IndicatorCautionType.NORMAL, IndicatorEventType.NO_CHANGE,
                                "EMA(9) Cross above SMA(50) confirmed BULLISH view."));
            }

            if (rsiV[i - 1] >= 50.0 && rsiV[i] <= 50.0) {
                // RSI crossing center line very strong signal.
                signalRack.addSellNotificationEvent(
                        new GenericNotificationEvent(i, priceVolumnDeliveries.get(i).getTradeDate(),
                                IndicatorSignalType.BEARISH, IndicatorCautionType.NORMAL,
                                IndicatorEventType.CENTER_LINE_CROSSOVER, "RSI Crossed bewlow center line."));
            }

            if (sma50[i - 1] != null && sma50[i] != null && ema9[i - 1] >= sma50[i - 1] && ema9[i] <= sma50[i]) {
                signalRack.addSellNotificationEvent(
                        new GenericNotificationEvent(i, priceVolumnDeliveries.get(i).getTradeDate(),
                                IndicatorSignalType.BEARISH, IndicatorCautionType.NORMAL, IndicatorEventType.NO_CHANGE,
                                "EMA(9) Cross below SMA(50) confirmed BEARISH view."));
            }

        }
        // System.out.println(signalRack.getBuyNotificationEventsStack());
        // System.out.println(signalRack.getSellNotificationEventsStack());
        Deque<BuySellEvent> events = buySellActionGeneratorService.generateBuySellActions(signalRack);
        evaluateBuySellActions.evaluate(sec, priceVolumnDeliveries, events, false);
    }

    public void testMACDBasedEvents(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        LocalDate startDate = BirlaxUtil.getDateFromString("20130101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180730");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20120520");

        IndicatorComputationOptions options12 = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 12, 26,
                9);

        IndicatorResultHolder holder = new IndicatorResultHolder();
        List<PriceVolumnDelivery> priceVolumnDeliveries = macd.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        Double[] data = IndicatorUtil.transform(priceVolumnDeliveries, holder, options12);

        if (priceVolumnDeliveries.size() < 26) {
            return;
        }
        IndicatorResultHolder macdResults = macd.compute(data, holder, options12);

        dailyBuySellReport.printReport(sec, resultDate.atStartOfDay(), printHeader, priceVolumnDeliveries, holder);
        int shortViewOnMACD = 3;
        int longerViewOnHistogram = 9;
        SignalRack signals = macd.getBuySellSignals(shortViewOnMACD, longerViewOnHistogram, macdResults,
                priceVolumnDeliveries);

        Deque<BuySellEvent> events = buySellActionGeneratorService.generateBuySellActions(signals);
        evaluateBuySellActions.evaluate(sec, priceVolumnDeliveries, events, false);
    }

    public void testStockWith50DayMovingAverageUpwards(Security sec, boolean printHeader) {
        String securitySymbol = sec.getSymbol();

        LocalDate startDate = BirlaxUtil.getDateFromString("20130101");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180730");
        LocalDate resultDate = BirlaxUtil.getDateFromString("20120520");

        IndicatorComputationOptions optionsSMA = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 50);

        List<PriceVolumnDelivery> priceVolumnDeliveries = rsi.getPriceVolumnDeliveryForSeries(
                historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate);

        if (priceVolumnDeliveries.size() < 50) {
            return;
        }
        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] closingPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder, optionsSMA);
        IndicatorComputationOptions optionsQtySMA = new IndicatorComputationOptions(
                PriceType.TOTAL_TRADED_QTY.toString(), 50);
        Double[] tradedQty = IndicatorUtil.transform(priceVolumnDeliveries, holder, optionsQtySMA);

        simpleMovingAverage.compute(closingPrice, holder, optionsSMA);
        simpleMovingAverage.compute(tradedQty, holder, optionsQtySMA);

        IndicatorComputationOptions options12 = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 12, 26,
                9);

        // macd.compute(closingPrice, holder, options12);

        Double[] sma50 = holder.getResultList(optionsSMA.getNameForComputation(SimpleMovingAverage.SMA));
        Double[] sma50Qty = holder.getResultList(optionsQtySMA.getNameForComputation(SimpleMovingAverage.SMA));
        // Double[] macdLine = holder.getResultList(MACD.MACD_LINE);
        Direction[] dirOfSMA50 = null;

        // Direction[] dirOfMacdLine = null;

        dirOfSMA50 = IndicatorUtil.getDirectionByLookBack(sma50, 3, 0.2, 2);
        SmoothCurve[] smoothCurvesOfPriceSM50 = null;
        smoothCurvesOfPriceSM50 = IndicatorUtil.getSmoothCurveByDirection(dirOfSMA50, 5, 0, 0, 0);
        // dirOfMacdLine = IndicatorUtil.getDirectionByLookBack(macdLine, 5, 0.1, 4);

        if (dirOfSMA50[dirOfSMA50.length - 1] != null && dirOfSMA50[dirOfSMA50.length - 1] == Direction.UPWARD) {
            System.out.println(sec + " : Still Upwards : " + dirOfSMA50[dirOfSMA50.length - 1] + " : ");
        }

        for (int i = 0; i < priceVolumnDeliveries.size(); i++) {
            if (smoothCurvesOfPriceSM50[i] != SmoothCurve.UNKNOWN) {
                System.out.println(priceVolumnDeliveries.get(i).getTradeDate() + "," + dirOfSMA50[i] + ","
                        + smoothCurvesOfPriceSM50[i] + "," + sma50Qty[i] + ","
                        + priceVolumnDeliveries.get(i).getTotalTradedQuantity());
            }
        }
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
            // testMACDBasedEvents(sec, printHeader);
            testRSIBaseBUY(sec, printHeader);
            // testStockWith50DayMovingAverageUpwards(sec, printHeader);
            printHeader = false;
            // break;
        }
    }

}
