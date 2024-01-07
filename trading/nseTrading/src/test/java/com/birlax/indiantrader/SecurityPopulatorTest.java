package com.birlax.indiantrader;

import com.birlax.dbCommonUtils.service.impl.SingleTemporalServiceImpl;
import com.birlax.dbCommonUtils.util.ReflectionHelper;
import com.birlax.etlCommonUtils.domain.DataSourceType;
import com.birlax.etlCommonUtils.domain.FieldDataType;
import com.birlax.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.etlCommonUtils.domain.RecordParserConfig;
import com.birlax.etlCommonUtils.parser.RecordParserExtractionService;
import com.birlax.etlCommonUtils.util.CSVFileDocumentHelper;
import com.birlax.indiantrader.domain.Security;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class SecurityPopulatorTest extends BaseIntegerationTest {

    private final String baseFileLocation = "/home/birlax/Desktop/Downloads_Win/nseData/2018";
    @Inject
    private SingleTemporalServiceImpl temporalService;

    @Test
    public void test() throws IOException, InstantiationException, IllegalAccessException {

        String fileName = "/LDE_EQUITIES_LAST_1_MONTH.csv";
        List<Map<Integer, String>> rawData = CSVFileDocumentHelper.parser(baseFileLocation + fileName,
                DataSourceType.FILE);

        List<Map<String, Object>> data = RecordParserExtractionService.rawParser(rawData, getParserConfig());

        List<Security> securities = new ArrayList<>();
        int spn = 1649;
        for (Map<String, Object> map : data) {
            map.put("spn", ++spn);
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

        RecordParserConfig recordParserConfig = new RecordParserConfig(parserGeneratedUniqueRecordIdFieldName,
                recordsFields, ignoreParserExceptions);
        return recordParserConfig;
    }
}
