
package com.birlax.indiantrader.service;

import java.io.IOException;

import com.birlax.indiantrader.BaseIntegerationTest;
import com.birlax.indiantrader.fno.NSEFuturesAndOptionsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, scripts = "/data-setup-native.sql")
public class NSEFuturesAndOptionsServiceTest extends BaseIntegerationTest {

    @Autowired
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
