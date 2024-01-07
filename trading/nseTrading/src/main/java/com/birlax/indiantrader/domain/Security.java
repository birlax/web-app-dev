package com.birlax.indiantrader.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class Security implements SingleTemporalDAO {

    private int spn;

    private String isin;

    private String shortName;

    private String symbol;

    /*
     * private Date firstListingDate;
     * private Double faceValue;
     * private String listedOnExchange;
     * private String nseSecurityId;
     * private String bseSecurityId;
     * private String industry;
     * private Integer marketLot;
     */

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getDAOKey()
     */
    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("isin"));
        return new ArrayList<>(keys);
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getFullyQualifiedTableName()
     */
    @Override
    public String getFullyQualifiedTableName() {
        return "sec_master.securities";
    }

    /**
     * @return the isin
     */
    public String getIsin() {
        return this.isin;
    }

    /**
     * @param isin
     *            the isin to set
     */
    public void setIsin(String isin) {
        this.isin = isin;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * @param shortName
     *            the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * @param symbol
     *            the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
        return "Security [spn=" + this.spn + ", isin=" + this.isin + ", shortName=" + this.shortName + ", symbol="
                + this.symbol + "]";
    }
}
