package com.birlax.dbCommonUtils.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StagingUtilMapper {

    String createTempStageTable(@Param("columnNames") Set<String> columnNames,
            @Param("fullyQualifiedParentTableName") String fullyQualifiedParentTableName,
            @Param("tempTableName") String tempTableName);

    void dummyStageRecords(@Param("tempTableName") String tempTableName, @Param("columnNames") Set<String> columnNames,
            @Param("jsonbColumnNames") Set<String> jsonbColumnNames, @Param("record") Map<String, Object> record);

    void dummyStageRecordsInBulk(@Param("tempTableName") String tempTableName,
            @Param("columnNames") Set<String> columnNames, @Param("jsonbColumnNames") Set<String> jsonbColumnNames,
            @Param("records") List<Map<String, Object>> records);

    void dummyStageRecordsInBulkForMultipleBatches(@Param("tempTableName") String tempTableName,
            @Param("columnNames") Set<String> columnNames, @Param("jsonbColumnNames") Set<String> jsonbColumnNames,
            @Param("batchesOfRecords") List<List<Map<String, Object>>> batchesOfRecords);

    List<Map<String, Object>> getData(@Param("tempTableName") String tempTableName,
            @Param("columnNames") Set<String> columnNames);

    void dropTempTable(@Param("tempTableName") String tempTableName);

    void dummyStageRecordsUsingSqls(@Param("sqls") List<String> sqls, @Param("tempTableName") String tempTableName);

    List<Map<String, Object>> getColumnsDataType(@Param("schema") String schema, @Param("tableName") String tableName);
}