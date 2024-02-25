package com.birlax.feedcapture.config;

import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service("customErrorDecoder")
public class CustomErrorDecoder implements ErrorDecoder {

  private static final String ERROR_REASON_BAD_END_TIME =
      "End time cannot be before the account opening date";

  @Override
  public Exception decode(String methodKey, Response response) {

    log.error("Api Failed: Response [{}]", response);

    if (response.status() == 403) {
      return new IllegalStateException("Access Denied!!");
    }

    if (response.status() == 503 || response.status() == 500) {

      log.error(
          "Request failed and will be retried: status=[{}], reason=[{}], request=[{}]",
          response.status(),
          response.reason(),
          response.request());

      return new RetryableException(
          response.status(),
          response.reason(),
          response.request().httpMethod(),
          0L,
          response.request());
    }

    if (response.status() == 424) {
      try (Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {
        String responseBody = IOUtils.toString(reader);
        if (responseBody.contains(ERROR_REASON_BAD_END_TIME)) {
          return new IllegalStateException(responseBody);
        }
      } catch (IOException e) {
        log.error("Unable to parse body of the 424 response", e);
      }
    }

    return new Default().decode(methodKey, response);
  }
}
