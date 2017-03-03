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
package org.terrapop.tabulation.test;

import java.util.*;

import org.terrapop.tabulation.utilities.OperatorUtility;

public class EndToEndTest extends junit.framework.TestCase{
	
	

//	
//	UniversalTabulator universalTabulator = new UniversalTabulator();
//	HashMap<String,Double> imap = new HashMap<String,Double>();
//	public EndToEndTest(String name){
//		super(name);
//	}
	
	
//	protected void setUp(){
//		
//	//	try{
//	//		Reader.setDataFile ( "./Resource/TestData/testData1" );
//	//		QueryLoader.setQueryFile ( "./Resource/TestData/TestQueryFile" );
//			
//			
//			
//	//	universalTabulator = new Reader().startTabulationProcess("./Resource/TestData/testData1");
//			
////		}catch(IOException e){
////		  fail("IOException " + e);	
////		}catch(ConditionFormatException e){
////		  fail("ConditionFormatException " + e );	
////		}catch(MetaDataException e){
////		  fail("MetaDataException " + e);
////		}
//		
//		
//
//		
//		
//	}
	
//	public void testUniversalLevel(){
//		assertTrue(QueryLoader.getQueryBeanList().size() == 5);
//		assertTrue(MetaDataLoader.getMetaData().size() > 0);
//		assertTrue(universalTabulator.getHouseHoldQueryDecisions().size() > 0);
//	}
//	
//	public void testNationalLevel() {
//
//		assertTrue(universalTabulator.getNationalTabulator().getFinalResultSet().size() >  0);
//		assertTrue(universalTabulator.getNationalTabulator().getFinalResultSet().get(0).equals( "170.0" ) );
//		assertTrue(universalTabulator.getNationalTabulator().getFinalResultSet().get(1).equals( "20.06896551724138"));
//		assertTrue(universalTabulator.getNationalTabulator().getFinalResultSet().get(2).equals( "50.0"));
//		assertTrue(universalTabulator.getNationalTabulator().getFinalResultSet().get(3).equals( "15.0"));
//		
//	}
//	
//	public void testStateLevel() {
//		assertTrue(universalTabulator.getSubLevelTabulator().get(0).getFinalResultSet().get("11").get(0).equals( "170.0") );
//		assertTrue(universalTabulator.getSubLevelTabulator().get(0).getFinalResultSet().get("11").get(1).equals( "20.06896551724138") );
//		assertTrue(universalTabulator.getSubLevelTabulator().get(0).getFinalResultSet().get("11").get(2).equals( "50.0") );
//		assertTrue(universalTabulator.getSubLevelTabulator().get(0).getFinalResultSet().get("11").get(3).equals( "15.0") );
//	}
//	
//	public void testMuniLevel() {
//		assertTrue(universalTabulator.getSubLevelTabulator().get(1).getFinalResultSet().get("1199903").get(0).equals( "150.0") );
//		assertTrue(universalTabulator.getSubLevelTabulator().get(1).getFinalResultSet().get("1199903").get(1).equals( "20.0") );
//		assertTrue(universalTabulator.getSubLevelTabulator().get(1).getFinalResultSet().get("1199903").get(2).equals( "50.0") );
//		assertTrue(universalTabulator.getSubLevelTabulator().get(1).getFinalResultSet().get("1199903").get(3).equals( "15.0") );
//		
//		
//	}
	
	   public void testMedian(){
		   HashMap<String,Double> imap = new HashMap<String,Double>();
		   imap.put("1", 34.0);
		   imap.put("2", 10.0);
		   imap.put("3", 32.0);
		   imap.put("4", 12.0);
		   
		assertTrue(new OperatorUtility().calculateMedian(imap).equals("2.5"));
		System.out.println(new OperatorUtility().calculateMedian(imap));
	     }
	
        public void testMean(){
        	 HashMap<String,Double> imap = new HashMap<String,Double>();
  		   imap.put("1", 34.0);
  		   imap.put("2", 10.0);
  		   imap.put("3", 32.0);
  		   imap.put("4", 12.0);
        	assertTrue(new OperatorUtility().calculateMean(imap).equals("2.25"));
	    }

       public void testCount(){
    	   HashMap<String,Double> imap = new HashMap<String,Double>();
		   imap.put("1", 34.0);
		   imap.put("2", 10.0);
		   imap.put("3", 32.0);
		   imap.put("4", 12.0);
    	   assertTrue(new OperatorUtility().calculateCount(imap).equals("88.0"));
       }
	
	 public void testNothing() {
	    }
	    
	    public void testWillAlwaysFail() {
	       // fail("An error message");
	    }
	
	
	
	
	
}
