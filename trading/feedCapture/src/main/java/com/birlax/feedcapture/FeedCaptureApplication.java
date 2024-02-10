package com.birlax.feedcapture;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@SpringBootApplication(scanBasePackages = {"com.birlax.feedcapture"})
@EnableFeignClients(basePackages = {"com.birlax.feedcapture"})
public class FeedCaptureApplication {

  public String applicationContext() {
    return "com.birlax.feedcapture.";
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper() {
    JavaTimeModule module = new JavaTimeModule();

    return new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModule(module);
  }

  public static void main(String[] args) {
    SpringApplication.run(FeedCaptureApplication.class, args);
  }
}
