package com.birlax.genericrulesystem;

import java.util.Date;
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
        if (ruleValue.getRuleValue() == null
                && ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE) {
            return true;
        }

        if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.REGEX) {
            return false;
        }

        if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE) {
            return ruleValue.getRuleValue().equals(inputValue);
        }

        if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_VALUES) {
            List<Object> list = ruleValue.getListOfValues();
            return list.contains(inputValue);
        }
        if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_RANGES) {
            List<Pair<Object, Object>> ranges = ruleValue.getListOfRanges();
            for (Pair<Object, Object> range : ranges) {
                if (evaluateSingleRange(range, inputValue, ruleCriteria.getRuleValueType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean evaluateSingleRange(Pair<Object, Object> range, Object inputValue,
            RuleValueAttributes.DataType dataType) {

        boolean result = false;
        switch (dataType) {
        case DATE:
            result = validateInputAgainstDateRange((Date) range.getLeft(), (Date) range.getRight(), (Date) inputValue);
            break;

        case STRING:
            result = validateInputAgainstStringRange((String) range.getLeft(), (String) range.getRight(),
                    (String) inputValue);
            break;

        case INTEGER:
            result = validateInputAgainstRange((Integer) range.getLeft(), (Integer) range.getRight(),
                    (Integer) inputValue);
            break;

        case DOUBLE:
            result = validateInputAgainstRange((Double) range.getLeft(), (Double) range.getRight(),
                    (Double) inputValue);
            break;
        }

        return result;

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

    public static boolean validateInputAgainstStringRange(String l, String r, String v) {

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
        if (r != null && v.compareTo(r) > 0) {
            return false;
        }
        // Now RIGHT is NULL which is treated as Lower bound test only. Ex v >= lower
        // value, without caring about RIGHT.
        if (l != null && v.compareTo(l) < 0) {
            return false;
        }
        return true;
    }

    public static boolean validateInputAgainstDateRange(Date l, Date r, Date v) {

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
        if (r != null && v.after(r)) {
            return false;
        }
        // Now RIGHT is NULL which is treated as Lower bound test only. Ex v >= lower
        // value, without caring about RIGHT.
        if (l != null && v.before(l)) {
            return false;
        }
        return true;
    }

}
