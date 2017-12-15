package com.birlax.genericrulesystem;

public interface RuleValueAttributes {

  public enum DataType implements RuleValueAttributes {
    STRING, DATE, INTEGER, DOUBLE, REGEX;
  }

  public enum SpecialValued implements RuleValueAttributes {
    SIMPLE_SINGLE_VALUE, SIMPLE_SINGLE_RANGE, LIST_OF_VALUES, LIST_OF_RANGES
  }

}
