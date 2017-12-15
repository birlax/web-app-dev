package com.birlax.genericrulesystem;

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
				RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_RANGE, (byte) 2);

		RuleValue priceRule = new RuleValue("-2:4;-46:9", priceRuleCriteria);

		RuleValue volumeRule = new RuleValue("700:9000", volumeRuleCriteria);

		Map<RuleCriteria, RuleValue> rvs = new HashMap<>();
		rvs.put(priceRuleCriteria, priceRule);
		rvs.put(volumeRuleCriteria, volumeRule);

		Rule rule = new Rule("basic rule", "rule-id-23", rvs);

		Map<String, Object> inputs = new HashMap<>();
		inputs.put("price", 2.9);
		inputs.put("volume", 700);

		Assert.assertEquals("rule-id-23", RuleEvaluator.evaluate(inputs, rule));

		inputs.put("price", 20.9);
		inputs.put("volume", 700);

		Assert.assertEquals(RuleEvaluator.NO_MATCHING_RULES, RuleEvaluator.evaluate(inputs, rule));
	}
}
