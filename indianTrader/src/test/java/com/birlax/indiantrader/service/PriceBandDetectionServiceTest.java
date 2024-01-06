/**
 *
 */
package com.birlax.indiantrader.service;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.junit.Test;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.indiantrader.BaseIntegerationTest;

/**
 * @author birlax
 */
public class PriceBandDetectionServiceTest extends BaseIntegerationTest {

    @Inject
    private PriceBandDetectionService priceBandDetectionService;

    @Test
    public void test() throws IOException, InstantiationException, IllegalAccessException {

        String securitySymbol = "HEG";
        Date startDate = BirlaxUtil.getDateFromString("20180510");
        Date endDate = BirlaxUtil.getDateFromString("20180525");
        priceBandDetectionService.detectPriceBand(securitySymbol, startDate, endDate);
    }
}
