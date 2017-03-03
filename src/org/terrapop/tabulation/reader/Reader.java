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

package org.terrapop.tabulation.reader;

import org.terrapop.tabulation.entities.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.terrapop.tabulation.entities.HouseholdBean;
import org.terrapop.tabulation.entities.SampleDesc;
import org.terrapop.tabulation.exception.ConditionFormatException;
import org.terrapop.tabulation.exception.MetaDataException;
import org.terrapop.tabulation.loader.MetaDataLoader;
import org.terrapop.tabulation.loader.QueryLoader;
import org.terrapop.tabulation.loader.SampleDescLoader;
import org.terrapop.tabulation.tabulator.NationalTabulator;
import org.terrapop.tabulation.tabulator.SubLevelTabulator;
import org.terrapop.tabulation.tabulator.UniversalTabulator;
import org.terrapop.tabulation.utilities.Util;

import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class Reader {

	private static Logger log = Logger.getLogger(Reader.class.getName());
	private static String dataFile = "./testData1";
	private static String parent_dir = null;
	private static List<String> EXTS = Arrays.asList("gz");
	private static SampleDesc sampleDesc = null;
	private static int debug;
	// private static List<String> DIRS = null; // Arrays.asList("Output");
	
	public static void initializeUniversalTabulators(UniversalTabulator universalTabulator) {
		
		List<TabulatorLevel> levels = SampleDescLoader.levelList;
		
		if(sampleDesc.getNAT()!=null){
			universalTabulator.setNationalTabulator( new NationalTabulator() );
			universalTabulator.getNationalTabulator().setCountry(sampleDesc.getCountry_id());
			universalTabulator.getNationalTabulator().setOutputFileName("LEVEL_"+ levels.get(0).level + "_" + levels.get(0).name+".yml" );
			universalTabulator.getNationalTabulator().setOutputCSVFileName("LEVEL_"+ levels.get(0).level + "_" + levels.get(0).name+".csv" );
			universalTabulator.getNationalTabulator().setGeogInstanceId(sampleDesc.getCountry_id());
			universalTabulator.getNationalTabulator().setSampleGeogLevelId(sampleDesc.getTerrapop_samples().get("sample_id")+"_" + levels.get(0).name );
		}
		
		for(int i=1;i<levels.size();i++){
			SubLevelTabulator subLevelTabulator = new SubLevelTabulator(levels.get(i).var);
			subLevelTabulator.setLabel(levels.get(i).label);
			subLevelTabulator.setOutputFileName("LEVEL_"+ levels.get(i).level + "_" + levels.get(i).name+".yml");
			subLevelTabulator.setOutputCSVFileName("LEVEL_"+ levels.get(i).level + "_" + levels.get(i).name+".csv");
			subLevelTabulator.setSampleGeogLevelId(sampleDesc.getTerrapop_samples().get("sample_id")+"_" + levels.get(i).name);
			universalTabulator.getSubLevelTabulator().add(subLevelTabulator);
		 }
		
	}
	
	public static String getExtension(String fileName) {
	  String found = null;
	  for (String ext : EXTS) {
	    if (fileName.endsWith("." + ext)) {
	      if (found == null || found.length() < ext.length()) {
	        found = ext;
	      }
	    }
	  }
	  
	  return found;
	}
	
	public static void createOutputDirectory(String directory) {
	  File theDir = new File(directory);

	  // if the directory does not exist, create it
	  if (!theDir.exists()) {
	    System.err.println("+=+ Creating directory [" + directory + "]");
	    boolean result = false;

	    try {
	      theDir.mkdir();
	      result = true;
	    } catch(SecurityException se){
	      //handle it
	    }
	    
	    if(result) {    
	      System.err.println("  +=+ Directory [" + directory + "] created");  
	    }
	  }
	}
	
	public static String timeToMinutesSecondsFormat(long millis) {
	  return String.format("%d min, %d sec", 
	      TimeUnit.MILLISECONDS.toMinutes(millis),
	      TimeUnit.MILLISECONDS.toSeconds(millis) - 
	      TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
	  );
	}
	
	public static void main(String args[]) throws Exception {
	  
		long timeStart = System.currentTimeMillis();
		
		log.warn("Process Started");
		
		if(args.length > 0){
	  		String sampleDescFile = args[0];
	  		SampleDescLoader.setQueryFile(sampleDescFile);
	  		MetaDataLoader.setMetaDataFile(args[1]);
	  		System.out.println("MetaDetaFile " + args[0]);
	  		System.out.println("MetaDetaFile " + args[1]);
		}
		
		Map<String, String> env = System.getenv();
		
		if(env.containsKey("DEBUG")) {
			debug++;
		}
		
		System.out.println("Debug Level: " + debug);
		
		long numberRecords = 0L;
		long numberPersonRecords = 0L;
		
		sampleDesc = SampleDescLoader.getSampleDescObject();
		parent_dir = sampleDesc.getTerrapop_samples().get("sample_id");
		
		UniversalTabulator universalTabulator = new UniversalTabulator(debug);
		initializeUniversalTabulators(universalTabulator);
		
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = dateFormat.format(date);
		
		String dat_file = sampleDesc.getFile_path();
		InputStream is = null;
		File data = new File(dat_file);
		
		String baseDir = "output_" + strDate;
		
		createOutputDirectory(baseDir);
		createOutputDirectory(baseDir + "/" + parent_dir);
		
		Util.setOutputDirectory(baseDir + "/" + parent_dir);
		
		System.err.println("==> Microdata File: " + dat_file);
		
		if(getExtension(dat_file) != null) {
			System.err.println("===> gzip input detected");
			is = new GZIPInputStream(new FileInputStream(data));
		} else {
		    System.err.println("===> uncompressed input detected");
		    is = new FileInputStream( data );
		}
		
		DataInputStream in = new DataInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		HouseholdBean granularDataBean = null;
		boolean newHouseHoldStarts = false;
	    String str_is_fullcount = sampleDesc.getFullcount();
	    Integer is_fullcount = -1;

		String inputLine = br.readLine();

	    if(str_is_fullcount != null) {
	      if(str_is_fullcount.toLowerCase().equals("true")) {
	        // data are full tagged as being full count
	        
	        System.err.println("**** Data are configured as full count ****");
	        
	        is_fullcount = 1;
	      }
	      else {
	        is_fullcount = 0;
	      }
	    }

		
		while (inputLine != null) {
			
			granularDataBean = new HouseholdBean();
			newHouseHoldStarts = false;
			
			granularDataBean.setIsFullcount(is_fullcount);
			
			while (!newHouseHoldStarts) {
				
			  if (inputLine != null && inputLine.charAt(0) == 'H') {
					
					granularDataBean.setHouseholdRecord(inputLine);
					++numberRecords;
				}
				
				while ((inputLine = br.readLine()) != null 	&& inputLine.charAt(0) == 'P') {
					
					granularDataBean.getPersonsRecordList().add(inputLine);
					granularDataBean.initializePersonPreviousData();
					
					++numberPersonRecords;
				}
				
				newHouseHoldStarts = true;
				
				universalTabulator.startTabulating(granularDataBean);
				//universalTabulator.addHouseholdBean(granularDataBean);
			}
			
			
			if(numberRecords % 20000 == 0){
				//System.err.println("Records processed: " + NumberFormat.getNumberInstance(Locale.US).format(numberRecords) + " households; " + NumberFormat.getNumberInstance(Locale.US).format(numberPersonRecords) + " persons");
			  printProcessedRecordsInformation(numberRecords, numberPersonRecords);
			}
		}

		printProcessedRecordsInformation(numberRecords, numberPersonRecords);
		
		// universalTabulator.tabulate();
		br.close();
		
		universalTabulator.startAggregatingResults();
		universalTabulator.startWritingResults();

		long timeEnd = System.currentTimeMillis();
		
		System.err.println("**** Total Time: " + timeToMinutesSecondsFormat((timeEnd - timeStart)) + " (" + (timeEnd - timeStart) + " ms) ****");
		
		System.err.println("Undefined Queries: ");
		
		for(String queryCode : QueryLoader.getUndefinedQueriesList()){
			System.err.print(" ++> " + queryCode + " ");
		}
		
		System.err.println("");
		
	}

	public UniversalTabulator startTabulationProcess(String dataFile) throws IOException, MetaDataException, ConditionFormatException {
		UniversalTabulator universalTabulator = new UniversalTabulator(debug);
		initializeUniversalTabulators(universalTabulator);

		FileInputStream inp = new FileInputStream(dataFile);
		DataInputStream in = new DataInputStream(inp);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		HouseholdBean granularDataBean = null;
		boolean newHouseHoldStarts = false;
		String inputLine = br.readLine();

		while (inputLine != null) {
			System.out.println(inputLine);
			granularDataBean = new HouseholdBean();
			newHouseHoldStarts = false;
			while (!newHouseHoldStarts) {
				if (inputLine != null && inputLine.charAt(0) == 'H') {
					granularDataBean.setHouseholdRecord(inputLine);
				}
				while ((inputLine = br.readLine()) != null
						&& inputLine.charAt(0) == 'P') {
					granularDataBean.getPersonsRecordList().add(inputLine);
					granularDataBean.initializePersonPreviousData();
				}
				newHouseHoldStarts = true;
				universalTabulator.startTabulating(granularDataBean);
			}
		}
		br.close();
		universalTabulator.startAggregatingResults();
		return universalTabulator;
	}

	public static String getDataFile() {
		return dataFile;
	}

	public static void setDataFile(String dataFile) {
		Reader.dataFile = dataFile;
	}
	
	public static void printProcessedRecordsInformation(long h, long p) {
	  System.err.println("Records processed: " + NumberFormat.getNumberInstance(Locale.US).format(h) + " households; " + NumberFormat.getNumberInstance(Locale.US).format(p) + " persons");
	}
	
}
