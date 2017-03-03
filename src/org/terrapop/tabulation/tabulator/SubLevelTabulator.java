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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.terrapop.tabulation.entities.query.InternalQueryBean;
import org.terrapop.tabulation.exception.ConditionFormatException;
import org.terrapop.tabulation.exception.MetaDataException;
import org.terrapop.tabulation.loader.QueryLoader;
import org.terrapop.tabulation.utilities.HashMapConstants;
import org.terrapop.tabulation.utilities.OperatorUtility;
import org.terrapop.tabulation.utilities.Util;

public class SubLevelTabulator extends BaseTabulator{
	//TODO..change String to float
	private List<Map<String, HashMap<String, Double>>> subLevelWisePersonResultHashMapListForAllQueries = new ArrayList<Map<String, HashMap<String, Double>>>();
	private Map<String, List<String>> queryResultSet = new HashMap<String, List<String>>();
	private Map<String, List<String>> finalResultSet = new HashMap<String, List<String>>();
	private Set<String> geographicalEntityList = new HashSet<String>();
	private Logger log = Logger.getLogger(SubLevelTabulator.class.getName());
	private String subLevel = "";
	private String label = "";
	
	

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public SubLevelTabulator(String subLevel) {
		this.subLevel = subLevel;
		for (int i = 0; i < QueryLoader.getQueryBeanList().size(); i++) {
			Map<String, HashMap<String, Double>> tempMap = new HashMap<String, HashMap<String, Double>>();
			this.subLevelWisePersonResultHashMapListForAllQueries.add(tempMap);
		}
	}
	
	public List<String> getCSVFormat() throws Exception{
		List<String> CSVFormatOutputData = new ArrayList<String>();
		
		String queryDescriptionLine = "CODE,";
		int instanceID = 0;
		
		for(String geogInstanceId: finalResultSet.keySet()){
			instanceID++;
			
			String csvOutputSingleLineData = geogInstanceId + ",";
			for(int queryNum = 0 ; queryNum < QueryLoader.getExternalQueryBeanList().size() ; queryNum++ ){
				if(instanceID==1){
				queryDescriptionLine = queryDescriptionLine + QueryLoader.getExternalQueryBeanList().get(queryNum).getCode() + ",";
				}
				
				if(QueryLoader.getUndefinedQueriesList().contains( QueryLoader.getExternalQueryBeanList().get(queryNum).getCode()) ){
					csvOutputSingleLineData = csvOutputSingleLineData +  "Undefined,";
				}else{
					csvOutputSingleLineData = csvOutputSingleLineData + finalResultSet.get(geogInstanceId).get(queryNum) + ",";
				}
				
			}	
			if(instanceID==1){
				queryDescriptionLine = queryDescriptionLine.substring(0,queryDescriptionLine.length()-1);
				CSVFormatOutputData.add(queryDescriptionLine);
			}
			csvOutputSingleLineData = csvOutputSingleLineData.substring(0,csvOutputSingleLineData.length()-1);
			CSVFormatOutputData.add(csvOutputSingleLineData);
		}
		
		return CSVFormatOutputData;
	}
	
	
	public void generateResults() throws Exception{
		Map<String,List<Map<String,Object>>> areaDataValuesMap = new HashMap<String,List<Map<String,Object>>>();
		List<Map<String,Object>> areaDataValuesList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> subEntryMap = new HashMap<String,Object>()  ;
		for(String geogInstanceId: finalResultSet.keySet()){
			for(int queryNum = 0 ; queryNum < QueryLoader.getExternalQueryBeanList().size() ; queryNum++ ){
				
			subEntryMap = new HashMap<String,Object>();
			
			if(QueryLoader.getUndefinedQueriesList().contains( QueryLoader.getExternalQueryBeanList().get(queryNum).getCode()) ){
				subEntryMap.put(HashMapConstants.value, "Undefined" );
			}else{
				subEntryMap.put(HashMapConstants.value, finalResultSet.get(geogInstanceId).get(queryNum) );
			}
			
			
			List<Map<String,String>> metaList = new ArrayList<Map<String,String>>();
			
			Map<String,String> metaMap = new HashMap<String,String>();
			metaMap.put(HashMapConstants.areaDataVariableId, QueryLoader.getExternalQueryBeanList().get(queryNum).getCode() );
			metaList.add(metaMap);
			
			metaMap = new HashMap<String,String>();
			metaMap.put(HashMapConstants.sampleGeogLevelId , this.sampleGeogLevelId );
			metaList.add(metaMap);
			
			subEntryMap.put(HashMapConstants.sampleLevelAreaDataVariableId, metaList);
			subEntryMap.put(HashMapConstants.areaDataVariableId, QueryLoader.getExternalQueryBeanList().get(queryNum).getCode() );
			
			
			List<Object> geogInstanceIdList = new ArrayList<Object>();
			HashMap<String,String> geogInstanceIdSubMap =  new HashMap<String,String>();
			geogInstanceIdSubMap.put(HashMapConstants.sampleGeogLevelId , this.sampleGeogLevelId);
			geogInstanceIdList.add(geogInstanceId.replaceFirst("^0+(?!$)", ""));
			geogInstanceIdList.add(geogInstanceIdSubMap);
			subEntryMap.put(HashMapConstants.geogInstanceId, geogInstanceIdList);
			
			areaDataValuesList.add(subEntryMap);    
			}
		}
		
		areaDataValuesMap.put(HashMapConstants.areaDataValues, areaDataValuesList );
		
		areaDataValuesMap.put(HashMapConstants.areaDataValues, areaDataValuesList);
		Util.createOutputYamlFile(areaDataValuesMap, this.outputFileName);
		
		List<String> outputCSVDataList = getCSVFormat();
		Util.createOutputCSVFile(outputCSVDataList, this.outputCSVFileName);
		
	}

