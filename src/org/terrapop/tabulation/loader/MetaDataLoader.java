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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.log4j.Logger;



public class MetaDataLoader {

	private static Map<String, String> metaData = null;
	private static Logger log = Logger
			.getLogger(MetaDataLoader.class.getName());
	private static String metaDataFile = "./Resource/ipumsi_metadata.csv";

	private MetaDataLoader() {

	}
	
  public static void setMetaDataFile(String file){
  	MetaDataLoader.metaDataFile = file;
  }
  
	public static synchronized Map<String, String> getMetaData() {
		DataInputStream in = null;
		BufferedReader br = null;
		String inputLine = null;
		String[] inputLineArray = null;

		     if(metaData==null){
					try {
						log.info("Instantiation of Singleton Object of Class MetaData Started");
						FileInputStream inp = new FileInputStream(metaDataFile);
						in = new DataInputStream(inp);
						br = new BufferedReader(new InputStreamReader(in));

						metaData = new HashMap<String, String>();
            // # is used as the separator.
						while ((inputLine = br.readLine()) != null) {
							inputLineArray = inputLine.split(",");
							metaData.put(inputLineArray[0] + "#"
									+ inputLineArray[1], inputLineArray[2] + "#"
											+ inputLineArray[3]);

						}
						// log.debug("Instantiation of Singleton Object of Class MetaData Completed");

					} catch (IOException e) {
						log.error(e.getStackTrace() + " ");
					} finally {
						try {
							br.close();
							in.close();
						} catch (IOException e) {
							log.error("Error in closing streams"
									+ e.getStackTrace());
						}
					}
		     }
		// log.debug("getMetaData Exit");
		return metaData;
	
	}
	
	public static void main(String args[]){
	  System.out.println(MetaDataLoader.getMetaData().size());
	}
	 
}
