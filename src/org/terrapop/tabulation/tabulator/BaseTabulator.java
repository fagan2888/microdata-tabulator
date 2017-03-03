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

import org.apache.log4j.Logger;

public abstract class BaseTabulator {

	protected Logger log = Logger.getLogger(BaseTabulator.class.getName());
	protected String sampleGeogLevelId;
	protected String geogInstanceId;
	protected String outputFileName;
	protected String outputCSVFileName;

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	public String getOutputCSVFileName() {
		return outputCSVFileName;
	}

	public void setOutputCSVFileName(String outputCSVFileName) {
		this.outputCSVFileName = outputCSVFileName;
	}

	public String getSampleGeogLevelId() {
		return sampleGeogLevelId;
	}

	public void setSampleGeogLevelId(String sampleGeogLevelId) {
		this.sampleGeogLevelId = sampleGeogLevelId;
	}

	public String getGeogInstanceId() {
		return geogInstanceId;
	}

	public void setGeogInstanceId(String geogInstanceId) {
		this.geogInstanceId = geogInstanceId;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public void aggregateResultsAtGeographicLevel() {

		log.debug("aggregateResultsAtGeographicLevel Enter");
		performAggregationForInternalQueries();
		convertResultsIntoExternalQueryFormat();
		log.debug("aggregateResultsAtGeographicLevel Exit");

	}

	protected abstract int convertResultsIntoExternalQueryFormat();

	protected abstract void performAggregationForInternalQueries();

	protected abstract void printResults();

}
