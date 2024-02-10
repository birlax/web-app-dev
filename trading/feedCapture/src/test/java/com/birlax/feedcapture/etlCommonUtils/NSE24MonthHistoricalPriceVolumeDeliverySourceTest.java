package com.birlax.feedcapture.etlCommonUtils;

import com.birlax.feedcapture.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NSE24MonthHistoricalPriceVolumeDeliverySourceTest extends BaseIntegrationTest {

  @Autowired
  private NSE24MonthHistoricalPriceVolumeDeliverySource
      nse24MonthHistoricalPriceVolumeDeliverySource;

  @Test
  void getParserConfig() {
    nse24MonthHistoricalPriceVolumeDeliverySource.getParserConfig();
  }
}
