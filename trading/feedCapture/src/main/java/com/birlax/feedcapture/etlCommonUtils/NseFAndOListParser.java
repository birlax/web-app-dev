package com.birlax.feedcapture.etlCommonUtils;

import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import com.birlax.feedcapture.etlCommonUtils.domain.FieldDataType;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordParserConfig;
import com.birlax.feedcapture.CSVFileDocumentParserService;
import com.birlax.feedcapture.RecordParserExtractionService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NseFAndOListParser {

  private CSVFileDocumentParserService csvFileDocumentParserService;

  public List<Map<String, Object>> getDataFromCSVFileNSEDownloaded(String fileName)
      throws IOException {

    List<Map<Integer, String>> rawData =
        csvFileDocumentParserService.parser(fileName, DataSourceType.FILE);

    List<Map<Integer, String>> recordsOfInterest = new ArrayList<>();
    boolean firstIndexAssets = true;
    for (Map<Integer, String> record : rawData) {
      if ("Symbol".equals(record.get(1).trim())) {
        firstIndexAssets = false;
      }
      if (record.get(1).compareToIgnoreCase("SYMBOL") == 0) {
        continue;
      }
      if (firstIndexAssets) {
        record.put(-1, "INDEX");
      } else {
        record.put(-1, "STOCK");
      }
      recordsOfInterest.add(record);
    }
    return RecordParserExtractionService.rawParser(recordsOfInterest, getParserConfig());
  }

  private RecordParserConfig getParserConfig() {

    List<RecordFieldConfig> recordsFields = new ArrayList<>();
    String validationRegex = "";

    recordsFields.add(
        new RecordFieldConfig(0, "underlying_name", FieldDataType.STRING, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(1, "underlying_symbol", FieldDataType.STRING, validationRegex, ""));
    recordsFields.add(
        new RecordFieldConfig(-1, "asset_type", FieldDataType.STRING, validationRegex, ""));

    String parserGeneratedUniqueRecordIdFieldName = "spn";

    boolean ignoreParserExceptions = false;

    return RecordParserConfig.builder()
        .ignoreParserExceptions(ignoreParserExceptions)
        .recordsFields(recordsFields)
        .parserGeneratedUniqueRecordIdFieldName(parserGeneratedUniqueRecordIdFieldName)
        .build();
  }
}
