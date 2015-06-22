package com.android.dream.app.bean;


/**
 * @author zhaoj
 *
 * 行记录明细
 */
public class AdErpReportRowItem {
	
	public String getReportNo() {
		return reportNo;
	}
	public void setReportNo(String reportNo) {
		this.reportNo = reportNo;
	}
	public int getFieldSort() {
		return fieldSort;
	}
	public void setFieldSort(int fieldSort) {
		this.fieldSort = fieldSort;
	}
	public String getTargetField() {
		return targetField;
	}
	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public String getTargetValue() {
		return targetValue;
	}
	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 * 
	 * 仅用于测试
	 */
	@Override
	public String toString() {
		return "targetName:"+targetName+",targetValue"+targetValue;
	}
	
	/**
	 * AdErpReportRowVo对应的reportNo，该字段主要用于定位
	 */
	
	private String reportNo;
	
	/**
	 * 排序
	 */
	private int fieldSort;
	
	/**
	 * 指标编号
	 */
	private String targetField;
	
	/**
	 * 指标名称
	 */
	private String targetName;
	
	/**
	 * 指标值
	 */
	private String targetValue;
	
	/**
	 * 单位
	 */
	private String unit;
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * 备注
	 */
	private String remark;
}
