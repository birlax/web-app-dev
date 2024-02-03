/**
 *
 */
package com.birlax.etlCommonUtils.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.etlCommonUtils.domain.DataSourceType;
import com.birlax.etlCommonUtils.domain.FieldDataType;
import com.birlax.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.etlCommonUtils.domain.RecordParserConfig;
import com.birlax.etlCommonUtils.parser.RecordParserExtractionService;


public class CSVFileDocumentHelper {

    static Logger LOGGER = LoggerFactory.getLogger(CSVFileDocumentHelper.class);

    /**
     * @param fileNameOrData
     * @param dataSourceType
     * @return
     * @throws IOException
     */
    public static List<Map<Integer, String>> parser(String fileNameOrData, DataSourceType dataSourceType)
            throws IOException {

        if (DataSourceType.FILE == dataSourceType) {
            Reader csvData = new FileReader(new File(fileNameOrData));
            CSVParser csvParser = CSVParser.parse(csvData, CSVFormat.DEFAULT);
            return parse(csvParser);
        }

        if (DataSourceType.STRING == dataSourceType) {
            CSVParser csvParser = CSVParser.parse(fileNameOrData, CSVFormat.DEFAULT);
            return parse(csvParser);
        }

        return null;
    }

    /**
     * @param csvParser
     * @return
     */
    private static List<Map<Integer, String>> parse(CSVParser csvParser) {

        List<Map<Integer, String>> parserData = new ArrayList<>();

        int i = 0;
        try {
            for (CSVRecord csvRecord : csvParser) {
                Map<Integer, String> data = new HashMap<>();
                Iterator<String> csvIterator = csvRecord.iterator();
                i = 0;
                while (csvIterator.hasNext()) {
                    String sp = csvIterator.next();
                    sp = sp == null ? sp : sp.trim();
                    data.put(i++, sp);
                }
                parserData.add(data);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed as records : " + i, e);
        }
        return parserData;
    }

    public static void main(String[] args) throws IOException {

        List<Map<Integer, String>> indexToDataMap = CSVFileDocumentHelper
                .parser("/home/birlax/Desktop/Downloads_Win/EQUITY_L.csv", DataSourceType.FILE);

        List<RecordFieldConfig> recordsFields = new ArrayList<>();

        String specialParsingFormat = "dd-MMM-yyyy";
        String validationRegex = "";

        RecordFieldConfig rc0 = new RecordFieldConfig(0, "symbol", FieldDataType.STRING, validationRegex, "");
        RecordFieldConfig rc1 = new RecordFieldConfig(1, "shortName", FieldDataType.STRING, validationRegex, "");
        RecordFieldConfig rc2 = new RecordFieldConfig(3, "listingDate", FieldDataType.DATE, validationRegex,
                specialParsingFormat);
        RecordFieldConfig rc3 = new RecordFieldConfig(6, "isin", FieldDataType.STRING, validationRegex, "");

        recordsFields.addAll(Arrays.asList(rc0, rc1, rc2));
        String parserGeneratedUniqueRecordIdFieldName = "rawRecordId";

        boolean ignoreParserExceptions = false;

        RecordParserConfig recordParserConfig = new RecordParserConfig(parserGeneratedUniqueRecordIdFieldName,
                recordsFields, ignoreParserExceptions);

        List<Map<String, Object>> data = RecordParserExtractionService.rawParser(indexToDataMap, recordParserConfig);
        System.out.println(data);
    }
}
