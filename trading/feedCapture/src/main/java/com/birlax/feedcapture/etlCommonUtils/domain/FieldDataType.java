
package com.birlax.feedcapture.etlCommonUtils.domain;

import lombok.Getter;

@Getter
public enum FieldDataType {
  STRING(1),
  INT(2),
  LONG(3),
  DOUBLE(4),
  DATE(5),
  BIGDECIMAL(6);

  private final int dataTypeId;

  FieldDataType(int dataTypeId) {
    this.dataTypeId = dataTypeId;
  }
}
