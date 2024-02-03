/**
 *
 */
package com.birlax.indiantrader.domain;

import com.birlax.indiantrader.indicator.util.IndicatorUtil.PriceType;


public class IndicatorComputationOptions {

    private Integer fastLeg;

    private Integer slowLeg;

    private Integer diffLeg;

    private String priceType;

    private int[] durations;

    private String randomSaltForName;

    public String getNameForComputation(String indicatorName) {
        return getNameForComputationByValues(indicatorName, getFastLeg());
    }

    public String getNameForComputationSlow(String indicatorName) {
        return getNameForComputationByValues(indicatorName, getSlowLeg());
    }

    public String getNameForComputationByValues(String indicatorName, int value) {
        return indicatorName + "(" + priceType + "|" + value
                + (randomSaltForName == null ? ")" : "_" + randomSaltForName + "|)");
    }

    public IndicatorComputationOptions(String priceType, int... a) {
        super();
        this.priceType = priceType;
        if (a.length <= 0) {
            throw new IllegalArgumentException("Lag details in options can't be null.");
        }
        if (a.length >= 1) {
            fastLeg = a[0];
        }
        if (a.length >= 2) {
            slowLeg = a[1];
        }
        if (a.length >= 3) {
            diffLeg = a[2];
        }
        this.durations = a;

        if (slowLeg != null && fastLeg != null && fastLeg >= slowLeg) {
            throw new IllegalArgumentException("Fast Leg can't be Equal or Greater than Slower Leg.");
        }
        if (fastLeg <= 1) {
            throw new IllegalArgumentException("Analysis lagDuration has to be more than 1.");
        }

    }

    public static IndicatorComputationOptions copy(IndicatorComputationOptions that) {
        IndicatorComputationOptions in = new IndicatorComputationOptions(that.priceType, that.durations);
        in.priceType = that.priceType;
        return in;
    }

    /**
     * @return the lagDuration
     */
    public int getLagDuration() {
        return fastLeg;
    }

    /**
     * @return the fastLeg
     */
    public Integer getFastLeg() {
        return this.fastLeg;
    }

    /**
     * @param fastLeg
     *            the fastLeg to set
     */
    public void setFastLeg(Integer fastLeg) {
        this.fastLeg = fastLeg;
    }

    /**
     * @return the slowLeg
     */
    public Integer getSlowLeg() {
        return this.slowLeg;
    }

    /**
     * @param slowLeg
     *            the slowLeg to set
     */
    public void setSlowLeg(Integer slowLeg) {
        this.slowLeg = slowLeg;
    }

    /**
     * @return the randomSaltForName
     */
    public String getRandomSaltForName() {
        return this.randomSaltForName;
    }

    /**
     * @param randomSaltForName
     *            the randomSaltForName to set
     */
    public void setRandomSaltForName(String randomSaltForName) {
        this.randomSaltForName = randomSaltForName;
    }

    /**
     * @return the diffLeg
     */
    public Integer getDiffLeg() {
        return this.diffLeg;
    }

    /**
     * @param diffLeg
     *            the diffLeg to set
     */
    public void setDiffLeg(Integer diffLeg) {
        this.diffLeg = diffLeg;
    }

    /**
     * @return the durations
     */
    public int[] getDurations() {
        return this.durations;
    }

    /**
     * @param durations
     *            the durations to set
     */
    public void setDurations(int[] durations) {
        this.durations = durations;
    }

    /**
     * @return the priceType
     */
    public String getPriceType() {
        return this.priceType;
    }

    /**
     * @param priceType
     *            the priceType to set
     */
    public void setPriceType(PriceType priceType) {
        this.priceType = priceType.toString();
    }

}
