package com.birlax.dbCommonUtils.industryClassification;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import lombok.Builder;
import lombok.Data;

import java.util.*;

@Builder
@Data
public class IndustrySector implements SingleTemporalDAO {

  public int industryId;

  public int sectorId;

  public int urlId;

  public int spn;

  public String sectorNameMajor;

  public String sectorNameMinor;

  public String subSectorName;

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
    // return "public.sector";
    return "trade.nse_historical_price_data";
  }
}
