/**
 *
 */
package com.birlax.indiantrader.candle;

import java.util.Date;
import java.util.List;

import javax.inject.Named;

import com.birlax.indiantrader.IndicatorOverlayService;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.indicator.util.IndicatorUtil.PriceType;

/**
 * @author birlax
 */
@Named
public class OpeningAbovePreviousHigh implements IndicatorOverlayService {

    public static final String OPENING_ABOVE_PREVIOUS_HIGH = "OPENING_ABOVE_PREVIOUS_HIGH";

    /*
     * (non-Javadoc)
     * @see com.birlax.indiantrader.IndicatorOverlayService#compute(java.lang.String, java.util.Date, java.util.Date,
     * com.birlax.indiantrader.domain.IndicatorComputationOptions)
     */
    @Override
    public IndicatorResultHolder compute(String securitySymbol, Date startDate, Date endDate,
            IndicatorComputationOptions options) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param priceVol
     * @param lagDuration
     * @param priceType
     * @return
     */
    public IndicatorResultHolder compute(Double[] closePrice, List<PriceVolumnDelivery> priceVolumnDeliveries,
            IndicatorResultHolder holder, int lagDurationPeriod, double percentage, String resultName) {

        if (closePrice.length < lagDurationPeriod) {
            holder.addResult(resultName, new Double[closePrice.length]);
            return holder;
        }

        Double[] highPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder,
                new IndicatorComputationOptions(PriceType.HIGH.toString(), lagDurationPeriod));

        Double[] openPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder,
                new IndicatorComputationOptions(PriceType.OPENING.toString(), lagDurationPeriod));
        int i = 1;
        Double[] openingAbovePreviousHigh = IndicatorUtil.getListOfDoubleOfSizeWithNulls(closePrice.length);
        openingAbovePreviousHigh[i - 1] = 0.0;

        for (; i < closePrice.length; i++) {
            openingAbovePreviousHigh[i] = 0.0;
            if (openPrice[i] >= closePrice[i - 1] && highPrice[i] > highPrice[i - 1]) {

                // if (closePrice[i] >= openPrice[i] * (100.0 + percentage) / 100.0) {
                openingAbovePreviousHigh[i] = 1.0;
                // }
                // if (highPrice[i] >= openPrice[i] * (100.0 + percentage) / 100.0) {
                // openingAbovePreviousHigh[i] = 1.0;
                // }
            }
        }
        holder.addResult(resultName, openingAbovePreviousHigh);
        return holder;
    }

}
