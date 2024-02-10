package com.birlax.feedcapture.nse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/nse")
@Slf4j
public class NSESyncController {

  @GetMapping(value = "/{nseSymbol}", produces = "application/json")
  public ResponseEntity<String> syncSymbol(@PathVariable("nseSymbol") String nseSymbol) {

    String msg = String.format("{data : %s, at Time : %s}", nseSymbol, LocalDateTime.now());

    log.info("Doing this now : [{}]", msg);
    return ResponseEntity.ok(msg);
  }
}
