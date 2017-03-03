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

import java.util.*;

public class HouseholdBean {

	// private static Logger log =
	// Logger.getLogger(GranularDataBean.class.getName());
	private String householdRecord = new String();;
	private List<String> personsRecordList = new ArrayList<String>();
	private List<Map<String, String>> personsRecordListData = new ArrayList<Map<String, String>>();
	private Map<String, String> householdData = new HashMap<String, String>();
	private Integer is_fullcount = -1;

	public void initializePersonPreviousData() {
		int length = personsRecordList.size();
		while (length > 0) {

			Map<String, String> tempMap = new HashMap<String, String>();
			personsRecordListData.add(tempMap);
			length--;
		}
	}

	public List<Map<String, String>> getPersonsRecordListData() {
		return personsRecordListData;
	}

	public void setPersonsRecordListData(List<Map<String, String>> personsRecordListData) {
		this.personsRecordListData = personsRecordListData;
	}

	public Map<String, String> getHouseholdData() {
		return householdData;
	}

	public void setHouseholdData(Map<String, String> householdData) {
		this.householdData = householdData;
	}

	public void setPersonsRecordList(List<String> personsRecordList) {
		this.personsRecordList = personsRecordList;
	}

	public String getHouseholdRecord() {
		return householdRecord;
	}

	public void setHouseholdRecord(String houseHold) {
		this.householdRecord = houseHold;
	}

	public List<String> getPersonsRecordList() {
		return personsRecordList;
	}

	public void setPersonRecrodList(List<String> persons) {
		this.personsRecordList = persons;
	}

	public Integer isFullcount() {
		return is_fullcount;
	}

	public void setIsFullcount(Integer is_fullcount) {
		this.is_fullcount = is_fullcount;
	}

}
