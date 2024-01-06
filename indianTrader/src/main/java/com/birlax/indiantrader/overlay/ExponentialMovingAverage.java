/**
 *
 */
package com.birlax.indiantrader.overlay;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.IndicatorOverlayService;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;

/**
 * @author birlax
 */
@Named
public class ExponentialMovingAverage implements IndicatorOverlayService {

    Logger LOGGER = LoggerFactory.getLogger(ExponentialMovingAverage.class);

    public static final String EMA = "EMA";

    @Inject
    private HistoricalPriceVolumnService historicalPriceVolumnService;

    @Inject
    private SimpleMovingAverage simpleMovingAverage;

    /**
     * @param securitySymbol
     * @param startDate
     *            analysis start-date, if null take data starting from being for the time.
     * @param endDate
     *            analysis end-date, mandatory.
     * @return
     */
    @Override
    public IndicatorResultHolder compute(String securitySymbol, Date startDate, Date endDate,
            IndicatorComputationOptions options) {

        int lagDurationPeriod = options.getLagDuration();

        if (BirlaxUtil.diffInDays(startDate, endDate) < lagDurationPeriod) {
            LOGGER.warn("Analysis won't work as lagDuration > # of days for which data was provided.");
            // throw new IllegalArgumentException("Analysis startDate can't be after endDate.");
        }

        IndicatorResultHolder holder = new IndicatorResultHolder();
        Double[] priceVolumnDeliveries = IndicatorUtil.transform(
                getPriceVolumnDeliveryForSeries(historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate),
                holder, options);

        if (priceVolumnDeliveries.length < lagDurationPeriod) {
            LOGGER.warn("Analysis won't work as lagDuration > # of days for which data was provided.");
        }

        return compute(priceVolumnDeliveries, holder, options);
    }

    public IndicatorResultHolder compute(Double[] priceVolumnDeliveries, IndicatorResultHolder holder,
            IndicatorComputationOptions options) {
        for (int lagDuration : options.getDurations()) {

            String smaName = options.getNameForComputationByValues(SimpleMovingAverage.SMA, lagDuration);
            String emaName = options.getNameForComputationByValues(ExponentialMovingAverage.EMA, lagDuration);

            if (priceVolumnDeliveries.length < lagDuration) {
                holder.addResult(smaName, new Double[priceVolumnDeliveries.length]);
                holder.addResult(emaName, new Double[priceVolumnDeliveries.length]);
                continue;
            }

            compute(priceVolumnDeliveries, holder, lagDuration, smaName, emaName);
        }
        return holder;
    }

    /**
     * @param priceVol
     * @param lagDuration
     * @param priceType
     * @return
     */
    public IndicatorResultHolder compute(Double[] priceVol, IndicatorResultHolder holder, int lagDurationPeriod,
            String smaName, String emaName) {

        simpleMovingAverage.compute(priceVol, holder, lagDurationPeriod, smaName);

        Double[] smaData = holder.getResultList(smaName);

        double multiplier = getWeigthMultiplier(lagDurationPeriod);

        Double[] emaData = IndicatorUtil.getListOfDoubleOfSizeWithNulls(priceVol.length);

        int dayOfPriceAction = 0;
        while (dayOfPriceAction < priceVol.length && smaData[dayOfPriceAction] == null) {
            dayOfPriceAction++;
        }
        double previouDayEMA = smaData[dayOfPriceAction];

        // add first EMA which is nothing but just SMA.
        emaData[dayOfPriceAction - 1] = previouDayEMA;

        for (; dayOfPriceAction < priceVol.length; dayOfPriceAction++) {
            double ema = getEMA(priceVol[dayOfPriceAction], previouDayEMA, multiplier);
            previouDayEMA = ema;
            emaData[dayOfPriceAction] = previouDayEMA;
        }
        holder.addResult(emaName, emaData);
        return holder;
    }

    private double getWeigthMultiplier(int lagDuration) {
        return (2.0 / (1.0 * (lagDuration + 1)));
    }

    private double getEMA(double price, double previouDayEMA, double multiplier) {
        return (price - previouDayEMA) * multiplier + previouDayEMA;
    }

}
