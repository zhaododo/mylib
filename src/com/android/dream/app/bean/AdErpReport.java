package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author zhaoj
 *
 * 生产快报主档
 */
public class AdErpReport {
	
	public AdErpReport()
	{
		rowItems = new ArrayList<AdErpReportRow>();
	}
	
	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public List<AdErpReportRow> getRowItems() {
		return rowItems;
	}

	public void setRowItems(List<AdErpReportRow> rowItems) {
		this.rowItems = rowItems;
	}
	
	public String getTypeNo() {
		return typeNo;
	}

	public void setTypeNo(String typeNo) {
		this.typeNo = typeNo;
	}
	
	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	/**
	 * 行记录集合
	 */
	private List<AdErpReportRow> rowItems;
	
	/**
	 * 报表标题
	 */
	private String reportTitle;
	
	/**
	 * 分类编号
	 */
	private String typeNo;

	/**
	 * 产制日期
	 */
	private String reportDate;
	
	/**
	 * 是否是当天
	 */
	private boolean isToday = false;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 
	 * 仅用于测试
	 */
	@Override
	public String toString() {
		return "reportTitle:"+reportTitle+",reportDate"+reportDate;
	}
	
	
	/**
	 * @param result
	 * @return
	 */
	public static List<AdErpReport> parse(String result)
	{
		JSONArray jsonReportArray = null;
		JSONArray jsonReportRowArray = null;
		JSONArray jsonReportRowItemArray = null;
		JSONObject jsonReport = null;
		JSONObject jsonReportRow = null;
		JSONObject jsonReportRowItem = null;
		
		
		List<AdErpReport> reportList = new ArrayList<AdErpReport>();
		List<AdErpReportRow> reportRowList = null;
		List<AdErpReportRowItem> reportRowItemList = null;
		
		AdErpReport adReport = null;
		AdErpReportRow adReportRow = null;
		AdErpReportRowItem adReportRowItem = null;
		try 
		{
			jsonReportArray = new JSONArray(result);
			if (jsonReportArray != null)
			{
				for (int i=0; i <jsonReportArray.length(); ++i)
				{
					jsonReport = jsonReportArray.getJSONObject(i);
					adReport = new AdErpReport();
					adReport.setReportTitle(jsonReport.getString("reportTitle"));
					adReport.setReportDate(jsonReport.getString("reportDate"));
					adReport.setToday(jsonReport.getBoolean("today"));
					
					//验证是否有效
					if (!jsonReport.isNull("reportDate"))
					{
						if (!jsonReport.isNull("rowItems"))
						{
							jsonReportRowArray = jsonReport.getJSONArray("rowItems");
							reportRowList = new ArrayList<AdErpReportRow>();

							//获得行记录对象
							for (int j=0;j <jsonReportRowArray.length();++j)
							{
								jsonReportRow = jsonReportRowArray.getJSONObject(j);
								if (!jsonReportRow.isNull("reportNo"))
								{
									adReportRow = new AdErpReportRow();
									adReportRow.setTypeNo(jsonReportRow.getString("typeNo"));
									adReportRow.setReportNo(jsonReportRow.getString("reportNo"));
									adReportRow.setFromAddr(jsonReportRow.getString("fromAddr"));
									adReportRow.setReportName(jsonReportRow.getString("reportName"));
									adReportRow.setReportProperty(jsonReportRow.getString("reportProperty"));
									adReportRow.setReportDate(jsonReportRow.getString("reportDate"));
									adReportRow.setReportSort(jsonReportRow.getInt("reportSort"));
									adReportRow.setRemark(jsonReportRow.getString("remark"));
									if (!jsonReportRow.isNull("items"))
									{
										jsonReportRowItemArray = jsonReportRow.getJSONArray("items");
										reportRowItemList = new ArrayList<AdErpReportRowItem>();
										
										for (int k =0; k<jsonReportRowItemArray.length(); ++k)
										{
											jsonReportRowItem = jsonReportRowItemArray.getJSONObject(k);
											
											if (!jsonReportRowItem.isNull("reportNo"))
											{
												adReportRowItem = new AdErpReportRowItem();
												adReportRowItem.setReportNo(jsonReportRowItem.getString("reportNo"));
												adReportRowItem.setTargetField(jsonReportRowItem.getString("targetField"));
												adReportRowItem.setFieldSort(jsonReportRowItem.getInt("fieldSort"));
												adReportRowItem.setTargetName(jsonReportRowItem.getString("targetName"));
												adReportRowItem.setTargetValue(jsonReportRowItem.getString("targetValue"));
												adReportRowItem.setRemark(jsonReportRowItem.getString("remark"));
												reportRowItemList.add(adReportRowItem);
											}
										}
										adReportRow.setItems(reportRowItemList);
									}
									reportRowList.add(adReportRow);
								}
								
							}
							//end of 获得行记录对象
							adReport.setRowItems(reportRowList);
						}
						
						//end of 保存报表对象
						reportList.add(adReport);
					}
					
				}
				
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return reportList;
	}
	


}
