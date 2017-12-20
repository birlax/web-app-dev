package com.birlax.genericrulesystem;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class RuleEvaluatorTest {

    @Test
    public void testNullValueAgainstDefinedRange() {
        Assert.assertEquals(false, RuleEvaluator.validateInputAgainstRange(2, 3, null));
    }

    @Test
    public void testDefinedValueAgainstNullRange() {
        Assert.assertEquals(false, RuleEvaluator.validateInputAgainstRange(null, null, 2));
    }

    @Test
    public void testNullValueAgainstNullRange() {
        Assert.assertEquals(false, RuleEvaluator.validateInputAgainstRange(null, null, null));
    }

    @Test
    public void testDefinedValueAgainstLowerBoundRange() {
        Assert.assertEquals(true, RuleEvaluator.validateInputAgainstRange(1, null, 2));
    }

    @Test
    public void testDefinedValueAgainstLowerEqualBelowBoundRange() {
        Assert.assertEquals(true, RuleEvaluator.validateInputAgainstRange(22, null, 22));
    }

    @Test
    public void testDefinedValueAgainstLowerBelowBoundRange() {
        Assert.assertEquals(false, RuleEvaluator.validateInputAgainstRange(13, null, 2));
    }

    @Test
    public void testDefinedValueAgainstUpperBoundRange() {
        Assert.assertEquals(true, RuleEvaluator.validateInputAgainstRange(null, 34, 2));
    }

    @Test
    public void testDefinedValueAgainstUpperEqualBoundRange() {
        Assert.assertEquals(true, RuleEvaluator.validateInputAgainstRange(null, 34, 34));
    }

    @Test
    public void testDefinedValueAgainstUpperOverBoundRange() {
        Assert.assertEquals(false, RuleEvaluator.validateInputAgainstRange(null, 34, 38));
    }

    @Test
    public void testDefinedValueAgainstUpperOverWithLeftBoundRange() {
        Assert.assertEquals(false, RuleEvaluator.validateInputAgainstRange(6, 34.0, 34.78));
    }

    @Test
    public void testDefinedValueAgainstUpperOverWithLeftOKBoundRange() {
        Assert.assertEquals(true, RuleEvaluator.validateInputAgainstRange(6, 340.0, 304.78));
    }

    @Test
    public void testDefinedValueAgainstBothUpperAndLowerBoundRange() {
        Assert.assertEquals(true, RuleEvaluator.validateInputAgainstRange(2, 34, 6));
    }

    @Test
    public void testRuleEvaluation() {

        RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 2);

        RuleValue priceRule = new RuleValue("-2:4;-46:9", priceRuleCriteria);

        RuleValue volumeRule = new RuleValue("700:9000", volumeRuleCriteria);

        Map<RuleCriteria, RuleValue> rvs = new HashMap<>();
        rvs.put(priceRuleCriteria, priceRule);
        rvs.put(volumeRuleCriteria, volumeRule);

        Rule rule = new Rule("rule-id-23", rvs);

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("price", 2.9);
        inputs.put("volume", 700);

        Assert.assertEquals("rule-id-23", RuleEvaluator.evaluate(inputs, rule));

        inputs.put("price", 20.9);
        inputs.put("volume", 700);

        Assert.assertEquals(RuleEvaluator.NO_MATCHING_RULES, RuleEvaluator.evaluate(inputs, rule));
    }

    @Test
    public void testRuleEvaluationNullInRuleValueMatchesAnyinput() {

        RuleCriteria ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        RuleValue ruleVal = new RuleValue(null, ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(34.45, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        ruleVal = new RuleValue(null, ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(567, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        ruleVal = new RuleValue(null, ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate("34String45", ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DATE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        ruleVal = new RuleValue(null, ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(new Date(), ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.REGEX,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        ruleVal = new RuleValue(null, ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(null, ruleVal, ruleCriteria));
    }

    // ---------------//
    @Test
    public void testRuleEvaluationSingleValuesMatch() {

        RuleCriteria ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        RuleValue ruleVal = new RuleValue(344.5, ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(344.5, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        ruleVal = new RuleValue(3445, ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(3445, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        ruleVal = new RuleValue("34String45", ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate("34String45", ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DATE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        ruleVal = new RuleValue(new Date(), ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(new Date(), ruleVal, ruleCriteria));
    }

    @Test
    public void testRuleEvaluationListOfRangeMatch() {

        RuleCriteria ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        RuleValue ruleVal = new RuleValue("-INF:-200.567;-100.34:-9.45;-8.56:0.67;400.34:INF;89.8766778", ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(-344.5, ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate(-180.455, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(-5.55667, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(0.65, ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate(0.78, ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate(389.04, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(3009.04, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(89.8766778, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        ruleVal = new RuleValue("-INF:-200;-100:-9;-8:0;400:INF;345", ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(-344, ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate(-180, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(-5, ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate(3, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(0, ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate(210, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(400, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(403, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(345, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        ruleVal = new RuleValue("a:d;f:h;k:;E:F;G:G;I:P;W:;iam:urr", ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate("c", ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate("e", ruleVal, ruleCriteria));// W to infinity rule
        Assert.assertEquals(true, RuleEvaluator.evaluate("mnoo", ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate("Ee", ruleVal, ruleCriteria));

        Assert.assertEquals(true, RuleEvaluator.evaluate("G", ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate("O", ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate("R", ruleVal, ruleCriteria));

        Assert.assertEquals(true, RuleEvaluator.evaluate("X", ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate("ibm", ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate("eam", ruleVal, ruleCriteria));// W to infinity rule
        Assert.assertEquals(false, RuleEvaluator.evaluate("Dam", ruleVal, ruleCriteria));// W to infinity rule will not
                                                                                         // cover this

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DATE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        // ruleVal = new RuleValue("20170102:20170823;20150603:20150609", ruleCriteria);

        // Assert.assertEquals(true, RuleEvaluator.evaluate(new Date(), ruleVal, ruleCriteria));
    }

    @Test
    public void testRuleEvaluationListOfValuesMatch() {

        RuleCriteria ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

        RuleValue ruleVal = new RuleValue("-200.567;-100.34;-9.45;-8.56;0.67;400.34;89.8766778;-INF;INF", ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(-100.34, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(89.8766778, ruleVal, ruleCriteria));

        Assert.assertEquals(false, RuleEvaluator.evaluate(null, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

        ruleVal = new RuleValue("-200;-100;-9;-8;0;400;345;-INF;INF;78:", ruleCriteria);

        Assert.assertEquals(true, RuleEvaluator.evaluate(-100, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(400, ruleVal, ruleCriteria));
        Assert.assertEquals(false, RuleEvaluator.evaluate(null, ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate(345, ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.STRING,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

        ruleVal = new RuleValue("a;FG;ui:xvg78", ruleCriteria);

        Assert.assertEquals(false, RuleEvaluator.evaluate("c", ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate("ui", ruleVal, ruleCriteria));// W to infinity rule
        Assert.assertEquals(false, RuleEvaluator.evaluate("mnoo", ruleVal, ruleCriteria));
        Assert.assertEquals(true, RuleEvaluator.evaluate("FG", ruleVal, ruleCriteria));

        ruleCriteria = new RuleCriteria("ruleCriteria", RuleValueAttributes.DataType.DATE,
                RuleValueAttributes.SpecialValued.LIST_OF_VALUES, (byte) 1);

        // ruleVal = new RuleValue("20170102:20170823;20150603:20150609", ruleCriteria);

        // Assert.assertEquals(true, RuleEvaluator.evaluate(new Date(), ruleVal, ruleCriteria));
    }

}
