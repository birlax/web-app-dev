package com.birlax.genericrulesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RuleValueTest {

    private RuleCriteria c1, c2, c3, c4, c5, r1, r2, r3, r4, r5, l1, l2, l3, l4, l5, v1, v2, v3, v4, v5;

    @Before
    public void setUp() {

        c1 = new RuleCriteria("c1", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        c2 = new RuleCriteria("c2", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        c3 = new RuleCriteria("c3", RuleValueAttributes.DataType.DATE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        c4 = new RuleCriteria("c4", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        c5 = new RuleCriteria("c5", RuleValueAttributes.DataType.REGEX,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        // -------------------------------//

        r1 = new RuleCriteria("r1", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        r2 = new RuleCriteria("r2", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        r3 = new RuleCriteria("r3", RuleValueAttributes.DataType.DATE, RuleValueAttributes.SpecialValued.LIST_OF_RANGES,
                (byte) 1);

        r4 = new RuleCriteria("r4", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        r5 = new RuleCriteria("r5", RuleValueAttributes.DataType.REGEX,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        // -------------------------------//

        l1 = new RuleCriteria("l1", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        l2 = new RuleCriteria("l2", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        l3 = new RuleCriteria("l3", RuleValueAttributes.DataType.DATE, RuleValueAttributes.SpecialValued.LIST_OF_RANGES,
                (byte) 1);

        l4 = new RuleCriteria("l4", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        l5 = new RuleCriteria("l5", RuleValueAttributes.DataType.REGEX,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        // -------------------------------//

        v1 = new RuleCriteria("v1", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

        v2 = new RuleCriteria("v2", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

        v3 = new RuleCriteria("v3", RuleValueAttributes.DataType.DATE, RuleValueAttributes.SpecialValued.LIST_OF_VALUES,
                (byte) 1);

        v4 = new RuleCriteria("v4", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

        v5 = new RuleCriteria("v5", RuleValueAttributes.DataType.REGEX,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDataTypeForSingleValuedFields() {
        int count = 0;
        try {
            Assert.assertEquals(null, new RuleValue("23", c1));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue(23, c1));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue(23.008, c2));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue("23", c2));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue(23.008, c3));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue("23", c3));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue(23, c3));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue(23.008, c4));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue(23, c4));
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            Assert.assertEquals(null, new RuleValue(new Date(), c4));
        } catch (IllegalArgumentException e) {
            count++;
        }

        Assert.assertEquals(count, 10);
        throw new IllegalArgumentException();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIntegerSingleRangeValueNegTest() {
        RuleValue rv = new RuleValue(22, r2);
    }

    @Test
    public void testIntegerSingleRangeValueBadIncompleteInput() {
        RuleValue rv = new RuleValue("23:;:45;45:INF;-INF:45", r2);
    }

    public void testIntegerSingleRangeValueBadMissingValuesInput() {
        RuleValue rv = new RuleValue(";;;;", r2);
    }

    @Test
    public void testIntegerListRangeValueTakesAllValue() {
        RuleValue rv = new RuleValue("22:67;89:90", l2);
        Assert.assertEquals(null, rv.getRuleValue());
        Assert.assertEquals(Arrays.asList(Pair.of(22, 67), Pair.of(89, 90)), rv.getListOfRanges());
        Assert.assertEquals(null, rv.getListOfValues());
    }

    @Test
    public void testDoubleListRangeValueTakesAllPair() {
        RuleValue rv = new RuleValue("23.009:7789.99;78.78:90.99;:345.5;45.77:INF", l1);
        Assert.assertEquals(null, rv.getRuleValue());
        Assert.assertEquals(Arrays.asList(Pair.of(23.009, 7789.99), Pair.of(78.78, 90.99), Pair.of(null, 345.5),
                Pair.of(45.77, null)), rv.getListOfRanges());
        Assert.assertEquals(null, rv.getListOfValues());
    }

    @Test
    public void testIntegerListValueTakesValue() {
        RuleValue rv = new RuleValue("22;89;90", v2);
        Assert.assertEquals(null, rv.getRuleValue());
        Assert.assertEquals(null, rv.getListOfRanges());
        Assert.assertEquals(new ArrayList<Object>(Arrays.asList(22, 89, 90)), rv.getListOfValues());

    }

    @Test
    public void testDoubleListValueTakesValues() {
        RuleValue rv = new RuleValue("23.009;7789.99;78.78;90.99", v1);
        Assert.assertEquals(null, rv.getRuleValue());
        Assert.assertEquals(null, rv.getListOfRanges());
        Assert.assertEquals(Arrays.asList(23.009, 7789.99, 78.78, 90.99), rv.getListOfValues());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoubleListValueTakesValuesRangeValidations() {
        int count = 0;
        try {
            new RuleValue("34:23", v1);
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            new RuleValue("-34:-45", v1);
        } catch (IllegalArgumentException e) {
            count++;
        }
        try {
            new RuleValue("0:-56", v1);
        } catch (IllegalArgumentException e) {
            count++;
        }

        Assert.assertEquals(count, 3);
        throw new IllegalArgumentException();

    }
}
