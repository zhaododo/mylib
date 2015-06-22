package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaoj
 * 
 * 生产快报页面对象,用于包装AdErpReport
 *
 */
public class AdErpReportPage {
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<AdErpReport> getReportItem() {
		return reportItem;
	}

	public void setReportItem(List<AdErpReport> reportItem) {
		this.reportItem = reportItem;
	}

	private int pageSize;
	
	private List<AdErpReport> reportItem = new ArrayList<AdErpReport>();
	
	
	public static AdErpReportPage parse(String result)
	{
		JSONArray jsonReportArray = null;
		JSONArray jsonReportRowArray = null;
		JSONArray jsonReportRowItemArray = null;
		
		JSONObject jsonReportPage = null;
		JSONObject jsonReport = null;
		JSONObject jsonReportRow = null;
		JSONObject jsonReportRowItem = null;
		
		
		List<AdErpReport> reportList = new ArrayList<AdErpReport>();
		List<AdErpReportRow> reportRowList = null;
		List<AdErpReportRowItem> reportRowItemList = null;
		
		AdErpReportPage adReportPage =new AdErpReportPage();
		AdErpReport adReport = null;
		AdErpReportRow adReportRow = null;
		AdErpReportRowItem adReportRowItem = null;
		try 
		{
			jsonReportPage = new JSONObject(result);
			
			if (!jsonReportPage.isNull("pageSize"))
			{
				adReportPage.setPageSize(jsonReportPage.getInt("pageSize"));
			}
			
			if (!jsonReportPage.isNull("reportItem"))
			{
				jsonReportArray = jsonReportPage.getJSONArray("reportItem");
			}
			
			if (jsonReportArray != null && jsonReportArray.length() >0)
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
									
									if (!jsonReportRow.isNull("reportProperty"))
									{
										adReportRow.setReportProperty(jsonReportRow.getString("reportProperty"));
									}
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
												adReportRowItem.setUnit(jsonReportRowItem.getString("unit"));
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
				
				adReportPage.setReportItem(reportList);
				
			}
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return adReportPage;
	}

}
