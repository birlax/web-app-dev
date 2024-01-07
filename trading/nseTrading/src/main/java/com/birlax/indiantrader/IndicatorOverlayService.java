/**
 *
 */
package com.birlax.indiantrader;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.indiantrader.domain.IndicatorComputationOptions;
import com.birlax.indiantrader.domain.IndicatorResultHolder;
import com.birlax.indiantrader.domain.PriceVolumnDelivery;
import com.birlax.indiantrader.service.HistoricalPriceVolumnService;

/**
 * @author birlax
 */
public interface IndicatorOverlayService {

    Logger LOGGER = LoggerFactory.getLogger(IndicatorOverlayService.class);

    /**
     * @param historicalPriceVolumnService
     * @param securitySymbol
     * @param series
     * @param startDate
     * @param endDate
     * @return
     */
    public default List<PriceVolumnDelivery> getPriceVolumnDeliveryForSeries(
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

    /**
     * @param securitySymbol
     * @param startDate
     * @param endDate
     * @param options
     * @return
     */
    public IndicatorResultHolder compute(String securitySymbol, LocalDate startDate, LocalDate endDate,
            IndicatorComputationOptions options);

}
