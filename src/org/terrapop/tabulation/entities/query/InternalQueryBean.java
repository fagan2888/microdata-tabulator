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
package org.terrapop.tabulation.entities.query;
import java.util.List;

import org.apache.log4j.Logger;



public class InternalQueryBean extends QueryBean {

	@SuppressWarnings("unused")
  private static Logger log = Logger.getLogger(InternalQueryBean.class.getName());

	private List<List<String>> where_p;
	private List<List<String>> where_h;
	
	private Integer is_fullcount = -1;
	
	public List<List<String>> getWhere_p() {
		return where_p;
	}
	
	public void setWhere_p(List<List<String>> where_p) {
		this.where_p = where_p;
	}
	
	public List<List<String>> getWhere_h() {
		return where_h;
	}
	
	public void setWhere_h(List<List<String>> where_h) {
		this.where_h = where_h;
	}

	 public Integer isFullcount() {
	    return is_fullcount;
	  }
	  
	  public void setIsFullcount(Integer is_fullcount) {
	    this.is_fullcount = is_fullcount;
	  }

}
