package com.birlax.feedcapture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication(scanBasePackages = {"com.birlax.feedcapture"})
public class FeedCaptureApplication {

  public String applicationContext() {
    return "com.birlax.feedcapture.";
  }

  public static void main(String[] args) {
    SpringApplication.run(FeedCaptureApplication.class, args);
  }
}
