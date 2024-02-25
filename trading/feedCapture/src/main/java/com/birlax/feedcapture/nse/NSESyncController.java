package com.birlax.feedcapture.nse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/nse")
@Slf4j
public class NSESyncController {

  // Very Important :
  // https://www.nseindia.com/api/historical/securityArchives?from=11-02-2023&to=11-02-2024&symbol=INFY&dataType=priceVolumeDeliverable&series=EQ&csv=true

  @Autowired
  private NSEFeignClient nseFeignClient;

  @GetMapping(value = "/{nseSymbol}", produces = "application/json")
  public ResponseEntity<String> syncSymbol(@PathVariable("nseSymbol") String nseSymbol) {

    String msg = String.format("{data : %s, at Time : %s}", nseSymbol, LocalDateTime.now());

    log.info("Doing this now : [{}]", msg);

    String ft = nseFeignClient.fetchData(nseSymbol, "24-02-2023", "24-02-2024");

    return ResponseEntity.ok(ft);
  }
}
