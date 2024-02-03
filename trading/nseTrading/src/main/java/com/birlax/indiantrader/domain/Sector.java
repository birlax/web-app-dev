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
import com.fasterxml.jackson.annotation.JsonAutoDetect;


@JsonAutoDetect
public class Sector implements SingleTemporalDAO {

    private int industryId;

    private int sectorId;

    private int urlId;

    private String sectorNameMajor;

    private String sectorNameMinor;

    private String subSectorName;

    /**
     * @return the industryId
     */
    public int getIndustryId() {
        return this.industryId;
    }

    /**
     * @param industryId
     *            the industryId to set
     */
    public void setIndustryId(int industryId) {
        this.industryId = industryId;
    }

    /**
     * @return the sectorId
     */
    public int getSectorId() {
        return this.sectorId;
    }

    /**
     * @param sectorId
     *            the sectorId to set
     */
    public void setSectorId(int sectorId) {
        this.sectorId = sectorId;
    }

    /**
     * @return the urlId
     */
    public int getUrlId() {
        return this.urlId;
    }

    /**
     * @param urlId
     *            the urlId to set
     */
    public void setUrlId(int urlId) {
        this.urlId = urlId;
    }

    /**
     * @return the sectorNameMajor
     */
    public String getSectorNameMajor() {
        return this.sectorNameMajor;
    }

    /**
     * @param sectorNameMajor
     *            the sectorNameMajor to set
     */
    public void setSectorNameMajor(String sectorNameMajor) {
        this.sectorNameMajor = sectorNameMajor;
    }

    /**
     * @return the sectorNameMinor
     */
    public String getSectorNameMinor() {
        return this.sectorNameMinor;
    }

    /**
     * @param sectorNameMinor
     *            the sectorNameMinor to set
     */
    public void setSectorNameMinor(String sectorNameMinor) {
        this.sectorNameMinor = sectorNameMinor;
    }

    /**
     * @return the subSectorName
     */
    public String getSubSectorName() {
        return this.subSectorName;
    }

    /**
     * @param subSectorName
     *            the subSectorName to set
     */
    public void setSubSectorName(String subSectorName) {
        this.subSectorName = subSectorName;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Sector [industryId=" + this.industryId + ", sectorId=" + this.sectorId + ", urlId=" + this.urlId
                + ", sectorNameMajor=" + this.sectorNameMajor + ", sectorNameMinor=" + this.sectorNameMinor
                + ", subSectorName=" + this.subSectorName + "]";
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getDAOKey()
     */
    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("sectorNameMajor"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("sectorNameMinor"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("subSectorName"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("industryId"));
        return new ArrayList<>(keys);
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.SingleTemporalDAO#getFullyQualifiedTableName()
     */
    @Override
    public String getFullyQualifiedTableName() {
        return "reference.sector";
    }

}
