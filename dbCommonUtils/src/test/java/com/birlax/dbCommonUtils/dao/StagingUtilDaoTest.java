/**
 *
 */
package com.birlax.dbCommonUtils.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.birlax.dbCommonUtils.BaseIntegerationTest;
import com.birlax.dbCommonUtils.service.DatabaseMetadataService;

/**
 * @author birlax
 */
public class StagingUtilDaoTest extends BaseIntegerationTest {

    @Inject
    private StagingUtilDao stagingUtilDao;

    @Inject
    private DatabaseMetadataService databaseMetadataService;

    private Set<String> createTableColumns;

    private Set<String> insertTableColumns;

    private List<Map<String, Object>> records;

    @Before
    public void setUp() {
        createTableColumns = new HashSet<>();
        createTableColumns.add("id");
        createTableColumns.add("series");
        createTableColumns.add("symbol");
        createTableColumns.add("trade_date");
        createTableColumns.add("close_price");
        createTableColumns.add("close_price as market_value"); // name of another column data-type copied over

        insertTableColumns = new HashSet<>();
        insertTableColumns.add("id");
        insertTableColumns.add("series");
        insertTableColumns.add("symbol");
        insertTableColumns.add("trade_date");
        insertTableColumns.add("close_price");
        insertTableColumns.add("market_value"); /// additional columns with different name

        // records

        records = new ArrayList<>();
        Map<String, Object> d1 = new HashMap<>();
        d1.put("id", 323);
        d1.put("series", "TY");
        d1.put("symbol", "IAM-SYMBOL");
        d1.put("trade_date", new Date());
        d1.put("close_price", 5.67);
        d1.put("market_value", 59.67);

        Map<String, Object> d2 = new HashMap<>();
        d2.put("id", 324);
        d2.put("series", "4rY");
        d2.put("symbol", "IARTFM-SYMBOL");
        d2.put("trade_date", new Date());
        d1.put("market_value", 15.67);

        records.add(d1);
        records.add(d2);
    }

    @After
    public void tearDown() {
        createTableColumns = null;
        records = null;
    }

    @Test
    public void testCreateTempStageTable() {
        Set<String> columnNames = new HashSet<>();
        columnNames.add("sector_id");
        columnNames.add("id");
        columnNames.add("industry_id");
        String onlyVARCHARColumnInTable = "sector_name_major";
        columnNames.add(onlyVARCHARColumnInTable);
        // Create temp table.
        String fullyQualifiedParentTableName = "reference.sector";
        String tempTableName = stagingUtilDao.createTempStageTable(columnNames, fullyQualifiedParentTableName);
        // Test that table was created and had the column with type VARCHAR
        Set<String> insertColumnNames = new HashSet<>(Arrays.asList(onlyVARCHARColumnInTable));
        Assert.assertTrue(databaseMetadataService.determineStringTypeColumns(tempTableName, insertColumnNames, true)
                .iterator().next() == true);
        // Drop the table post testing.
        stagingUtilDao.dropTempTable(tempTableName);
    }

    @Test
    public void testStageRecords() {
        Set<String> createTableColName = new HashSet<>();
        createTableColName.add("sector_id");
        createTableColName.add("industry_id");
        String onlyVARCHARColumnInTable = "sector_name_major";
        createTableColName.add(onlyVARCHARColumnInTable);
        // Create temp table.
        String fullyQualifiedParentTableName = "reference.sector";
        List<Map<String, Object>> dataRecords = new ArrayList<>();
        Map<String, Object> record = new HashMap<>();
        record.put("sector_id", 1);
        record.put(onlyVARCHARColumnInTable, "sector-name");
        record.put("industry_id", 2);
        dataRecords.add(record);
        dataRecords.add(record);
        dataRecords.add(record);

        Set<String> jsobInsertColName = null;
        Set<String> insertColName = new HashSet<>();
        insertColName.addAll(createTableColName);
        String tempTableName = stagingUtilDao.dummyStageRecords(createTableColName, insertColName, jsobInsertColName,
                fullyQualifiedParentTableName, dataRecords);
        // Test that table was created and had the column with type VARCHAR
        List<String> insertColumnNames = Arrays.asList(onlyVARCHARColumnInTable);
        // Assert.assertTrue(databaseMetadataService.determineStringTypeColumns(tempTableName, insertColumnNames, true)
        // .iterator().next() == true);
        // Drop the table post testing.
        // stagingUtilDao.dropTempTable(tempTableName);
    }

    @Test
    public void testCreateTempStageTable1() {
        String tempTable = stagingUtilDao.createTempStageTable(createTableColumns, "sec.nse_historical_price_data");
        Assert.assertTrue("Was able to create stage table" + tempTable, tempTable.contains("nse_historical_price"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDummyStageRecordsWithEmptyRecord() {
        String tempTableName = stagingUtilDao.dummyStageRecords(createTableColumns, insertTableColumns, null,
                "sec.nse_historical_price_data", Collections.emptyList());
    }

    @Test
    public void testDummyStageRecordsWithRecord() {
        String tempTableName = stagingUtilDao.dummyStageRecords(createTableColumns, insertTableColumns, null,
                "sec.nse_historical_price_data", records);
        System.out.println(stagingUtilDao.getData(createTableColumns, tempTableName));
    }
}
