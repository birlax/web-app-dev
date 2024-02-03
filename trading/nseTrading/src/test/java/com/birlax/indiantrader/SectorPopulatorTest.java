/**
 *
 */
package com.birlax.indiantrader;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import org.junit.jupiter.api.Test;


public class SectorPopulatorTest extends BaseIntegerationTest {

    @Inject
    private SingleTemporalServiceImpl temporalService;

    @Test
    public void test() throws IOException {

        Map<String, String> parsedData = new HashMap<>();
        // List<Sector> sectors = TestData.extractSectors("/home/birlax/Desktop/Downloads_Win/sectors.html", parsedData,
        // SOURCE_TYPE.FILE);
        // temporalService.insertRecords(sectors);
        temporalService.mergeRecords(null);
    }
}
