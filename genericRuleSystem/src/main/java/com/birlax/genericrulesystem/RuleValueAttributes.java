package com.birlax.genericrulesystem;

/** help
 * 
 * @author birlax */
public interface RuleValueAttributes {

    public enum DataType implements RuleValueAttributes {
        STRING, DATE, INTEGER, DOUBLE, REGEX;
    }

    public enum SpecialValued implements RuleValueAttributes {
        SIMPLE_SINGLE_VALUE, LIST_OF_VALUES, LIST_OF_RANGES
    }

}
