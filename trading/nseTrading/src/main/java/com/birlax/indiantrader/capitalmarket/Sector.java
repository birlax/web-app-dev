
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
import lombok.NoArgsConstructor;


@JsonAutoDetect
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sector implements SingleTemporalDAO {

    private int industryId;

    private int sectorId;

    private String urlId;

    private String sectorNameMajor;

    private String sectorNameMinor;

    private String subSectorName;

    @Override
    public List<String> getDAOKey() {
        Set<String> keys = new HashSet<>();
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("sectorNameMajor"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("sectorNameMinor"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("subSectorName"));
        keys.add(ReflectionHelper.getLowerCaseSnakeCase("industryId"));
        return new ArrayList<>(keys);
    }


    @Override
    public String getFullyQualifiedTableName() {
        return "sec_master.sector";
    }

}
