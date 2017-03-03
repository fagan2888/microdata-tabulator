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
package org.terrapop.tabulation.tabulator;


import org.terrapop.tabulation.entities.query.InternalQueryBean;
import org.terrapop.tabulation.exception.ConditionFormatException;
import org.terrapop.tabulation.exception.MetaDataException;


import org.terrapop.tabulation.loader.QueryLoader;

import org.terrapop.tabulation.utilities.HashMapConstants;
import org.terrapop.tabulation.utilities.OperatorUtility;
import org.terrapop.tabulation.utilities.Util;

import java.util.*;

import org.apache.log4j.Logger;

public class NationalTabulator extends BaseTabulator {
	private  List<HashMap<String, Double>> personResultHashMapListForAllQueries = new ArrayList<HashMap<String, Double>>();
	protected  List<String> queryResultSet = new ArrayList<String>();
	protected  List<String> finalResultSet = new ArrayList<String>();
	private  Logger log = Logger.getLogger(NationalTabulator.class
			.getName());
	private String country ;

	public String getCountry() {
	  return country;
	}
	
	public void setCountry(String country) {
	  this.country = country;
	}

	public NationalTabulator() {
		for (int i = 0; i < QueryLoader.getQueryBeanList().size(); i++) {
			HashMap<String, Double> tempMap = new HashMap<String, Double>();
			this.personResultHashMapListForAllQueries.add(tempMap);
		}
	}
	
	public List<String> getCSVFormat() throws Exception{
		List<String> CSVFormatOutputData = new ArrayList<String>();
		String csvOutputSingleLineData = this.country + ",";
		String queryDescriptionLine = "CODE,";
		int queryNum = 0;
		for(String st: finalResultSet){
			
			if(QueryLoader.getUndefinedQueriesList().contains( QueryLoader.getExternalQueryBeanList().get(queryNum).getCode()) ){
				csvOutputSingleLineData = csvOutputSingleLineData + "Undefined" + ",";
			}else{
				csvOutputSingleLineData = csvOutputSingleLineData + st + ",";
			}
			
      queryDescriptionLine = queryDescriptionLine + QueryLoader.getExternalQueryBeanList().get(queryNum).getCode() + ",";
      queryNum++;		
		}
		csvOutputSingleLineData = csvOutputSingleLineData.substring(0,csvOutputSingleLineData.length()-1);
		queryDescriptionLine = queryDescriptionLine.substring(0,queryDescriptionLine.length()-1);
		CSVFormatOutputData.add(queryDescriptionLine);
		CSVFormatOutputData.add(csvOutputSingleLineData);
		return CSVFormatOutputData;
	}
	
	public void identifyUndefinedQueries() throws Exception{
		
		int queryNumber = 0;
		
		for(String st: finalResultSet){
			if(st.equals("Undefined")){
				
			}else{
			  double queryResult = Double.parseDouble(st);
			  
			  if(queryResult < 0.1 ){
				  QueryLoader.getUndefinedQueriesList().add(QueryLoader.getExternalQueryBeanList().get(queryNumber).getCode());		  
			  }			  
			}
			queryNumber++;
		}	
	}
	
