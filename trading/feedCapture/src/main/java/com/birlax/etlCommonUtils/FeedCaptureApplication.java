package com.birlax.etlCommonUtils;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.birlax.etlCommonUtils")
public class FeedCaptureApplication {

  public static final String applicationContext() {
    return "com.com.etlCommonUtils.";
  }
}
