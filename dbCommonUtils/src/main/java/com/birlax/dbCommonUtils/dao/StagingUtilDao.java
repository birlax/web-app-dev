package com.birlax.dbCommonUtils.dao;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.birlax.dbCommonUtils.mapper.StagingUtilMapper;
import com.birlax.dbCommonUtils.service.DatabaseMetadataService;

@Named("stagingUtilDao")
public class StagingUtilDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(StagingUtilDao.class);

    // DOUBLE QUOTE
    private static final char COLUMN_ENCLOSER = '"';

    private static final char COLUMN_DELIMITER = ',';

    private static final String TWO_DOUBLE_QUOTES = "\"\"";

    private static final String ONE_DOUBLE_QUOTE = "\"";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Inject
    private StagingUtilMapper stagingUtilMapper;

    @Inject
    private DatabaseMetadataService databaseMetadataService;

    // Copy Manager needs this. // Transactions are problem
    @Inject
    private DataSourceTransactionManager transactionManager;

    // This is the upper limit by postgres on the number of
    // parameters allowed in a SQL query
    private static final Integer MAX_PARAMS_ALLOWED = 30000;

    /*
     * Note : Postgres does not allow SQLs across db hence can't create temp table in any different database. Workaround
     * is to use schema sandbox within the same db and get your temp tables created there.<br>
     */
    @Inject
    private String temporaryDatabaseOrSchemaName;

    /**
     * Create a staging table against the schema of provided table and returns the name of create temp table.<br>
     * <br>
     * Table will always be create in temp DATABASE(Microsoft SQL) or in temp schema in current DATABASE(For
     * PostgreSQL). Only for Postgres SQL, table will be always be created in same database that's in current use, hence
     * all databases should have above mentioned workaround.<br>
     * <br>
     * Note : Above is true even when fully qualified name from temp table is passed.<br>
     * Postgres also does not have concept for GTT (Global Temp Table) and I don't wan't to build one.<br>
     * <br>
     * Note : When copying schema from any parent table, Child table's columns will be nullable, even if columns in
     * parent table was non-nullable. As temp tables are mostly used to staging this is fine. But on bulk loading data
     * to main(parent) table this may lead to failures. Other SQLs : Microsoft SQL has different approachm there stating
     * it-self will fail.
     */
    public String createTempStageTable(Set<String> columnNames, String fullyQualifiedParentTableName)
            throws IllegalArgumentException {
        if (columnNames == null || columnNames.isEmpty() || fullyQualifiedParentTableName == null
                || fullyQualifiedParentTableName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Columns/TableName can't be null or empty. For table : " + fullyQualifiedParentTableName);
        }
        LOGGER.debug("Creating temp table : against master table : {} with columns : {}", fullyQualifiedParentTableName,
                columnNames);
        String tempTableName = temporaryDatabaseOrSchemaName + ".t" + Thread.currentThread().getId()
                + fullyQualifiedParentTableName.replaceAll("\\.", "_") + new Date().getTime();
        return createTempStageTableWithTableName(columnNames, fullyQualifiedParentTableName, tempTableName);
    }

    /**
     * Helper method to {@link #createTempStageTable}
     *
     * @param columnNames
     * @param fullyQualifiedParentTableName
     * @param tempTableName
     * @return
     */
    private String createTempStageTableWithTableName(Set<String> columnNames, String fullyQualifiedParentTableName,
            String tempTableName) {
        stagingUtilMapper.createTempStageTable(columnNames, fullyQualifiedParentTableName, tempTableName);
        return tempTableName;
    }

    /**
     * Transaction propagation is set to NOT_SUPPORTED so creation of staging table and inserting data into staging
     * table happens outside of the main transaction. Staging records into table is done with COPY method. If that fails
     * for any reason, it falls back to using SQL method. The fallback is created as the SQL way is proven in production
     * and this method is a very critical one.
     *
     * @param createTableColName
     * @param insertColName
     * @param jsobInsertColName
     * @param fullyQualifiedParentTableName
     * @param dataRecords
     * @return
     */
    // @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String dummyStageRecords(Set<String> createTableColName, Set<String> insertColName,
            Set<String> jsobInsertColName, String fullyQualifiedParentTableName,
            List<Map<String, Object>> dataRecords) {

        if (dataRecords == null || dataRecords.isEmpty()) {
            throw new IllegalArgumentException(
                    "Empty Records list nothing to stage. In case you just want the empty temp table, use create call..");
        }

        if (insertColName == null || insertColName.isEmpty()) {// still, create the table as other queries may depend on
            // presence of the table
            throw new IllegalArgumentException("Columns to be Staged/Inserted can't be EMPTY.");
        }

        String tempTableName = null;

        try {
            tempTableName = createTempStageTable(createTableColName, fullyQualifiedParentTableName);
        } catch (Exception e) {
            LOGGER.error("Error creating temp/staging table from table : " + fullyQualifiedParentTableName, e);
        }

        LOGGER.info("Temp/Staging table : {} create. Start staging records size :{}", tempTableName,
                dataRecords.size());

        try {
            LOGGER.debug("Loading records into a staging table : {} using COPY for table : {}", tempTableName,
                    fullyQualifiedParentTableName);

            insertRecordsIntoStagingTableUsingCopy(insertColName, jsobInsertColName, dataRecords, tempTableName,
                    fullyQualifiedParentTableName);
        } catch (Exception e) {
            //LOGGER.error("Error in loading records into staging table through COPY for " + fullyQualifiedParentTableName
             //       + " staging table : " + tempTableName, e);
        }

        try {
            LOGGER.debug("Retrying the SQL way.");
            tempTableName = retryDummyStageRecordsThroughSQL(insertColName, jsobInsertColName, tempTableName,
                    dataRecords);
        } catch (Exception e) {
            LOGGER.error("Error in loading records into staging table through SQL  " + fullyQualifiedParentTableName
                    + " staging table : " + tempTableName, e);
        }

        return tempTableName;
    }

    private String retryDummyStageRecordsThroughSQL(Set<String> insertColName, Set<String> jsobInsertColName,
            String tempTableName, List<Map<String, Object>> dataRecords) {

        try {
            Thread.sleep(2); // Ensure the clock changes so the temp table name is unique
        } catch (InterruptedException e) {
            // Nothing to do
        }

        // String tempTableName = null;
        try {
            // LOGGER.debug("Loading records into a staging table using SQL for {}", fullyQualifiedParentTableName);
            // tempTableName = createTempStageTable(createTableColName, fullyQualifiedParentTableName);
            insertRecordsIntoStagingTable(insertColName, jsobInsertColName, dataRecords, tempTableName);
            return tempTableName;
        } catch (Exception e) {
            LOGGER.error("Error in loading records into staging table through SQL for staging table : " + tempTableName,
                    e);
            throw e;
        }
    }

    private void insertRecordsIntoStagingTable(Set<String> insertColName, Set<String> jsobInsertColName,
            List<Map<String, Object>> dataRecords, String tempTableName) {

        // if (dataRecords == null || dataRecords.isEmpty()) {// still, create the table as other queries may depend on
        // presence of the table
        // LOGGER.info("Skipping staging table persistence as record set is empty for " + tempTableName);
        // return;
        // }

        LOGGER.debug("Starting staging records to sandbox/temp table in bulk [ " + tempTableName
                + " ] Records to be staged [ " + dataRecords.size() + " ].");

        int toIndex = 0, fromIndex = 0;
        int batchSize = MAX_PARAMS_ALLOWED / insertColName.size();
        while (fromIndex < dataRecords.size()) {
            toIndex = fromIndex + batchSize;
            if (toIndex > dataRecords.size()) {
                toIndex = dataRecords.size();
            }
            List<Map<String, Object>> dataRecordsForABatch = dataRecords.subList(fromIndex, toIndex);
            stagingUtilMapper.dummyStageRecordsInBulk(tempTableName, insertColName, jsobInsertColName,
                    dataRecordsForABatch);
            fromIndex = toIndex;
        }
        LOGGER.debug("Completed staging records to sandbox/temp table [ " + tempTableName + " ] Records staged [ "
                + dataRecords.size() + " ].");
    }

    @SuppressWarnings("unused")
    private void insertRecordsIntoStagingTableInBatchesUsingSqls(Set<String> insertColName,
            Set<String> jsobInsertColName, List<Map<String, Object>> dataRecords, String tempTableName,
            String fullyQualifiedParentTableName) {

        if (dataRecords == null || dataRecords.isEmpty()) {// still, create the table as other queries may depend on
                                                           // presence of the table
            LOGGER.debug("Skipping staging table persistence as record set is empty for " + tempTableName);
            return;
        }
        LOGGER.debug("Starting staging records to sandbox/temp table in bulk in multiple batches [ " + tempTableName
                + " ] Records to be staged [ " + dataRecords.size() + " ].");

        List<String> sqls = new ArrayList<>();
        int toIndex = 0, fromIndex = 0;
        int batchSize = MAX_PARAMS_ALLOWED
                / (insertColName.size() + (jsobInsertColName != null ? jsobInsertColName.size() : 0));
        List<String> insertColumnNames = new ArrayList<>(insertColName);
        List<String> jsobColumnNames = jsobInsertColName != null ? new ArrayList<>(jsobInsertColName)
                : Collections.emptyList();

        List<Boolean> stringTypeColumns = databaseMetadataService
                .determineStringTypeColumns(fullyQualifiedParentTableName, insertColName, false);
        String insertSqlBase = getBaseInsertSql(tempTableName, insertColumnNames, jsobColumnNames);

        while (fromIndex < dataRecords.size()) {
            toIndex = fromIndex + batchSize;
            if (toIndex > dataRecords.size()) {
                toIndex = dataRecords.size();
            }
            List<Map<String, Object>> dataRecordsForABatch = dataRecords.subList(fromIndex, toIndex);
            String insertSql = prepareInsertSqlForBatchOfRecords(insertColumnNames, jsobColumnNames, stringTypeColumns,
                    insertSqlBase, dataRecordsForABatch);
            sqls.add(insertSql);
            fromIndex = toIndex;
        }
        LOGGER.debug("SQL:" + sqls.get(0));
        try {
            stagingUtilMapper.dummyStageRecordsUsingSqls(sqls, tempTableName);
        } catch (Exception e) {
            LOGGER.error("Error in staging sql insert", e);
            throw e;
        }
        LOGGER.debug("Completed staging records to sandbox/temp table  [ " + tempTableName
                + " ] Records to be staged [ " + dataRecords.size() + " ].");
    }

    /**
     * This uses COPY method of postgres to write data to staging tables. This has proved to be the fastest way to send
     * in bulk data from Java to DB. Other methods are left in the class for reference for now till this stabilizes.
     */
    private void insertRecordsIntoStagingTableUsingCopy(Set<String> insertColName, Set<String> jsobInsertColName,
            List<Map<String, Object>> dataRecords, String tempTableName, String fullyQualifiedParentTableName) {

        LOGGER.debug("Starting staging records to sandbox/temp table through csv copy [ " + tempTableName
                + " ] Records to be staged [ " + dataRecords.size() + " ].");

        List<String> insertColumnNames = new ArrayList<>(insertColName);
        List<String> jsobColumnNames = jsobInsertColName != null ? new ArrayList<>(jsobInsertColName)
                : Collections.emptyList();

        List<Boolean> stringTypeColumns = databaseMetadataService
                .determineStringTypeColumns(fullyQualifiedParentTableName, insertColName, false);

        // Connection and BaseConnection have to be created separately, otherwise releaseConnection will not work
        // Please refer to ConnectionHealthTest
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(transactionManager.getDataSource());
            BaseConnection baseConnection = connection.unwrap(BaseConnection.class);
            CopyManager copyManager = new CopyManager(baseConnection);
            copyRecordsToTableInBatches(dataRecords, tempTableName, insertColumnNames, jsobColumnNames,
                    stringTypeColumns, copyManager);
        } catch (SQLException e) {
            //LOGGER.error("Error in creating CopyManager to copy records.", e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    LOGGER.debug("Releasing connection: {}", connection);
                    DataSourceUtils.releaseConnection(connection, transactionManager.getDataSource());
                } catch (Exception e) {
                    // Nothing to do
                    LOGGER.error("Error in releasing connection taken for COPY. Nothing to do.", e);
                }
            }
        }

        LOGGER.debug("Completed staging records to sandbox/temp table  [ " + tempTableName
                + " ] Records to be staged [ " + dataRecords.size() + " ].");
    }

    private void copyRecordsToTableInBatches(List<Map<String, Object>> dataRecords, String tempTableName,
            List<String> insertColumnNames, List<String> jsobColumnNames, List<Boolean> stringTypeColumns,
            CopyManager copyManager) {
        List<String> allColumnNames = new ArrayList<>(insertColumnNames);
        allColumnNames.addAll(jsobColumnNames);

        String delimitedColumns = allColumnNames.stream().collect(Collectors.joining(","));

        String sql = " COPY " + tempTableName + "(" + delimitedColumns + ") FROM STDIN WITH CSV";

        // Records are copied to table in batches to keep the CSV records stringbuffer memory to a reasonable size
        int toIndex = 0, fromIndex = 0;
        int batchSize = 20000; // 20K records with 1K record size will take 20MB of memory for a stringbuilder
        while (fromIndex < dataRecords.size()) {
            toIndex = fromIndex + batchSize;
            if (toIndex > dataRecords.size()) {
                toIndex = dataRecords.size();
            }
            List<Map<String, Object>> dataRecordsForABatch = dataRecords.subList(fromIndex, toIndex);
            copyRecordsToTable(dataRecordsForABatch, insertColumnNames, jsobColumnNames, stringTypeColumns, sql,
                    copyManager);
            fromIndex = toIndex;
        }
    }

    private void copyRecordsToTable(List<Map<String, Object>> dataRecords, List<String> insertColumnNames,
            List<String> jsobColumnNames, List<Boolean> stringTypeColumns, String sql, CopyManager copyManager) {
        try {
            String content = writeRecordsToMemory(dataRecords, insertColumnNames, jsobColumnNames, stringTypeColumns);
            StringReader strReader = new StringReader(content);
            copyManager.copyIn(sql, strReader);
        } catch (Exception e) {
            LOGGER.error("Error in creating file and copying records. SQL:" + sql, e);
            throw new RuntimeException(e);
        }
    }

    private String writeRecordsToMemory(List<Map<String, Object>> dataRecords, List<String> insertColumnNames,
            List<String> jsobColumnNames, List<Boolean> stringTypeColumns) throws IOException {
        StringBuilder contentBuilder = new StringBuilder(dataRecords.size() * insertColumnNames.size() * 10);
        for (Map<String, Object> record : dataRecords) {
            contentBuilder.append(convertRecordToCsv(insertColumnNames, jsobColumnNames, stringTypeColumns, record));
            contentBuilder.append(LINE_SEPARATOR);
        }
        return contentBuilder.toString();
    }

    /**
     * CSV record is prepared. String columns are enclosed with double quotes, comma is the separator, an empty unquoted
     * string is considerd as null.
     */
    private String convertRecordToCsv(List<String> insertColumnNames, List<String> jsobColumnNames,
            List<Boolean> stringTypeColumns, Map<String, Object> record) {
        StringBuilder csvRecordBuilder = new StringBuilder(10000);
        int normalColumnSize = insertColumnNames.size();
        int normalColumnSizeMinusOne = normalColumnSize - 1;
        int jsonColumnSize = jsobColumnNames.size();
        int jsonColumnSizeMinusOne = jsonColumnSize - 1;

        for (int colIndex = 0; colIndex < normalColumnSize; colIndex++) {
            Object columnValue = record.get(insertColumnNames.get(colIndex));
            if (columnValue != null) {
                if (stringTypeColumns.get(colIndex)) {
                    csvRecordBuilder.append(COLUMN_ENCLOSER).append(columnValue).append(COLUMN_ENCLOSER);
                } else {
                    csvRecordBuilder.append(columnValue);
                }
            }
            if (colIndex != normalColumnSizeMinusOne) {
                csvRecordBuilder.append(COLUMN_DELIMITER);
            }
        }
        if (jsonColumnSize > 0) {
            // open="to_json(" close="::jsonb)::jsonb" separator=","
            csvRecordBuilder.append(COLUMN_DELIMITER).append(COLUMN_ENCLOSER);
            for (int colIndex = 0; colIndex < jsonColumnSize; colIndex++) {
                String jsonData = (String) record.get(jsobColumnNames.get(colIndex));
                csvRecordBuilder.append(jsonData.replaceAll(ONE_DOUBLE_QUOTE, TWO_DOUBLE_QUOTES));
                if (colIndex != jsonColumnSizeMinusOne) {
                    csvRecordBuilder.append(COLUMN_DELIMITER);
                }
            }
            csvRecordBuilder.append(COLUMN_ENCLOSER);
        }
        return csvRecordBuilder.toString();
    }

    private String prepareInsertSqlForBatchOfRecords(List<String> insertColumnNames, List<String> jsobColumnNames,
            List<Boolean> stringTypeColumns, String insertSqlBase, List<Map<String, Object>> dataRecordsForABatch) {
        StringBuilder insertSqlBuilder = new StringBuilder(100000);
        insertSqlBuilder.append(insertSqlBase);
        int normalColumnSize = insertColumnNames.size();
        int normalColumnSizeMinusOne = normalColumnSize - 1;
        int jsonColumnSize = jsobColumnNames.size();
        int jsonColumnSizeMinusOne = jsonColumnSize - 1;
        int currentBatchSize = dataRecordsForABatch.size();
        int currentBatchSizeMinusOne = currentBatchSize - 1;
        for (int recIndex = 0; recIndex < currentBatchSize; recIndex++) {
            Map<String, Object> record = dataRecordsForABatch.get(recIndex);
            insertSqlBuilder.append('(');
            for (int colIndex = 0; colIndex < normalColumnSize; colIndex++) {
                Object columnValue = record.get(insertColumnNames.get(colIndex));
                if (stringTypeColumns.get(colIndex) && columnValue != null) {
                    insertSqlBuilder.append('\'').append(columnValue).append('\'');
                } else {
                    insertSqlBuilder.append(columnValue);
                }
                if (colIndex != normalColumnSizeMinusOne) {
                    insertSqlBuilder.append(',');
                }
            }
            if (jsonColumnSize > 0) {
                // open="to_json(" close="::jsonb)::jsonb" separator=","
                insertSqlBuilder.append(',').append("to_json('");
                for (int colIndex = 0; colIndex < jsonColumnSize; colIndex++) {
                    insertSqlBuilder.append(record.get(jsobColumnNames.get(colIndex)));
                    if (colIndex != jsonColumnSizeMinusOne) {
                        insertSqlBuilder.append(',');
                    }
                }
                insertSqlBuilder.append("'::jsonb)::jsonb");
            }
            insertSqlBuilder.append(')');
            if (recIndex != currentBatchSizeMinusOne) {
                insertSqlBuilder.append(',');
            }
        }
        return insertSqlBuilder.toString();
    }

    private String getBaseInsertSql(String tempTableName, List<String> insertColumnNames,
            List<String> jsobColumnNames) {
        StringBuilder insertSqlBaseBuilder = new StringBuilder();
        insertSqlBaseBuilder.append("insert into ").append(tempTableName).append('(');

        String delimitedInsertColumns = insertColumnNames.stream().collect(Collectors.joining(","));

        insertSqlBaseBuilder.append(delimitedInsertColumns);

        if (jsobColumnNames != null && !jsobColumnNames.isEmpty()) {
            String delimitedInsertJsonbColumns = jsobColumnNames.stream().collect(Collectors.joining(","));
            insertSqlBaseBuilder.append(',').append(delimitedInsertJsonbColumns);
        }

        insertSqlBaseBuilder.append(')').append(" values ");
        return insertSqlBaseBuilder.toString();
    }

    /**
     * @param tempTableName
     *                      Drops that give table only if it exists and is in sandbox schema
     */
    public void dropTempTable(String tempTableName) {
        if (tempTableName == null || tempTableName.isEmpty()
                || !tempTableName.contains(temporaryDatabaseOrSchemaName)) {
            throw new IllegalArgumentException("Attempt to drop master table. Or table is invalid/none-existent..");
        }
        try {
            stagingUtilMapper.dropTempTable(tempTableName);
        } catch (Exception e) {
            LOGGER.warn("Unable to drop staging table : {} ", tempTableName, e);
            // Nothing to do, Staging database will have to be cleaned up later
        }
    }

    public List<Map<String, Object>> getData(Set<String> columnNames, String tempTableName) {
        return stagingUtilMapper.getData(tempTableName, columnNames);
    }

    /**
     * @return the temporaryDatabaseOrSchemaName
     */
    public String getTemporaryDatabaseOrSchemaName() {
        return this.temporaryDatabaseOrSchemaName;
    }

    /**
     * Note : COPY Manager from PG need dbh more like perl DBO, and I am not yet able to figure out the transactions in
     * it.
     *
     * @param columnNames
     * @param fullyQualifiedParentTableName
     * @param records
     * @return
     */
    /*
     * public String stageRecordsToTempTable(Set<String> columnNames, String fullyQualifiedParentTableName, List<x>
     * records) { String tempTableName = createTempStageTable(columnNames, fullyQualifiedParentTableName); StringBuilder
     * strBulder = new StringBuilder(); for (x r : records) { for (String keyFieldName : columnNames) { Object keyId =
     * r.getKeyFieldValue(keyFieldName) == null ? -1 : r.getKeyFieldValue(keyFieldName); strBulder.append(keyId);
     * strBulder.append(','); } strBulder.append("-1\r\n"); // very bad } String content = strBulder.toString();
     * CopyManager copyManager = null; try { // TODO: return this back to pool copyManager = new CopyManager(
     * transactionManager.getDataSource().getConnection().unwrap(BaseConnection.class)); String sql = " COPY " +
     * tempTableName + "(" + columnNames.stream().map(a -> a.toString()).collect(Collectors.joining(",")) +
     * ",record_id) FROM STDIN WITH DELIMITER AS ',' "; StringReader strReader = new StringReader(content); try {
     * copyManager.copyIn(sql, strReader); } catch (IOException exception) {
     * LOGGER.error("IO Exception while creating StringReader...", exception); throw new
     * IllegalArgumentException("IO Error can't proceed forward..", exception); } } catch (SQLException exception) {
     * LOGGER.error("Error staging record.", exception); throw new
     * IllegalArgumentException("SQLException Error can't proceed forward..", exception); } return tempTableName; }
     */
}
