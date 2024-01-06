package com.birlax.dbCommonUtils.spring;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.birlax.dbCommonUtils")
@EnableTransactionManagement
public class DbCommonUtilsApplicationContext {

    public static final String applicationContext() {
        return "com.birlax.dbCommonUtils.";
    }

    @Inject
    private DataAccessContext dataAccessContext;

}
