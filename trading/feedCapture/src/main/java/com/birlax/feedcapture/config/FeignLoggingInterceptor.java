package com.birlax.feedcapture.config;

import feign.InvocationContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.ResponseInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("customRequestInterceptor")
public class FeignLoggingInterceptor implements RequestInterceptor {
  @Override
  public void apply(RequestTemplate requestTemplate) {

    log.info("Method : [{}], Url : [{}]", requestTemplate.method(), requestTemplate.url());
    log.info("Headers : [{}]", requestTemplate.headers());
    log.info("Body : [{}]", requestTemplate.bodyTemplate());
  }
}
