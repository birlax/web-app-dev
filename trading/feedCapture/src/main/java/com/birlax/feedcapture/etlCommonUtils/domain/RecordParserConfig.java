/** */
package com.birlax.feedcapture.etlCommonUtils.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@Builder
@Getter
public class RecordParserConfig {

  @NonNull public String parserGeneratedUniqueRecordIdFieldName;

  @Builder.Default public boolean ignoreParserExceptions = false;

  @Builder.Default public List<RecordFieldConfig> recordsFields = new ArrayList<>();
}
