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
package org.terrapop.tabulation.loader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.esotericsoftware.yamlbeans.YamlReader;

import org.terrapop.tabulation.entities.query.ExternalQueryBean;
import org.terrapop.tabulation.entities.query.InternalQueryBean;
import org.terrapop.tabulation.exception.InputDataException;
import org.terrapop.tabulation.exception.QueryFormatException;

public class QueryLoader {

	private QueryLoader() {
	}

	private static Logger log = Logger.getLogger(QueryLoader.class.getName());
	private static List<Map<String, Object>> testLoader;
	private static Object obj = null;
	private static List<ExternalQueryBean> externalQueryBeanList = null;
	private static List<InternalQueryBean> internalQueryBeanList = null;
	private static Set<Integer> queryNumbersWithPercentageOperationList = new HashSet<Integer>();
	private static String queryFile = SampleDescLoader.getSampleDescObject().getRules_file_path();
  private static Set<String> undefinedQueriesSet = new HashSet<String>();

	
	public static Set<String> getUndefinedQueriesList() {
		return undefinedQueriesSet;
	}
	
	public static void setUndefinedQueriesList(Set<String> undefinedQueriesList) {
		undefinedQueriesSet = undefinedQueriesList;
	}

	public static synchronized Object loadEntireFileObject() {
		// log.debug("getQueryBeanList Enter");
		YamlReader reader = null;
		DataInputStream in = null;
		BufferedReader br = null;
		if (obj == null) {

			try {
				// log.debug("Instantiation of QueryBeanList Started");
				System.out.println("Query File is " + queryFile);
				// InputStream inp =

				FileInputStream inp = new FileInputStream(queryFile);
				in = new DataInputStream(inp);
				br = new BufferedReader(new InputStreamReader(in));
				reader = new YamlReader(br);

				obj = reader.read();

			} catch (Exception e) {
			  
				try {
					log.error(e.getMessage());
					throw new InputDataException("Please check the QUERY File --> " + e.getStackTrace());

				} catch (Exception e1) {
					log.error(e1.getMessage());
					// log.debug("Instantiation of QueryBeanList Incomplete");
				}
				
			}
			// log.debug("Instantiation of QueryBeanList Completed");
		}
		// log.debug("getQueryBeanList Exit");

		return obj;

	}

	public static synchronized List<Map<String, Object>> getTableInformation() {
		if (obj == null) {
			loadEntireFileObject();
		}

		if (testLoader == null) {
		  /*
			Map<String, List<Map<String, Object>>> map = (Map<String, List<Map<String, Object>>>) obj;
			List<Map<String, Object>> tester = (List<Map<String, Object>>) map
					.get("data_tables");
		//	testLoader = new ArrayList<Map<String, Object>>();
            testLoader = tester;
//			for (Map<String, Object> temp : tester) {
//				 Map<String,String> m = new HashMap<String,String>();
//
//				for (String s : temp.keySet()) {
//                     
//					if (s.equals("area_data_table_group_ids")) {
//						List<String> list = (List<String>) temp
//								.get("area_data_table_group_ids");
//						String output = "";
//						for (String tt : list) {
//							output = "\'" + tt + "\'" + ",";
//						}
//						output = output.substring(0, output.length() - 1);
//						output = '[' + output + "]";
//						m.put(s, output);
//					}else{
//						m.put(s, (String)temp.get(s));
//					}
//
//				}
//				
//				testLoader.add(m);
//			}
  */
		}
         
		return testLoader;
	}

	@SuppressWarnings("unchecked")
  public static synchronized List<InternalQueryBean> getQueryBeanList() {
		if (obj == null) {
			loadEntireFileObject();
		}

		if (internalQueryBeanList == null) {

			Map<String, List<Map<String, Object>>> map = (HashMap<String, List<Map<String, Object>>>) obj;
			List<Map<String, Object>> tempExternalQueryBeanList = map.get("rules");
			externalQueryBeanList = new ArrayList<ExternalQueryBean>();

			for (Map<String, Object> st : tempExternalQueryBeanList) {

				ExternalQueryBean eqb = new ExternalQueryBean();
				eqb.setCode((String) st.get("code"));
				eqb.setDataTableId((String) st.get("data_table_id"));
				eqb.setDescription((String) st.get("description"));
				eqb.setName((String) st.get("name"));
				eqb.setOperation((String) st.get("operation"));
				eqb.setQueryVariable((String) st.get("query_variable"));
				eqb.setQuery_on((String) st.get("query_on"));
				eqb.setDenominator((Map<String, List<List<String>>>) st.get("denominator"));
				eqb.setNumerator((Map<String, List<List<String>>>) st.get("numerator"));
				eqb.setTabulation((Map<String, List<List<String>>>) st.get("tabulation"));
				externalQueryBeanList.add(eqb);
			}

			try {
				internalQueryBeanList = convertIntoQueryBean(externalQueryBeanList);
			} catch (QueryFormatException e) {
				log.error(e.getStackTrace() + "  ");
			} catch (Exception e) {
				try {
					throw new InputDataException("Please check the QUERY File --> " + e.getMessage());
				} catch (Exception e1) {
					log.error(e1.getMessage());
					// log.debug("Instantiation of QueryBeanList Incomplete");
					return new ArrayList<InternalQueryBean>();
				}
			}

		}

		return internalQueryBeanList;

	}

