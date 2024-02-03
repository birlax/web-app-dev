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
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.birlax.feedcapture.etlCommonUtils.domain.SymbolAndCount;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NSE24MonthHistoricalPriceVolumeDeliverySource {

  public static String SYMBOL_COUNT_URL = "https://nseindia.com/marketinfo/sym_map/symbolCount.jsp";
  public static String HISTORICAL_DATA_URL =
      "https://nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp";
  public static String SEGMENT_LINK_FILTER = "&segmentLink=3"; // as of now 3 for all of us
  public static String SERIES_FILTER = "&series=ALL";
  public static String DATE_RANGE_NAMED_FILTER = "&dateRange=24month";
  public static String DATE_RANGE_SPECIFIC_FILTER = "&fromDate=&toDate=";
  public static String DATA_TYPE_FILTER = "&dataType=PRICEVOLUMEDELIVERABLE";

  private CSVFileDocumentParserService csvFileDocumentParserService;

  private HtmlDocumentParserService htmlDocumentParserService;

  private RecordParserExtractionService recordParserExtractionService;

  public List<Map<String, Object>> getDataFromNSE(String nseSymbol) {

    List<Map<Integer, String>> rawData =
        csvFileDocumentParserService.parser(getHTMLContent(nseSymbol), DataSourceType.STRING);

    return recordParserExtractionService.rawParser(rawData, getParserConfig());
  }

  @SneakyThrows
  public List<Map<String, Object>> getDataFromNSEForDateRange(
      String nseSymbol, LocalDate startDate, LocalDate endDate) {

    List<Map<Integer, String>> rawData =
        csvFileDocumentParserService.parser(
            getHTMLContentForRange(
                nseSymbol,
                BirlaxUtil.getFormattedStringDate(startDate, "dd-MM-yyyy"),
                BirlaxUtil.getFormattedStringDate(endDate, "dd-MM-yyyy")),
            DataSourceType.STRING);

    return recordParserExtractionService.rawParser(rawData, getParserConfig());
  }

  public List<Map<String, Object>> getDataFromCSVFileNSEDownloaded(String fileName) {
    List<Map<Integer, String>> rawData =
        csvFileDocumentParserService.parser(fileName, DataSourceType.FILE);

    return recordParserExtractionService.rawParser(rawData, getParserConfig());
  }

  /** Returns the data from the Webpage as String. */
  @SneakyThrows
  private String getHTMLContent(String nseSymbol) {

    SymbolAndCount result = getValidateAndExecute(nseSymbol);

    String url =
        HISTORICAL_DATA_URL
            + "?symbol="
            + result.getNseSymbol()
            + SEGMENT_LINK_FILTER
            + "&symbolCount="
            + result.getSecurityCount()
            + SERIES_FILTER
            + DATE_RANGE_NAMED_FILTER
            + DATE_RANGE_SPECIFIC_FILTER
            + DATA_TYPE_FILTER;

    Document dc1 = htmlDocumentParserService.getHtmlDocument(url, DataSourceType.URL);
    return captureAndParserCSV(dc1);
  }

  @SneakyThrows
  private SymbolAndCount getValidateAndExecute(String nseSymbol) {
    if (Objects.isNull(nseSymbol)) {
      throw new IllegalArgumentException("Invalid NSE Symbol...");
    }
    nseSymbol = URLEncoder.encode(nseSymbol, Common.CHAR_ENCODING);
    Document doc =
        htmlDocumentParserService.getHtmlDocument(buildUrl(nseSymbol), DataSourceType.URL);

    // Get the symbol count it's received as HTML with value in the body. Parse it.
    String securityCount = doc.getElementsByTag("body").text();
    return SymbolAndCount.builder().securityCount(securityCount).nseSymbol(nseSymbol).build();
  }

  private String buildUrl(String nseSymbol) {
    return SYMBOL_COUNT_URL + "?symbol=" + nseSymbol;
  }

  /** Returns the data from the Webpage as String. */
  private String getHTMLContentForRange(String nseSymbol, String startDate, String endDate)
      throws IOException {

    if (nseSymbol == null || nseSymbol.isEmpty()) {
      throw new IllegalArgumentException("Invalid NSE Symbol...");
    }
    nseSymbol = URLEncoder.encode(nseSymbol, Common.CHAR_ENCODING);
    Document doc =
        htmlDocumentParserService.getHtmlDocument(buildUrl(nseSymbol), DataSourceType.URL);
    // Get the symbol count it's received as HTML with value in the body. Parse it.
    String securityCount = doc.getElementsByTag("body").text();

    String url =
        HISTORICAL_DATA_URL
            + "?symbol="
            + nseSymbol
            + SEGMENT_LINK_FILTER
            + "&symbolCount="
            + securityCount
            + SERIES_FILTER
            + "&dateRange=+"
            + "&fromDate="
            + startDate
            + "&toDate="
            + endDate
            + DATA_TYPE_FILTER;
    System.out.println(url);
    Document dc1 = htmlDocumentParserService.getHtmlDocument(url, DataSourceType.URL);
    return captureAndParserCSV(dc1);
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
