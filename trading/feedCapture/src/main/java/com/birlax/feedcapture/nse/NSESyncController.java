package com.birlax.feedcapture.nse;

import com.birlax.feedcapture.etlCommonUtils.NSE24MonthHistoricalPriceVolumeDeliverySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/nse")
public class NSESyncController {

  @Autowired
  private NSE24MonthHistoricalPriceVolumeDeliverySource
      nse24MonthHistoricalPriceVolumeDeliverySource;

  @GetMapping(value = "/{nseSymbol}", produces = "application/json")
  public @ResponseBody void syncSymbol(@PathVariable("nseSymbol") String nseSymbol) {

    LocalDate from = LocalDate.of(2024, 1, 4);
    LocalDate to = LocalDate.of(2024, 2, 4);
    List<Map<String, Object>> data =
        nse24MonthHistoricalPriceVolumeDeliverySource.getDataFromNSEForDateRange(
            nseSymbol, from, to);

    System.out.println(data);
  }
}
