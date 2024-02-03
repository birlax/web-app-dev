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

    /**
     * @param tempTableName
     * @param mainTableName
     * @param columnNames
     * @return
     */
    List<Map<String, Object>> deleteRecords(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("columnNames") Set<String> columnNames);

    /**
     * @param tempTableName
     * @param mainTableName
     * @param columnNames
     * @return
     */
    List<Map<String, Object>> insertRecords(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("columnNames") Set<String> columnNames);

    /**
     * @param tempTableName
     * @param mainTableName
     * @param filterByColumns
     * @return
     */
    List<Map<String, Object>> searchRecords(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("filterByColumns") Set<String> filterByColumns,
            @Param("retrieveColumns") Set<String> retrieveColumns);

    /**
     * @param mainTableName
     * @param retrieveColumns
     * @return
     */
    List<Map<String, Object>> getAllRecords(@Param("mainTableName") String mainTableName,
            @Param("retrieveColumns") Set<String> retrieveColumns);

    /**
     * @param tempTableName
     * @param mainTableName
     * @param effectiveDateColName
     * @param startDate
     * @param endDate
     * @return
     */
    List<Map<String, Object>> searchRecordsForDateRange(@Param("tempTableName") String tempTableName,
            @Param("mainTableName") String mainTableName, @Param("filterByColumns") Set<String> filterByColumns,
            @Param("retrieveColumns") Set<String> retrieveColumns,
            @Param("effectiveDateColName") String effectiveDateColName, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
