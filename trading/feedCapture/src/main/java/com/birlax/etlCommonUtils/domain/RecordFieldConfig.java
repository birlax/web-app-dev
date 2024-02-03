/** */
package com.birlax.etlCommonUtils.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RecordFieldConfig {

  private int index;

  private String fieldName;

  private FieldDataType fieldDataType;

  private String validationRegex;

  // Used for date parsing.
  private String specialParsingFormat;

  public RecordFieldConfig(
      int index,
      String fieldName,
      FieldDataType fieldDataType,
      String validationRegex,
      String specialParsingFormat) {
    super();
    this.index = index;
    this.fieldName = fieldName;
    this.fieldDataType = fieldDataType;
    this.validationRegex = validationRegex;
    this.specialParsingFormat = specialParsingFormat;
    if (this.fieldDataType == FieldDataType.DATE
        && (this.specialParsingFormat == null || this.specialParsingFormat.isEmpty())) {
      throw new IllegalArgumentException(
          "Special Parsing Format is Required for field type : " + this.fieldDataType);
    }
  }
}
