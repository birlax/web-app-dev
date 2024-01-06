package com.birlax.etlCommonUtils.spring;

import javax.inject.Inject;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.birlax.etlCommonUtils")
@EnableTransactionManagement
public class ApplicationContext {

    public static final String applicationContext() {
        return "com.birlax.etlCommonUtils.";
    }

}
