/**
 *
 */
package com.birlax.indiantrader.oscillator;

import jakarta.inject.Named;
import java.time.LocalDate;
import java.util.Date;

import com.birlax.indiantrader.IndicatorOverlayService;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;

/**
 * The Percentage Price Oscillator (PPO) is a momentum oscillator that measures the difference between two
 * moving averages as a percentage of the larger moving average. As with its cousin, MACD, the Percentage Price
 * Oscillator is shown with a signal line, a histogram and a centerline. Signals/Events are generated with signal line
 * crossovers, centerline crossovers, and divergences. Because these signals are no different than those
 * associated with MACD, documentation here will focus on a few differences between PPO v/s MACD.</br>
 * </br>
 * First, PPO readings are not subject to the price level of the security.</br>
 * </br>
 * </br>
 * Second, PPO readings for different securities can be compared, even when there are large differences in the price.
 * </br>
 * MACD levels are affected by the price of a security. A high-priced security will have higher or lower MACD values
 * than a low-priced security, even if volatility is basically equal. This is because MACD is based on the absolute
 * difference in the two moving averages.
 * </br>
 * Although the indicator lines look the same, there are often subtle differences between MACD and PPO.
 * </br>
 * </br>
 * Large Price Changes -Problem of MACD.</br>
 * Because MACD is based on absolute levels, large price changes can affect MACD levels over an extended period of time.
 * If a stock advances from 20 to 100, its MACD levels will be considerably smaller around 20 than around 100. The PPO
 * solves this problem by showing MACD values in percentage terms.</br>
 * As we already know CenterLine cross overs reflect the change in momentum direction, greater no of crossover for same
 * range of period reflects more volatility.</br>
 * MACD/PPO CenterLine cross overs are not very frequent for short windows like week/month, they happen once or twice at
 * max in a week/month. But range of PPO it-self can be used a a measure of volatility.</br>
 * Note: Same can't be done with MACD as different securities having different price ranges will have MACD proportional
 * to price, hence we should use PPO which are % and hence relative.
 *
 * @author birlax
 */
@Named
public class PercentagePriceOscillator implements IndicatorOverlayService {

