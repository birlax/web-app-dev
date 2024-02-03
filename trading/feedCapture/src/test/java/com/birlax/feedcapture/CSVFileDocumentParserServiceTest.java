package com.birlax.feedcapture;

import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import com.birlax.feedcapture.etlCommonUtils.domain.FieldDataType;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordParserConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CSVFileDocumentParserServiceTest extends BaseIntegrationTest {

  @Autowired CSVFileDocumentParserService csvFileDocumentParserService;

  @Test
  void parser_from_String() throws IOException {

    String dataInput = """
      symbole,closePrice,open_price
      TCS,3987.23,45900
    """;
    List<Map<Integer, String>> data =
        csvFileDocumentParserService.parser(dataInput, DataSourceType.STRING);

    Assertions.assertEquals("closePrice", data.get(0).get(1));
    Assertions.assertEquals("45900", data.get(1).get(2));
  }

  @Test
  void parser_from_File() throws IOException {

    final String fileName = "src/test/resources/nse-nifty-50-top-10.txt";

    List<Map<Integer, String>> indexToDataMap =
        csvFileDocumentParserService.parser(fileName, DataSourceType.FILE);

    String validationRegex = "";

    RecordFieldConfig rc0 =
        new RecordFieldConfig(0, "symbol", FieldDataType.STRING, validationRegex, "");
    RecordFieldConfig rc1 =
        new RecordFieldConfig(1, "Open", FieldDataType.STRING, validationRegex, "");
    RecordFieldConfig rc2 =
        new RecordFieldConfig(2, "High", FieldDataType.DOUBLE, validationRegex, "");
    RecordFieldConfig rc3 =
        new RecordFieldConfig(3, "Low", FieldDataType.STRING, validationRegex, "");

    List<RecordFieldConfig> recordsFields = new ArrayList<>(Arrays.asList(rc0, rc1, rc2, rc3));
    String parserGeneratedUniqueRecordIdFieldName = "id";

    boolean ignoreParserExceptions = false;

    RecordParserConfig recordParserConfig =
        RecordParserConfig.builder()
            .parserGeneratedUniqueRecordIdFieldName(parserGeneratedUniqueRecordIdFieldName)
            .recordsFields(recordsFields)
            .ignoreParserExceptions(ignoreParserExceptions)
            .build();

    List<Map<String, Object>> data =
        RecordParserExtractionService.rawParser(indexToDataMap, recordParserConfig);

    Assertions.assertEquals(572.4, data.get(1).get("High"));
    Assertions.assertEquals("BPCL", data.get(1).get("symbol"));
  }
}
