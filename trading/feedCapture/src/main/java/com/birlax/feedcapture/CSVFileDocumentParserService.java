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

      CSVFormat format = CSVFormat.POSTGRESQL_CSV;
      CSVParser csvParser = CSVParser.parse(fileNameOrData, format);
      return parse(csvParser);
    }

    return List.of();
  }

  private List<Map<Integer, String>> parse(CSVParser csvParser) {

    List<Map<Integer, String>> parserData = new ArrayList<>();

    int rowIdx = 0;

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

    return parserData;
  }
}
