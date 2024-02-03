/**
 *
 */
package com.birlax.indiantrader.domain;


public enum IndicatorEventType {

    // ------------- Un-Bounded/MIXED Oscillator Events ---------------//

    NO_CHANGE,
    /**
     * Opening above previous days HIGH
     */
    SINGLE_CANDLE_BIG_GAP_UP_OPENING,
    /**
     * Opening above previous days CLOSE
     */
    SINGLE_CANDLE_GAP_UP_OPENING,

    /**
     * Opening below previous days LOW
     */
    SINGLE_CANDLE_BIG_GAP_DOWN_OPENING,
    /**
     * Opening below previous days OPENING
     */
    SINGLE_CANDLE_GAP_DOWN_OPENING,
    /**
     * Centerline crossovers are the second most common MACD signals.</br>
     * </br>
     * A bullish centerline crossover occurs when the MACD
     * Line moves above the zero line to turn positive. This happens when the 12-day EMA of the underlying security
     * moves above the 26-day EMA.</br>
     * </br>
     * A bearish centerline crossover occurs when the MACD moves below the zero line to turn negative. This
     * happens when the 12-day EMA moves below the 26-day EMA.</br>
     * </br>
     * Centerline crossovers can last a <b>few days or a few months.</b> It all depends on the strength of the
     * trend.</br>
     * The MACD will remain positive as long as there is a sustained uptrend. The MACD will remain negative when there
     * is a
     * sustained downtrend.
     */

    CENTER_LINE_CROSSOVER,

    /**
     * The MACD Line oscillates above and below the zero line, which is also known as the centerline.</br>
     * These crossovers signal that the 12-day EMA has crossed the 26-day EMA. The direction, of course, depends on the
     * direction of the moving average cross. Positive MACD indicates that the 12-day EMA is above the 26-day EMA.
     * </br>
     * </br>
     * Positive values increase as the shorter EMA diverges further from the longer EMA. This means upside momentum
     * is increasing.
     * </br>
     * </br>
     * Negative MACD values indicate that the 12-day EMA is below the 26-day EMA. Negative values
     * increase as the shorter EMA diverges further below the longer EMA. This means downside momentum is
     * increasing.
     * </br>
     * </br>
     * Signal line crossovers are the most common MACD signals. The signal line is a 9-day EMA of the MACD Line.
     * As a moving average of the indicator, it trails the MACD and makes it easier to spot MACD turns.</br>
     * </br>
     * A bullish crossover occurs when the MACD turns up and crosses above the signal line.
     * </br>
     * </br>
     * A bearish crossover occurs when the MACD turns down and crosses below the signal line.
     * </br>
     * </br>
     * Crossovers can last a few days or a few weeks, it all depends on the strength of the move.
     * </br>
     * </br>
     * Due diligence is required before relying on these common signals.<i><b> Signal line crossovers at positive or
     * negative
     * extremes should be viewed with caution.</i></b> Even though the <b>MACD does not have upper and lower limits,</b>
     * chartists can estimate historical extremes with a simple visual assessment. It takes a strong move in the
     * underlying
     * security to push momentum to an extreme. Even though the move may continue, momentum is likely to slow and this
     * will
     * usually produce a signal line crossover at the extremities.</br>
     * </br>
     * Volatility in the underlying security can also increase the number of crossovers.</br>
     * Count of crossover can be used as a measure of Volatility in a stock.
     */

    SIGNAL_LINE_CROSSOVER,

    // ------------- Bounded Oscillator Events ---------------//

    /**
     * Overbought and oversold levels can be used to identify unsustainable price extremes. Extremes in MFI(Bound
     * Oscillators) suggested that these advances/declines were unsustainable and a pullbacks are imminent.
     * </br>
     * </br>
     * <b>Strong trends can present a problem for these classic overbought and oversold levels.</b></br>
     * </br>
     * Overbought/Oversold levels alone are not reason enough to turn bearish/bullish.</br>
     * Some sort of reversal or downturn/upturn is needed to confirm that prices have indeed turned a corner.</br>
     * </br>
     * Look out for gap(up/down) and trend line break(support/resistance) on good volume.
     */
    OVERBOUGHT, OVERSOLD,

    // ------------- Divergence Events ---------------//

    /**
     * Divergences form when the MACD diverges from the price action of the underlying security.</br>
     * </br>
     * A bullish divergence forms when a security records a <b>lower low</b> and the MACD forms a <b>higher
     * low</b>.</br>
     * The lower low in the security affirms the current down trend, but the higher low in the MACD shows less down side
     * momentum. Despite less down side momentum, down side momentum is still outpacing upside momentum as long as the
     * MACD
     * remains in negative territory.</br>
     * </br>
     * WARNING : Slowing down side momentum can sometimes foreshadow a trend reversal or a sizable rally.</br>
     * </br>
     * A bearish divergence forms when a security records a <b>higher high</b> and the MACD Line forms a <b>lower
     * high</b>.
     * The higher high in the security is normal for an up trend, but the lower high in the MACD shows less upside
     * momentum.
     * </br>
     * Even though upside momentum may be less, upside momentum is still outpacing down side momentum as long as the
     * MACD is
     * positive.</br>
     * </br>
     * WARNING : Waning upward momentum can sometimes foreshadow a trend reversal or sizable decline.</br>
     * When stock makes a higher high, but the MACD line fell short of its prior high(din't make new high) and formed a
     * lower high. The subsequent signal line crossover and support break in the MACD were bearish.</br>
     * </br>
     * Divergences should be taken with caution.<b> Bearish divergences are commonplace in a strong uptrend</b>, while
     * <b>Bullish divergences occur often in a strong downtrend</b>.
     * </br>
     * Uptrends often start with a strong advance that produces a surge in upside momentum (MACD). Even though the
     * uptrend continues, it continues at a slower pace that causes the MACD to decline from its highs. Upside momentum
     * may
     * not be as strong, but upside momentum is still outpacing downside momentum as long as the MACD Line is above
     * zero.
     * </br>
     * The opposite occurs at the beginning of a strong downtrend.Divergences should be taken with caution. </br>
     * <b>Bearish divergences are commonplace in a strong uptrend</b>, while bullish divergences occur often in a strong
     * downtrend. Yes, you read that right. Uptrends often start with a strong advance that produces a surge in upside
     * momentum (MACD). Even though the uptrend continues, it continues at a slower
     * pace that causes the MACD to decline from its highs. Upside momentum may not be as strong, but upside momentum is
     * still outpacing downside momentum as long as the MACD Line is above zero. The opposite occurs at the beginning of
     * a
     * strong downtrend.
     */
    DIVERGENCE,

    // ------------- Swing Failure Events ---------------//

    /**
     * A bearish failure swing occurs in a uptrend security, started falling due to Bearish(Sell) pressure but Bulls
     * took control in the middle, tried to take the security up again and failed, Then Bears had full control and took
     * it further down. On Indicator this happens to indicator main line, compare it against the price action line.</br>
     * </br>
     * It's called failure as in progress reversal trend is reversed for sort period, before continue with original
     * reversal.</br>
     * </br>
     * Ex: When MFI becomes overbought above 80, plunges below 80, fails to exceed 80 on a bounce and then breaks below
     * the prior reaction low.</br>
     * </br>
     * Ex: When MFI becomes oversold below 20, rises above 20, fails to decline below 20 on a bounce and then breaks
     * above
     * the prior reaction high.
     */
    SWING_FAILURE
}
