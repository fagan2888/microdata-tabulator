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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.esotericsoftware.yamlbeans.YamlWriter;

import java.util.*;

public class Util {

	private static String outputDirectory;

	public static void createOutputYamlFile(Object obj, String outputFilePath) throws Exception {

		if (outputDirectory == null) {
			throw new Exception("Output Directory is not set");
		}

		File file1 = new File(outputDirectory);
		File file2 = new File(file1, outputFilePath);

		YamlWriter writer = new YamlWriter(new FileWriter(file2.getPath()));
		writer.getConfig().writeConfig.setAlwaysWriteClassname(false);
		writer.getConfig().writeConfig.setExplicitFirstDocument(true);
		writer.getConfig().writeConfig.setWriteRootTags(false);
		writer.write(obj);
		writer.close();
	}

	public static void createOutputCSVFile(List<String> outputCSVDataList, String outputFilePath) throws Exception {

		if (outputDirectory == null) {
			throw new Exception("Output Directory is not set");
		}

		File file1 = new File(outputDirectory);
		File file2 = new File(file1, outputFilePath);

		PrintWriter writer = new PrintWriter(file2.getPath(), "UTF-8");
		for (String outputCSVLine : outputCSVDataList) {
			writer.println(outputCSVLine);
		}

		writer.close();

	}

	public static String getOutputDirectory() {
		return outputDirectory;
	}

	public static void setOutputDirectory(String outputDirectory) {
		Util.outputDirectory = outputDirectory;
	}

}
