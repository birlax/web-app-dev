/**
 *
 */
package com.birlax.indiantrader.service;

import jakarta.inject.Inject;
import java.io.IOException;


import com.birlax.indiantrader.BaseIntegerationTest;
import org.junit.jupiter.api.Test;

/**
 * @author birlax
 */
public class NSEFuturesAndOptionsServiceTest extends BaseIntegerationTest {

    @Inject
    private NSEFuturesAndOptionsService nseFuturesAndOptionsService;

    private final String baseFileLocation = "/home/birlax/Desktop/Downloads_Win/nseData/2018";

    @Test
    public void testSyncFuturesAndOptionsFromFile() throws IOException, InstantiationException, IllegalAccessException {

        nseFuturesAndOptionsService.syncFuturesAndOptionsFromFile(baseFileLocation + "/fo_mktlots.csv");
    }

    @Test
    public void testgetFNOSecuritiesByAssetType() throws IOException, InstantiationException, IllegalAccessException {

        System.out.println(nseFuturesAndOptionsService.getFNOSecuritiesByAssetType("STOCK"));
    }
}
