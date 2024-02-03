package com.birlax.feedcapture;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.birlax.etlCommonUtils")
public class FeedCaptureApplication {

  public String applicationContext() {
    return "com.com.etlCommonUtils.";
  }
}
