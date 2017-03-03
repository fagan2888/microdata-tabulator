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
package org.terrapop.tabulation.utilities;
import java.util.*;

import org.apache.log4j.Logger;

import org.terrapop.tabulation.entities.ConditionBean;
import org.terrapop.tabulation.entities.Operator;

public class OperatorUtility {

	private static Logger log = Logger.getLogger(OperatorUtility.class.getName());
	
	public  boolean evaluateOperator(ConditionBean conditionBean, String conditionValue) {
       // log.debug("Enter evaluateOperator" );
		switch (conditionBean.getOperator()) {
		case EQUALS:
			return equals(conditionBean, conditionValue);

		case NOTEQUALS:
			return notEquals(conditionBean, conditionValue);

		case GREATERTHAN:
			return greaterThan(conditionBean, conditionValue);

		case GREATERTHANEQUALTO:
			return greaterThanEqualTo(conditionBean, conditionValue);

		case LESSTHAN:
			return lessThan(conditionBean, conditionValue);

		case LESSTHANEQUALTO:
			return lessThanEqualTo(conditionBean, conditionValue);

		case IN:
			return contains(conditionBean, conditionValue);

		case BETWEEN:
			return between(conditionBean, conditionValue);
			
		default : return false;	

		}
	}

	public  String performOperation(HashMap<String,Double> queryHashMap, String operation){
		// log.debug("Enter performOperation For Operation" + operation );
		Operator.operations enumOperation = getEnumValue(operation);
		switch(enumOperation){
		  case MEAN : return calculateMean(queryHashMap);
              
		  case MEDIAN : return calculateMedian(queryHashMap);
 
		  case PERCENTAGE : return calculateCount(queryHashMap);
 
		  case COUNT : return calculateCount(queryHashMap);
		  default :  return "";
		}
		
	}
	
	
	public  String calculateMean(HashMap<String,Double> unAggregatedQueryResultData){
		// log.debug("Enter CalculateMean");
		double numerator = 0;
		double denominator= 0;
		
		String mean = "";
		for(String key : unAggregatedQueryResultData.keySet()){
			Double value = unAggregatedQueryResultData.get(key);
		     //Put Checks
			  log.debug(" Key " + key + " Value " + value); 
		      numerator = numerator + Integer.parseInt(key)*value;
		      denominator  = denominator + value;
		      log.info(key +  " " + value);
		    }
		
		log.info(numerator + " " + denominator);
		
		if(denominator>0)
		mean = "" + numerator/(float)denominator ;
		else mean = "0";
		
		// log.debug("Exit Mean");
		return mean;
		
	}
	
	public  Operator.operators getEnumValueForOperator(String operator){
		// log.debug("Enter getEnumValueForOperator with Operator " + operator);
		if(operator.equals("=")) return Operator.operators.EQUALS ;
		if(operator.equals("<>")) return Operator.operators.NOTEQUALS ;
		if(operator.equals(">")) return Operator.operators.GREATERTHAN ;
		if(operator.equals(">=")) return Operator.operators.GREATERTHANEQUALTO ;
		if(operator.equals("<")) return Operator.operators.LESSTHAN ;
		if(operator.equals("<=")) return Operator.operators.LESSTHANEQUALTO ;
		if(operator.equals("IN")) return Operator.operators.IN ;
		if(operator.equals("BETWEEN")) return Operator.operators.BETWEEN ;
		// log.debug("Exit getEnumValueForOperator with Operator " + operator);
		return Operator.operators.NULLOPERATOR;
	}
	
	
	public  String calculateCount(HashMap<String,Double> unAggregatedQueryResultData){
		// log.debug("Enter CalculateCount");
		double numerator = 0;
		for(String key : unAggregatedQueryResultData.keySet()){
		     double value = unAggregatedQueryResultData.get(key);
		     //Put Checks
		      numerator = numerator + value;
		    }
		// log.debug("Exit CalculateCount with Count As" + numerator);
		return "" + numerator;
	}

	public  List<String> getSortedList(HashMap<String,Double> unAggregatedQueryResultHashMap){
		List<String> valueList= new ArrayList<String>(unAggregatedQueryResultHashMap.keySet());

		for (int k = 0; k < valueList.size(); k++) {
			for (int tail = 0; tail < valueList.size() - 1; tail++) {
				int head = tail + 1;
				if (valueList.get(tail).length() < valueList.get(head).length()) {

				} else {
					if ((valueList.get(tail).length() > valueList.get(head).length())
							|| ((valueList.get(tail).length() == valueList.get(head).length()) && (valueList
									.get(tail).compareTo(valueList.get(head)) > 0))) {
						String tailValue = valueList.get(tail);
						String headValue = valueList.get(head);

						// remove same I
						valueList.remove(tail);
						valueList.remove(tail);
						valueList.add(tail, headValue);
						valueList.add(head, tailValue);
					}
			  }
      }
    }
		return valueList;
	}
	
