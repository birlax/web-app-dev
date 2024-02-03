package com.birlax.feedcapture;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.birlax.feedcapture")
public class FeedCaptureApplication {

  public String applicationContext() {
    return "com.birlax.feedcapture.";
  }
}
