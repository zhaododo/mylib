package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-18上午10:49:36
 */
public class AdMESOrdEnterPage {

	/**
	 *  本次加载的记录数（实际记录数）
	 */
	private int pageSize;
	
	//当月总量
	private Double orderentersum;
	/**
	 *  客户订单录入信息
	 */
	private List<AdMESOrdEnter> ordEnters = new ArrayList<AdMESOrdEnter>();

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<AdMESOrdEnter> getOrdEnters() {
		return ordEnters;
	}

	public void setOrdEnters(List<AdMESOrdEnter> ordEnters) {
		this.ordEnters = ordEnters;
	}
	public Double getOrderentersum() {
		return orderentersum;
	}

	public void setOrderentersum(Double orderentersum) {
		this.orderentersum = orderentersum;
	}

	/**
	 * 解析数据
	 * @param result
	 * @return
	 */
	public static AdMESOrdEnterPage parse(String result)
	{
		JSONObject jsonBoardPage = null;
		JSONArray jsonBoardArray = null;
		JSONObject jsonBoard = null;
		
		List<AdMESOrdEnter> orderenterList = new ArrayList<AdMESOrdEnter>();
		AdMESOrdEnterPage orderEnterPage = new AdMESOrdEnterPage();
		AdMESOrdEnter orderEnter = null;
		try {
			jsonBoardPage = new JSONObject(result);

			if (!jsonBoardPage.isNull("pageSize")) {
				orderEnterPage.setPageSize(jsonBoardPage.getInt("pageSize"));
			}

			if (!jsonBoardPage.isNull("orderentersum")) {
				orderEnterPage.setOrderentersum(jsonBoardPage.getDouble("orderentersum"));
			}
			
			if (!jsonBoardPage.isNull("ordEnters")) {
				jsonBoardArray = jsonBoardPage.getJSONArray("ordEnters");

				for (int i = 0; i < jsonBoardArray.length(); ++i) {
					jsonBoard = jsonBoardArray.getJSONObject(i);
					orderEnter = new AdMESOrdEnter();

					orderEnter.setConpanyid(jsonBoard.getString("conpanyid"));
					orderEnter.setConpanyname(jsonBoard.getString("conpanyname"));
					orderEnter.setEnterno(jsonBoard.getDouble("enterno"));
					
					orderenterList.add(orderEnter);
				}
				orderEnterPage.setOrdEnters(orderenterList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return orderEnterPage;
	}
}