	public String calculateMedian(
			HashMap<String, Double> unAggregatedQueryResultData) {
		
		// log.debug("Enter CalcualteMedian");
		List<String> sortedListOfQueryHashMapValues = new ArrayList<String>();
		Double totalSum = 0.0;
		String median = "";
		String medianForEven = "";

		for (String key : unAggregatedQueryResultData.keySet()) {
             
			totalSum = totalSum
					+ unAggregatedQueryResultData.get(key);
			
		}

		sortedListOfQueryHashMapValues = getSortedList(unAggregatedQueryResultData);

		if ((totalSum % 2 == 1)) {
			median = getValueAtIndex(totalSum / 2 + 1,
					unAggregatedQueryResultData, sortedListOfQueryHashMapValues);
			
			return "" + median;
		} else {
			median = getValueAtIndex(totalSum / 2, unAggregatedQueryResultData,
					sortedListOfQueryHashMapValues);
			medianForEven = getValueAtIndex(totalSum / 2 + 1,
					unAggregatedQueryResultData, sortedListOfQueryHashMapValues);
			
			median = ""
					+ ( Double.parseDouble(median) + Double.parseDouble( medianForEven)) / 2;
		

			return median;
		}
		
		
	}
	
	public  String getValueAtIndex(double sum, HashMap<String,Double> unAggregatedQueryResultHashMap , List<String> sortedListOfQueryHashMapValues){
		// log.debug("Enter getValueAtIndex");
		double value = 0 ;
		String returnValue = "";
		for(String orderedEntry: sortedListOfQueryHashMapValues){
			value = value +unAggregatedQueryResultHashMap.get(orderedEntry);
			if(value >= sum) {
				returnValue =  orderedEntry;
			    break;
			}
		}
		// log.debug("Exit getValueAtIndex");
		return returnValue;
		
	}
	
	public  boolean equals(ConditionBean condition, String value) {
  
		return condition.equals(value); //value.equals(condition.getValue());
		
	}

	public  boolean notEquals(ConditionBean condition, String value) {
		return condition.notEquals(value); //!value.equals(condition.getValue());
	}

	public  boolean greaterThan(ConditionBean condition, String value) {
		return Integer.parseInt(value) > Integer.parseInt(condition.getValue());
	}

	public  boolean greaterThanEqualTo(ConditionBean condition, String value) {
		return Integer.parseInt(value) >= Integer.parseInt(condition.getValue());
	}

	public  boolean lessThan(ConditionBean condition, String value) {
		return Integer.parseInt(value) < Integer.parseInt(condition.getValue());
	}

	public  boolean lessThanEqualTo(ConditionBean condition, String value) {
		return Integer.parseInt(value) <= Integer.parseInt(condition.getValue());
	}

	public boolean contains(ConditionBean condition, String value) {
		
		boolean conditionResult = false;

		for(String condValue : condition.getInnerList()){
			if(condValue.equals(value)){
				conditionResult = true;
				break;
			}
		}
		return conditionResult;
	}

	public boolean between(ConditionBean condition, String value) {
		return (Integer.parseInt(value) >= Integer.parseInt(condition.getValue())) && 
				(Integer.parseInt(value) <= Integer.parseInt(condition.getExtremeValue())) ;
	}
	
	public  Operator.operations getEnumValue(String s){
		if( s.equals("Percentage")) return Operator.operations.PERCENTAGE;
		if( s.equals("Mean")) return Operator.operations.MEAN;
		if( s.equals("Median")) return Operator.operations.MEDIAN;
		if( s.equals("Count")) return Operator.operations.COUNT;
		return Operator.operations.NULLVALUE;
		
	}
	
	public static void main(String args[]){
			
		HashMap<String,Double> imap = new HashMap<String,Double>();
		imap.put("1",   34.0);
		imap.put("2", 10.0);
		imap.put("3",  32.0);
		imap.put("4",  12.0);
		
		 System.out.println( "Mean is " + new OperatorUtility().calculateMean(imap) );
		 System.out.println( "Count is " + new OperatorUtility().calculateCount(imap) );
		 System.out.println( "Median is " + new OperatorUtility().calculateMedian(imap) );
	}
  
}




