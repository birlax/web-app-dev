
package com.birlax.indiantrader;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

@UtilityClass
public class RangeGeneratorUtil {

    public static List<Pair<Double, Double>> getRanges(double lower, double upper, double incrementsOf) {

        List<Pair<Double, Double>> ranges = new ArrayList<>();

        ranges.add(Pair.of(0.0, lower));

        ranges.add(Pair.of(lower, incrementsOf));

        int a = 1;
        for (double t = incrementsOf; t < upper; t = t + incrementsOf) {
            // String key = ("PC_" + (++a)) + "/" + t + "_to_" + (t + incrementsOf);
            ranges.add(Pair.of(t, t + incrementsOf));
        }
        ranges.add(Pair.of(upper, Double.MAX_VALUE));
        return ranges;
    }

}
