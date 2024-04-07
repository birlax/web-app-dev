
package com.birlax.indiantrader.fno;

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
public class SecuritiesInFuturesAndOptions implements SingleTemporalDAO {

    private String underlyingSymbol;

    private String underlyingName;

    private String assetType;


    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("underlyingSymbol"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("underlyingName"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("assetType"));
        return new ArrayList<>(keys);
    }


    @Override
    public String getFullyQualifiedTableName() {
        return "trade.nse_futures_and_options_list";
    }


}
