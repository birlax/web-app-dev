
package com.birlax.indiantrader.patterndetection.overlay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.birlax.indiantrader.patterndetection.ActionType;
import com.birlax.indiantrader.patterndetection.PriceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.IndicatorOverlayService;
import com.birlax.indiantrader.patterndetection.Direction;
import com.birlax.indiantrader.patterndetection.IndicatorCautionType;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorComputationOptions;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorEventType;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorResultHolder;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorSignalType;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import com.birlax.indiantrader.patterndetection.events.BuySellEvent;
import com.birlax.indiantrader.patterndetection.events.GenericNotificationEvent;
import com.birlax.indiantrader.patterndetection.events.SignalRack;
import com.birlax.indiantrader.patterndetection.indicator.IndicatorUtil;
import com.birlax.indiantrader.capitalmarket.HistoricalPriceVolumnService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MACD implements IndicatorOverlayService {


    public static final String MACD_LINE = "MACD_LINE";

    public static final String SIGNAL_LINE = "SIGNAL_LINE";

    public static final String HISTROGRAM = "HISTROGRAM";

    private HistoricalPriceVolumnService historicalPriceVolumnService;

    private ExponentialMovingAverage exponentialMovingAverage;

    private SimpleMovingAverage simpleMovingAverage;

    int fastMovingEMALag = 12;

    int slowMovingEMALag = 26;

    @Override
    public IndicatorResultHolder compute(String securitySymbol, LocalDate startDate, LocalDate endDate,
                                         IndicatorComputationOptions options) {

        int lagDurationPeriod = options.getLagDuration();

        if (BirlaxUtil.diffInDays(startDate, endDate) < lagDurationPeriod) {
            log.warn(
                    "Analysis won't work as lagDuration > # of days for which data was provided. Options : {}, {}, {}",
                    options, startDate, endDate);
            // throw new IllegalArgumentException("Analysis startDate can't be after endDate.");
        }

        IndicatorResultHolder holder = new IndicatorResultHolder();
        Double[] priceVolumnDeliveries = IndicatorUtil.transform(
                getPriceVolumnDeliveryForSeries(historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate),
                holder, options);

        compute(priceVolumnDeliveries, holder, options);
        return holder;
    }

    public IndicatorResultHolder compute(Double[] priceVolumnDeliveries, IndicatorResultHolder holder,
                                         IndicatorComputationOptions options) {
        for (int lagDuration : options.getDurations()) {

            if (priceVolumnDeliveries.length < lagDuration) {
                holder.addResult(MACD.MACD_LINE, new Double[priceVolumnDeliveries.length]);
                holder.addResult(MACD.SIGNAL_LINE, new Double[priceVolumnDeliveries.length]);
                holder.addResult(MACD.HISTROGRAM, new Double[priceVolumnDeliveries.length]);
                continue;
            }

            exponentialMovingAverage.compute(priceVolumnDeliveries, holder, options);

            String fastEmaName = options.getNameForComputationByValues(ExponentialMovingAverage.EMA,
                    options.getFastLeg());
            String slowEmaName = options.getNameForComputationByValues(ExponentialMovingAverage.EMA,
                    options.getSlowLeg());

            compute(priceVolumnDeliveries, holder, fastEmaName, slowEmaName, MACD.MACD_LINE);

            IndicatorComputationOptions options9 = new IndicatorComputationOptions(MACD.MACD_LINE, 9);

            Double[] macdLine = holder.getResultList(MACD.MACD_LINE);

            exponentialMovingAverage.compute(macdLine, holder, options9);

            String signalName9Ema = options9.getNameForComputation(ExponentialMovingAverage.EMA);

            holder.addResult(MACD.SIGNAL_LINE, holder.getResultList(signalName9Ema));

            compute(priceVolumnDeliveries, holder, MACD.MACD_LINE, MACD.SIGNAL_LINE, MACD.HISTROGRAM);

        }
        return holder;
    }

    public IndicatorResultHolder compute(Double[] priceVol, IndicatorResultHolder holder, String fastEma,
                                         String slowEma, String resultName) {

        Double[] lineOne = holder.getResultList(fastEma);

        Double[] lineTwo = holder.getResultList(slowEma);

        Double[] resultLine = IndicatorUtil.getListOfDoubleOfSizeWithNulls(priceVol.length);

        for (int index = 0; index < priceVol.length; index++) {
            if (lineTwo[index] == null || lineOne[index] == null) {
                continue;
            }
            resultLine[index] = lineOne[index] - lineTwo[index];
        }
        holder.addResult(resultName, resultLine);
        return holder;
    }

    public List<GenericNotificationEvent> centerLineCrossOverEvents(IndicatorResultHolder macdResults) {

        int index = 0;

        List<GenericNotificationEvent> centerLineCrossOverEvents = new ArrayList<>();

        GenericNotificationEvent prevEvent = null;

        Double[] macdLine = macdResults.getResultList(MACD.MACD_LINE);

        for (int i = 0; i < macdLine.length; i++) {

            Double macdValue = macdLine[i];

            if (macdValue == null) {
                continue;
            }
            GenericNotificationEvent event = null;

            if (macdValue >= -0.01) {
                event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BULLISH,
                        IndicatorCautionType.NORMAL, IndicatorEventType.CENTER_LINE_CROSSOVER, "");
            } else {
                event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BEARISH,
                        IndicatorCautionType.NORMAL, IndicatorEventType.CENTER_LINE_CROSSOVER, "");
            }
            if (prevEvent == null || prevEvent.getSignalType() != event.getSignalType()) {
                centerLineCrossOverEvents.add(event);
                prevEvent = event;
            }
        }
        return centerLineCrossOverEvents;
    }

    public List<GenericNotificationEvent> signalLineCrossOverEvents(IndicatorResultHolder macdResults) {

        int index = 0;

        List<GenericNotificationEvent> signalLineCrossOverEvents = new ArrayList<>();
        Double[] signalLine = macdResults.getResultList(MACD.SIGNAL_LINE);
        Double[] macdLine = macdResults.getResultList(MACD.MACD_LINE);
        GenericNotificationEvent prevEvent = null;

        for (int i = 0; i < macdLine.length; i++) {

            if (signalLine[i] == null || macdLine[i] == null) {
                continue;
            }

            double macdValue = macdLine[i];
            double signalValue = signalLine[i];
            GenericNotificationEvent event = null;
            if (macdValue > signalValue) {
                event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BULLISH,
                        IndicatorCautionType.NORMAL, IndicatorEventType.SIGNAL_LINE_CROSSOVER, "");
            }
            if (macdValue < signalValue) {
                event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BEARISH,
                        IndicatorCautionType.NORMAL, IndicatorEventType.SIGNAL_LINE_CROSSOVER, "");
            }
            if (prevEvent == null || prevEvent.getSignalType() != event.getSignalType()) {
                signalLineCrossOverEvents.add(event);
                prevEvent = event;
            }
        }
        return signalLineCrossOverEvents;
    }

    public Pair<Double, Double> getMinMaxRange(String forLine, IndicatorResultHolder macdResults,
            List<PriceVolumnDelivery> priceVolumnDeliveries) {
        IndicatorComputationOptions options = new IndicatorComputationOptions(PriceType.CLOSING.toString(), 50);
        IndicatorResultHolder holder = new IndicatorResultHolder();

        Double[] data = macdResults.getResultList(forLine);

        simpleMovingAverage.compute(data, holder, options);
        Double[] res = holder.getResultList(options.getNameForComputation(SimpleMovingAverage.SMA));
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (Double value : res) {
            if (value == null) {
                continue;
            }
            max = Math.max(max, value);
            min = Math.min(min, value);
        }
        return Pair.of(min, max);

    }

    public SignalRack getBuySellSignals(int shortLookBack, int longLookBack, IndicatorResultHolder macdResults,
                                        List<PriceVolumnDelivery> priceVolumnDeliveries) {

        Double[] signalLine = macdResults.getResultList(MACD.SIGNAL_LINE);
        Double[] macdLine = macdResults.getResultList(MACD.MACD_LINE);
        Double[] histrogramLine = macdResults.getResultList(MACD.HISTROGRAM);

        Direction[] longSignalDirection = IndicatorUtil.getDirectionByLookBack(signalLine, longLookBack, 0.001,
                longLookBack);

        Direction[] longHistogramDirection = IndicatorUtil.getDirectionByLookBack(histrogramLine, longLookBack, 0.001,
                longLookBack);

        Direction[] longMACDDirection = IndicatorUtil.getDirectionByLookBack(macdLine, longLookBack, 0.001,
                longLookBack);

        Direction[] shortMACDDirection = IndicatorUtil.getDirectionByLookBack(macdLine, shortLookBack, 0.001,
                shortLookBack);

        Direction[] shortHistogramDirection = IndicatorUtil.getDirectionByLookBack(histrogramLine, shortLookBack, 0.001,
                shortLookBack);

        Direction[] shortSignalDirection = IndicatorUtil.getDirectionByLookBack(signalLine, shortLookBack, 0.001,
                shortLookBack);

        // getBuySignals(shortViewOnMACD, longerViewOnHistogram, macdResults, priceVolumnDeliveries, resDate);
        return getSellSignals(macdResults, priceVolumnDeliveries, longSignalDirection, longHistogramDirection,
                longMACDDirection, shortSignalDirection, shortHistogramDirection, shortMACDDirection, shortLookBack,
                longLookBack);
    }

    public SignalRack getSellSignals(IndicatorResultHolder macdResults, List<PriceVolumnDelivery> priceVolumnDeliveries,
                                     Direction[] longSignalDirection, Direction[] longHistogramDirection, Direction[] longMACDDirection,
                                     Direction[] shortSignalDirection, Direction[] shortHistogramDirection, Direction[] shortMACDDirection,
                                     int shortLookBack, int longLookBack) {

        Double[] signalLine = macdResults.getResultList(MACD.SIGNAL_LINE);
        Double[] macdLine = macdResults.getResultList(MACD.MACD_LINE);
        Double[] histrogramLine = macdResults.getResultList(MACD.HISTROGRAM);

        Double[] logRunningUpDownDays = IndicatorUtil.getLongRunningUpDownDays(histrogramLine);

        // BuySellEvent[][] buySellEvents = new BuySellEvent[2][priceVolumnDeliveries.size()];
        SignalRack signalRack = new SignalRack();

        Pair<Double, Double> macdThreshold = getMinMaxRange(MACD.MACD_LINE, macdResults, priceVolumnDeliveries);
        Pair<Double, Double> histogramThreshold = getMinMaxRange(MACD.HISTROGRAM, macdResults, priceVolumnDeliveries);

        double lowerHistogramThreshold = histogramThreshold.getLeft() * 0.01;
        double upperHistogramThreshold = histogramThreshold.getRight() * 0.01;
        // these no are based on 30 day SMA of MACD but still not good.
        double aboveAveragPositiveMACDLimit = macdThreshold.getRight() * 0.2;
        double aboveAveragNegativeMACDLimit = macdThreshold.getLeft() * 0.2;
        System.out.println("Working Ranges : " + lowerHistogramThreshold + " : " + upperHistogramThreshold + " : "
                + aboveAveragNegativeMACDLimit + " : " + aboveAveragPositiveMACDLimit);
        for (int index = 0; index < macdLine.length; index++) {

            BuySellEvent event = new BuySellEvent(index, priceVolumnDeliveries.get(index).getTradeDate(),
                    IndicatorSignalType.LOCKED, IndicatorCautionType.NORMAL, 0.0, ActionType.HOLD, "No-Action");

            if (shortMACDDirection[index] == Direction.UNKNOWN || longHistogramDirection[index] == Direction.UNKNOWN) {
                continue;
            }
            double currentCloseness = histrogramLine[index];
            double previousCloseness = currentCloseness;
            if (index - 1 >= 0) {
                previousCloseness = histrogramLine[index - 1];
            }

            // General Delayed STRONG SELL SIGNAL
            if (longMACDDirection[index] == Direction.DOWNWARD && longHistogramDirection[index] == Direction.DOWNWARD) {
                String detail = "";
                IndicatorEventType eventType = IndicatorEventType.NO_CHANGE;
                if (shortSignalDirection[index] == Direction.DOWNWARD) {
                    detail = "Do not BUY/May be SELL";
                    eventType = IndicatorEventType.NO_CHANGE;
                }
                if (longSignalDirection[index] == Direction.DOWNWARD) {
                    detail = "Do not BUY/May be STRONG-SELL";
                    eventType = IndicatorEventType.NO_CHANGE;
                }
                // THIS IS IS A STRONG SELL
                signalRack.addSellNotificationEvent(
                        new GenericNotificationEvent(index, priceVolumnDeliveries.get(index).getTradeDate(),
                                IndicatorSignalType.BEARISH, IndicatorCautionType.EXTREME, eventType, detail));

            }

            if (longMACDDirection[index] == Direction.UPWARD && longSignalDirection[index] == Direction.UPWARD) {

                String detail = "Up Trend in consolidation/continue.";
                IndicatorEventType eventType = IndicatorEventType.NO_CHANGE;
                if (longHistogramDirection[index] == Direction.DOWNWARD) {
                    detail = "LONG-histogram (Reaching TOP)STRONG-";
                    // eventType = ActionType.HOLD;

                }
                if (shortHistogramDirection[index] == Direction.DOWNWARD) {
                    detail = "SHORT-histogram (Reaching TOP)MEDIUM-";
                    // eventType = ActionType.HOLD;
                    eventType = IndicatorEventType.NO_CHANGE;
                }
                if (currentCloseness >= 0 && previousCloseness <= 0) {
                    detail += "SELL-NOW";
                } else {
                    detail += "SELL-SOON";
                }
                signalRack.addSellNotificationEvent(
                        new GenericNotificationEvent(index, priceVolumnDeliveries.get(index).getTradeDate(),
                                IndicatorSignalType.BEARISH, IndicatorCautionType.NORMAL, eventType, detail));
            }

            // General Delayed STRONG SELL SIGNAL
            if (shortMACDDirection[index] == Direction.DOWNWARD && longSignalDirection[index] == Direction.UPWARD) {

                String detail = "Up Trend Weakening|Increasing Sell pressure.";
                IndicatorEventType eventType = IndicatorEventType.NO_CHANGE;
                if (longHistogramDirection[index] == Direction.DOWNWARD) {
                    if (macdLine[index] >= aboveAveragPositiveMACDLimit) {
                        detail = "Long Histogram in Decline MEDIUM Drop ahead | BOOK Profit";
                        eventType = IndicatorEventType.NO_CHANGE;
                    } else {
                        detail = "Long Histogram in Decline STRONG Drop ahead | BOOK Profit";
                        eventType = IndicatorEventType.NO_CHANGE;
                    }
                } else if (shortHistogramDirection[index] == Direction.DOWNWARD) {
                    if (macdLine[index] >= aboveAveragPositiveMACDLimit) {
                        detail = "Short Histogram in Decline MEDIUM Drop ahead | But HOLD for Now";
                        eventType = IndicatorEventType.NO_CHANGE;
                    } else {
                        detail = "Short Histogram in Decline MEDIUM Drop ahead | BOOK Profit";
                        eventType = IndicatorEventType.NO_CHANGE;
                    }
                }
                signalRack.addSellNotificationEvent(
                        new GenericNotificationEvent(index, priceVolumnDeliveries.get(index).getTradeDate(),
                                IndicatorSignalType.BEARISH, IndicatorCautionType.NORMAL, eventType, detail));
            }

            if (shortMACDDirection[index] == Direction.UPWARD && longSignalDirection[index] == Direction.DOWNWARD) {

                String detail = "";
                IndicatorEventType eventType = IndicatorEventType.NO_CHANGE;
                if (longHistogramDirection[index] == Direction.DOWNWARD) {
                    if (macdLine[index] >= aboveAveragPositiveMACDLimit) {
                        detail = "(Long Histogram Confirmation)|This is bottom-out[+ve]| HOLD for more profit";
                        // eventType = ActionType.HOLD;
                        eventType = IndicatorEventType.NO_CHANGE;

                    } else {
                        detail = "(Long Histogram Confirmation)|This is bottom-out[-ve]| Wait for Upward Momentum | Good Entry Price | Buy";
                        // eventType = ActionType.BUY;
                        eventType = IndicatorEventType.NO_CHANGE;
                    }
                } else if (shortHistogramDirection[index] == Direction.DOWNWARD) {
                    if (macdLine[index] >= aboveAveragPositiveMACDLimit) {
                        detail = "(Short Histogram Confirmation)|This is bottom-out[+ve]| HOLD for more profit";
                        // eventType = ActionType.HOLD;
                        eventType = IndicatorEventType.NO_CHANGE;
                    } else {
                        detail = "(Short Histogram Confirmation)|This is bottom-out[-ve]| Wait for Upward Momentum | Good Entry Price | Buy";
                        // eventType = ActionType.BUY;
                        eventType = IndicatorEventType.NO_CHANGE;
                    }
                }
                signalRack.addBuyNotificationEvent(
                        new GenericNotificationEvent(index, priceVolumnDeliveries.get(index).getTradeDate(),
                                IndicatorSignalType.BULLISH, IndicatorCautionType.NORMAL, eventType, detail));

            }
            // Pure Buy calls
            if (longHistogramDirection[index] == Direction.UPWARD && longSignalDirection[index] == Direction.DOWNWARD) {

                String detail = "";
                IndicatorEventType eventType = IndicatorEventType.NO_CHANGE;
                if (longMACDDirection[index] == Direction.DOWNWARD && shortMACDDirection[index] == Direction.UPWARD) {
                    detail += "BUY-SOON";
                }

                if (previousCloseness <= 0 && currentCloseness >= 0) {
                    detail += "BUY-NOW(ZEROLINE CROSSed)";
                }
                if (macdLine[index] >= 0 && macdLine[index] >= aboveAveragPositiveMACDLimit) {
                    // only applicable when dropping from top.
                    detail += "(Wait for Bounce from ZeroLine| then buy/sell)";
                    // eventType = ActionType.HOLD;
                    eventType = IndicatorEventType.NO_CHANGE;
                }
                if (macdLine[index] <= 0 && macdLine[index - 1] < 0) {
                    // only applicable when dropping from top.
                    detail += "(Wait for ZeroLine CrossOver event then buy)";
                    // eventType = ActionType.BUY;
                    eventType = IndicatorEventType.NO_CHANGE;
                }
                signalRack.addBuyNotificationEvent(
                        new GenericNotificationEvent(index, priceVolumnDeliveries.get(index).getTradeDate(),
                                IndicatorSignalType.BULLISH, IndicatorCautionType.NORMAL, eventType, detail));

            }
            // short curved buy from below the zero line
            if (shortHistogramDirection[index] == Direction.UPWARD
                    && shortSignalDirection[index] == Direction.DOWNWARD) {

                String detail = "";
                IndicatorEventType eventType = IndicatorEventType.NO_CHANGE;

                if (previousCloseness <= 0 && currentCloseness >= 0) {
                    detail += "BUY-NOW(ZEROLINE CROSSed)";
                }
                if (macdLine[index] < 0) {
                    // only applicable when dropping from top.
                    detail += "MACD -ve and now moving toward Zero line(BUY SOON/NOW)";
                    // eventType = ActionType.BUY;
                    eventType = IndicatorEventType.NO_CHANGE;
                }
                signalRack.addBuyNotificationEvent(
                        new GenericNotificationEvent(index, priceVolumnDeliveries.get(index).getTradeDate(),
                                IndicatorSignalType.BULLISH, IndicatorCautionType.NORMAL, eventType, detail));

            }

        }
        return signalRack;
    }

}
