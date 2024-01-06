package com.birlax.indiantrader.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class PriceVolumnDelivery implements SingleTemporalDAO {

    private int spn;

    private String series;

    private Date tradeDate;

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

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Double getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(Double previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    public Double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Double getTotalTradedQuantity() {
        return totalTradedQuantity;
    }

    public void setTotalTradedQuantity(Double totalTradedQuantity) {
        this.totalTradedQuantity = totalTradedQuantity;
    }

    public Double getTurnover() {
        return turnover;
    }

    public void setTurnover(Double turnover) {
        this.turnover = turnover;
    }

    public Double getNoOfTrades() {
        return noOfTrades;
    }

    public void setNoOfTrades(Double noOfTrades) {
        this.noOfTrades = noOfTrades;
    }

    public Double getDeliverableQuantity() {
        return deliverableQuantity;
    }

    public void setDeliverableQuantity(Double deliverableQuantity) {
        this.deliverableQuantity = deliverableQuantity;
    }

    public Double getPctDeliverableQtyToTradeQty() {
        return pctDeliverableQtyToTradeQty;
    }

    public void setPctDeliverableQtyToTradeQty(Double pctDeliverableQtyToTradeQty) {
        this.pctDeliverableQtyToTradeQty = pctDeliverableQtyToTradeQty;
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getDAOKey()
     */
    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("spn"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("series"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("tradeDate"));
        return new ArrayList<>(keys);
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getFullyQualifiedTableName()
     */
    @Override
    public String getFullyQualifiedTableName() {
        return "trade.nse_historical_price_data";
    }

    /**
     * @return the spn
     */
    public int getSpn() {
        return this.spn;
    }

    /**
     * @param spn
     *            the spn to set
     */
    public void setSpn(int spn) {
        this.spn = spn;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PriceVolumnDelivery [spn=" + this.spn + ", series=" + this.series + ", tradeDate=" + this.tradeDate
                + ", previousClosePrice=" + this.previousClosePrice + ", openPrice=" + this.openPrice + ", highPrice="
                + this.highPrice + ", lowPrice=" + this.lowPrice + ", lastPrice=" + this.lastPrice + ", closePrice="
                + this.closePrice + ", averagePrice=" + this.averagePrice + ", totalTradedQuantity="
                + this.totalTradedQuantity + ", turnover=" + this.turnover + ", noOfTrades=" + this.noOfTrades
                + ", deliverableQuantity=" + this.deliverableQuantity + ", pctDeliverableQtyToTradeQty="
                + this.pctDeliverableQtyToTradeQty + "] \n";
    }

}
