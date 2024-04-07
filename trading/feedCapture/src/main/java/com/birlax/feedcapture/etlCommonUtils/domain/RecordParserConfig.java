
package com.birlax.feedcapture.etlCommonUtils.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordParserConfig {

  public String parserGeneratedUniqueRecordIdFieldName;

  public boolean ignoreParserExceptions = false;

  public List<RecordFieldConfig> recordsFields = new ArrayList<>();
}
