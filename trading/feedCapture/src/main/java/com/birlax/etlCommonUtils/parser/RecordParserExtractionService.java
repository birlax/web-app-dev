/**
 *
 */
package com.birlax.etlCommonUtils.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.birlax.dbCommonUtils.util.BirlaxUtil;
import com.birlax.etlCommonUtils.domain.FieldDataType;
import com.birlax.etlCommonUtils.domain.RecordFieldConfig;
import com.birlax.etlCommonUtils.domain.RecordParserConfig;


public class RecordParserExtractionService {

    static Logger LOGGER = LoggerFactory.getLogger(RecordParserExtractionService.class);

    /**
     * Parse set of raw records against a configuration.
     *
     * @param indexToDataMap
     * @param recordParserConfig
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> rawParser(List<Map<Integer, String>> indexToDataMap,
            RecordParserConfig recordParserConfig) throws IOException {

        List<Map<String, Object>> parserData = new ArrayList<>();
        int pkId = 0;
        boolean headerDone = false;
        for (Map<Integer, String> rawRecord : indexToDataMap) {
            // TODO Cut this out ..
            // Accept, Record identification method/lambda and use that to skip header.
            // Accept, Lambda/Filter to skip records of certain types.
            if (!headerDone) {
                headerDone = true;
                continue;
            }

            Map<String, Object> dm = null;
            try {
                dm = parserSingleRecord(rawRecord, recordParserConfig);
                dm.put(recordParserConfig.getParserGeneratedUniqueRecordIdFieldName(), ++pkId);
                parserData.add(dm);
            } catch (Exception e) {
                // Increment lineNo to keep track of failing record.
                ++pkId;
                LOGGER.error("Failed to Parser Line-No : {} Record : {} ", pkId, rawRecord);
                if (!recordParserConfig.isIgnoreParserExceptions()) {
                    throw new RuntimeException("Failed to parser Line-No : " + pkId + ", Record : " + rawRecord, e);
                }
                // Log and continue processing other records.
            }
        }
        return parserData;
    }

    /**
     * @param rawRecord
     * @param recordParserConfig
     * @return
     */
    private static Map<String, Object> parserSingleRecord(Map<Integer, String> rawRecord,
            RecordParserConfig recordParserConfig) {

        Map<String, Object> dm = new HashMap<>();

        for (RecordFieldConfig ev : recordParserConfig.getRecordsFields()) {
            String rawVal = rawRecord.get(ev.getIndex());

            if (rawVal == null) {
                dm.put(ev.getFieldName(), rawVal);
            }
            rawVal = rawVal.trim();
            if (FieldDataType.STRING == ev.getFieldDataType()) {
                dm.put(ev.getFieldName(), rawVal);
            }
            if (!rawVal.isEmpty() && FieldDataType.DOUBLE == ev.getFieldDataType()) {
                double p = 0.0;
                try {
                    p = Double.parseDouble(rawVal);
                } catch (Exception e) {
                    p = 0.0;
                }
                dm.put(ev.getFieldName(), p);
            }
            if (!rawVal.isEmpty() && FieldDataType.INT == ev.getFieldDataType()) {
                dm.put(ev.getFieldName(), Integer.parseInt(rawVal));
            }
            if (!rawVal.isEmpty() && FieldDataType.LONG == ev.getFieldDataType()) {
                dm.put(ev.getFieldName(), Long.parseLong(rawVal));
            }
            // TODO Fix the date parsing thing.
            if (!rawVal.isEmpty() && FieldDataType.DATE == ev.getFieldDataType()) {
                dm.put(ev.getFieldName(), BirlaxUtil.getDateFromString(rawVal, ev.getSpecialParsingFormat()));
            }
            // TODO Add BIGDECIMAL here.
        }
        return dm;
    }
}
