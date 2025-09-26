package com.birlax.indiantrader.capitalmarket;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@JsonAutoDetect
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceVolumnDelivery implements SingleTemporalDAO {

    private String spn;

    private String series;

    private LocalDateTime tradeDate;

    private Double previousClosePrice;

    private Double openPrice;

    private Double highPrice;

    private Double lowPrice;

    private Double lastPrice;

    private Double closePrice;

    private Double averagePrice;

    private Double totalTradedQuantity;

    private Double turnover;

    private Double noOfTrades;

    private Double deliverableQuantity;

    private Double pctDeliverableQtyToTradeQty;

    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("spn"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("series"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("tradeDate"));
        return new ArrayList<>(keys);
    }


    @Override
    public String getFullyQualifiedTableName() {
        return "trade.nse_historical_price_data";
    }


}
