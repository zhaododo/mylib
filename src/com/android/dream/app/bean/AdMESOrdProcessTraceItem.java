package com.android.dream.app.bean;

/**
 * 
 * @author Wang Cheng
 *  
 * @date 2014年12月5日上午11:45:50
 */
public class AdMESOrdProcessTraceItem {

	private String pro_name; // 工序名称
	private String end_date; // 工序结束时间
	private String fin_weight; //工序已完成的量
	private String bef_weight; //工序截止之前完成的量
	private String aft_weight; //工序截止之后完成的量
	public String getPro_name() {
		return pro_name;
	}
	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getFin_weight() {
		return fin_weight;
	}
	public void setFin_weight(String fin_weight) {
		this.fin_weight = fin_weight;
	}
	public String getBef_weight() {
		return bef_weight;
	}
	public void setBef_weight(String bef_weight) {
		this.bef_weight = bef_weight;
	}
	public String getAft_weight() {
		return aft_weight;
	}
	public void setAft_weight(String aft_weight) {
		this.aft_weight = aft_weight;
	}
	
}
