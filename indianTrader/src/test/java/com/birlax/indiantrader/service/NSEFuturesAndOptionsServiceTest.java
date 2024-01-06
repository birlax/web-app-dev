/**
 *
 */
package com.birlax.indiantrader.service;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Test;

import com.birlax.indiantrader.BaseIntegerationTest;

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
