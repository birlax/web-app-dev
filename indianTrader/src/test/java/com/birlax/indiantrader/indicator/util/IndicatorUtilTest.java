/**
 *
 */
package com.birlax.indiantrader.indicator.util;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author birlax
 */
public class IndicatorUtilTest {

    @Test
    public void testGetDirectionByLookBack() {
        int lookBack = 5;
        double threshold = .5;
        int climbingDaysVSDecliningDaysThreshold = 3;
        Double[] data = new Double[] { 1.0, 2.0, 3.0, -4.0, -6.0, -12.0, -15.0, -124.0, -2.0, 2.0, 5.0, 14.0, 2.0, 4.0,
                6.0, 12.0, 15.0, 124.0, 2.0, 4.0, 6.0, 12.0, 15.0, 124.0, 2.0, };
        System.out.println(Arrays.toString(data));

        System.out.println(Arrays.toString(
                IndicatorUtil.getDirectionByLookBack(data, lookBack, threshold, climbingDaysVSDecliningDaysThreshold)));

        System.out.println(Arrays.toString(IndicatorUtil.getLongRunningUpDownDays(data)));
    }

}
