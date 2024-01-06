/**
 *
 */
package com.birlax.indiantrader.oscillator;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.birlax.indiantrader.IndicatorOverlayService;
import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.indicator.util.IndicatorUtil;
import com.birlax.indiantrader.indicator.util.IndicatorUtil.PriceType;
import com.birlax.indiantrader.overlay.SimpleMovingAverage;

/**
 * The Money Flow Index (MFI) is an oscillator that uses both price and volume to measure buying and selling pressure.
 * Created by Gene Quong and Avrum Soudack, <b>MFI is also known as volume-weighted RSI</b>.</br>
 * </br>
 * MFI starts with the typical price (H+L+C/3) for each period. Money flow is positive when the typical price rises
 * (buying pressure) and negative when the typical price declines (selling pressure).</br>
 * </br>
 * A ratio of positive and negative money flow is then plugged into an RSI formula to create an oscillator that moves
 * between zero and one hundred. As a momentum oscillator tied to volume.</br>
 * </br>
 * <b>Money Flow Index (MFI) is best suited to identify reversals and price extremes with a variety of signals</b>.
 * </br>
 * </br>
 * As a volume-weighted version of RSI, the Money Flow Index (MFI) can be interpreted similarly to RSI. The big
 * difference is, of course, volume. Because volume is added to the mix, the Money Flow Index will act a little
 * differently than RSI. Theories suggest that volume leads prices. RSI is a momentum oscillator that already leads
 * prices. Incorporating volume can increase this lead time.
 */
@Named
public class MoneyFlowIndex implements IndicatorOverlayService {

    public static final String MFI = "MFI";

    @Inject
    private SimpleMovingAverage simpleMovingAverage;

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

    public IndicatorResultHolder compute(List<PriceVolumnDelivery> priceVolumnDeliveries, Double[] rawMoney,
            IndicatorResultHolder holder, IndicatorComputationOptions options) {
        for (int lagDuration : options.getDurations()) {

            String name = options.getNameForComputationByValues(MoneyFlowIndex.MFI, options.getFastLeg());
            if (rawMoney.length < lagDuration) {
                holder.addResult(name, new Double[rawMoney.length]);
                continue;
            }

            compute(priceVolumnDeliveries, rawMoney, holder, lagDuration, name);

        }
        return holder;
    }

    /**
     * @param rawMoneyValues
     * @param holder
     * @param options
     * @return
     */
    public IndicatorResultHolder compute(List<PriceVolumnDelivery> priceVolumnDeliveries, Double[] rawMoneyValues,
            IndicatorResultHolder holder, int lagDurationPeriod, String name) {

        if (rawMoneyValues.length < lagDurationPeriod) {
            holder.addResult(name, new Double[rawMoneyValues.length]);
            return holder;
        }

        IndicatorComputationOptions optionPrice = new IndicatorComputationOptions(PriceType.TYPICAL_PRICE.toString(),
                lagDurationPeriod);

        Double[] typicalPrice = IndicatorUtil.transform(priceVolumnDeliveries, holder, optionPrice);

        Double[] changes = IndicatorUtil.getDiffWithPreviousIndex(typicalPrice);

        Double[] positiveGains = IndicatorUtil.getListOfDoubleOfSizeWithNulls(changes.length);
        Double[] negativeGains = IndicatorUtil.getListOfDoubleOfSizeWithNulls(changes.length);

        for (int i = 0; i < changes.length; i++) {
            if (changes[i] == null) {
                continue;
            }
            if (changes[i] > 0) {
                positiveGains[i] = rawMoneyValues[i];
                negativeGains[i] = 0.0;
            }
            if (changes[i] < 0) {
                negativeGains[i] = rawMoneyValues[i];
                positiveGains[i] = 0.0;
            }
            if (changes[i] == 0.0) {
                positiveGains[i] = 0.0;
                negativeGains[i] = 0.0;
            }
        }
        String tp = name + "POSITIVE_RAW_MONEY";
        Double[] positiveRawMoneySMA = simpleMovingAverage.compute(positiveGains, holder, lagDurationPeriod, tp)
                .getResultList(tp);

        String tn = name + "NEGATIVE_RAW_MONEY";
        Double[] negativeRawMoneySMA = simpleMovingAverage.compute(negativeGains, holder, lagDurationPeriod, tn)
                .getResultList(tn);

        Double[] mfi = IndicatorUtil.getListOfDoubleOfSizeWithNulls(rawMoneyValues.length);

        for (int i = 0; i < changes.length; i++) {
            if (positiveRawMoneySMA[i] == null || negativeRawMoneySMA[i] == null) {
                continue;
            }
            mfi[i] = IndicatorUtil.getRelativeStrengthIndex(positiveRawMoneySMA[i], negativeRawMoneySMA[i]);
        }
        holder.addResult(name, mfi);
        return holder;
    }

}
