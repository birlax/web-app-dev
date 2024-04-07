package com.birlax.indiantrader;

import com.birlax.dbCommonUtils.spring.DbCommonUtilsApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.birlax.indiantrader",
         "com.birlax.feedcapture",
        "com.birlax.dbCommonUtils",
})
public class IndianTraderAppContext {

    DbCommonUtilsApplicationContext dbCommonUtilsApplicationContext;

    /**
     * @return the dbCommonUtilsApplicationContext
     */
    public DbCommonUtilsApplicationContext getDbCommonUtilsApplicationContext() {
        return this.dbCommonUtilsApplicationContext;
    }

}
