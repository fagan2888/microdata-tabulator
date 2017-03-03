/*
 Copyright (c) 2012-2017 Regents of the University of Minnesota

 This file is part of the Minnesota Population Center's Terra Populus Project.
 For copyright and licensing information, see the NOTICE and LICENSE files
 in this project's top-level directory, and also on-line at:
   https://github.com/mnpopcenter/microdata-tabulator
 */
package org.terrapop.tabulation.loader;

import java.io.File;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.log4j.Logger;

import com.esotericsoftware.yamlbeans.YamlReader;

import org.terrapop.tabulation.entities.SampleDesc;
import org.terrapop.tabulation.entities.TabulatorLevel;


public class SampleDescLoader {

	private SampleDescLoader(){}
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SampleDescLoader.class.getName());
	private static SampleDesc sampleDescObject = null;
	private static String queryFile  = "sample_desc.yml" ; 
	public static List<TabulatorLevel> levelList = new ArrayList<TabulatorLevel>();

	public static String getQueryFile() {
		return SampleDescLoader.queryFile;
	}

	public static void setQueryFile(String queryFile) {
		SampleDescLoader.queryFile = queryFile;
	}

	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static synchronized SampleDesc getSampleDescObject() {
		YamlReader reader = null;

		if (sampleDescObject == null) {

			try {

				File f = new File(queryFile);

				if(!f.exists()) {
					System.err.println("QUERY File: " + queryFile + " does not exist." );
				} else {

					reader = new YamlReader(readFile(queryFile, StandardCharsets.UTF_8));

					sampleDescObject = reader.read(SampleDesc.class);

					addPrimaryLevel(sampleDescObject);

				}

			} catch (Exception e){

				System.err.println("Please check the QUERY File: " + queryFile );

				e.printStackTrace();

				throw new RuntimeException("STOP, THERE WAS AN ERROR");

			}

		}

		return sampleDescObject;		
	}



	public static void main(String args[]){
		SampleDesc map = getSampleDescObject();
		System.out.println(map.getFile_path());
	}

	@SuppressWarnings("unchecked")
	public static void addLevels(List<Object> temp , int level){

		if (temp==null) return;

		for(int i=0;i < temp.size();i++){

			Map<Object,Object> map = (Map<Object,Object>)temp.get(i);
			TabulatorLevel a = new TabulatorLevel();

			a.name = (String)(map.keySet().toArray()[0]);
			a.label =  (String)((Map<String,Object>)map.get(a.name)).get("label") ;
			a.var =  (String)((Map<String,Object>)map.get(a.name)).get("var") ;
			a.level = level;
			levelList.add(a);

			addLevels(
					(List<Object>)((Map<String,Object>)map.get(a.name)).get("children")
					, level+1);

		}
	}

	@SuppressWarnings("unchecked")
	public static void addPrimaryLevel (SampleDesc o){

		TabulatorLevel root = new TabulatorLevel();
		root.name = "NAT";
		root.label = (String)(o.getNAT().get("label"));
		root.var = (String)o.getNAT().get("var");
		root.level = 0;
		levelList.add(root);

		List<Object> temp = (ArrayList<Object>)(o.getNAT().get("children"));
		if(temp!=null){
			addLevels(temp,1);
		}


		System.out.println("Done");
		for(TabulatorLevel tl : levelList){
			System.out.println(tl.name + "***" + tl.label + "***" + tl.var + "***" + tl.level );
		}

	}


}