	public void generateResults() throws Exception{
		identifyUndefinedQueries();
		List<Map<String,Object>> finalOutputList = new ArrayList<Map<String,Object>>();
		Map<String,List<Map<String,Object>>> map = new HashMap<String,List<Map<String,Object>>>();
		Map<String,Object> finalOutputSubMap = new HashMap<String,Object>()  ;
		int queryNum = 0;
		for(String st: finalResultSet){
			finalOutputSubMap = new HashMap<String,Object>();
			
			//Make results as Undefined if variables are not defined
			if(QueryLoader.getUndefinedQueriesList().contains( QueryLoader.getExternalQueryBeanList().get(queryNum).getCode()) ){
				finalOutputSubMap.put(HashMapConstants.value, "Undefined");
			}else{
				finalOutputSubMap.put(HashMapConstants.value, st);
			}		
			
			List<Map<String,String>> sampleLevelAreaDataVariableList = new ArrayList<Map<String,String>>();
			Map<String,String> sampleLevelAreaDataVariableSubMap = new HashMap<String,String>();
			sampleLevelAreaDataVariableSubMap.put(HashMapConstants.areaDataVariableId, QueryLoader.getExternalQueryBeanList().get(queryNum).getCode() );
			sampleLevelAreaDataVariableList.add(sampleLevelAreaDataVariableSubMap);
			
			sampleLevelAreaDataVariableSubMap = new HashMap<String,String>();
			sampleLevelAreaDataVariableSubMap.put(HashMapConstants.sampleGeogLevelId , this.sampleGeogLevelId );
			sampleLevelAreaDataVariableList.add(sampleLevelAreaDataVariableSubMap);
			
			finalOutputSubMap.put(HashMapConstants.sampleLevelAreaDataVariableId, sampleLevelAreaDataVariableList);
			finalOutputSubMap.put(HashMapConstants.areaDataVariableId,  QueryLoader.getExternalQueryBeanList().get(queryNum).getCode() );
			
			List<Object> geogInstanceId = new ArrayList<Object>();
			HashMap<String,String> geogInstanceIdSubMap =  new HashMap<String,String>();
			geogInstanceIdSubMap.put(HashMapConstants.sampleGeogLevelId , this.sampleGeogLevelId);
			geogInstanceId.add(this.country);
			geogInstanceId.add(geogInstanceIdSubMap);
			
			finalOutputSubMap.put(HashMapConstants.geogInstanceId, geogInstanceId);
		    queryNum++;
			finalOutputList.add(finalOutputSubMap);
		}
		
		
		map.put(HashMapConstants.areaDataValues, finalOutputList);
		//Generate the YAML FIle
		Util.createOutputYamlFile(map, this.outputFileName);

		
		//Generating the CSV file.
		List<String> outputDataInCSVFormat = this.getCSVFormat();
		Util.createOutputCSVFile(outputDataInCSVFormat, this.outputCSVFileName);
		
		
	}

    public  Map<String,Double> getHashMapForGeographicEntity(int queryNumber , String houseHold) throws ConditionFormatException, MetaDataException{
		return personResultHashMapListForAllQueries.get(queryNumber);
	}


	protected void printResults() {
		log.warn("RESULTS PRINTING FOR NATIONAL LEVEL");
		int queryNumber = 0;
		for (String st : finalResultSet) {
			log.warn(++queryNumber + " " + st);
		}
	}

	protected int convertResultsIntoExternalQueryFormat() {
		// log.debug("convertResultsIntoExternalQueryFormat Enter");
		int currentCount;
		double numeratorResult, denominatorResult;
		for (currentCount = 0; currentCount < queryResultSet.size(); currentCount++) {
			if (QueryLoader.getQueryNumbersWithPercentageOperationList()
					.contains(currentCount)) {
				numeratorResult = Double.parseDouble(queryResultSet
						.get(currentCount));
				denominatorResult = Double.parseDouble(queryResultSet
						.get(currentCount + 1));
				if(denominatorResult>0){
				finalResultSet.add(""
						+ ((numeratorResult / denominatorResult) * 100));
				}else{
					finalResultSet.add("Undefined");
				}
				currentCount +=1;
			} else {
				finalResultSet.add(queryResultSet.get(currentCount));
			}
			
		}
		// log.debug("convertResultsIntoExternalQueryFormat Exit");
		return currentCount;
	}

	protected void performAggregationForInternalQueries() {
		// log.debug("aggregateResultsForInternalQueries Enter");
		String operation, result;
		int queryNumber=0;
		OperatorUtility operatorUtility;
		
		for (InternalQueryBean internalQueryBean : QueryLoader.getQueryBeanList()) {

			operation = internalQueryBean.getOperation();
			operatorUtility = new OperatorUtility();
			
			//System.err.println(internalQueryBean.getCode());
			
			result = operatorUtility.performOperation(personResultHashMapListForAllQueries.get(queryNumber), operation);
			
			queryResultSet.add(result);
			queryNumber++;
		}
		// log.debug("aggregateResultsForInternalQueries Exit");
		
	}
	
	public List<HashMap<String, Double>> getPersonResultHashMapListForAllQueries() {
		return personResultHashMapListForAllQueries;
	}

	public void setPersonResultHashMapListForAllQueries(
			List<HashMap<String, Double>> personResultHashMapListForAllQueries) {
		this.personResultHashMapListForAllQueries = personResultHashMapListForAllQueries;
	}
	
	public List<String> getQueryResultSet() {
		return queryResultSet;
	}

	public void setQueryResultSet(List<String> queryResultSet) {
		this.queryResultSet = queryResultSet;
	}

	public List<String> getFinalResultSet() {
		return finalResultSet;
	}

	public void setFinalResultSet(List<String> finalResultSet) {
		this.finalResultSet = finalResultSet;
	}

}
