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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.terrapop.tabulation.entities.ConditionBean;
import org.terrapop.tabulation.entities.HouseholdBean;
import org.terrapop.tabulation.entities.query.InternalQueryBean;
import org.terrapop.tabulation.exception.ConditionFormatException;
import org.terrapop.tabulation.exception.MetaDataException;
import org.terrapop.tabulation.loader.MetaDataLoader;
import org.terrapop.tabulation.loader.QueryLoader;
import org.terrapop.tabulation.parser.Parse;
import org.terrapop.tabulation.utilities.OperatorUtility;

public class UniversalTabulator {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(UniversalTabulator.class.getName());
	private int debug;

	public UniversalTabulator(int debug) {
		this.debug = debug;
	}

	// TODO make list of subleveltabulators based on input from yaml -
	// sample_desc.yaml, FLAD and SLAD blocks.
	private List<Boolean> houseHoldQueryDecisions = null;
	private NationalTabulator nationalTabulator;

	private List<SubLevelTabulator> subLevelTabulatorList = new ArrayList<SubLevelTabulator>();

	public List<SubLevelTabulator> getSubLevelTabulator() {
		return subLevelTabulatorList;
	}

	public void setSubLevelTabulator(List<SubLevelTabulator> subLevelTabulator) {
		this.subLevelTabulatorList = subLevelTabulator;
	}

	public void startAggregatingResults() {
		nationalTabulator.aggregateResultsAtGeographicLevel();

		for (SubLevelTabulator subLevelTabulator : subLevelTabulatorList) {
			subLevelTabulator.aggregateResultsAtGeographicLevel();
		}
	}

	public void startWritingResults() throws Exception {
		nationalTabulator.generateResults();

		for (SubLevelTabulator subLevelTabulator : subLevelTabulatorList) {
			subLevelTabulator.generateResults();
		}
	}

	public void startTabulating(HouseholdBean houseHoldBean) throws ConditionFormatException, MetaDataException {

		houseHoldQueryDecisions = this.evaluateQueriesOnHouseHoldData(houseHoldBean, QueryLoader.getQueryBeanList());

		this.evaluatePersonalConditionsForEveryHouseHoldForEveryQuery(houseHoldBean, QueryLoader.getQueryBeanList(),
				houseHoldQueryDecisions);

		// log.debug("Universal Tabulating Process Ends");
	}

	public void evaluatePersonalConditionsForEveryHouseHoldForEveryQuery(HouseholdBean householdBean,
			List<InternalQueryBean> internalQueryBeanList, List<Boolean> queryHouseHoldConditionResultList)
			throws ConditionFormatException, MetaDataException {
		// log.debug("evaluatePersonalConditionsForEveryHouseHoldForEveryQuery
		// Enter");
		int currentQueryIndex = 0;
		Boolean conditionResult = true;
		List<List<String>> pConditionList = null;

		for (InternalQueryBean internalQueryBean : internalQueryBeanList) {

			internalQueryBean.setIsFullcount(householdBean.isFullcount());

			if (queryHouseHoldConditionResultList.get(currentQueryIndex) == true) {
				if (internalQueryBean.getQuery_on() != null && internalQueryBean.getQuery_on().equals("P")) {
					int personNumber = 0;

					for (String person : householdBean.getPersonsRecordList()) {

						conditionResult = true;
						pConditionList = internalQueryBean.getWhere_p();

						conditionResult = evaluatePConditionList(conditionResult, pConditionList, person,
								householdBean.getPersonsRecordListData().get(personNumber));

						boolean individualUpdateCheck = false;

						if (conditionResult == true) {
							individualUpdateCheck = this.updatePersonResultHashMapForCurrentQuery(person,
									householdBean.getHouseholdRecord(), internalQueryBean, currentQueryIndex, false);
						} else {
							individualUpdateCheck = this.updatePersonResultHashMapForCurrentQuery(person,
									householdBean.getHouseholdRecord(), internalQueryBean, currentQueryIndex, true);
						}

						personNumber++;

						if (individualUpdateCheck) {

						}

					}

				} else {

					updatePersonResultHashMapForCurrentQuery(householdBean.getHouseholdRecord(),
							householdBean.getHouseholdRecord(), internalQueryBean, currentQueryIndex, false);

				}
			} else {
				// household condition not satisfied
				if (internalQueryBean.getQuery_on() != null && internalQueryBean.getQuery_on().equals("P")) {
					for (String person : householdBean.getPersonsRecordList()) {
						this.updatePersonResultHashMapForCurrentQuery(person, householdBean.getHouseholdRecord(),
								internalQueryBean, currentQueryIndex, true);
					}
				} else {
					updatePersonResultHashMapForCurrentQuery(householdBean.getHouseholdRecord(),
							householdBean.getHouseholdRecord(), internalQueryBean, currentQueryIndex, true);

				}
			}
			currentQueryIndex++;
		}
		// log.debug("evaluatePersonalConditionsForEveryHouseHoldForEveryQuery
		// Exit");
	}

