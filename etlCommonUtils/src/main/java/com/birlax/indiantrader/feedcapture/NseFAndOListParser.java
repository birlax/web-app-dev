package com.birlax.indiantrader.feedcapture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.etlCommonUtils.domain.DataSourceType;
import com.birlax.etlCommonUtils.domain.FieldDataType;
import com.birlax.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.etlCommonUtils.domain.RecordParserConfig;
import com.birlax.etlCommonUtils.parser.RecordParserExtractionService;
import com.birlax.etlCommonUtils.util.CSVFileDocumentHelper;

public class NseFAndOListParser {

    Logger LOGGER = LoggerFactory.getLogger(NseFAndOListParser.class);

    public static List<Map<String, Object>> getDataFromCSVFileNSEDownloaded(String fileName) throws IOException {
        List<Map<Integer, String>> rawData = CSVFileDocumentHelper.parser(fileName, DataSourceType.FILE);

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

    private static RecordParserConfig getParserConfig() {

        List<RecordFieldConfig> recordsFields = new ArrayList<>();
        String validationRegex = "";

        recordsFields.add(new RecordFieldConfig(0, "underlying_name", FieldDataType.STRING, validationRegex, ""));
        recordsFields.add(new RecordFieldConfig(1, "underlying_symbol", FieldDataType.STRING, validationRegex, ""));
        recordsFields.add(new RecordFieldConfig(-1, "asset_type", FieldDataType.STRING, validationRegex, ""));

        String parserGeneratedUniqueRecordIdFieldName = "spn";

        boolean ignoreParserExceptions = false;

        RecordParserConfig recordParserConfig = new RecordParserConfig(parserGeneratedUniqueRecordIdFieldName,
                recordsFields, ignoreParserExceptions);
        return recordParserConfig;
    }

}
