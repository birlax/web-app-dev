package com.birlax.genericrulesystem;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public class RuleEvaluator {

	public static final String NO_MATCHING_RULES = "NO_MATCHING_RULES";

	public static String evaluate(Map<String, Object> inputs, Rule rule) {

		Set<Boolean> ruleEvaluationResult = new HashSet<>(2);

		for (Map.Entry<RuleCriteria, RuleValue> entry : rule.getRuleValues().entrySet()) {
			RuleCriteria rc = entry.getKey();
			Object inputValue = inputs.get(rc.getRuleCriteriaName());
			ruleEvaluationResult.add(evaluate(inputValue, entry.getValue(), rc));
		}
		if (ruleEvaluationResult.contains(true) && !ruleEvaluationResult.contains(false)) {
			return rule.getRuleId();
		}
		return NO_MATCHING_RULES;
	}

	public static boolean evaluate(Object inputValue, RuleValue ruleValue, RuleCriteria ruleCriteria) {

		// if the rule value is NULL if should match any input value.
		if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE) {

			if (ruleValue.getRuleValue() == null) {
				return true;
			}

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.REGEX) {
			}
			/**
			 * To string comparison for single valued rule values, For date only permitted
			 * format will be YYYYMMDD
			 * 
			 */
			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DATE) {
				// return ((Date) ruleValue.getRuleValue()).

			}
			return ruleValue.getRuleValue().equals(inputValue);
		}

		if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_RANGE) {

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.REGEX) {
			}

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DATE) {
				// return ((Date) ruleValue.getRuleValue()).
			}

			Pair<Object, Object> range = ruleValue.getRangeRuleValue();

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.INTEGER) {
				return validateInputAgainstRange((Integer) range.getLeft(), (Integer) range.getRight(),
						(Integer) inputValue);
			}
		}

		if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_VALUES) {

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.REGEX) {
			}

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DATE) {
				// return ((Date) ruleValue.getRuleValue()).
			}
			List<Object> list = ruleValue.getListOfValues();
			return list.contains(inputValue);

		}

		if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_RANGES) {

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.REGEX) {
			}

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DATE) {
				// return ((Date) ruleValue.getRuleValue()).
			}
			List<Pair<Object, Object>> ranges = ruleValue.getListOfRanges();

			for (Pair<Object, Object> range : ranges) {
				if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.INTEGER) {
					boolean result = validateInputAgainstRange((Integer) range.getLeft(), (Integer) range.getRight(),
							(Integer) inputValue);
					// break circuit early, if criteria is meet.
					if (result) {
						return result;
					}
				}
				if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DOUBLE) {
					boolean result = validateInputAgainstRange((Double) range.getLeft(), (Double) range.getRight(),
							(Double) inputValue);
					// break circuit early, if criteria is meet.
					if (result) {
						return result;
					}
				}

			}
			// return list.contains(inputValue);

		}

		return false;
	}

	public static <T extends Number> boolean validateInputAgainstRange(T l, T r, T v) {

		// if valued to be tested is itself null no range testing can be performed.
		if (v == null) {
			return false;
		}
		// Both the values against which input was to be tested are also null no testing
		// is possible.
		if (l == null && r == null) {
			return false;
		}
		// Now LEFT is NULL which is treated as Upper bound test only. Ex v <= upper
		// value, without caring about LEFT.
		if (r != null && v.doubleValue() > r.doubleValue()) {
			return false;
		}
		// Now RIGHT is NULL which is treated as Lower bound test only. Ex v >= lower
		// value, without caring about RIGHT.
		if (l != null && v.doubleValue() < l.doubleValue()) {
			return false;
		}
		return true;
	}
}
