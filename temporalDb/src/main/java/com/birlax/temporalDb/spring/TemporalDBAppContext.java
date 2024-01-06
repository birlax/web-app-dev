package com.birlax.temporalDb.spring;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(
        basePackages = "com.birlax.temporalDb")
@EnableTransactionManagement
public class TemporalDBAppContext {

    @Inject
    private DataAccessContext dataAccessContext;

    @Bean
    public Integer getValueOfA() {
        return 45;
    }

}
