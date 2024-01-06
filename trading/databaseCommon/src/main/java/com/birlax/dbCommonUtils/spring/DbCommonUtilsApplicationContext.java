package com.birlax.dbCommonUtils.spring;


import jakarta.inject.Inject;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.birlax.dbCommonUtils")
@EnableTransactionManagement
public class DbCommonUtilsApplicationContext {

    @Inject
    private DataAccessContext dataAccessContext;

    public static final String applicationContext() {
        return "com.birlax.dbCommonUtils.";
    }

}