	public static List<InternalQueryBean> convertIntoQueryBean(
			List<ExternalQueryBean> externalQueryBeanList)
			throws QueryFormatException {
		// log.debug("convertIntoQueryBean Enter");
		List<InternalQueryBean> internalQueryBeanList = new ArrayList<InternalQueryBean>();
		int queryNumber = 0;
		InternalQueryBean internalQueryBean = null;

		for (ExternalQueryBean externalQueryBean : externalQueryBeanList) {
			if (externalQueryBean.getNumerator() != null && externalQueryBean.getDenominator() != null) {

				// log.debug("Query With Percentage Operation Encountered");
				queryNumbersWithPercentageOperationList.add(queryNumber);
				internalQueryBean = CovertQueryBeanFormat(externalQueryBean, "numerator");
				internalQueryBeanList.add(internalQueryBean);
				internalQueryBean = CovertQueryBeanFormat(externalQueryBean, "denominator");
				internalQueryBeanList.add(internalQueryBean);
				queryNumber = queryNumber + 2;
				
			} else {

				if (externalQueryBean.getTabulation() != null) {
					// log.debug("Query With Non-Percentage Operation Encountered");
					internalQueryBean = CovertQueryBeanFormat(externalQueryBean, "tabulation");
					internalQueryBeanList.add(internalQueryBean);
					queryNumber++;
				} else {
					throw new QueryFormatException("Numerator / Denominator / tabulation Tag Missing");
				}
			}
		}
		// log.debug("convertIntoQueryBean Exit");
		return internalQueryBeanList;

	}

	public static InternalQueryBean CovertQueryBeanFormat(
			ExternalQueryBean externalQueryBean, String placeHolder) {
		// Add a Lot of Null Checks out Here
		// log.debug("CovertQueryBeanFormat Enter: ExternalQueryBean to InternalQueryBean Conversion Will Take Place Here");
		InternalQueryBean internalQueryBean = new InternalQueryBean();

		try {

			if (externalQueryBean.getCode() == null)
				throw new QueryFormatException("Id");
			else
				internalQueryBean.setCode(externalQueryBean.getCode());

			if (externalQueryBean.getDataTableId() == null)
				throw new QueryFormatException("Data_Table_ID");
			else
				internalQueryBean.setDataTableId(externalQueryBean.getDataTableId());

			if (externalQueryBean.getDescription() == null)
				throw new QueryFormatException("Description");
			else
				internalQueryBean.setDescription(externalQueryBean.getDescription());

			if (externalQueryBean.getName() == null)
				throw new QueryFormatException("Name");
			else
				internalQueryBean.setName(externalQueryBean.getName());

			if (externalQueryBean.getOperation() == null)
				throw new QueryFormatException("Operation");
			else
				internalQueryBean.setOperation(externalQueryBean.getOperation());

			if (externalQueryBean.getQueryVariable() == null)
				throw new QueryFormatException("Query_Variable");
			else
				internalQueryBean.setQueryVariable(externalQueryBean.getQueryVariable());

			if (externalQueryBean.getQueryVariable() == null)
				throw new QueryFormatException("query_on");
			else
				internalQueryBean.setQuery_on(externalQueryBean.getQuery_on());

			if (placeHolder.equals("tabulation")) {

				if (externalQueryBean.getTabulation() == null
						|| externalQueryBean.getTabulation().get("where_h") == null
						|| externalQueryBean.getTabulation().get("where_p") == null) {
					throw new QueryFormatException("tabulation");
				} else {
					internalQueryBean.setWhere_h(externalQueryBean.getTabulation().get("where_h"));
					internalQueryBean.setWhere_p(externalQueryBean.getTabulation().get("where_p"));
				}
			}

			if (placeHolder.equals("numerator")) {

				if (externalQueryBean.getNumerator() == null
						|| externalQueryBean.getNumerator().get("where_h") == null
						|| externalQueryBean.getNumerator().get("where_p") == null) {
					throw new QueryFormatException("Numerator");
				} else {
					internalQueryBean.setWhere_h(externalQueryBean.getNumerator().get("where_h"));
					internalQueryBean.setWhere_p(externalQueryBean.getNumerator().get("where_p"));
				}
			}

			if (placeHolder.equals("denominator")) {

				if (externalQueryBean.getDenominator() == null
						|| externalQueryBean.getDenominator().get("where_h") == null
						|| externalQueryBean.getDenominator().get("where_p") == null) {
					throw new QueryFormatException("Denominator");
				} else {
					internalQueryBean.setWhere_h(externalQueryBean.getDenominator().get("where_h"));
					internalQueryBean.setWhere_p(externalQueryBean.getDenominator().get("where_p"));
				}
			}

		} catch (QueryFormatException e) {
			log.error(e.getMessage());
		}
		// log.debug("Conversion of Queries From One Form To Another is Completed Exit");
		return internalQueryBean;
	}

	public static Set<Integer> getQueryNumbersWithPercentageOperationList() {
		return queryNumbersWithPercentageOperationList;
	}

	public static void setQueryNumbersWithPercentageOperationList(
			Set<Integer> _queryNumbersWithPercentageOperationList) {
	  queryNumbersWithPercentageOperationList = _queryNumbersWithPercentageOperationList;
	}

	public static synchronized List<ExternalQueryBean> getExternalQueryBeanList() {
		if (externalQueryBeanList == null) {
			getQueryBeanList();
		}
		return externalQueryBeanList;
	}

	public static String getQueryFile() {
		return queryFile;
	}

	public static void setQueryFile(String queryFile) {
		QueryLoader.queryFile = queryFile;
	}

	public static void main(String args[]) {
		//List<InternalQueryBean> quer = getQueryBeanList();
		getTableInformation();
	}

}
