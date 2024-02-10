package com.birlax.feedcapture.etlCommonUtils;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.feedcapture.Common;
import com.birlax.feedcapture.JsonUtils;
import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import com.birlax.feedcapture.etlCommonUtils.domain.FieldDataType;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordParserConfig;
import com.birlax.feedcapture.CSVFileDocumentParserService;
import com.birlax.feedcapture.HtmlDocumentParserService;
import com.birlax.feedcapture.RecordParserExtractionService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.birlax.feedcapture.nse.NSEFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.checkerframework.checker.units.qual.N;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class NSE24MonthHistoricalPriceVolumeDeliverySource {

  public RecordParserConfig getParserConfig() {

    String configData = Common.readFileToString("classpath:parserconfig/config.json");

    final RecordParserConfig recordParserConfig =
        JsonUtils.convertToClass(configData, RecordParserConfig.class);
    System.out.println(recordParserConfig);

    return recordParserConfig;
  }
}
