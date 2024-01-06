package com.birlax.indiantrader.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.birlax.indiantrader")
public class IndianTraderDataAccessContext {

}
