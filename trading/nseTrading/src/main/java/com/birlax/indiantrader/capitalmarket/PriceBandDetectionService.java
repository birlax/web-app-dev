
package com.birlax.indiantrader.capitalmarket;

import com.birlax.indiantrader.RangeGeneratorUtil;
import com.birlax.indiantrader.capitalmarket.HistoricalPriceVolumnService;
import com.birlax.indiantrader.capitalmarket.PriceVolumnDelivery;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;


@Service
public class PriceBandDetectionService {

    private HistoricalPriceVolumnService historicalPriceVolumnService;

    /**
     * @param securitySymbol
     * @param startDate
     *            analysis start-date, if null take data starting from being for the time.
     * @param endDate
     *            analysis end-date, mandatory.
     * @return
     */
    public List<PriceVolumnDelivery> detectPriceBand(String securitySymbol, LocalDate startDate, LocalDate endDate) {

        //
        if (endDate == null) {
            throw new IllegalArgumentException("Analysis endDate can't be null.");
        }
        if (startDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Analysis startDate can't be after endDate.");
        }

        List<PriceVolumnDelivery> priceVol = historicalPriceVolumnService.getPriceVolumnForSecurity(securitySymbol,
                "EQ", startDate, endDate);

        // Sort in most recent to least recent data, by trade-date
        priceVol.sort((a, b) -> {
            return a.getTradeDate().compareTo(b.getTradeDate());
        });

        // System.out.println(priceVol);
        if (priceVol.size() < 1) {
            throw new IllegalArgumentException("Analysis Date-Ragen period is less than 30.");
        }
        List<Pair<Double, Double>> ranges = RangeGeneratorUtil.getRanges(0, 8000, 100);

        Map<Integer, Integer> rangeCountMap = new HashMap<>();
        for (PriceVolumnDelivery pc : priceVol) {
            for (int rangeIndex = 0; rangeIndex < ranges.size(); rangeIndex++) {
                if (pc.getClosePrice() >= ranges.get(rangeIndex).getLeft()
                        && pc.getClosePrice() < ranges.get(rangeIndex).getRight()) {
                    rangeCountMap.putIfAbsent(rangeIndex, 0);
                    rangeCountMap.put(rangeIndex, rangeCountMap.get(rangeIndex) + 1);
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry : rangeCountMap.entrySet()) {
            System.out.println("In Range : " + ranges.get(entry.getKey()) + " for days : " + entry.getValue());
        }

        Collections.reverse(priceVol);
        for (int i = 0; i < priceVol.size() - 4; i++) {
            double pctChange = getPcChange(priceVol.get(i), priceVol.get(i + 1));
            double pctChange2 = getPcChange(priceVol.get(i), priceVol.get(i + 2));
            double pctChange3 = getPcChange(priceVol.get(i), priceVol.get(i + 3));
            double pctChange4 = getPcChange(priceVol.get(i), priceVol.get(i + 4));

            System.out.println(priceVol.get(i).getTradeDate() + " : " + pctChange + " : " + pctChange2 + " : "
                    + pctChange3 + " : " + pctChange4);
        }
        return null;
    }

    private double getPcChange(PriceVolumnDelivery a, PriceVolumnDelivery b) {
        double pctChange2 = 100.0 * (a.getClosePrice() - b.getClosePrice()) / b.getClosePrice();
        return pctChange2;
    }
}