	public Map<String, Double> getHashMapForGeographicEntity(int queryNumber,
			String houseHold) throws ConditionFormatException,
			MetaDataException {
		// log.debug("getRelevantHashMap Enter");
		String geographicalEntity;
		geographicalEntity = UniversalTabulator.getExtractedValue(houseHold,
				subLevel, "H");
		
		if(geographicalEntity.equals("ZZZZZZZ")){
			return null;
		}

		if (this.subLevelWisePersonResultHashMapListForAllQueries.get(queryNumber)
				.get(geographicalEntity) == null) {
			Map<String, Double> personHashMap = new HashMap<String, Double>();
			this.subLevelWisePersonResultHashMapListForAllQueries.get(queryNumber)
					.put(geographicalEntity,
							(HashMap<String, Double>) personHashMap);
		}
		// log.debug("getRelevantHashMap Exit");
		return subLevelWisePersonResultHashMapListForAllQueries.get(queryNumber)
				.get(geographicalEntity);
	}


	protected void printResults() {
		log.warn("Printing Results for SUBLEVEL  " + subLevel);
		int queryNumber = 0;
		for (String state : finalResultSet.keySet()) {
			queryNumber = 0;
			for (String st : finalResultSet.get(state)) {
			  log.warn(++queryNumber + " " + this.subLevel + " " + state + " " + st);
			}
		}
	}

	protected int convertResultsIntoExternalQueryFormat() {
		// log.debug("Enter convertResultsOfInternalQueryToExternalQuery");
		double numeratorResult, denominatorResult;
		int currentCount = 0;
		for (String subLevel : queryResultSet.keySet()) {
		  List<String> subLevelList = queryResultSet.get(subLevel);

      if (finalResultSet.get(subLevel) == null) {
        finalResultSet.put(subLevel, new ArrayList<String>());
      }

		  for (currentCount = 0; currentCount < subLevelList.size(); currentCount++) {
				// log.debug("for this sub level " + subLevel + " queryResultSetSize" + queryResultSet.get(subLevel).size() );
				
				if (QueryLoader.getQueryNumbersWithPercentageOperationList().contains(currentCount)) {
				  
					numeratorResult = Double.parseDouble(queryResultSet.get(subLevel).get(currentCount));
					denominatorResult = Double.parseDouble(queryResultSet.get(subLevel).get(currentCount + 1));

					if (denominatorResult > 0) {
						finalResultSet
								.get(subLevel)
								.add(""
										+ ((numeratorResult / denominatorResult) * 100));
					} else {
						finalResultSet.get(subLevel).add("Undefined");
					}

					currentCount += 1;
				} else {
					finalResultSet.get(subLevel).add(
							queryResultSet.get(subLevel).get(currentCount));
				}

			}
		}
		// log.debug("Exit convertResultsOfInternalQueryToExternalQuery");
		return currentCount;
	}

	protected void performAggregationForInternalQueries() {
		// log.debug("Enter performAggregationForInternalQueries");
		String operation, result;
		int queryNumber = 0;
		
		for (InternalQueryBean internalQueryBean : QueryLoader.getQueryBeanList()) {
		  
		  Map<String, HashMap<String, Double>>queryValues = subLevelWisePersonResultHashMapListForAllQueries.get(queryNumber);
		  operation = internalQueryBean.getOperation();
		  
			for (String subLevel : queryValues.keySet()) {
   		
				result = new OperatorUtility().performOperation(queryValues.get(subLevel), operation);
   
				if (queryResultSet.get(subLevel) == null) {
					queryResultSet.put(subLevel, new ArrayList<String>());
				}
	
				queryResultSet.get(subLevel).add(result);
			}
			
			queryNumber++;
		}
		// log.debug("Exit performAggregationForInternalQueries");
	}

	public Set<String> getGeographicalEntityList() {
		return geographicalEntityList;
	}

	public void setGeographicalEntityList(Set<String> geographicalEntityList) {
		this.geographicalEntityList = geographicalEntityList;
	}

	public List<Map<String, HashMap<String, Double>>> getSubLevelWisePersonResultHashMapListForAllQueries() {
		return subLevelWisePersonResultHashMapListForAllQueries;
	}

	public void setSubLevelWisePersonResultHashMapListForAllQueries(
			List<Map<String, HashMap<String, Double>>> stateWisePersonResultHashMapListForAllQueries) {
		this.subLevelWisePersonResultHashMapListForAllQueries = stateWisePersonResultHashMapListForAllQueries;
	}

	public Map<String, List<String>> getQueryResultSet() {
		return queryResultSet;
	}

	public void setQueryResultSet(Map<String, List<String>> queryResultSet) {
		this.queryResultSet = queryResultSet;
	}

	public Map<String, List<String>> getFinalResultSet() {
		return finalResultSet;
	}

	public void setFinalResultSet(Map<String, List<String>> finalResultSet) {
		this.finalResultSet = finalResultSet;
	}

	public String getSubLevel() {
		return subLevel;
	}

	public void setSubLevel(String subLevel) {
		this.subLevel = subLevel;
	}

}