package com.android.dream.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

/**
 * @author zhaoj
 * 图表坐标系
 *
 */
public class AdErpReportChartItemVo implements Serializable{
	
	public String getxValue() {
		return xValue;
	}

	public void setxValue(String xValue) {
		this.xValue = xValue;
	}

	public String getyValue() {
		return yValue;
	}

	public void setyValue(String yValue) {
		this.yValue = yValue;
	}
	
	private static final long serialVersionUID = -447801390529110407L;

	/**
	 *  x轴值
	 */
	private String xValue;
	
	/**
	 *  y轴值
	 */
	private String yValue;

}
