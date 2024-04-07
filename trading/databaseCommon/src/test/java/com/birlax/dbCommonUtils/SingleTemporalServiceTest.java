/**
 *
 */
package com.birlax.dbCommonUtils;

import com.birlax.dbCommonUtils.industryClassification.IndustrySector;
import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.BirlaxUtil;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.*;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "data-setup-native.sql")
public class SingleTemporalServiceTest extends BaseIntegrationTest {

    @Inject
    private SingleTemporalServiceImpl singleTemporalService;

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
        IndustrySector s1 = IndustrySector.builder().sectorNameMinor("Tea").build();
        IndustrySector s2 = IndustrySector.builder().sectorNameMinor("Coffee").build();

        Set<String> column = new HashSet<>();
        column.add("sector_name_minor");
        List<Map<String, Object>> data = singleTemporalService.searchRecords(List.of(s1, s2), column);
        System.out.println(data);
        Assertions.assertEquals(4, data.size());
    }

    @Test
    public void testSearchRecordsByDateRange() {

        IndustrySector s = IndustrySector.builder().spn(124).build();

        Set<String> searchByColumns = new HashSet<>();
        searchByColumns.add("spn");

        List<IndustrySector> records = List.of(s);

        Set<String> retrieveColumns = Set.of("sector_name_major", "sector_name_minor", "validity_begin", "validity_end");

        LocalDate startDate = BirlaxUtil.getDateFromString("20180320");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180323");

        String effectiveDateColName = "validity_begin";
        System.out.println(
                singleTemporalService.searchRecordsForDateRange(
                        records, searchByColumns, retrieveColumns, effectiveDateColName, startDate, endDate));
    }
}
