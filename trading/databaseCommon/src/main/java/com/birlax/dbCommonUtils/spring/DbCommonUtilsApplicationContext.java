package com.birlax.dbCommonUtils.spring;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.birlax.dbCommonUtils")
@EnableTransactionManagement
public class DbCommonUtilsApplicationContext {

    private DataAccessContext dataAccessContext;

    public static final String applicationContext() {
        return "com.birlax.dbCommonUtils.";
    }

}
