package com.birlax.feedcapture.nse;

import com.birlax.feedcapture.etlCommonUtils.NSE24MonthHistoricalPriceVolumeDeliverySource;
import jakarta.websocket.server.PathParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("books")
public class NSESyncController {

  NSE24MonthHistoricalPriceVolumeDeliverySource nse24MonthHistoricalPriceVolumeDeliverySource;

  @GetMapping(value = "/sync", produces = "application/json")
  public @ResponseBody void syncSymbol(@PathParam("nseSymbol") String nseSymbol) {

    List<Map<String, Object>> data =
        nse24MonthHistoricalPriceVolumeDeliverySource.getDataFromNSE(nseSymbol);

    System.out.println(data);
  }
}
