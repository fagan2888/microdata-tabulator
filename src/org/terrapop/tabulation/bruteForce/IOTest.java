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

package org.terrapop.tabulation.bruteForce;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class IOTest {

	public static void main(String args[]) throws Exception {
		// 131874
		FileInputStream fstream = new FileInputStream("__path__to__real__big__data__");
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int cnt = 0;
		long start = System.currentTimeMillis();
		while ((strLine = br.readLine()) != null) {
			if (strLine.charAt(0) == 'H') {
				cnt++;
			}
		}

		br.close();

		long end = System.currentTimeMillis();

		System.out.println("Total is " + cnt);
		System.out.println("Total Time Taken is " + (end - start));
	}
}
