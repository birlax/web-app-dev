package com.birlax.genericrulesystem;

import java.util.Objects;

import com.birlax.genericrulesystem.RuleValueAttributes.DataType;
import com.birlax.genericrulesystem.RuleValueAttributes.SpecialValued;

public class RuleCriteria implements Comparable<RuleCriteria> {

    private String ruleCriteriaName;

    private RuleValueAttributes.DataType ruleValueType;

    private RuleValueAttributes.SpecialValued specialValued;

    /** Rule can have multiple criteria or {@link RuleValue}, each criteria has weightage, Rule with higher overall
     * weight is evaluate first. Overall rank for a rules is computed using the Map. <br>
     * <br>
     * <br>
     * Putting absurd high values for rank may lead to failure of rule-system init process due to <b>Number Over/Under
     * flow exceptions</b>.<br>
     * <br>
     * Also giving same ranks to all input criteria will not produce best results. Try to keep the values in same order
     * as number of inputs. Say your rule-system has 12 input criteria try to give every input different rank and don't
     * exceed the ranks more than 12. Even if ranks are provided with values higher than 12 rule-system will <b>continue
     * to work and honor the ranking</b> until the Over/Under flow exceptions occur. <br>
     * <br>
     * -128 to 127 which is 255 values is good range for criteria. */
    private byte criteriaRank;

    public RuleCriteria(String ruleCriteriaName, DataType ruleValueType, SpecialValued specialValued,
            byte criteriaRank) {
        super();
        Objects.requireNonNull(ruleCriteriaName);
        Objects.requireNonNull(specialValued);
        Objects.requireNonNull(criteriaRank);
        this.ruleCriteriaName = ruleCriteriaName;
        this.ruleValueType = ruleValueType;
        this.specialValued = specialValued;
        this.criteriaRank = criteriaRank;
    }

    public RuleValueAttributes.DataType getRuleValueType() {
        return ruleValueType;
    }

    public RuleValueAttributes.SpecialValued getSpecialValued() {
        return specialValued;
    }

    public String getRuleCriteriaName() {
        return ruleCriteriaName;
    }

    @Override
    public String toString() {
        return "RuleCriteria [ruleCriteriaName=" + ruleCriteriaName + ", ruleValueType=" + ruleValueType
                + ", specialValued=" + specialValued + ", criteriaRank=" + criteriaRank + "]";
    }

    public byte getCriteriaRank() {
        return criteriaRank;
    }

    @Override
    public int compareTo(RuleCriteria o) {
        return Byte.compare(this.criteriaRank, o.criteriaRank);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + criteriaRank;
        result = prime * result + ((ruleCriteriaName == null) ? 0 : ruleCriteriaName.hashCode());
        result = prime * result + ((ruleValueType == null) ? 0 : ruleValueType.hashCode());
        result = prime * result + ((specialValued == null) ? 0 : specialValued.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RuleCriteria other = (RuleCriteria) obj;
        if (criteriaRank != other.criteriaRank)
            return false;
        if (ruleCriteriaName == null) {
            if (other.ruleCriteriaName != null)
                return false;
        } else if (!ruleCriteriaName.equals(other.ruleCriteriaName))
            return false;
        if (ruleValueType != other.ruleValueType)
            return false;
        if (specialValued != other.specialValued)
            return false;
        return true;
    }

}
