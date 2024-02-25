/**
 *
 */
package com.birlax.indiantrader;

import java.time.LocalDate;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;


public interface IndicatorOverlayService {

    default List<PriceVolumnDelivery> getPriceVolumnDeliveryForSeries(
            HistoricalPriceVolumnService historicalPriceVolumnService, String securitySymbol, String series,
            LocalDate startDate, LocalDate endDate) {

        if (series == null || series.isEmpty()) {
            throw new IllegalArgumentException("Analysis without/across series not possbile : " + series);
        }
        List<PriceVolumnDelivery> priceVolumnDeliveries = historicalPriceVolumnService
                .getPriceVolumnForSecurity(securitySymbol, series, startDate, endDate);
        // Sort in least recent to most recent data, by trade-date
        priceVolumnDeliveries.sort((a, b) -> {
            return a.getTradeDate().compareTo(b.getTradeDate());
        });
        return priceVolumnDeliveries;
    }


     IndicatorResultHolder compute(String securitySymbol, LocalDate startDate, LocalDate endDate,
                                         IndicatorComputationOptions options);

}
