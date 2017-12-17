package com.birlax.genericrulesystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class RuleSystemTest {

    @SuppressWarnings("serial")
    @Test
    public void testRuleSystemInit() {

        RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 2);

        Set<RuleCriteria> ruleCriterias = new HashSet<>(Arrays.asList(priceRuleCriteria, volumeRuleCriteria));
        String ruleSystemName = "firstEverRule";

        RuleSystem ruleSystem = new RuleSystem(ruleSystemName, ruleCriterias, "id", "ruleNameTTKHA", "myRanking");

        Set<Map<String, Object>> rules = new HashSet<>();
        rules.add(new HashMap<String, Object>() {
            {
                put("price", "-2:4;-46:5");
                put("volume", "700:9000");
                put("id", "bes price with big vol");
                put("ruleNameTTKHA", "both present");

                // put("myRanking", 34.89);
            }
        });
        rules.add(new HashMap<String, Object>() {
            {

                put("volume", "700:9000");
                put("id", "only big vol");
                put("ruleNameTTKHA", "Vol rec");

                // put("myRanking", 34.89);
            }
        });
        rules.add(new HashMap<String, Object>() {
            {

                put("price", "-2:4;-46:9");
                put("id", "only price variatio");
                put("ruleNameTTKHA", "Price rec");

                // put("myRanking", 34.89);
            }
        });

        rules.add(new HashMap<String, Object>() {
            {

                put("id", "23-op");
                put("ruleNameTTKHA", "both missing rec");

                // put("myRanking", 34.89);
            }
        });
        ruleSystem.init(rules);
        // System.out.println(re.evaluate(map, rule));
        Map<String, Object> map = new HashMap<>();
        map.put("price", 7.9);
        map.put("volume", 710);
        System.out.println(ruleSystem.evaluate(map));

    }

    @Test
    public void testRuleSystemInitSingleValued() {

        RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 2);

        RuleCriteria PERuleCriteria = new RuleCriteria("peRatio", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 4);

        Set<RuleCriteria> ruleCriterias = new HashSet<>(Arrays.asList(priceRuleCriteria, volumeRuleCriteria));
        String ruleSystemName = "firstEverRule";

        RuleSystem ruleSystem = new RuleSystem(ruleSystemName, ruleCriterias, "id", "ruleName", "ruleRank");

        Set<Map<String, Object>> rules = new HashSet<>();
        rules.add(new HashMap<String, Object>() {
            {
                put("price", 34.0);
                put("volume", 700);
                put("id", "23-op");
                put("ruleName", "both present");
                // put("myRanking", 34.89);
            }
        });
        rules.add(new HashMap<String, Object>() {
            {
                put("price", 700.0);
                put("id", "23-op");
                put("ruleName", "Vol rec");
                // put("myRanking", 34.89);
            }
        });
        rules.add(new HashMap<String, Object>() {
            {
                put("price", 2.9);
                put("id", "23-op");
                put("ruleName", "Price rec");
                // put("myRanking", 34.89);
            }
        });

        rules.add(new HashMap<String, Object>() {
            {

                put("id", "23-op");
                put("ruleName", "both missing rec");

                // put("myRanking", 34.89);
            }
        });
        // ruleSystem.init(rules);
        // System.out.println(re.evaluate(map, rule));

    }

    @Test
    public void testRuleSystemEvaluate() {

        RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 2);

        RuleCriteria PERuleCriteria = new RuleCriteria("peRatio", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 4);

        Set<RuleCriteria> ruleCriterias = new HashSet<>(Arrays.asList(priceRuleCriteria, volumeRuleCriteria));
        String ruleSystemName = "firstEverRule";

        RuleSystem ruleSystem = new RuleSystem(ruleSystemName, ruleCriterias, "id", "ruleName", "ruleRank");

        Set<Map<String, Object>> rules = new HashSet<>();
        rules.add(new HashMap<String, Object>() {
            {
                put("price", 34.0);
                put("volume", 700);
                put("id", "34.0 is price");
                put("ruleName", "both present");

                // put("myRanking", 34.89);
            }
        });
        rules.add(new HashMap<String, Object>() {
            {

                put("price", 2.9);
                put("volume", 700);
                put("id", "700 is vol");
                put("ruleName", "Vol rec");

                // put("myRanking", 34.89);
            }
        });
        rules.add(new HashMap<String, Object>() {
            {

                put("price", 2.9);
                put("id", "price is 2.9");
                put("ruleName", "Price rec");

                // put("myRanking", 34.89);
            }
        });

        rules.add(new HashMap<String, Object>() {
            {

                put("id", "bhai kuch bhi de do.");
                put("ruleName", "both missing rec");

                // put("myRanking", 34.89);
            }
        });
        // ruleSystem.init(rules);

        Map<String, Object> map = new HashMap<>();
        map.put("price", 2.9);
        map.put("volume", 700);
        // System.out.println(ruleSystem.evaluate(map));

    }

    @SuppressWarnings("serial")
    @Test(expected = IllegalArgumentException.class)
    public void testRuleSystemEvaluateValidationsRuleId() {

        RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 2);

        Set<RuleCriteria> ruleCriterias = new HashSet<>(Arrays.asList(priceRuleCriteria, volumeRuleCriteria));
        String ruleSystemName = "firstEverRule";

        RuleSystem ruleSystem = new RuleSystem(ruleSystemName, ruleCriterias, "unknow", "ruleName", "ruleRank");

        Set<Map<String, Object>> rules = new HashSet<>();
        rules.add(new HashMap<String, Object>() {
            {
                put("price", 34.0);
                put("volume", 700);
                put("id", "34.0 is price");
                put("ruleName", "both present");

            }
        });

        ruleSystem.init(rules);
    }

    @SuppressWarnings("serial")
    @Test(expected = IllegalArgumentException.class)
    public void testRuleSystemEvaluateValidationsRuleName() {

        RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 2);

        Set<RuleCriteria> ruleCriterias = new HashSet<>(Arrays.asList(priceRuleCriteria, volumeRuleCriteria));
        String ruleSystemName = "firstEverRule";

        RuleSystem ruleSystem = new RuleSystem(ruleSystemName, ruleCriterias, "id", "ruleName", "ruleRank");

        Set<Map<String, Object>> rules = new HashSet<>();
        rules.add(new HashMap<String, Object>() {
            {
                put("price", 34.0);
                put("volume", 700);
                put("id", "34.0 is price");
                put("ruleName", 5.66); // invalid rule name
            }
        });

        ruleSystem.init(rules);
    }

    @SuppressWarnings("serial")
    @Test(expected = IllegalArgumentException.class)
    public void testRuleSystemEvaluateValidationsRuleRank() {
        RuleCriteria priceRuleCriteria = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        RuleCriteria volumeRuleCriteria = new RuleCriteria("volume", RuleValueAttributes.DataType.INTEGER,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 2);

        Set<RuleCriteria> ruleCriterias = new HashSet<>(Arrays.asList(priceRuleCriteria, volumeRuleCriteria));
        String ruleSystemName = "firstEverRule";

        RuleSystem ruleSystem = new RuleSystem(ruleSystemName, ruleCriterias, "id", "ruleName", "ruleRank");

        Set<Map<String, Object>> rules = new HashSet<>();
        rules.add(new HashMap<String, Object>() {
            {
                put("price", 34.0);
                put("volume", 700);
                put("id", "34.0 is price");
                put("ruleName", "both present");
                put("ruleRank", "string rule rank"); // invalid rule rank

            }
        });

        ruleSystem.init(rules);
    }
}
