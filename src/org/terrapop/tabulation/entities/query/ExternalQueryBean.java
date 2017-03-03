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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ExternalQueryBean extends QueryBean {

	@SuppressWarnings("unused")
  private static Logger log = Logger.getLogger(ExternalQueryBean.class.getName());

	private Map<String,List<List<String>>> numerator = null;
	private Map<String,List<List<String>>> denominator = null;
	private Map<String,List<List<String>>> tabulation = null;
	
	public Map<String, List<List<String>>> getTabulation() {
		return tabulation;
	}
	public void setTabulation(Map<String, List<List<String>>> universe) {
		this.tabulation = universe;
	}
	public Map<String, List<List<String>>> getNumerator() {
		return numerator;
	}
	public void setNumerator(Map<String, List<List<String>>> numerator) {
		this.numerator = numerator;
	}
	public Map<String, List<List<String>>> getDenominator() {
		return denominator;
	}
	public void setDenominator(Map<String, List<List<String>>> denominator) {
		this.denominator = denominator;
	}

}
