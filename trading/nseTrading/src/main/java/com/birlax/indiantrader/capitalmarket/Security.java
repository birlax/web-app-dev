package com.birlax.indiantrader.capitalmarket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@JsonAutoDetect
@Data
@Builder
@AllArgsConstructor
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


    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("isin"));
        return new ArrayList<>(keys);
    }


    @Override
    public String getFullyQualifiedTableName() {
        return "sec_master.securities";
    }

}
