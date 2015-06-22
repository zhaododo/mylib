
package com.android.dream.app.bean;

import java.io.Serializable;

/**
 * @author zhaoj
 * 生产快报左边导航菜单
 *
 */
public class AdReportLeftMenu implements Serializable {
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	private static final long serialVersionUID = 5465221514088074863L;

	public String getNo() {
		return no;
	}


	public void setNo(String no) {
		this.no = no;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	/**
	 * 生产快报编号
	 */
	private String no;
	
	
	/**
	 * 生产快报显示的名字
	 */
	private String name;
	
	/**
	 * 生产快报显示的标题
	 */
	private String title;

}
