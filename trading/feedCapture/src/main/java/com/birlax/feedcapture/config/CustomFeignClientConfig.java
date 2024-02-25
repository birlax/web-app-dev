package com.birlax.feedcapture.config;

import static feign.Logger.Level.FULL;
import static feign.Logger.Level.HEADERS;
import static java.util.concurrent.TimeUnit.SECONDS;

import feign.Feign;
import feign.Request;
import feign.Request.Options;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/** Feign configuration for interaction with external services */
@Import({FeignClientsConfiguration.class})
public class CustomFeignClientConfig {

  /**
   * Feign config requires its own separate bean listed and used otherwise it will not retry
   * requests
   */
  @Bean("customRetryer")
  public Retryer retryer() {
    return new Retryer.Default(100, SECONDS.toMillis(1), 2);
  }

  @Bean
  @Scope("prototype")
  public Feign.Builder feignBuilder(
      final Encoder encoder,
      final Decoder decoder,
      final @Qualifier("customErrorDecoder") ErrorDecoder errorDecoder,
      final @Qualifier("customRequestInterceptor") RequestInterceptor requestInterceptor,
      final @Qualifier("customRetryer") Retryer retryer) {

    Duration connectTimeoutMillis = Duration.of(5_000, ChronoUnit.MILLIS);
    Duration readTimeoutMillis = Duration.of(5_000, ChronoUnit.MILLIS);
    // Set your desired timeouts
    Options options = new Options(connectTimeoutMillis, readTimeoutMillis, true);

    return Feign.builder()
        .options(options)
        // .client(new ApacheHttpClient())
        .encoder(encoder)
        .decoder(decoder)
        .errorDecoder(errorDecoder)
        .contract(new SpringMvcContract())
        .requestInterceptor(requestInterceptor)
        .logLevel(FULL)
        .retryer(retryer);
  }
}
