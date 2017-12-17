package com.birlax.genericrulesystem;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/** Internal class to be used only with Rule. and Rule Evaluation. <br>
 * <br>
 * For String : Leading and Trailing spaces will always be removed before rule evaluation. <br>
 * <br>
 * 
 * @author birlax */

public class RuleValue {

    private Object ruleValue;

    private List<Pair<Object, Object>> listOfRanges;
    private List<Object>               listOfValues;

    public int returnRank() {
        int rk = 0;
        if (ruleValue != null) {
            rk = 1;
        }
        if (listOfValues != null) {
            rk = 2;
        }
        if (listOfRanges != null) {
            rk = 3;
        }
        return rk;
    }

    public void validateSimpleSingleValues(final Object value, final RuleCriteria ruleCriteria) {
        if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DATE && !(value instanceof Date)) {
            throw new IllegalArgumentException(MessageFormat.format("Value for type {0} : has to be of type : {1}",
                    ruleCriteria.getRuleCriteriaName(), Date.class));
        }
        if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DOUBLE && !(value instanceof Double)) {
            throw new IllegalArgumentException(MessageFormat.format("Value for type {0} : has to be of type : {1}",
                    ruleCriteria.getRuleCriteriaName(), Double.class));
        }
        if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.INTEGER && !(value instanceof Integer)) {
            throw new IllegalArgumentException(MessageFormat.format("Value for type {0} : has to be of type : {1}",
                    ruleCriteria.getRuleCriteriaName(), Integer.class));
        }
        if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.STRING && !(value instanceof String)) {
            throw new IllegalArgumentException(MessageFormat.format("Value for type {0} : has to be of type : {1}",
                    ruleCriteria.getRuleCriteriaName(), String.class));
        }
    }

    /*
     * public RuleValue(final RuleCriteria ruleCriteria) { super(); }
     */
    public RuleValue(final Object value, final RuleCriteria ruleCriteria) {

        // Value is null, no validations are required.
        if (value == null)
            return;

        if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_RANGES) {
            this.listOfRanges = new ArrayList<>();
        }
        if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_VALUES) {
            this.listOfValues = new ArrayList<>();
        }

        // Simple single values can be validated against data-type
        if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE) {
            validateSimpleSingleValues(value, ruleCriteria);
            this.ruleValue = value;
            return;
        }
        /** If you have reached here then value is not NUL and as Special Values can be represented only in STRING we
         * can cast it blindly or throw exception. */
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "Special Valued Type like : {0} of ({1}) should be represented like : {2}{3}{4}{5}{6}{7}{8}",
                    ruleCriteria.getSpecialValued(), ruleCriteria.getRuleValueType(), 2.67,
                    RuleSystemConstant.RANGE_DELIMITER, 67.89, RuleSystemConstant.LIST_DELIMITER, 24,
                    RuleSystemConstant.RANGE_DELIMITER, 69));
        }

        String strValue = (String) value;

        for (String allValue : strValue.split(RuleSystemConstant.LIST_DELIMITER)) {

            allValue = allValue.trim();
            if (allValue.isEmpty())
                continue;

            String array[] = allValue.split(RuleSystemConstant.RANGE_DELIMITER);
            String startRange;
            String endRange;
            startRange = array[0];

            if (allValue.contains(RuleSystemConstant.RANGE_DELIMITER)) {

                if (RuleSystemConstant.RANGE_DELIMITER.equals(allValue.charAt(0) + "")) {
                    startRange = null;
                    endRange = array[1];
                } else {
                    startRange = array[0];
                    endRange = null;
                }
                if (array.length == 2) {
                    startRange = array[0];
                    endRange = array[1];
                }
            } else {
                // cases where used mentioned single values items in range as "2;3;4"
                startRange = endRange = array[0];
            }

            Pair<Object, Object> p = null;
            if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.INTEGER) {
                Integer l = parseInt(startRange);
                Integer r = parseInt(endRange);
                validateRange(l, r);
                p = Pair.of(l, r);
            }
            if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DOUBLE) {
                Double l = parseDouble(startRange);
                Double r = parseDouble(endRange);
                validateRange(l, r);
                p = Pair.of(l, r);
            }
            if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.STRING) {
                validateRange(startRange, endRange);
                p = Pair.of(startRange, endRange);
            }

            if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DATE) {
                // validateRange(startRange, endRange);
                // p = Pair.of(startRange, endRange);
            }

            if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_RANGES) {
                this.listOfRanges.add(p);
            }
            if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_VALUES) {
                if (p.getLeft() != null)
                    this.listOfValues.add(p.getLeft());
                if (p.getRight() != null)
                    this.listOfValues.add(p.getRight());
            }
            System.out.println(p);

        }

    }

    /** @param l
     * @param r
     */
    private <T extends Number> void validateRange(T l, T r) {
        if (l == null || r == null) {
            return;
        }
        if (l.doubleValue() > r.doubleValue()) {
            throw new IllegalArgumentException(
                    "Left vale of RANGE can't me greater than RIGHT range. Left :" + l + " Right :" + r);
        }
    }

    private void validateRange(String l, String r) {
        if (l == null || r == null) {
            return;
        }
        if (l.compareTo(r) > 0) {
            throw new IllegalArgumentException(
                    "Left vale of RANGE can't me greater than RIGHT range. Left :" + l + " Right :" + r);
        }
    }

    /** @param v
     * @return */
    private boolean isNullEmptyOrInfinity(String v) {
        if (v == null) {
            return true;
        }

        v = v.trim();

        if (v.isEmpty()) {
            return true;
        }

        if (RuleSystemConstant.POSITIVE_INFINITY.equals(v) || RuleSystemConstant.NEGATIVE_INFINITY.equals(v)) {
            return true;
        }
        return false;
    }

    private Integer parseInt(String v) {
        if (isNullEmptyOrInfinity(v))
            return null;
        return Integer.parseInt(v);
    }

    private Double parseDouble(String v) {
        if (isNullEmptyOrInfinity(v))
            return null;
        return Double.parseDouble(v);
    }

    public Object getRuleValue() {
        return ruleValue;
    }

    public List<Pair<Object, Object>> getListOfRanges() {
        return listOfRanges;
    }

    public List<Object> getListOfValues() {
        return listOfValues;
    }

    @Override
    public String toString() {
        return "RuleValue [ruleValue=" + ruleValue + ", listOfRanges=" + listOfRanges + ", listOfValues=" + listOfValues
                + " ]";
    }

}
