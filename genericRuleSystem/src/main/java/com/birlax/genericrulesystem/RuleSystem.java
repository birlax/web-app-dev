package com.birlax.genericrulesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Class represents a rule-system. A rule system can hold many rules. When
 * evaluate on a rule system is called it will return the rule-id of the rule
 * which matches the given input.<br>
 * <br>
 * <ul>
 * <li>If multiple rules match the input criteria then first matching rule will
 * be returned.</li>
 * <li>If no rules satisfy input conditions, constant string
 * <b>"NO_MATCHING_RULES"</b> will be returned.</li>
 * </ul>
 * 
 * @author birlax
 *
 */
public class RuleSystem {

	/**
	 * Name of the rule-system.
	 */
	private String ruleSystemName;

	/**
	 * Same as what name suggests
	 */
	private Set<RuleCriteria> ruleCriteria;

	/**
	 * Store of rules part of this rule-system.
	 */
	private Map<Integer, Set<Rule>> rules;

	private String ruleOutputColumnName;

	private String columnStoringRuleName = "ruleName";

	private String ruleRankColumnName = "ruleRank";

	/**
	 * 
	 * @param ruleSystemName
	 * @param ruleInputsAndRank
	 */
	public RuleSystem(final String ruleSystemName, final Set<RuleCriteria> ruleCriteria,
			final String ruleOutputColumnName, final String columnStoringRuleName, final String ruleRankColumnName) {
		super();
		this.ruleSystemName = ruleSystemName;
		this.ruleCriteria = ruleCriteria;
		this.ruleOutputColumnName = ruleOutputColumnName;
		this.columnStoringRuleName = columnStoringRuleName;
		this.ruleRankColumnName = ruleRankColumnName;
		// need to keep the rules in map in the order of their evaluation ranking.
		this.rules = new TreeMap<>();
	}

	public String evaluate(Map<String, Object> input) {
		for (Map.Entry<Integer, Set<Rule>> entry : rules.entrySet()) {
			for (Rule rule : entry.getValue()) {
				String result = RuleEvaluator.evaluate(input, rule);
				if (!RuleEvaluator.NO_MATCHING_RULES.equals(result)) {
					return result;
				}
			}
		}
		return RuleEvaluator.NO_MATCHING_RULES;
	}

	/**
	 * Instantiates the rule-system with provided rules. If provided rules do not
	 * have the rule criteria for which input ranks were provided all inputs will be
	 * ranked with equal weight of 1.
	 * 
	 * @param rules
	 * @return
	 */

	private Integer validateRuleRank(Object ruleRank) {
		Integer ruleRankInt = 1;
		if (ruleRank != null && !(ruleRank instanceof Integer)) {
			throw new IllegalArgumentException(
					"Only Integer values allowed for ruleRank column : " + ruleRankColumnName);
		} else {
			ruleRankInt = (Integer) ruleRank;
		}
		return ruleRankInt;
	}

	private String validateMandatroyStringFields(Object ruleName, String fielName, String ruleOutputColumnName) {
		String ruleNameStr = "SysGenRule-" + System.currentTimeMillis();
		if (ruleName != null && !(ruleName instanceof String)) {
			throw new IllegalArgumentException(
					"Only String values allowed for " + fielName + " column : " + ruleOutputColumnName);
		} else {
			ruleNameStr = (String) ruleName;
		}
		return ruleNameStr;
	}

	public boolean init(Set<Map<String, Object>> rules) {

		List<RuleCriteria> rcs = new ArrayList<>(ruleCriteria);
		Collections.sort(rcs);
		for (Map<String, Object> rule : rules) {
			String ruleName = validateMandatroyStringFields(rule.get(columnStoringRuleName), "ruleName",
					columnStoringRuleName);

			Integer ruleRank = validateRuleRank(rule.get(ruleRankColumnName));

			String ruleId = validateMandatroyStringFields(rule.get(ruleOutputColumnName), "ruleId",
					ruleOutputColumnName);

			if (ruleName == null) {
				// Generate a warning ...
			}

			if (ruleRank == null) {
				// Generate a warning ...
			}
			if (ruleId == null) {
				throw new IllegalArgumentException("Column/Field : [" + ruleOutputColumnName
						+ "] is mandatory input as that is the ouput of the rule-system");
			}
			Map<RuleCriteria, RuleValue> ruleValues = new TreeMap<>();

			Integer finalRank = 0;
			int rcsLength = rcs.size() + 1;
			for (RuleCriteria rc : rcs) {

				rcsLength--;

				String rcName = rc.getRuleCriteriaName();
				Object rcValue = rule.get(rcName);
				if (rcValue != null) {
					finalRank -= rc.getCriteriaRank();
				} else {
					finalRank -= (rcsLength + rc.getCriteriaRank()) * rcs.size();
				}

				RuleValue rv = new RuleValue(rcValue, rc);
				if (rcValue == null) {
					finalRank -= rv.returnRank();
				} else {

					ruleValues.put(rc, rv);
				}
			}
			Rule ruleObj = new Rule(ruleName, ruleId, ruleValues);

			int rank = (-1 * finalRank);
			this.rules.putIfAbsent(rank, new HashSet<>());
			this.rules.get(rank).add(ruleObj);

		}
		System.out.println(this.rules);
		return false;

	}

	public String getRuleSystemName() {
		return ruleSystemName;
	}

	public void setRuleSystemName(String ruleSystemName) {
		this.ruleSystemName = ruleSystemName;
	}

}
