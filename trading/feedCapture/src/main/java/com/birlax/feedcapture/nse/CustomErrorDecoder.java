package com.birlax.feedcapture.nse;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.coyote.BadRequestException;

public class CustomErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {

    if (response.status() == 400) {
      return new BadRequestException();
    }
    return new Exception("Generic error");
  }
}
