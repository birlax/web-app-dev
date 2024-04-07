
package com.birlax.feedcapture.etlCommonUtils.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordFieldConfig {

  private int index;

  private String fieldName;

  private FieldDataType fieldDataType;

  private String validationRegex;

  // Used for date parsing.
  private String specialParsingFormat;
}
