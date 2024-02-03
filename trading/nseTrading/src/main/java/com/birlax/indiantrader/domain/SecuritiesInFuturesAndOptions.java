/**
 *
 */
package com.birlax.indiantrader.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.util.ReflectionHelper;


public class SecuritiesInFuturesAndOptions implements SingleTemporalDAO {

    private String underlyingSymbol;

    private String underlyingName;

    private String assetType;

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getDAOKey()
     */
    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("underlyingSymbol"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("underlyingName"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("assetType"));
        return new ArrayList<>(keys);
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getFullyQualifiedTableName()
     */
    @Override
    public String getFullyQualifiedTableName() {
        return "trade.nse_futures_and_options_list";
    }

    /**
     * @return the underlyingSymbol
     */
    public String getUnderlyingSymbol() {
        return this.underlyingSymbol;
    }

    /**
     * @param underlyingSymbol
     *            the underlyingSymbol to set
     */
    public void setUnderlyingSymbol(String underlyingSymbol) {
        this.underlyingSymbol = underlyingSymbol;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SecuritiesInFuturesAndOptions [underlyingSymbol=" + this.underlyingSymbol + "]";
    }

    /**
     * @return the underlyingName
     */
    public String getUnderlyingName() {
        return this.underlyingName;
    }

    /**
     * @param underlyingName
     *            the underlyingName to set
     */
    public void setUnderlyingName(String underlyingName) {
        this.underlyingName = underlyingName;
    }

    /**
     * @return the assetType
     */
    public String getAssetType() {
        return this.assetType;
    }

    /**
     * @param assetType
     *            the assetType to set
     */
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

}
