/**
 *
 */
package com.birlax.indiantrader.overlay;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.util.Date;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.IndicatorOverlayService;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SimpleMovingAverage implements IndicatorOverlayService {


    public static final String SMA = "SMA";

    private HistoricalPriceVolumnService historicalPriceVolumnService;

    /**
     * @param securitySymbol
     * @param startDate
     *            analysis start-date, if null take data starting from being for the time.
     * @param endDate
     *            analysis end-date, mandatory.
     * @return
     */
    @Override
    public IndicatorResultHolder compute(String securitySymbol, LocalDate startDate, LocalDate endDate,
            IndicatorComputationOptions options) {

        int lagDurationPeriod = options.getLagDuration();

        if (BirlaxUtil.diffInDays(startDate, endDate) < lagDurationPeriod) {
            log.warn("Analysis won't work as lagDuration > # of days for which data was provided.");
            // throw new IllegalArgumentException("Analysis startDate can't be after endDate.");
        }

        IndicatorResultHolder holder = new IndicatorResultHolder();
        Double[] priceVolumnDeliveries = IndicatorUtil.transform(
                getPriceVolumnDeliveryForSeries(historicalPriceVolumnService, securitySymbol, "EQ", startDate, endDate),
                holder, options);

        return compute(priceVolumnDeliveries, holder, options);

    }

    public IndicatorResultHolder compute(Double[] inputData, IndicatorResultHolder holder,
            IndicatorComputationOptions options) {

        for (int lagDuration : options.getDurations()) {
            String name = options.getNameForComputationByValues(SimpleMovingAverage.SMA, lagDuration);

            if (inputData.length < lagDuration) {
                holder.addResult(name, new Double[inputData.length]);
                return holder;
            }

            compute(inputData, holder, lagDuration, name);
        }
        return holder;
    }

    /**
     * @return
     */
    public IndicatorResultHolder compute(Double[] inputData, IndicatorResultHolder holder, int lagDurationPeriod,
            String name) {

        double sum = 0.0;

        Double[] priceAverages = IndicatorUtil.getListOfDoubleOfSizeWithNulls(inputData.length);

        int days = 0;
        // ignore the additional data points that are in the beginning.
        int dayOfPriceAction = 0;// priceVol.length % lagDurationPeriod;
        boolean averagingStarted = false;
        double avg = 0.0;
        boolean seenValue = false;
        for (; dayOfPriceAction < inputData.length; dayOfPriceAction++) {
            // no need to process data still not seen first value
            if (!seenValue && inputData[dayOfPriceAction] == null) {
                continue;
            }
            seenValue = true;
            if (inputData[dayOfPriceAction] == null) {
                // TODO Fix this
                throw new IllegalArgumentException("Missing value in the middle : " + dayOfPriceAction + " For : ");
            }

            if (averagingStarted) {
                double tempSum = avg * lagDurationPeriod - inputData[dayOfPriceAction - lagDurationPeriod];

                tempSum += inputData[dayOfPriceAction];
                avg = tempSum / lagDurationPeriod;
                // priceAverages.put(priceVol.get(dayOfPriceAction).getDate(), avg);
                priceAverages[dayOfPriceAction] = avg;
                // log.info("For:" + priceVol.get(dayOfPriceAction).getDate() + ",Avg:," + avg);
                continue;
            }
            sum += inputData[dayOfPriceAction];
            days++;
            if (days % lagDurationPeriod == 0) {
                avg = sum / lagDurationPeriod;
                // priceAverages.put(priceVol.get(dayOfPriceAction).getDate(), avg);
                priceAverages[dayOfPriceAction] = avg;
                sum = 0;
                days = 0;
                averagingStarted = true;
                // log.info("For:" + priceVol.get(dayOfPriceAction).getDate() + ",Avg:," + avg);
            }
        }
        // holder.addResult(SMA, priceAverages);
        holder.addResult(name, priceAverages);
        return holder;
    }

}
