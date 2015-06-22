package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

/**
 * 
 * @author Wang Cheng
 *  
 * @date 2014年12月5日上午11:45:58
 */
public class AdMESOrdProcessTracePage {

	private int pagesize;
	private List<AdMESOrdProcessTrace> mesOrdProcessTracelis;
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public List<AdMESOrdProcessTrace> getMesOrdProcessTracelis() {
		return mesOrdProcessTracelis;
	}
	public void setMesOrdProcessTracelis(
			List<AdMESOrdProcessTrace> mesOrdProcessTracelis) {
		this.mesOrdProcessTracelis = mesOrdProcessTracelis;
	}
	/**
	 * 解析数据
	 * @param result
	 * @return
	 */
	public static AdMESOrdProcessTracePage parse(String result)
	{
		JSONObject jsonOrdProcessTracePage = null;
		JSONArray jsonOrdProcessTraceArray = null;
		JSONObject jsonOrdProcessTrace = null;
		JSONArray jsonOrdProcessTraceitemArray = null;
		JSONObject jsonOrdProcessTraceitem = null;
		
		List<AdMESOrdProcessTrace> mesOrdProcessTracelis = new ArrayList<AdMESOrdProcessTrace>();
		AdMESOrdProcessTracePage mesOrdProcessTracePage = new AdMESOrdProcessTracePage();
		AdMESOrdProcessTrace mesOrdProcessTrace = null;
		List<AdMESOrdProcessTraceItem> mesOrdProcessTraceitemlis =null;
		AdMESOrdProcessTraceItem mesOrdProcessTraceitem = null;
		try {
			jsonOrdProcessTracePage = new JSONObject(result);

			if (!jsonOrdProcessTracePage.isNull("pagesize")) {
				mesOrdProcessTracePage.setPagesize(jsonOrdProcessTracePage.getInt("pagesize"));
			}

			if (!jsonOrdProcessTracePage.isNull("mesOrdProcessTracelis")) {
				jsonOrdProcessTraceArray = jsonOrdProcessTracePage.getJSONArray("mesOrdProcessTracelis");
//				每一个订单
				for (int i = 0; i < jsonOrdProcessTraceArray.length(); i++) {
					jsonOrdProcessTrace = jsonOrdProcessTraceArray.getJSONObject(i);
					mesOrdProcessTrace = new AdMESOrdProcessTrace();
					mesOrdProcessTraceitemlis = new ArrayList<AdMESOrdProcessTraceItem>();
					mesOrdProcessTrace.setIns_date(jsonOrdProcessTrace.getString("ins_date"));
					mesOrdProcessTrace.setOrd_no(jsonOrdProcessTrace.getString("ord_no"));
					mesOrdProcessTrace.setOrd_item(jsonOrdProcessTrace.getString("ord_item"));
					mesOrdProcessTrace.setOrd_wgt(jsonOrdProcessTrace.getString("ord_wgt"));
					mesOrdProcessTrace.setCust_no(jsonOrdProcessTrace.getString("cust_no"));
					mesOrdProcessTrace.setStdspec(jsonOrdProcessTrace.getString("stdspec"));
					mesOrdProcessTrace.setOrd_size(jsonOrdProcessTrace.getString("ord_size"));
					jsonOrdProcessTraceitemArray = jsonOrdProcessTrace.getJSONArray("mesOrdProcessTraceItemlis");
//					订单详细内容
					for(int j = 0; j < jsonOrdProcessTraceitemArray.length(); j++){
						jsonOrdProcessTraceitem = jsonOrdProcessTraceitemArray.getJSONObject(j);
						mesOrdProcessTraceitem = new AdMESOrdProcessTraceItem();
						mesOrdProcessTraceitem.setPro_name(jsonOrdProcessTraceitem.getString("pro_name"));
						if (!jsonOrdProcessTraceitem.isNull("end_date")) {
							mesOrdProcessTraceitem.setEnd_date(jsonOrdProcessTraceitem.getString("end_date"));
						}
						if (!jsonOrdProcessTraceitem.isNull("fin_weight")) {
							mesOrdProcessTraceitem.setFin_weight(jsonOrdProcessTraceitem.getString("fin_weight"));
						}else{
							mesOrdProcessTraceitem.setFin_weight("0");
						}
						if (!jsonOrdProcessTraceitem.isNull("bef_weight")) {
							mesOrdProcessTraceitem.setBef_weight(jsonOrdProcessTraceitem.getString("bef_weight"));
						}else{
							mesOrdProcessTraceitem.setBef_weight("0");
						}
						if (!jsonOrdProcessTraceitem.isNull("aft_weight")) {
							mesOrdProcessTraceitem.setAft_weight(jsonOrdProcessTraceitem.getString("aft_weight"));
						}else{
							mesOrdProcessTraceitem.setAft_weight("0");
						}
						mesOrdProcessTraceitemlis.add(mesOrdProcessTraceitem);
					}
					mesOrdProcessTrace.setMesOrdProcessTraceItemlis(mesOrdProcessTraceitemlis);
					mesOrdProcessTracelis.add(mesOrdProcessTrace);
					mesOrdProcessTrace.setVisibity(View.GONE);
				}
				mesOrdProcessTracePage.setMesOrdProcessTracelis(mesOrdProcessTracelis);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mesOrdProcessTracePage;
	}
}
