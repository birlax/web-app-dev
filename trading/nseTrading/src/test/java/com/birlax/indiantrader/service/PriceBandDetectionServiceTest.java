/**
 *
 */
package com.birlax.indiantrader.service;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;


public class PriceBandDetectionServiceTest extends BaseIntegerationTest {

    @Inject
    private PriceBandDetectionService priceBandDetectionService;

    @Test
    public void test() throws IOException, InstantiationException, IllegalAccessException {

        String securitySymbol = "HEG";
        LocalDate startDate = BirlaxUtil.getDateFromString("20180510");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180525");
        priceBandDetectionService.detectPriceBand(securitySymbol, startDate, endDate);
    }
}
