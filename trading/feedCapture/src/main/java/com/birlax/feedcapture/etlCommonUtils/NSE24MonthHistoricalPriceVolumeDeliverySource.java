package com.birlax.feedcapture.etlCommonUtils;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.feedcapture.Common;
import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import com.birlax.feedcapture.etlCommonUtils.domain.FieldDataType;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordParserConfig;
import com.birlax.feedcapture.CSVFileDocumentParserService;
import com.birlax.feedcapture.HtmlDocumentParserService;
import com.birlax.feedcapture.RecordParserExtractionService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.birlax.feedcapture.nse.NSEFeignClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NSE24MonthHistoricalPriceVolumeDeliverySource {

  // https://www.nseindia.com/api/historical/cm/equity?symbol=BPCL&series=[%22EQ%22]&from=03-01-2024&to=03-02-2024

  @Autowired private NSEFeignClient nseFeignClient;

  // public static String NSE_BASE_URL = "https://www.nseindia.com/api/historical/cm/equity";

  public static String NSE_BASE_URL = "https://www.google.com";

  // public static String SEGMENT_LINK_FILTER = "&segmentLink=3"; // as of now 3 for all of us
  public static String SERIES_FILTER = "&series=[\"ALL\"]";
  public static String FROM_DATE = "&from=";
  public static String TO_DATE = "&to=";
  // public static String DATE_RANGE_SPECIFIC_FILTER = "&fromDate=&toDate=";
  // public static String DATA_TYPE_FILTER = "&dataType=PRICEVOLUMEDELIVERABLE";

  @Autowired private CSVFileDocumentParserService csvFileDocumentParserService;

  @Autowired private HtmlDocumentParserService htmlDocumentParserService;

  @Autowired private RecordParserExtractionService recordParserExtractionService;

  @SneakyThrows
  public List<Map<String, Object>> getDataFromNSEForDateRange(
      String nseSymbol, LocalDate startDate, LocalDate endDate) {

    List<Map<Integer, String>> rawData = List.of();
    String dt = getHTMLContentForRange(nseSymbol, startDate, endDate);

    System.out.println(dt);
    return null;
  }

  public List<Map<String, Object>> getDataFromCSVFileNSEDownloaded(String fileName) {
    List<Map<Integer, String>> rawData =
        csvFileDocumentParserService.parser(fileName, DataSourceType.FILE);

    return recordParserExtractionService.rawParser(rawData, getParserConfig());
  }

  /** Returns the data from the Webpage as String. */
  private String getHTMLContentForRange(String nseSymbol, LocalDate startDate, LocalDate endDate) {

    if (Objects.isNull(nseSymbol)) {
      throw new IllegalArgumentException("Invalid NSE Symbol...");
    }

    // log.info(url);

    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyy");

    return nseFeignClient.fetchData(
        nseSymbol, startDate.format(format), endDate.format(format));
  }

  private String captureAndParserCSV(Document dc1) {
    // Capture the hidden element which has the data.
    Element els = dc1.getElementById("csvContentDiv");
    // No trades for the day.
    if (els == null) {
      return "";
    }
    String htmlData = els.text();

    htmlData = htmlData.replaceAll(":", "\n");
    return htmlData;
  }

  private RecordParserConfig getParserConfig() {

    List<RecordFieldConfig> recordsFields = new ArrayList<>();

    String specialParsingFormat = "dd-MMM-yyyy";
    String validationRegex = "";

    recordsFields.add(
        new RecordFieldConfig(0, "symbol", FieldDataType.STRING, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(1, "series", FieldDataType.STRING, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(
            2, "trade_date", FieldDataType.DATE, validationRegex, specialParsingFormat));

    recordsFields.add(
        new RecordFieldConfig(
            3, "previous_close_price", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(4, "open_price", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(5, "high_price", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(6, "low_price", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(7, "last_price", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(8, "close_price", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(9, "average_price", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(
            10, "total_traded_quantity", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(11, "turnover", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(12, "no_of_trades", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(
            13, "deliverable_quantity", FieldDataType.DOUBLE, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(
            14, "pct_deliverable_qty_to_trade_qty", FieldDataType.DOUBLE, validationRegex, ""));

    String parserGeneratedUniqueRecordIdFieldName = "spn";

    boolean ignoreParserExceptions = false;

    return RecordParserConfig.builder()
        .ignoreParserExceptions(ignoreParserExceptions)
        .recordsFields(recordsFields)
        .parserGeneratedUniqueRecordIdFieldName(parserGeneratedUniqueRecordIdFieldName)
        .build();
  }
}
