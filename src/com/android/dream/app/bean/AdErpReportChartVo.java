package com.android.dream.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

/**
 * @author zhaoj 无分页的图表信息
 * 
 */
public class AdErpReportChartVo implements Serializable {

	private static final long serialVersionUID = -411651219748100842L;
	/**
	 * 图表标题
	 */
	private String chartTitle;

	/**
	 * x轴名称
	 */
	private String xTitle;

	/**
	 * y轴名称
	 */
	private String yTitle;

	/**
	 * y轴最大值
	 */
	private String yMax;

	/**
	 * y轴最小值
	 */
	private String yMin;

	/**
	 * x轴最大值
	 */
	private String xMax;

	/**
	 * x轴最小值
	 */
	private String xMin;

	/**
	 * 行数据
	 */
	private List<AdErpReportChartRowVo> rows;

	public AdErpReportChartVo() {

		xMin = "";
		xMax = "";
		yMin = "";
		yMax = "";
		xTitle = "";
		yTitle = "";
		chartTitle = "";
		rows = new ArrayList<AdErpReportChartRowVo>();
	}

	public List<AdErpReportChartRowVo> getRows() {
		return rows;
	}

	public void setRows(List<AdErpReportChartRowVo> rows) {
		this.rows = rows;
	}

	public String getxMax() {
		return xMax;
	}

	public void setxMax(String xMax) {
		this.xMax = xMax;
	}

	public String getxMin() {
		return xMin;
	}

	public void setxMin(String xMin) {
		this.xMin = xMin;
	}

	public String getChartTitle() {
		return chartTitle;
	}

	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}

	public String getxTitle() {
		return xTitle;
	}

	public void setxTitle(String xTitle) {
		this.xTitle = xTitle;
	}

	public String getyTitle() {
		return yTitle;
	}

	public void setyTitle(String yTitle) {
		this.yTitle = yTitle;
	}

	public String getyMax() {
		return yMax;
	}

	public void setyMax(String yMax) {
		this.yMax = yMax;
	}

	public String getyMin() {
		return yMin;
	}

	public void setyMin(String yMin) {
		this.yMin = yMin;
	}

	/**
	 * json解析
	 * 
	 * @param result
	 * @return
	 */
	public static AdErpReportChartVo parse(String result) {
		
		JSONObject jsonReportchart = null;
		JSONArray jsonReportRowArray = null;
		JSONArray jsonReportRowItemArray = null;
		JSONObject jsonReportRow = null;
		JSONObject jsonReportRowItem = null;

		AdErpReportChartVo adErpReportChartVo = new AdErpReportChartVo();
		List<AdErpReportChartRowVo> reportRowList = null;
		List<AdErpReportChartItemVo> reportRowItemList = null;
 
		AdErpReportChartRowVo adReportRow = null;
		AdErpReportChartItemVo adReportRowItem = null;
		try {
			jsonReportchart = new JSONObject(result);
			if (jsonReportchart != null) {
				adErpReportChartVo.setChartTitle(jsonReportchart.getString("chartTitle"));
				adErpReportChartVo.setxMin(jsonReportchart.getString("xMin"));
				adErpReportChartVo.setxMax(jsonReportchart.getString("xMax"));
				adErpReportChartVo.setyMax(jsonReportchart.getString("yMax"));
				adErpReportChartVo.setyMin(jsonReportchart.getString("yMin"));
				adErpReportChartVo.setxTitle(jsonReportchart.getString("xTitle"));
				adErpReportChartVo.setyTitle(jsonReportchart.getString("yTitle"));
				if (!jsonReportchart.isNull("rows")) {
					jsonReportRowArray = jsonReportchart.getJSONArray("rows");
					reportRowList = new ArrayList<AdErpReportChartRowVo>();
					// 获得行记录对象
					for (int j = 0; j < jsonReportRowArray.length(); ++j) {
						jsonReportRow = jsonReportRowArray.getJSONObject(j);
						adReportRow = new AdErpReportChartRowVo();
						adReportRow.setTitle(jsonReportRow.getString("title"));
						if (!jsonReportRow.isNull("items")) {
							jsonReportRowItemArray = jsonReportRow.getJSONArray("items");
							reportRowItemList = new ArrayList<AdErpReportChartItemVo>();
							for (int k = 0; k < jsonReportRowItemArray.length(); ++k) {
								jsonReportRowItem = jsonReportRowItemArray.getJSONObject(k);
								adReportRowItem = new AdErpReportChartItemVo();
								adReportRowItem.setxValue(jsonReportRowItem.getString("xValue"));
								adReportRowItem.setyValue(jsonReportRowItem.getString("yValue"));
								reportRowItemList.add(adReportRowItem);
							}
							adReportRow.setItems(reportRowItemList);
						}
						reportRowList.add(adReportRow);
					}
					// end of 获得行记录对象
					adErpReportChartVo.setRows(reportRowList);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return adErpReportChartVo;
	}

}
