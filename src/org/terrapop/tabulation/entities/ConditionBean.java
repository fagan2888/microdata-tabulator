 /*
  Microdata Tabulator
  https://github.com/mnpopcenter/microdata-tabulator
  Copyright (c) 2012-2017 Regents of the University of Minnesota

  Contributors:
    Alex Jokela, Minnesota Population Center, University of Minnesota
    Pranjul Yadav, Minnesota Population Center, University of Minnesota
 
  This project is licensed under the Mozilla Public License, version 2.0 (the
  "License"). A copy of the License is in the project file "LICENSE.txt",
  and is also available at https://www.mozilla.org/en-US/MPL/2.0/.
 */

package org.terrapop.tabulation.entities;

import java.util.List;

import org.apache.log4j.Logger;
import org.terrapop.tabulation.exception.ValueOperatorException;
import org.terrapop.tabulation.utilities.OperatorUtility;

public class ConditionBean {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ConditionBean.class.getName());

	private String variable;
	private Operator.operators operator;
	private String value;
	private Integer int_value;
	private Boolean is_integer;
	private String extremeValue;
	private List<String> innerList;

	public ConditionBean(String variable, String operator, String value, String extremeValue) {
		// log.debug("Construction for Condition Called
		// <Variable,Operator,Value> "
		// + variable + " " + operator + " " + value);
		this.variable = variable;
		OperatorUtility operatorUtility = new OperatorUtility();
		this.operator = operatorUtility.getEnumValueForOperator(operator);
		this.value = value;
		this.extremeValue = extremeValue;
		// log.debug("Constructor Condition Completed");
	}

	public ConditionBean(String variable, String operator, String value) {
		// log.debug("Construction for Condition Called
		// <Variable,Operator,Value> "
		// + variable + " " + operator + " " + value);
		this.variable = variable;
		OperatorUtility operatorUtility = new OperatorUtility();
		this.operator = operatorUtility.getEnumValueForOperator(operator);
		setValue(value);
		// log.debug("Constructor Condition Completed");
	}

	public ConditionBean(String variable, String operator, List<String> innerList) {
		// log.debug("Construction for Condition Called
		// <Variable,Operator,Value> "
		// + variable + " " + operator + " " + value);
		this.variable = variable;
		OperatorUtility operatorUtility = new OperatorUtility();
		this.operator = operatorUtility.getEnumValueForOperator(operator);
		setInnerList(innerList);
		// log.debug("Constructor Condition Completed");
	}

	public String getExtremeValue() {
		return extremeValue;
	}

	public List<String> getInnerList() {
		return innerList;
	}

	public void setInnerList(List<String> innerList) {
		this.innerList = innerList;
	}

	public void setExtremeValue(String extremeValue) {
		this.extremeValue = extremeValue;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public Operator.operators getOperator() {
		return operator;
	}

	public void setOperator(Operator.operators operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getIntValue() {
		return int_value;
	}

	public void setIntValue(Integer value) {
		this.int_value = value;
	}

	public Boolean isNumeric() {
		return isNumeric(getValue());
	}

	public Boolean isInteger() {

		if (is_integer == null) {
			is_integer = isNumeric();

			if (is_integer) {
				int_value = Integer.parseInt(getValue());
			}
		}

		return is_integer;
	}

	public boolean equals(String v) {
		Boolean b;

		if (isNumeric(v) && isInteger()) {
			b = getIntValue() == Integer.parseInt(v);
		} else {
			b = v.equals(getValue());
		}

		return b;
	}

	public boolean notEquals(String v) {
		return !equals(v);
	}

	public boolean greaterThan(String v) throws ValueOperatorException {
		Boolean b;

		if (isNumeric(v) && isInteger()) {
			b = getIntValue() > Integer.parseInt(v);
		} else {
			throw new ValueOperatorException("Greater-than is not supported on values: " + getValue() + " | " + v);
		}

		return b;
	}

	public boolean greaterThanEqualTo(String v) throws ValueOperatorException {
		Boolean b;

		if (isNumeric(v) && isInteger()) {
			b = getIntValue() >= Integer.parseInt(v);
		} else {
			throw new ValueOperatorException(
					"Greater-than-equals is not supported on values: " + getValue() + " | " + v);
		}

		return b;
	}

	public boolean lessThan(String v) throws ValueOperatorException {

		Boolean b;

		if (isNumeric(v) && isInteger()) {
			b = getIntValue() < Integer.parseInt(v);
		} else {
			throw new ValueOperatorException("Less-than is not supported on values: " + getValue() + " | " + v);
		}

		return b;
	}

	public boolean lessThanEqualTo(String v) throws ValueOperatorException {
		Boolean b;

		if (isNumeric(v) && isInteger()) {
			b = getIntValue() <= Integer.parseInt(v);
		} else {
			throw new ValueOperatorException("Less-than is not supported on values: " + getValue() + " | " + v);
		}

		return b;
	}

	public boolean contains(ConditionBean condition, String value) {

		boolean conditionResult = false;

		for (String condValue : condition.getInnerList()) {

			if (isNumeric(condValue) && isNumeric(value)) {
				if (Integer.parseInt(condValue) == Integer.parseInt(value)) {
					conditionResult = true;
					break;
				}
			} else {
				if (condValue.equals(value)) {
					conditionResult = true;
					break;
				}
			}

		}

		return conditionResult;
	}

	public boolean between(ConditionBean condition, String value) {
		return (Integer.parseInt(value) >= Integer.parseInt(condition.getValue()))
				&& (Integer.parseInt(value) <= Integer.parseInt(condition.getExtremeValue()));
	}

	public static boolean isNumeric(String s) {
		boolean valid = true;

		char[] a = s.toCharArray();

		for (char c : a) {
			valid = ((c >= '0') && (c <= '9'));

			if (!valid) {
				break;
			}
		}

		return valid;
	}

}
