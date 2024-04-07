package com.birlax.indiantrader;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.birlax.feedcapture.CSVFileDocumentParserService;
import com.birlax.feedcapture.RecordParserExtractionService;
import com.birlax.feedcapture.etlCommonUtils.domain.DataSourceType;
import com.birlax.feedcapture.etlCommonUtils.domain.FieldDataType;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.feedcapture.etlCommonUtils.domain.RecordParserConfig;
import com.birlax.indiantrader.capitalmarket.Security;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SecurityPopulatorTest extends BaseIntegerationTest {

    @Autowired
    private SingleTemporalServiceImpl temporalService;

    @Autowired
    private CSVFileDocumentParserService csvFileDocumentParserService;

    @Autowired
    private RecordParserExtractionService recordParserExtractionService;

    @Test
    public void test() throws IOException, InstantiationException, IllegalAccessException {

        ClassLoader classLoader = getClass().getClassLoader();
        String resourceName = "LDE_EQUITIES_LAST_1_MONTH.csv";
        File file = new File(classLoader.getResource(resourceName).getFile());
        String absolutePath = file.getAbsolutePath();

        List<Map<Integer, String>> rawData = csvFileDocumentParserService.parser(absolutePath , DataSourceType.FILE);

        List<Map<String, Object>> data = recordParserExtractionService.rawParser(rawData, getParserConfig());

        List<Security> securities = new ArrayList<>();
        int spn = 1649;
        for (Map<String, Object> map : data) {
            map.put("spn", ++spn + "");
            securities.add(ReflectionHelper.getDomainObject(map, Security.class));
        }
        temporalService.mergeRecords(securities);
    }

    private RecordParserConfig getParserConfig() {

        List<RecordFieldConfig> recordsFields = new ArrayList<>();

        String validationRegex = "";

        recordsFields.add(new RecordFieldConfig(0, "symbol", FieldDataType.STRING, validationRegex, ""));
        recordsFields.add(new RecordFieldConfig(2, "short_name", FieldDataType.STRING, validationRegex, ""));
        recordsFields.add(new RecordFieldConfig(1, "isin", FieldDataType.STRING, validationRegex, ""));

        String parserGeneratedUniqueRecordIdFieldName = "spn";

        boolean ignoreParserExceptions = false;

        return  new RecordParserConfig(parserGeneratedUniqueRecordIdFieldName,
                ignoreParserExceptions, recordsFields);
    }
}
