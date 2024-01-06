package com.birlax.dbCommonUtils.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.dbCommonUtils.mapper.StagingUtilMapper;

/**
 * @author birlax
 */
@Named("databaseMetadataService")
public class DatabaseMetadataService {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseMetadataService.class);

    // Numeric datatypes are used to determined which columns are numbers and which are not, so non-numeric fields can
    // be surrounded with double quotes
    private Set<String> numericDataTypes = Collections.emptySet();

    // Caching table to columns to avoid reading the same columns for the table. Assumption is any change to schema
    // will also entail a restart
    private Map<String, Map<String, String>> tableToColumnsDataTypeMap = new ConcurrentHashMap<>();

    @Inject
    private StagingUtilMapper stagingUtilMapper;

    @PostConstruct
    public void init() {

        // Add known numeric datatypes as defined in postgres 9.6
        String[] numericDataTypesArray = { "smallint", "integer", "bigint", "decimal", "numeric", "real",
                "double precision", "smallserial", "serial", "bigserial", "money" };
        numericDataTypes = new HashSet<>(Arrays.asList(numericDataTypesArray));

        /*
         * String numericDataTypesConfigured = // read from some config; if (numericDataTypesConfigured != null &&
         * !numericDataTypesConfigured.trim().isEmpty()) { List<String> dataTypesToAdd =
         * StringUtil.split(numericDataTypesConfigured, ','); if (dataTypesToAdd != null && !dataTypesToAdd.isEmpty()) {
         * numericDataTypes.addAll(dataTypesToAdd); } }
         */
    }

    /**
     * @param targetTableName
     * @param columnNames
     * @return
     */
    public List<Boolean> determineStringTypeColumns(String targetTableName, Set<String> columnNames,
            boolean failIfColumnNoFound) {

        if (targetTableName == null || targetTableName.isEmpty()) {
            throw new IllegalArgumentException("Table name is Mandatory : " + targetTableName);
        }

        Map<String, String> columnDataTypeMap = getColumnsForTable(targetTableName);

        List<Boolean> stringTypeColumns = new ArrayList<>();
        for (String columnName : columnNames) {
            String dataType = columnDataTypeMap.get(columnName);
            if (failIfColumnNoFound && dataType == null) {
                throw new IllegalArgumentException(
                        "Column : " + columnName + " not present in table : " + targetTableName);
            }
            stringTypeColumns.add(dataType != null ? !numericDataTypes.contains(dataType) : Boolean.TRUE);
        }
        return stringTypeColumns;
    }

    /**
     * @param targetTableName
     * @return
     */
    private Map<String, String> getColumnsForTable(String targetTableName) {

        LOG.debug("Getting columns for table : {}", targetTableName);

        String schemaTableParts[] = targetTableName.split("\\.");
        if (schemaTableParts.length != 2) {
            throw new IllegalArgumentException("Table name is not in the format schema.tablename : " + targetTableName);
        }

        if (tableToColumnsDataTypeMap.containsKey(targetTableName)) {
            return tableToColumnsDataTypeMap.get(targetTableName);
        }

        List<Map<String, Object>> columnsList = stagingUtilMapper.getColumnsDataType(schemaTableParts[0],
                schemaTableParts[1]);

        if (columnsList == null || columnsList.isEmpty()) {
            throw new RuntimeException("Error in getting colum names for table : " + targetTableName);
        }

        Map<String, String> columnToDataTypeMap = columnsList.stream()
                .collect(Collectors.toMap(s -> (String) s.get("column_name"), s -> (String) s.get("data_type")));

        tableToColumnsDataTypeMap.put(targetTableName, columnToDataTypeMap);
        return Collections.unmodifiableMap(columnToDataTypeMap);
    }
}