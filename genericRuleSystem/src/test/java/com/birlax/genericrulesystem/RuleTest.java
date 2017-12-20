package com.birlax.genericrulesystem;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RuleTest {

    @Test
    public void testRuleEvalute() {

        /*
         * RuleEvaluator re = new RuleEvaluator(); Object inputValue = 5.90; Map<String, Object> map = new HashMap<>();
         * map.put("price", 5.90); map.put("volume", 5000);
         */
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

        System.out.println(rule);

    }

}
