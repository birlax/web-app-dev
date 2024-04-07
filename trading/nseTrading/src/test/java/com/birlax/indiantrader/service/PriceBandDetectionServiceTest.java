
package com.birlax.indiantrader.service;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.capitalmarket.PriceBandDetectionService;
import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class PriceBandDetectionServiceTest extends BaseIntegerationTest {

    @Autowired
    private PriceBandDetectionService priceBandDetectionService;

    @Test
    public void test() throws IOException, InstantiationException, IllegalAccessException {

        String securitySymbol = "HEG";
        LocalDate startDate = BirlaxUtil.getDateFromString("20180510");
        LocalDate endDate = BirlaxUtil.getDateFromString("20180525");
        priceBandDetectionService.detectPriceBand(securitySymbol, startDate, endDate);
    }
}
