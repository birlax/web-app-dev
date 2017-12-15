package com.birlax.genericrulesystem;

import java.util.Map;
import java.util.Set;

/**
 * Every rule is a composition of set of values, each rule is represented using
 * {@link RuleValue}
 * 
 * @author birlax
 *
 */
public class Rule {

	/**
	 * Name of the rule. Only for enhancing the readability of the rule-systems
	 * output.
	 */
	private String ruleName;

	/**
	 * Mandatory field for Rule class. If this field is missing at the time of
	 * {@link RuleSystem#init(Set)}, rule-system load will fail, and rule-system
	 * can't be used until this problem is fixed.
	 */
	private String ruleId;

	/**
	 * Rule-System at the time of {@link RuleSystem#init(Set)} will compute rank for
	 * every rule. Ranking done by the {@link RuleSystem} will be based on the rank
	 * provided for individual input criteria which can be provided via
	 * {@link RuleSystem#setRuleInputsAndRank(java.util.Map)}. Only if two or more
	 * rules generate same evaluation rank, only then this field will be used to
	 * break the tie. As the field is name "rank" lower value means rule with
	 * smaller value will be evaluated first. <br>
	 * In case this fields is also not able to break the tie say for reasons like
	 * all rules were provided dummy rank 1, then rules will be evaluated in random
	 * order.
	 * 
	 */
	private Integer ruleRank;

	/**
	 * Rule criteria.
	 */
	private Map<RuleCriteria, RuleValue> ruleValues;

	public Rule(String ruleName, String ruleId, Map<RuleCriteria, RuleValue> ruleValues) {
		super();
		this.ruleName = ruleName;
		this.ruleId = ruleId;
		this.ruleValues = ruleValues;
	}

	public String getRuleName() {
		return ruleName;
	}

	public String getRuleId() {
		return ruleId;
	}

	public Integer getRuleRank() {
		return ruleRank;
	}

	public void setRuleRank(Integer ruleRank) {
		this.ruleRank = ruleRank;
	}

	@Override
	public String toString() {
		return "Rule [ruleName=" + ruleName + ", ruleId=" + ruleId + ", ruleRank=" + ruleRank + ", ruleValues="
				+ ruleValues + "]";
	}

	public Map<RuleCriteria, RuleValue> getRuleValues() {
		return ruleValues;
	}

}
