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
package org.terrapop.tabulation.entities.query;

import org.apache.log4j.Logger;

public class QueryBean {

	@SuppressWarnings("unused")
  private static Logger log = Logger.getLogger(QueryBean.class
			.getName());

	private String name;
	private String code;
	private String description;
	private String data_table_id;
	private String operation;
	private String query_variable;
	private String query_on;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDataTableId() {
		return data_table_id;
	}

	public void setDataTableId(String data_table_id) {
		this.data_table_id = data_table_id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getQueryVariable() {
		return query_variable;
	}

	public void setQueryVariable(String query_variable) {
		this.query_variable = query_variable;
	}

	public String getQuery_on() {
		return query_on;
	}

	public void setQuery_on(String query_on) {
		this.query_on = query_on;
	}
	

}
