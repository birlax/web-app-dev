/**
 *
 */
package com.birlax.dbCommonUtils;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Mapper
public interface SingleTemporalMapper {


    List<Map<String, Object>> deleteRecords(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("columnNames") Set<String> columnNames);


    List<Map<String, Object>> insertRecords(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("columnNames") Set<String> columnNames);

    List<Map<String, Object>> searchRecords(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("filterByColumns") Set<String> filterByColumns,
            @Param("retrieveColumns") Set<String> retrieveColumns);

    List<Map<String, Object>> getAllRecords(@Param("mainTableName") String mainTableName,
            @Param("retrieveColumns") Set<String> retrieveColumns);


    List<Map<String, Object>> searchRecordsForDateRange(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("filterByColumns") Set<String> filterByColumns,
            @Param("retrieveColumns") Set<String> retrieveColumns,
            @Param("effectiveDateColName") String effectiveDateColName, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