	private Boolean evaluatePConditionList(Boolean conditionResult, List<List<String>> pConditionList, String person,
			Map<String, String> previousDataMap) throws ConditionFormatException, MetaDataException {
		// log.debug("evaluatePConditionList Enter");
		ConditionBean conditionBean;

		for (List<String> pCondition : pConditionList) {
			// TODO: This should be done at the beginning.
			// pConditionNullCaseHandling(pCondition);

			if (pCondition.size() >= 2) {

				if (pCondition.get(1).equals("BETWEEN")) {
					conditionBean = new ConditionBean(pCondition.get(0), pCondition.get(1), pCondition.get(2),
							pCondition.get(3));
				} else {

					if (pCondition.get(1).equals("IN")) {
						List<String> innerList = new ArrayList<String>();
						for (int index = 2; index < pCondition.size(); index++) {
							innerList.add(pCondition.get(index));
						}
						conditionBean = new ConditionBean(pCondition.get(0), pCondition.get(1), innerList);
					} else {
						conditionBean = new ConditionBean(pCondition.get(0), pCondition.get(1), pCondition.get(2));
					}
				}

				conditionResult = conditionResult & this.evaluateCondition(conditionBean, person, "P", previousDataMap);

			} else {
				if (pCondition.size() == 1) {
					// parenthetical expression
					// System.err.println("EXPRESSION: " + pCondition.get(0));

					try {

						Parse sp = new Parse(pCondition.get(0), previousDataMap, person, "P", debug);

						// conditionResult = conditionResult &
						// sp.parseExpression();

						sp.parseExpression();

						sp.closeInput();

					} catch (UnsupportedEncodingException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}

				}
			}
		}
		// log.debug("evaluatePConditionList Exit");
		return conditionResult;
	}

	/*
	 * private void pConditionNullCaseHandling(List<String> pCondition) throws
	 * ConditionFormatException { //
	 * log.debug("pConditionNullCaseHandling Enter"); if (pCondition == null ||
	 * pCondition.get(0) == null || pCondition.get(1) == null ||
	 * pCondition.get(2) == null) { throw new
	 * ConditionFormatException("Condition Format Issue " + pCondition.get(0) +
	 * " " + pCondition.get(1) + " " + pCondition.get(2)); } //
	 * log.debug("pConditionNullCaseHandling Exit"); }
	 */

