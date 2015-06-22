package com.android.dream.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

/**
 * @author zhaoj
 * 图表行数据
 *
 */
public class AdErpReportChartRowVo implements Serializable {
	
	private static final long serialVersionUID = 3546001835332198239L;
	
	public AdErpReportChartRowVo()
	{
		title = "";
		items = new ArrayList<AdErpReportChartItemVo>();
	}

	/**
	 *  每行数据的标题
	 */
	private String title;
	
	/**
	 *  坐标轴数据
	 */
	private List<AdErpReportChartItemVo> items;
	
	public List<AdErpReportChartItemVo> getItems() {
		return items;
	}

	public void setItems(List<AdErpReportChartItemVo> items) {
		this.items = items;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
