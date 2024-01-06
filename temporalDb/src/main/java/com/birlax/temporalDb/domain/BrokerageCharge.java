package com.birlax.temporalDb.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class BrokerageCharge {

    private int    id;
    private String ruleName;
    private String brokerCode;
    private String productType;
    private String tradeType;
    private String commets;
    private int    ruleId;

    public int getId() {
        return id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getProductType() {
        return productType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public void setBrokerCode(String brokerCode) {
        this.brokerCode = brokerCode;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getRuleId() {
        return ruleId;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    @Override
    public String toString() {
        return "BrokerageCharge [id=" + id + ", ruleName=" + ruleName + ", brokerCode=" + brokerCode + ", productType="
                + productType + ", tradeType=" + tradeType + ", ruleId=" + ruleId + "]";
    }

    public String getCommets() {
        return commets;
    }

    public void setCommets(String commets) {
        this.commets = commets;
    }

}
