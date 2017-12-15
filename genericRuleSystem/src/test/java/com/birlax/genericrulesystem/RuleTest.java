package com.birlax.genericrulesystem;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RuleTest {

	@Test
	public void testRuleEvalute() {

		/*
		 * RuleEvaluator re = new RuleEvaluator();
		 * 
		 * Object inputValue = 5.90;
		 * 
		 * Map<String, Object> map = new HashMap<>(); map.put("price", 5.90);
		 * map.put("volume", 5000);
		 */
		RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
				RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

		RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
				RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_RANGE, (byte) 2);

		// RuleValue ruleValue = new RuleValue(RuleValueAttributes.DataType.INTEGER,
		// RuleValueAttributes.SpecialValued.LIST_OF_RANGES,
		// "2:INF;-INF:45;78;4:7;7:9;9:79;4:;:8");
		// System.out.println(re.evaluate(inputValue, ruleValue));

		// RuleValue ruleValue2 = new RuleValue(RuleValueAttributes.DataType.DOUBLE,
		// RuleValueAttributes.SpecialValued.LIST_OF_RANGES,
		// "2.56:INF;-INF:45.34566;78.56;4.67988009:7.56;-7.9:-6.78;-9.002:-9.001");

		// System.out.println(re.evaluate(inputValue, ruleValue2));

		RuleValue priceRule = new RuleValue("-2:4;-46:9", priceRuleCriteria);

		RuleValue volumeRule = new RuleValue("700:9000", volumeRuleCriteria);

		Map<RuleCriteria, RuleValue> rvs = new HashMap<>();
		rvs.put(priceRuleCriteria, priceRule);
		rvs.put(volumeRuleCriteria, volumeRule);

		Rule rule = new Rule("basic rule", "rule-id-23", rvs);

		System.out.println(rule);

	}

}
