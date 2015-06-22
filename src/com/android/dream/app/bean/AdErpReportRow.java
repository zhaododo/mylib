package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoj
 * 
 * 生产快报行记录
 *
 */
public class AdErpReportRow {
	
	public AdErpReportRow()
	{
		items = new ArrayList<AdErpReportRowItem>();
	}

	public List<AdErpReportRowItem> getItems() {
		return items;
	}

	public void setItems(List<AdErpReportRowItem> items) {
		this.items = items;
	}

	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	public int getReportSort() {
		return reportSort;
	}
	public void setReportSort(int reportSort) {
		this.reportSort = reportSort;
	}
	public String getTypeNo() {
		return typeNo;
	}
	public void setTypeNo(String typeNo) {
		this.typeNo = typeNo;
	}
	public String getFromAddr() {
		return fromAddr;
	}
	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	
	
	public String getReportProperty() {
		return reportProperty;
	}

	public void setReportProperty(String reportProperty) {
		this.reportProperty = reportProperty;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 
	 * 仅用于测试
	 */
	@Override
	public String toString() {
		return "reportNo:"+reportNo+",reportName"+reportName+",reportDate"+reportDate;
	}
	
	
	/**
	 * 报表编号
	 */
	private String reportNo;
	
	/**
	 * 产制日期
	 */
	private String reportDate;

	/**
	 * 报表排序
	 */
	private int reportSort;
	
	/**
	 * 分类编号
	 */
	private String typeNo;
	
	/**
	 * 来源
	 */
	private String fromAddr;
	
	/**
	 * 报表名称
	 */
	private String reportName;
	
	
	/**
	 * 备注
	 */
	private String remark;
	
	
	/**
	 *  报表明细记录
	 */
	private List<AdErpReportRowItem> items;
	
	/**
	 *  报表属性
	 */
	private String  reportProperty;
	
	
}
