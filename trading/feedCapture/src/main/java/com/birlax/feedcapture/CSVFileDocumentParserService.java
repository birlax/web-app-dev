/** */
package com.birlax.feedcapture;

import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import com.birlax.feedcapture.etlCommonUtils.domain.FieldDataType;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordParserConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CSVFileDocumentParserService {

  public List<Map<Integer, String>> parser(String fileNameOrData, DataSourceType dataSourceType)
      throws IOException {

    if (DataSourceType.FILE == dataSourceType) {
      Reader csvData = new FileReader(fileNameOrData);
      CSVParser csvParser = CSVParser.parse(csvData, CSVFormat.DEFAULT);
      return parse(csvParser);
    }

    if (DataSourceType.STRING == dataSourceType) {
      CSVParser csvParser = CSVParser.parse(fileNameOrData, CSVFormat.DEFAULT);
      return parse(csvParser);
    }

    return List.of();
  }

  private List<Map<Integer, String>> parse(CSVParser csvParser) {

    List<Map<Integer, String>> parserData = new ArrayList<>();

    int rowIdx = 0;
    try {
      for (CSVRecord csvRecord : csvParser) {
        Map<Integer, String> data = new HashMap<>();
        Iterator<String> csvIterator = csvRecord.iterator();
        rowIdx = 0;
        while (csvIterator.hasNext()) {
          String sp = csvIterator.next();
          sp = sp == null ? sp : sp.trim();
          data.put(rowIdx++, sp);
        }
        parserData.add(data);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed as records : " + rowIdx, e);
    }
    return parserData;
  }

  public static void main(String[] args) throws IOException {

    CSVFileDocumentParserService csvFileDocumentParserService = new CSVFileDocumentParserService();

    List<Map<Integer, String>> indexToDataMap =
        csvFileDocumentParserService.parser(
            "/home/birlax/Desktop/Downloads_Win/EQUITY_L.csv", DataSourceType.FILE);

    List<RecordFieldConfig> recordsFields = new ArrayList<>();

    String specialParsingFormat = "dd-MMM-yyyy";
    String validationRegex = "";

    RecordFieldConfig rc0 =
        new RecordFieldConfig(0, "symbol", FieldDataType.STRING, validationRegex, "");
    RecordFieldConfig rc1 =
        new RecordFieldConfig(1, "shortName", FieldDataType.STRING, validationRegex, "");
    RecordFieldConfig rc2 =
        new RecordFieldConfig(
            3, "listingDate", FieldDataType.DATE, validationRegex, specialParsingFormat);
    RecordFieldConfig rc3 =
        new RecordFieldConfig(6, "isin", FieldDataType.STRING, validationRegex, "");

    recordsFields.addAll(Arrays.asList(rc0, rc1, rc2));
    String parserGeneratedUniqueRecordIdFieldName = "rawRecordId";

    boolean ignoreParserExceptions = false;

    RecordParserConfig recordParserConfig =
        RecordParserConfig.builder()
            .parserGeneratedUniqueRecordIdFieldName(parserGeneratedUniqueRecordIdFieldName)
            .recordsFields(recordsFields)
            .ignoreParserExceptions(ignoreParserExceptions)
            .build();

    List<Map<String, Object>> data =
        RecordParserExtractionService.rawParser(indexToDataMap, recordParserConfig);

    System.out.println(data);
  }
}
