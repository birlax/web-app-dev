package com.birlax.feedcapture.etlCommonUtils.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SymbolAndCount {

  String nseSymbol;
  String securityCount;
}
