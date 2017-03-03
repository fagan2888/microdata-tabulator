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
package org.terrapop.tabulation.entities;

import java.util.*;

public class SampleDesc {

	private String rules_file_path;
	private String meta_data_file;
	private String output_results_national_level;
	private String output_results_state_level;
	private String output_results_micro_level;
	private String output_sample_setup_yaml;
	private String country_id;
	private String file_path;
	private Map<String, String> terrapop_samples;
	private Map<String, Object> NAT;
	private String fullcount;

	public String getMeta_data_file() {
		return meta_data_file;
	}

	public void setMeta_data_file(String meta_data_file) {
		this.meta_data_file = meta_data_file;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getRules_file_path() {
		return rules_file_path;
	}

	public void setRules_file_path(String rules_file_path) {
		this.rules_file_path = rules_file_path;
	}

	public String getOutput_results_national_level() {
		return output_results_national_level;
	}

	public void setOutput_results_national_level(String output_results_national_level) {
		this.output_results_national_level = output_results_national_level;
	}

	public String getOutput_results_state_level() {
		return output_results_state_level;
	}

	public void setOutput_results_state_level(String output_results_state_level) {
		this.output_results_state_level = output_results_state_level;
	}

	public String getOutput_results_micro_level() {
		return output_results_micro_level;
	}

	public void setOutput_results_micro_level(String output_results_micro_level) {
		this.output_results_micro_level = output_results_micro_level;
	}

	public String getOutput_sample_setup_yaml() {
		return output_sample_setup_yaml;
	}

	public void setOutput_sample_setup_yaml(String output_sample_setup_yaml) {
		this.output_sample_setup_yaml = output_sample_setup_yaml;
	}

	public String getCountry_id() {
		return country_id;
	}

	public void setCountry_id(String country_id) {
		this.country_id = country_id;
	}

	public Map<String, String> getTerrapop_samples() {
		return terrapop_samples;
	}

	public void setTerrapop_samples(Map<String, String> terrapop_samples) {
		this.terrapop_samples = terrapop_samples;
	}

	public Map<String, Object> getNAT() {
		return NAT;
	}

	public void setNAT(Map<String, Object> NAT) {
		this.NAT = NAT;
	}

	public String getFullcount() {
		return fullcount;
	}

	public void setFullcount(String fullcount) {
		this.fullcount = fullcount;
	}

}
