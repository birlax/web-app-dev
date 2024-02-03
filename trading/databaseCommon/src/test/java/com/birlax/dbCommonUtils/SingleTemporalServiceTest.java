/** */
package com.birlax.dbCommonUtils;

import com.birlax.dbCommonUtils.industryClassification.IndustrySector;
import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.BirlaxUtil;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.*;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "data-setup-native.sql")
public class SingleTemporalServiceTest extends BaseIntegrationTest {

  @Inject private SingleTemporalServiceImpl singleTemporalService;

  @Test
  public void testInsertRecords() {

    IndustrySector s1 =
        IndustrySector.builder().sectorId(7).sectorNameMajor("TEST").industryId(1).build();

    IndustrySector s2 =
        IndustrySector.builder().sectorId(8).sectorNameMajor("TEST-2").industryId(2).build();

    List<IndustrySector> records = new ArrayList<>();
    records.add(s1);
    records.add(s2);

    System.out.println(singleTemporalService.insertRecords(records));
  }

  @Test
  public void testDeleteRecords() {
    List<IndustrySector> records = new ArrayList<>();

    IndustrySector s1 =
        IndustrySector.builder().sectorId(7).sectorNameMajor("TEST").industryId(1).build();

    IndustrySector s2 =
        IndustrySector.builder().sectorId(8).sectorNameMajor("TEST-2").industryId(2).build();
    records.add(s1);
    records.add(s2);
    System.out.println(singleTemporalService.deleteRecords(records));
  }

  @Test
  public void testSearchRecords() {
    List<IndustrySector> records = new ArrayList<>();
    IndustrySector s1 = IndustrySector.builder().sectorNameMinor("A").build();

    IndustrySector s2 = IndustrySector.builder().sectorNameMinor("B").build();

    records.add(s1);
    records.add(s2);
    Set<String> column = new HashSet<>();
    column.add("sector_name_minor");
    System.out.println(singleTemporalService.searchRecords(records, column));
  }

  @Test
  public void testSearchRecordsByDateRange() {
    List<IndustrySector> records = new ArrayList<>();
    IndustrySector s = IndustrySector.builder().spn(156).build();
    records.add(s);
    Set<String> searchByColumns = new HashSet<>();
    searchByColumns.add("spn");

    Set<String> retrieveColumns = new HashSet<>();
    retrieveColumns.add("close_price");
    retrieveColumns.add("open_price");

    LocalDate startDate = BirlaxUtil.getDateFromString("20180320");
    LocalDate endDate = BirlaxUtil.getDateFromString("20180323");

    String effectiveDateColName = "trade_date";
    System.out.println(
        singleTemporalService.searchRecordsForDateRange(
            records, searchByColumns, retrieveColumns, effectiveDateColName, startDate, endDate));
  }
}