	public boolean updatePersonResultHashMapForCurrentQuery(String person, String houseHold,
			InternalQueryBean internalQueryBean, int queryNumber, Boolean recordWeightUnset)
			throws MetaDataException, ConditionFormatException {

		// log.debug("UpdatePersonResultHashMapForCurrentQuery Enter");
		String queryVariable = internalQueryBean.getQueryVariable(), indicesValue;
		int recordWeight;

		if (internalQueryBean.getQuery_on() != null && internalQueryBean.getQuery_on().equals("P"))
			indicesValue = getExtractedValue(person, queryVariable, "P");
		else
			indicesValue = getExtractedValue(person, queryVariable, "H");

		/*
		 * if (recordWeightUnset) { recordWeight = 0; } else { recordWeight =
		 * Integer.parseInt(person.substring(25, 33)); }
		 */

		if (recordWeightUnset) {
			recordWeight = 0;
		} else {
			if (internalQueryBean.isFullcount() == 1) {
				recordWeight = 100;
			} else {
				recordWeight = Integer.parseInt(person.substring(25, 33));
			}
		}

		this.updateHashMap(this.nationalTabulator.getHashMapForGeographicEntity(queryNumber, houseHold), indicesValue,
				recordWeight);

		for (SubLevelTabulator subLevelTabulator : subLevelTabulatorList) {
			this.updateHashMap(subLevelTabulator.getHashMapForGeographicEntity(queryNumber, houseHold), indicesValue,
					recordWeight);
		}

		// log.debug("UpdatePersonResultHashMapForCurrentQuery Exit");
		return true;
	}

	public static String getExtractedValue(String person, String queryVariable, String type)
			throws ConditionFormatException, MetaDataException {
		// log.debug("Enter getExtractedValue");
		String indicesValue;
		int variableIndicesFromMetaDataHashMap;
		int variableWidthFromMetaDataHashMap;

		String value[] = UniversalTabulator.getConditionVariableIndicesFromMetaData(queryVariable, type);

		variableIndicesFromMetaDataHashMap = Integer.parseInt(value[0]);
		variableWidthFromMetaDataHashMap = Integer.parseInt(value[1]);

		indicesValue = person.substring(variableIndicesFromMetaDataHashMap - 1,
				variableIndicesFromMetaDataHashMap + variableWidthFromMetaDataHashMap - 1);
		// log.debug("Exit getExtractedValue");
		return indicesValue;
	}

	public void updateHashMap(Map<String, Double> personResultHashMapForCurrentQuery, String indicesValue,
			int recordWeight) {

		if (personResultHashMapForCurrentQuery == null) {
			return;
		}
		// log.debug("updateHashMap Enter");
		if (personResultHashMapForCurrentQuery.containsKey(indicesValue)) {
			personResultHashMapForCurrentQuery.put(indicesValue,
					((personResultHashMapForCurrentQuery.get(indicesValue)) + recordWeight / 100));
		} else {
			personResultHashMapForCurrentQuery.put(indicesValue, recordWeight / 100.0);
		}
		// log.debug("updateHashMap Exit");
	}

	public List<Boolean> evaluateQueriesOnHouseHoldData(HouseholdBean householdBean,
			List<InternalQueryBean> internalQueryBeanList) throws ConditionFormatException, MetaDataException {
		// log.debug("evaluateHouseHoldConditions Enter");
		List<Boolean> houseHoldQueryDecisions = new ArrayList<Boolean>();
		Boolean conditionResult = true;

		for (InternalQueryBean internalQueryBean : internalQueryBeanList) {
			List<List<String>> hConditionList = internalQueryBean.getWhere_h();
			conditionResult = true;

			for (List<String> hCondition : hConditionList) {
				// TODO: this should be rather done during initialization and
				// not now.
				// checkHConditionNullCaseHandling(hCondition);

				ConditionBean conditionBean = null;

				// null household Conditions should not be evaluated...they
				// shoudl be considered as true.
				if (hCondition.size() > 2) {

					if (hCondition.get(1).equals("BETWEEN")) {
						conditionBean = new ConditionBean(hCondition.get(0), hCondition.get(1), hCondition.get(2),
								hCondition.get(3));
					} else {

						if (hCondition.get(1).equals("IN")) {
							List<String> innerList = new ArrayList<String>();
							for (int index = 2; index < hCondition.size(); index++) {
								innerList.add(hCondition.get(index));
							}
							conditionBean = new ConditionBean(hCondition.get(0), hCondition.get(1), innerList);
						} else {
							conditionBean = new ConditionBean(hCondition.get(0), hCondition.get(1), hCondition.get(2));
						}
					}

					// so that both the records and the hashMapdata is passed
					// alongwith.
					conditionResult = conditionResult & evaluateCondition(conditionBean,
							householdBean.getHouseholdRecord(), "H", householdBean.getHouseholdData());

				} else {

					if (hCondition.size() == 1) {
						// parenthetical expression
						// System.err.println("EXPRESSION: " +
						// pCondition.get(0));

						try {

							Parse sp = new Parse(hCondition.get(0), householdBean.getHouseholdData(),
									householdBean.getHouseholdRecord(), "H", debug);

							// conditionResult = conditionResult &
							// sp.parseExpression();

							sp.parseExpression();

							sp.closeInput();

						} catch (UnsupportedEncodingException e) {

							e.printStackTrace();
						} catch (IOException e) {

							e.printStackTrace();
						}

					}

				}

			}
			houseHoldQueryDecisions.add(conditionResult);
		}

		// log.debug("evaluateHouseHoldConditions Exit");
		return houseHoldQueryDecisions;
	}

