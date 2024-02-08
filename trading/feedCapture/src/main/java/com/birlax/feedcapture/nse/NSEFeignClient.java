package com.birlax.feedcapture.nse;

import com.fasterxml.jackson.databind.JsonNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(
    name = "NseDataSync",
    url = "https://www.nseindia.com/api/historical/cm/equity",
    configuration = FeignClientConfiguration.class)
@Service
public interface NSEFeignClient {

  @Headers({
    "authority: www.nseindia.com",
    "accept: */*",
    "accept-language: en-GB,en-US;q=0.9,en;q=0.8",
    "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
  })
  @RequestMapping(method = RequestMethod.GET, consumes = "application/json")
  String fetchData(
      @RequestParam("symbol") String symbol,
      @RequestParam("from") String from,
      @RequestParam("to") String to);
}
