package com.birlax.temporalDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.junit.Test;

import com.birlax.genericrulesystem.RuleCriteria;
import com.birlax.genericrulesystem.RuleSystem;
import com.birlax.genericrulesystem.RuleValueAttributes;
import com.birlax.temporalDb.domain.BrokerageCharge;
import com.birlax.temporalDb.service.UserService;

public class BasicTest extends BaseIntegerationTest {

    @Inject
    private Integer valueOfA;

    @Inject
    private UserService userService;

    public void testUpdate() {
        long startTime = System.currentTimeMillis();
        BrokerageCharge b = new BrokerageCharge();
        b.setId(1);
        b.setBrokerCode("ZERODHA");
        b.setProductType("EQUITY");
        b.setRuleName("default");
        b.setTradeType("MIS");
        b.setRuleId(2);
        b.setCommets("comments");

        List<BrokerageCharge> s = new ArrayList<>();

        IntStream.rangeClosed(1, 1000000).forEach(a -> s.add(b));

        // userService.update(s);

        long endTime = System.currentTimeMillis();
        System.out.println("It took : " + ((endTime - startTime)) + "ms");
    }

    @Test
    public void testRuleSystem() {
        String ruleOutputColumnName = "ruleId";
        String ruleSystemName = "basicRuleSystem";

        RuleCriteria PERuleCriteria = new RuleCriteria("peRatio", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 4);

        RuleCriteria priceRange = new RuleCriteria("price", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 1);

        RuleCriteria volumeRange = new RuleCriteria("volume", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.LIST_OF_RANGES, (byte) 2);

        Set<RuleCriteria> ruleCriterias = new HashSet<>();
        ruleCriterias.add(PERuleCriteria);
        ruleCriterias.add(priceRange);
        ruleCriterias.add(volumeRange);

        String ruleRankColumnName = "ruleRank";
        Set<Map<String, Object>> rules = new HashSet<>();

        rules.add(new HashMap<String, Object>() {
            {
                put("price", "34.0;56:78.67");
                put("volume", "700:4566");
                put("ruleId", "both present");
            }
        });
        rules.add(new HashMap<String, Object>() {
            {
                put("price", "700.0");
                put("ruleId", "Vol rec");
            }
        });
        rules.add(new HashMap<String, Object>() {
            {
                put("price", "2.9");
                put("ruleId", "Price rec");
            }
        });

        rules.add(new HashMap<String, Object>() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            {
                put("ruleId", "both missing rec");
            }
        });

        RuleSystem rs = new RuleSystem(ruleSystemName, ruleCriterias, ruleOutputColumnName, ruleRankColumnName, rules);
        System.out.println(rs);
        Map<String, Object> input = new HashMap<>();
        input.put("price", 75.9);
        input.put("volume", 750.9);
        System.out.println(rs.evaluate(input));
    }
}
