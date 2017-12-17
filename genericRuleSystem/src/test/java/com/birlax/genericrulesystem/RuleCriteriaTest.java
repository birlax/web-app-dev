package com.birlax.genericrulesystem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RuleCriteriaTest {

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
    }

    @Test
    public void testRuleCriteriaEquals() {

        RuleCriteria r1 = new RuleCriteria("c1", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        Assert.assertEquals(r1, c1);

    }

}
