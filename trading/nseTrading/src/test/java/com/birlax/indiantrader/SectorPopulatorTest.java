
package com.birlax.indiantrader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.birlax.dbCommonUtils.service.SingleTemporalDAO;
import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.indiantrader.capitalmarket.Sector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SectorPopulatorTest extends BaseIntegerationTest {

    @Autowired
    private SingleTemporalServiceImpl temporalService;

    @Test
    public void test() throws IOException {

        List<Sector> sectors = List.of(
                Sector.builder()
                        .sectorId(123)
                        .industryId(23)
                        .urlId("na")
                        .sectorNameMajor("Beverages")
                        .sectorNameMinor("Tea-Coffee")
                        .subSectorName("lelo")
                        .build()
        );
        temporalService.insertRecords(sectors);
        temporalService.mergeRecords(sectors);
    }
}