    /*
     * (non-Javadoc)
     * @see com.birlax.indiantrader.IndicatorOverlayService#compute(java.lang.String, java.util.Date, java.util.Date,
     * com.birlax.indiantrader.domain.IndicatorComputationOptions)
     */
    @Override
    public IndicatorResultHolder compute(String securitySymbol, LocalDate startDate, LocalDate endDate,
            IndicatorComputationOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * @Inject
     * private HistoricalPriceVolumnService historicalPriceVolumnService;
     * @Inject
     * private ExponentialMovingAverage exponentialMovingAverage;
     * int fastMovingEMALag = 12;
     * int slowMovingEMALag = 26;
     * @Override
     * public IndicatorResultHolder compute(String securitySymbol, Date startDate, Date endDate,
     * IndicatorComputationOptions options) {
     * int lagDurationPeriod = options.getFastLeg();
     * if (BirlaxUtil.diffInDays(startDate, endDate) < lagDurationPeriod) {
     * LOGGER.warn(
     * "Analysis won't work as lagDuration > # of days for which data was provided. Options : {}, {}, {}",
     * options, startDate, endDate);
     * // throw new IllegalArgumentException("Analysis startDate can't be after endDate.");
     * }
     * List<IndicatorInputHolder> priceVolumnDeliveries = IndicatorUtil.convert(
     * getPriceVolumnDeliveryForSeries(historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate),
     * options);
     * IndicatorResultHolder holder = new IndicatorResultHolder();
     * compute(priceVolumnDeliveries, holder, options);
     * return holder;
     * }
     * private IndicatorResultHolder compute(List<IndicatorInputHolder> priceVol, IndicatorResultHolder holder,
     * IndicatorComputationOptions options) {
     * IndicatorComputationOptions options26 = IndicatorComputationOptions.copy(options);
     * options26.setUseFast(false);
     * if (priceVol.size() < options26.getSlowLeg()) {
     * holder.addResult(MACD.MACD_LINE, new Double[0]);
     * holder.addResult(MACD.SIGNAL_LINE, new Double[0]);
     * holder.addResult(MACD.HISTROGRAM, new Double[0]);
     * return holder;
     * }
     * Double[] ema12 = exponentialMovingAverage.compute(priceVol, holder, options)
     * .getResultList(options.getNameForComputation(ExponentialMovingAverage.EMA));
     * Double[] ema26 = exponentialMovingAverage.compute(priceVol, holder, options26)
     * .getResultList(options26.getNameForComputation(ExponentialMovingAverage.EMA));
     * Double[] macdLine = IndicatorUtil.getListOfDoubleOfSizeWithNulls(priceVol.size());
     * List<IndicatorInputHolder> macdLineDataForSignalComputation = new ArrayList<>();
     * // System.out.println("-12-Starting with : " + ema12.entrySet().iterator().next());
     * for (int index = 0; index < priceVol.size(); index++) {
     * if (ema26[index] == null || ema12[index] == null) {
     * macdLineDataForSignalComputation.add(index, new IndicatorInputHolder(index,
     * priceVol.get(index).getSpn(), priceVol.get(index).getDate(), null));
     * continue;
     * }
     * // 12 EMA minus 26 EMA
     * double ppo = (ema12[index] - ema26[index]) / ema26[index] * 100;
     * macdLine[index] = ppo;
     * macdLineDataForSignalComputation.add(index, new IndicatorInputHolder(index, priceVol.get(index).getSpn(),
     * priceVol.get(index).getDate(), macdLine[index]));
     * }
     * IndicatorComputationOptions options9 = IndicatorComputationOptions.copy(options);
     * options9.setFastLeg(9);
     * exponentialMovingAverage.compute(macdLineDataForSignalComputation, holder, options9);
     * holder.addResult(MACD.MACD_LINE, macdLine);
     * holder.addResult(MACD.SIGNAL_LINE,
     * holder.getResultList(options9.getNameForComputation(ExponentialMovingAverage.EMA)));
     * // compute histogram
     * Double[] signalLine = holder.getResultList(MACD.SIGNAL_LINE);
     * Double[] histogram = IndicatorUtil.getListOfDoubleOfSizeWithNulls(priceVol.size());
     * for (int index = 0; index < priceVol.size(); index++) {
     * if (signalLine[index] == null || macdLine[index] == null) {
     * continue;
     * }
     * double macdValue = macdLine[index];
     * double signalValue = signalLine[index];
     * histogram[index] = macdValue - signalValue;
     * }
     * holder.addResult(MACD.HISTROGRAM, histogram);
     * return holder;
     * }
     * public List<GenericNotificationEvent> centerLineCrossOverEvents(IndicatorResultHolder macdResults) {
     * int index = 0;
     * List<GenericNotificationEvent> centerLineCrossOverEvents = new ArrayList<>();
     * GenericNotificationEvent prevEvent = null;
     * Double[] macdLine = macdResults.getResultList(MACD.MACD_LINE);
     * for (Double macdValue : macdLine) {
     * GenericNotificationEvent event = null;
     * if (macdValue >= 0.0) {
     * event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BULLISH,
     * IndicatorCautionType.NORMAL, IndicatorEventType.MACD_SIGNAL_LINE_CROSSOVER);
     * } else {
     * event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BEARISH,
     * IndicatorCautionType.NORMAL, IndicatorEventType.MACD_SIGNAL_LINE_CROSSOVER);
     * }
     * if (prevEvent == null || prevEvent.getSignalType() != event.getSignalType()) {
     * centerLineCrossOverEvents.add(event);
     * prevEvent = event;
     * }
     * }
     * return centerLineCrossOverEvents;
     * }
     * public List<GenericNotificationEvent> signalLineCrossOverEvents(IndicatorResultHolder macdResults) {
     * int index = 0;
     * List<GenericNotificationEvent> signalLineCrossOverEvents = new ArrayList<>();
     * Double[] signalLine = macdResults.getResultList(MACD.SIGNAL_LINE);
     * Double[] macdLine = macdResults.getResultList(MACD.MACD_LINE);
     * GenericNotificationEvent prevEvent = null;
     * for (int i = 0; i < macdLine.length; i++) {
     * if (signalLine[i] == null || macdLine[i] == null) {
     * continue;
     * }
     * double macdValue = macdLine[i];
     * double signalValue = signalLine[i];
     * GenericNotificationEvent event = null;
     * if (macdValue > signalValue) {
     * event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BULLISH,
     * IndicatorCautionType.NORMAL, IndicatorEventType.MACD_SIGNAL_LINE_CROSSOVER);
     * }
     * if (macdValue < signalValue) {
     * event = new GenericNotificationEvent(index++, null, IndicatorSignalType.BEARISH,
     * IndicatorCautionType.NORMAL, IndicatorEventType.MACD_SIGNAL_LINE_CROSSOVER);
     * }
     * if (prevEvent == null || prevEvent.getSignalType() != event.getSignalType()) {
     * signalLineCrossOverEvents.add(event);
     * prevEvent = event;
     * }
     * }
     * return signalLineCrossOverEvents;
     * }
     * public Pair<Double, Double> getPPOVolatilityRange(IndicatorResultHolder macdResults) {
     * double min = Arrays.stream(macdResults.getResultList(MACD.MACD_LINE)).min(IndicatorUtil.getDoubleComparator())
     * .orElseGet(() -> 0.0);
     * double max = Arrays.stream(macdResults.getResultList(MACD.MACD_LINE)).max(IndicatorUtil.getDoubleComparator())
     * .orElseGet(() -> 0.0);
     * return Pair.of(min, max);
     * }
     */
}
