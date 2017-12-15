package com.birlax.genericrulesystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Internal class to be used only with Rule. and Rule Evaluation. <br>
 * <br>
 * For String : Leading and Trailing spaces will always be removed before rule
 * evaluation. <br>
 * <br>
 * 
 * @author birlax
 *
 */

public class RuleValue {

	private Object ruleValue;

	private Pair<Object, Object> rangeRuleValue;

	private List<Pair<Object, Object>> listOfRanges;
	private List<Object> listOfValues;

	public int returnRank() {
		int rk = 0;
		if (ruleValue != null) {
			rk = 1;
		}
		if (listOfValues != null) {
			rk = 2;
		}
		if (rangeRuleValue != null) {
			rk = 3;
		}
		if (listOfRanges != null) {
			rk = 4;
		}
		return rk;
	}
	// private RuleCriteria ruleCriteria;

	/*
	 * public RuleValue(final RuleCriteria ruleCriteria) { super(); }
	 */
	public RuleValue(final Object value, RuleCriteria ruleCriteria) {

		if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_RANGES) {
			this.listOfRanges = new ArrayList<>();
		}
		if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_VALUES) {
			this.listOfValues = new ArrayList<>();
		}

		// Value is null, no validations are required.
		if (value == null)
			return;

		// Simple single values can be validated against data-type
		if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_VALUE) {
			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DATE && !(value instanceof Date)) {
				throw new IllegalArgumentException("Value for type : " + ruleCriteria.getRuleCriteriaName()
						+ " has to be of type : " + Date.class);
			}
			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.DOUBLE && !(value instanceof Double)) {
				throw new IllegalArgumentException("Value for type : " + ruleCriteria.getRuleCriteriaName()
						+ " has to be of type : " + Double.class);
			}

			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.INTEGER
					&& !(value instanceof Integer)) {
				throw new IllegalArgumentException("Value for type : " + ruleCriteria.getRuleCriteriaName()
						+ " has to be of type : " + Integer.class);
			}
			if (ruleCriteria.getRuleValueType() == RuleValueAttributes.DataType.STRING && !(value instanceof String)) {
				throw new IllegalArgumentException(
						"Value for type : " + ruleCriteria.getRuleValueType() + " has to be of type : " + String.class);
			}
			this.ruleValue = value;
			return;
		} /**
			 * If you have reached here then value is not NUL and as Special Values can be
			 * represented only in STRING we can cast it blindly of throw exception.
			 * 
			 */
		if (!(value instanceof String)) {
			throw new IllegalArgumentException("Special Valued Type like : " + ruleCriteria.getSpecialValued() + " of ("
					+ ruleCriteria.getRuleValueType() + ")s should be represented as : " + String.class + ", Ex : 2"
					+ RuleSystemConstant.RANGE_DELIMITER + "3" + RuleSystemConstant.LIST_DELIMITER + "45");
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

			// if(this.ruleValueType == RuleValueAttributes.DataType.DATE) {
			// p =Pair.of(Integer.parseInt(startRange),);
			// }
			if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_RANGES) {
				this.listOfRanges.add(p);
			}
			if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.LIST_OF_VALUES) {
				// this.listOfValues.add();
			}
			if (ruleCriteria.getSpecialValued() == RuleValueAttributes.SpecialValued.SIMPLE_SINGLE_RANGE) {
				this.rangeRuleValue = p;
			}
			System.out.println(p);

		}

	}

	private void validateRange(Integer l, Integer r) {
		if (l == null || r == null) {
			return;
		}
		if (l > r) {
			throw new IllegalArgumentException(
					"Left vale of RANGE can't me greater than RIGHT value. Left :" + l + " Right :" + r);
		}
	}

	private void validateRange(Double l, Double r) {
		if (l == null || r == null) {
			return;
		}
		if (l > r) {
			throw new IllegalArgumentException(
					"Left vale of RANGE can't me greater than RIGHT range. Left :" + l + " Right :" + r);
		}
	}

	private Integer parseInt(String v) {
		if (v == null || v.trim().isEmpty() || RuleSystemConstant.POSITIVE_INFINITY.equals(v.trim())
				|| RuleSystemConstant.NEGATIVE_INFINITY.equals(v.trim())) {
			return null;
		}
		return Integer.parseInt(v);
	}

	private Double parseDouble(String v) {
		if (v == null || v.trim().isEmpty() || RuleSystemConstant.POSITIVE_INFINITY.equals(v.trim())
				|| RuleSystemConstant.NEGATIVE_INFINITY.equals(v.trim())) {
			return null;
		}
		return Double.parseDouble(v);
	}

	public Object getRuleValue() {
		return ruleValue;
	}

	public Pair<Object, Object> getRangeRuleValue() {
		return rangeRuleValue;
	}

	public List<Pair<Object, Object>> getListOfRanges() {
		return listOfRanges;
	}

	public List<Object> getListOfValues() {
		return listOfValues;
	}

	@Override
	public String toString() {
		return "RuleValue [ruleValue=" + ruleValue + ", rangeRuleValue=" + rangeRuleValue + ", listOfRanges="
				+ listOfRanges + ", listOfValues=" + listOfValues + " ]";
	}

}
