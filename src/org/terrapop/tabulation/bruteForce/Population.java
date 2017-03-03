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

import java.io.*;

public class Population {

	public static void main(String args[]) {
		MeanAge();
	}

	static void TotalPopulationEvaluation() {
		try {

			// 1.73265E8

			long start = System.currentTimeMillis();
			FileInputStream fstream = new FileInputStream("__CHANGE_TO_A_PATH__");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			double weight = 0;
			long cnt = 0;

			while ((strLine = br.readLine()) != null) {
				if ((cnt % 10000) == 0) {
					System.out.println("Completed Persons " + cnt);
				}
				cnt++;
				if (strLine.charAt(0) == 'P') {
					weight = weight + (Double.parseDouble(strLine.substring(25, 33)) / 100.0);

				}
			}

			System.out.println("Total Population is " + weight);
			long end = System.currentTimeMillis();
			System.out.println(":Time it Takes: " + (end - start));
			in.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	static void TotalPopulationWithAge15_19() {
		try {

			// 2.015E7
			long start = System.currentTimeMillis();
			FileInputStream fstream = new FileInputStream("__CHANGE_TO_A_PATH__");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			double weight = 0;
			long cnt = 0;

			while ((strLine = br.readLine()) != null) {
				if ((cnt % 10000) == 0) {
					System.out.println("Completed Persons " + cnt);
				}
				cnt++;
				if (strLine.charAt(0) == 'P') {

					int age = Integer.parseInt(strLine.substring(79, 82));
					if (age >= 15 && age <= 19) {
						weight = weight + (Double.parseDouble(strLine.substring(25, 33)) / 100.0);
					}

				}
			}

			System.out.println("Total Population is " + weight);
			long end = System.currentTimeMillis();
			System.out.println(":Time it Takes: " + (end - start));
			in.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	static void MeanAge() {
		try {

			// 20.15
			long start = System.currentTimeMillis();
			FileInputStream fstream = new FileInputStream("__CHANGE_TO_A_PATH__");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			double weight = 0;
			double onlyWeight = 0;
			long cnt = 0;

			while ((strLine = br.readLine()) != null) {
				if ((cnt % 10000) == 0) {
					System.out.println("Completed Persons " + cnt);
				}
				cnt++;
				if (strLine.charAt(0) == 'P') {

					int age = Integer.parseInt(strLine.substring(79, 82));
					if (age >= 0 && age <= 80) {
						double tempWt = Double.parseDouble(strLine.substring(25, 33)) / 100.0;
						weight = weight + age * (tempWt);
						onlyWeight = onlyWeight + tempWt;
					}

				}
			}

			System.out.println("Total Population is " + weight / onlyWeight);
			long end = System.currentTimeMillis();
			System.out.println(":Time it Takes: " + (end - start));
			in.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	static void NumberOfHouseHolds() {
		try {

			// 20.15
			long start = System.currentTimeMillis();
			FileInputStream fstream = new FileInputStream("__CHANGE_TO_A_PATH__");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			double weight = 0;
			double onlyWeight = 0;
			long cnt = 0;

			while ((strLine = br.readLine()) != null) {
				if ((cnt % 10000) == 0) {
					System.out.println("Completed Persons " + cnt);
				}
				cnt++;
				if (strLine.charAt(0) == 'H') {

					double tempWt = Double.parseDouble(strLine.substring(25, 33)) / 100.0;
					weight = weight + (tempWt);

				}
			}

			System.out.println("Total houseHold is " + weight / onlyWeight);
			long end = System.currentTimeMillis();
			System.out.println(":Time it Takes: " + (end - start));
			in.close();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

}