	public void addNewGeographicalEntity(Set<String> geographicalEntities, String household, String subLevel)
			throws MetaDataException, ConditionFormatException {
		String geographicalEntity = UniversalTabulator.getExtractedValue(household, subLevel, "H");
		if (!geographicalEntities.contains(geographicalEntity)) {
			geographicalEntities.add(geographicalEntity);
		}
	}

	/*
	 * private void checkHConditionNullCaseHandling(List<String> hCondition)
	 * throws ConditionFormatException { //
	 * log.debug("checkHConditionNullCaseHandling Enter"); if (hCondition !=
	 * null && ( hCondition.get(0) == null || hCondition.get(1) == null ||
	 * hCondition.get(2) == null) ){ throw new
	 * ConditionFormatException("hCondition Format Problem" + hCondition.get(0)
	 * + " " + hCondition.get(1) + " " + hCondition.get(2)); } //
	 * log.debug("checkHConditionNullCaseHandling Exit"); }
	 */

	public Boolean evaluateCondition(ConditionBean conditionBean, String personOrHousehold, String type,
			Map<String, String> dataMap) throws MetaDataException, ConditionFormatException {

		OperatorUtility operatorUtility = new OperatorUtility();
		boolean conditionResult;
		String houseHoldVariableValue;

		if (dataMap.get(conditionBean.getVariable()) == null) {

			houseHoldVariableValue = getExtractedValue(personOrHousehold, conditionBean.getVariable(), type);

			dataMap.put(conditionBean.getVariable(), houseHoldVariableValue);
		} else {
			houseHoldVariableValue = dataMap.get(conditionBean.getVariable());
		}

		conditionResult = operatorUtility.evaluateOperator(conditionBean, houseHoldVariableValue);

		// log.debug("evalauteCondition Exits");
		return conditionResult;

	}

	public static String[] getConditionVariableIndicesFromMetaData(String conditionVariable, String type)
			throws ConditionFormatException, MetaDataException {
		// log.debug("getConditionVariableIndicesFromMetaData Enter"
		// + conditionVariable + " " + type + " " + index);
		Map<String, String> metaDataHashMap = MetaDataLoader.getMetaData();
		String valueArray[] = null;
		String key = "";
		if (conditionVariable == null)
			throw new ConditionFormatException("Variable Value Missing " + conditionVariable);
		key = (type + "#" + conditionVariable);
		if (key == null || metaDataHashMap.get(key) == null)
			throw new MetaDataException("Missing key: type " + type + " conditionVariable: " + conditionVariable);
		// log.debug("getConditionVariableIndicesFromMetaData Exit");
		valueArray = metaDataHashMap.get(key).split("#");

		return valueArray;
	}

	public List<Boolean> getHouseHoldQueryDecisions() {
		return houseHoldQueryDecisions;
	}

	public void setHouseHoldQueryDecisions(List<Boolean> houseHoldQueryDecisions) {
		this.houseHoldQueryDecisions = houseHoldQueryDecisions;
	}

	public NationalTabulator getNationalTabulator() {
		return nationalTabulator;
	}

	public void setNationalTabulator(NationalTabulator nationalTabulator) {
		this.nationalTabulator = nationalTabulator;
	}

}
