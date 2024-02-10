/** */
package com.birlax.feedcapture.etlCommonUtils.domain;

import com.birlax.feedcapture.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
//@Builder
public class RecordFieldConfig {

  private int index;

  private String fieldName;

  private FieldDataType fieldDataType;

  private String validationRegex;

  // Used for date parsing.
  private String specialParsingFormat;
}
