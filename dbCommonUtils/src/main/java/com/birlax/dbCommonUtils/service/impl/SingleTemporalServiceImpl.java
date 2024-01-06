/**
 *
 */
package com.birlax.dbCommonUtils.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.dbCommonUtils.dao.StagingUtilDao;
import com.birlax.dbCommonUtils.mapper.SingleTemporalMapper;
import com.birlax.dbCommonUtils.service.DatabaseMetadataService;
import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.service.TemporalService;
import com.birlax.dbCommonUtils.util.ReflectionHelper;

/**
 * @author birlax
 */
@Named("singleTemporalService")
public class SingleTemporalServiceImpl implements TemporalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleTemporalServiceImpl.class);

    @Inject
    private StagingUtilDao stagingUtilDao;

    @Inject
    private SingleTemporalMapper singleTemporalMapper;

    @Inject
    private DatabaseMetadataService databaseMetadataService;

    @Override
    public <T extends SingleTemporalDAO> List<Map<String, Object>> deleteRecords(List<T> records) {

        validation(records);

        String fullyQualifiedParentTableName = records.iterator().next().getFullyQualifiedTableName();

        LOGGER.info("Started in deleting records : {} from table : {}", records.size(), fullyQualifiedParentTableName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SingleTemporalDAO record : records) {
            Map<String, Object> map = record.getDAOFlatView();
            dataList.add(map);
        }

        Set<String> jsobInsertColName = null;
        Set<String> insertColName = new HashSet<>();
        Set<String> createTableColName = new HashSet<>(records.iterator().next().getDAOKey());
        insertColName.addAll(createTableColName);
        String tempTableName = stagingUtilDao.dummyStageRecords(createTableColName, insertColName, jsobInsertColName,
                fullyQualifiedParentTableName, dataList);

        List<Map<String, Object>> deletedRecords = singleTemporalMapper.deleteRecords(tempTableName,
                fullyQualifiedParentTableName, insertColName);

        stagingUtilDao.dropTempTable(tempTableName);

        LOGGER.info("Completed deleting records : {} from table : {}", records.size(), fullyQualifiedParentTableName);
        return deletedRecords;
    }

    @Override
    public <T extends SingleTemporalDAO> List<Map<String, Object>> insertRecords(List<T> records) {

        validation(records);

        String fullyQualifiedParentTableName = records.iterator().next().getFullyQualifiedTableName();

        LOGGER.info("Started in inserting records : {} from table : {}", records.size(), fullyQualifiedParentTableName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SingleTemporalDAO record : records) {
            Map<String, Object> map = record.getDAOFlatView();
            dataList.add(map);
        }

        Set<String> jsobInsertColName = null;
        Set<String> insertColName = new HashSet<>();
        Set<String> createTableColName = new HashSet<>(records.iterator().next().getDAOKey());
        createTableColName.addAll(records.iterator().next().getDAOFacts());
        insertColName.addAll(createTableColName);
        String tempTableName = stagingUtilDao.dummyStageRecords(createTableColName, insertColName, jsobInsertColName,
                fullyQualifiedParentTableName, dataList);

        List<Map<String, Object>> insertedRecords = singleTemporalMapper.insertRecords(tempTableName,
                fullyQualifiedParentTableName, insertColName);
        stagingUtilDao.dropTempTable(tempTableName);
        LOGGER.info("Completed inserting records : {} from table : {}", records.size(), fullyQualifiedParentTableName);
        return insertedRecords;

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends SingleTemporalDAO> List<Map<String, Object>> mergeRecords(List<T> records) {

        validation(records);

        String fullyQualifiedParentTableName = records.iterator().next().getFullyQualifiedTableName();

        LOGGER.info("Started in inserting records : {} from table : {}", records.size(), fullyQualifiedParentTableName);

        Map<Long, T> inputRecordToInternalIdMap = new HashMap<>();
        Map<String, Long> inputRecordKeyMD5SumToInternalIdMap = new HashMap<>();
        long id = 0;
        for (SingleTemporalDAO record : records) {
            inputRecordToInternalIdMap.put(id++, ((T) record));
        }

        // Map<String, Map<String, Object>> inMemoryRecordsKeyMD5SumMap = new HashMap<>();
        Map<String, String> inMemoryRecordsFactMD5SumMap = new HashMap<>();
        List<String> keys = records.iterator().next().getDAOKey();
        List<String> facts = records.iterator().next().getDAOFacts();

        for (Map.Entry<Long, T> entry : inputRecordToInternalIdMap.entrySet()) {
            String md5sum = getHexDigest(entry.getValue().getDAOFlatView(), keys);

            if (inputRecordKeyMD5SumToInternalIdMap.containsKey(md5sum)) {
                // throw new IllegalArgumentException("Inputs records are duplicate on \n Key fields : " + keys
                // + ".\n Record #1: " + entry.getValue() + "\n Record #2: " + inputRecordToInternalIdMap
                // .get(inputRecordKeyMD5SumToInternalIdMap.get(md5sum)));
            }
            inputRecordKeyMD5SumToInternalIdMap.put(md5sum, entry.getKey());
            inMemoryRecordsFactMD5SumMap.put(md5sum, getHexDigest(entry.getValue().getDAOFlatView(), facts));
        }

        Set<String> keySet = new HashSet<>();
        keySet.addAll(keys);

        String cs = records.iterator().next().getClass().getName();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(cs);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Class not found." + cs, e);
            throw new RuntimeException("Class not found." + cs, e);
        }
        // List<Map<String, Object>> dataAlreadyInDB = searchRecords(records, keySet);
        List<T> dataAlreadyInDB = searchRecords(records, keySet, clazz);

        Map<String, T> dataAlreadyInDBKeyMD5SumMap = new HashMap<>();
        Map<String, String> dataAlreadyInDBFactsMD5SumMap = new HashMap<>();

        for (T map : dataAlreadyInDB) {
            String md5sum = getHexDigest(map.getDAOFlatView(), keys);
            dataAlreadyInDBKeyMD5SumMap.put(md5sum, map);
            dataAlreadyInDBFactsMD5SumMap.put(md5sum, getHexDigest(map.getDAOFlatView(), facts));
        }
        List<T> insertOnly = new ArrayList<>();
        List<T> deleteOnly = new ArrayList<>();
        List<T> mergeOrUpdateOnly = new ArrayList<>();

        for (Map.Entry<String, T> entry : dataAlreadyInDBKeyMD5SumMap.entrySet()) {
            if (inputRecordKeyMD5SumToInternalIdMap.containsKey(entry.getKey())) {
                // present in both
                if (inMemoryRecordsFactMD5SumMap.get(entry.getKey())
                        .equals(dataAlreadyInDBFactsMD5SumMap.get(entry.getKey()))) {
                    // key and fact all are same nothing to do for this record.
                    continue;
                } else {
                    deleteOnly.add(entry.getValue());
                    mergeOrUpdateOnly.add(
                            inputRecordToInternalIdMap.get(inputRecordKeyMD5SumToInternalIdMap.get(entry.getKey())));
                }
            } else {
                // not present in inbound data but present in database delete it
                deleteOnly.add(entry.getValue());
            }
        }
        // Work on the data only present inMemory records
        for (Map.Entry<String, Long> entry : inputRecordKeyMD5SumToInternalIdMap.entrySet()) {
            if (!dataAlreadyInDBKeyMD5SumMap.containsKey(entry.getKey())) {
                // present in inbound data but not present in database insert it
                insertOnly.add(inputRecordToInternalIdMap.get(inputRecordKeyMD5SumToInternalIdMap.get(entry.getKey())));
            }
        }
        LOGGER.info("Only delete records : " + (deleteOnly.size()));
        if (!deleteOnly.isEmpty()) {
            deleteRecords(deleteOnly);
        }
        LOGGER.info("Only insert records : " + insertOnly.size());
        if (!insertOnly.isEmpty()) {
            insertRecords(insertOnly);
        }
        LOGGER.info("Only update records : " + mergeOrUpdateOnly.size());
        if (!mergeOrUpdateOnly.isEmpty()) {
            insertRecords(mergeOrUpdateOnly);
        }
        return null;
    }

    private String getHexDigest(Map<String, Object> data, List<String> keys) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keys) {
            // TODO: handle the Double cases
            Object value = data.get(key);
            if (value == null) {
                stringBuilder.append("__null__");
            } else {
                stringBuilder.append(value);
            }
        }
        return DigestUtils.md5Hex(stringBuilder.toString());
    }

    /**
     * @param records
     */
    private <T extends SingleTemporalDAO> void validation(List<T> records) {

        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException("Records to be inserted is MANDATORY.");
        }
        String fullyQualifiedParentTableName = records.iterator().next().getFullyQualifiedTableName();
        if (fullyQualifiedParentTableName == null || fullyQualifiedParentTableName.isEmpty()) {
            throw new IllegalArgumentException("Table Name is MANDATORY.");
        }
    }

    /**
     * @param records
     */
    private <T extends SingleTemporalDAO> List<T> mapToObjectConvertor(List<Map<String, Object>> data, Class<?> clazz) {
        List<T> list = new ArrayList<>();
        for (Map<String, Object> flatView : data) {
            try {
                list.add(ReflectionHelper.getDomainObject(flatView, clazz));
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Failed to search.", e);
                throw new RuntimeException("Failed to search.", e);
            }
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.TemporalService#searchRecordsForDomain(java.util.List, java.util.Set)
     */
    @Override
    public <T extends SingleTemporalDAO> List<T> searchRecords(List<T> records, Set<String> searchByColumns,
            Class<?> clazz) {
        List<Map<String, Object>> data = searchRecords(records, searchByColumns);
        return mapToObjectConvertor(data, clazz);
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.TemporalService#getRecordsByKey(java.util.List)
     */
    @Override
    public <T extends SingleTemporalDAO> List<Map<String, Object>> searchRecords(List<T> records,
            Set<String> searchByColumns) {

        validation(records);

        String fullyQualifiedParentTableName = records.iterator().next().getFullyQualifiedTableName();

        LOGGER.debug("Started in searching records : {} from table : {}", records.size(),
                fullyQualifiedParentTableName);
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SingleTemporalDAO record : records) {
            Map<String, Object> map = record.getDAOFlatView();
            dataList.add(map);

        }

        Set<String> jsobInsertColName = null;
        // get all keys and facts
        Set<String> defaultColumnsInObjectView = new HashSet<>();
        defaultColumnsInObjectView.addAll(records.iterator().next().getDAOKey());
        defaultColumnsInObjectView.addAll(records.iterator().next().getDAOFacts());
        // create and stage with columns that are part of filter
        String tempTableName = stagingUtilDao.dummyStageRecords(searchByColumns, searchByColumns, jsobInsertColName,
                fullyQualifiedParentTableName, dataList);
        // Columns which are already part of filter remove them from default view to have distinct column in result
        defaultColumnsInObjectView.removeAll(searchByColumns);
        List<Map<String, Object>> searchedRecords = singleTemporalMapper.searchRecords(tempTableName,
                fullyQualifiedParentTableName, searchByColumns, defaultColumnsInObjectView);
        stagingUtilDao.dropTempTable(tempTableName);
        LOGGER.debug("Completed searching records : {}, found records : {}, filtered by : {}, from table : {}",
                records.size(), searchedRecords.size(), searchByColumns, fullyQualifiedParentTableName);
        return searchedRecords;
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.TemporalService#searchRecordsForDomain(java.util.List, java.util.Set)
     */
    @Override
    public <T extends SingleTemporalDAO> List<T> getAllRecords(Set<String> retrieveColumns, Class<?> clazz) {
        List<Map<String, Object>> data = singleTemporalMapper.getAllRecords(getDatabaseTableFromPOJO(clazz),
                retrieveColumns);
        return mapToObjectConvertor(data, clazz);
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.TemporalService#searchRecordsForDateRange(java.util.List, java.util.Set,
     * java.lang.String, java.util.Date, java.util.Date, java.lang.Class)
     */
    @Override
    public <T extends SingleTemporalDAO> List<T> searchRecordsForDateRange(List<T> records, Set<String> searchByColumns,
            Set<String> retrieveColumns, String effectiveDateColName, Date startDate, Date endDate, Class<?> clazz) {

        List<Map<String, Object>> data = searchRecordsForDateRange(records, searchByColumns, retrieveColumns,
                effectiveDateColName, startDate, endDate);

        return mapToObjectConvertor(data, clazz);
    }

    /*
     * (non-Javadoc)
     * @see com.birlax.dbCommonUtils.service.TemporalService#searchRecordsForDateRange(java.util.List, java.util.Set,
     * java.util.Set, java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public <T extends SingleTemporalDAO> List<Map<String, Object>> searchRecordsForDateRange(List<T> records,
            Set<String> searchByColumns, Set<String> retrieveColumns, String effectiveDateColName, Date startDate,
            Date endDate) {

        validation(records);

        String fullyQualifiedParentTableName = records.iterator().next().getFullyQualifiedTableName();

        LOGGER.debug("Started in searching records : {} from table : {}", records.size(),
                fullyQualifiedParentTableName);

        if (effectiveDateColName == null || effectiveDateColName.isEmpty()) {
            throw new IllegalArgumentException(
                    "EffectiveDate column name is mandatory. Provided value  : " + effectiveDateColName);
        }
        // validate the existence of the effective-date column in the table.
        databaseMetadataService.determineStringTypeColumns(fullyQualifiedParentTableName,
                new HashSet<>(Arrays.asList(effectiveDateColName)), true);

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SingleTemporalDAO record : records) {
            Map<String, Object> map = record.getDAOFlatView();
            dataList.add(map);
        }

        Set<String> jsobInsertColName = null;
        // get all keys and facts
        Set<String> defaultColumnsInObjectView = new HashSet<>();
        defaultColumnsInObjectView.addAll(records.iterator().next().getDAOKey());
        defaultColumnsInObjectView.addAll(records.iterator().next().getDAOFacts());
        // create and stage with columns that are part of filter
        String tempTableName = stagingUtilDao.dummyStageRecords(searchByColumns, searchByColumns, jsobInsertColName,
                fullyQualifiedParentTableName, dataList);
        // Columns which are already part of filter remove them from default view to have distinct column in result
        defaultColumnsInObjectView.removeAll(searchByColumns);
        retrieveColumns.add(effectiveDateColName);
        retrieveColumns.removeAll(searchByColumns);
        List<Map<String, Object>> searchedRecords = singleTemporalMapper.searchRecordsForDateRange(tempTableName,
                fullyQualifiedParentTableName, searchByColumns, retrieveColumns, effectiveDateColName, startDate,
                endDate);
        stagingUtilDao.dropTempTable(tempTableName);
        LOGGER.debug(
                "Completed searching for \n Records : {}, \n Found records : {}, \n Filtered by : {}, \n From table : {} \n Retrieved Colum: {} :",
                records.size(), searchedRecords.size(), searchByColumns, fullyQualifiedParentTableName,
                retrieveColumns);
        return searchedRecords;
    }

    /**
     * @param clazz
     * @return
     */
    private <T extends SingleTemporalDAO> String getDatabaseTableFromPOJO(Class<?> clazz) {
        T cls = null;
        try {
            cls = (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Failed to instantiate class, or provide clazz does not implement SingleTemporalDAO." + clazz,
                    e);
        }
        return cls.getFullyQualifiedTableName();
    }

}
