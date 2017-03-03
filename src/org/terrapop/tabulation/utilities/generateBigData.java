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
package org.terrapop.tabulation.utilities;
import java.io.*;
public class generateBigData {

	public static void main(String args[]) throws Exception{
		
		for(int i=0;i<1000;i++){
			
			 FileInputStream fstream = new FileInputStream("INPUT_PATH");
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  
			  FileWriter fst = new FileWriter("OUTPUT_PATH",true);
			  BufferedWriter out = new BufferedWriter(fst);
			 
			  String st  ;
			  
			  while((st=br.readLine())!=null){
				  out.write(st);
				  out.write("\n");
				  
			  }
			  br.close();
			  
			  out.flush();
			  out.close();
		}
		
	}
}
