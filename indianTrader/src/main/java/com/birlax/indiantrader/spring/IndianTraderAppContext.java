package com.birlax.indiantrader.spring;

import javax.inject.Inject;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.birlax.dbCommonUtils.spring.DbCommonUtilsApplicationContext;

@Configuration
@ComponentScan(basePackages = "com.birlax.indiantrader,com.birlax.dbCommonUtils")
public class IndianTraderAppContext {

    @Inject
    DbCommonUtilsApplicationContext dbCommonUtilsApplicationContext;

    /**
     * @return the dbCommonUtilsApplicationContext
     */
    public DbCommonUtilsApplicationContext getDbCommonUtilsApplicationContext() {
        return this.dbCommonUtilsApplicationContext;
    }

}
