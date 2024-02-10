/** */
package com.birlax.feedcapture.etlCommonUtils.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@Getter
public class RecordParserConfig {

  public String parserGeneratedUniqueRecordIdFieldName;

  public boolean ignoreParserExceptions = false;

  public List<RecordFieldConfig> recordsFields = new ArrayList<>();
}
