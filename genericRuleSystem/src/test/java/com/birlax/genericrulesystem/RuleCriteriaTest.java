package com.birlax.genericrulesystem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RuleCriteriaTest {

    private RuleCriteria c1;

    @Before
    public void setUp() {

        c1 = new RuleCriteria("c1", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

    }

    @Test
    public void testRuleCriteriaEquals() {

        RuleCriteria r1 = new RuleCriteria("c1", RuleValueAttributes.DataType.DOUBLE,
                RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE, (byte) 1);

        Assert.assertEquals(true, c1.equals(r1));
        Assert.assertEquals(true, c1.equals(c1));
        Assert.assertEquals(false, c1.equals(67));
        Assert.assertEquals(false, c1.equals(null));

    }

}
