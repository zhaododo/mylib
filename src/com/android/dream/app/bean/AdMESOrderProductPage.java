package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

public class AdMESOrderProductPage {

//	本次加载的记录数（实际记录数）
	private int pageSize;
	private List<AdMESOrderProduct>  erpOrderProductlis;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<AdMESOrderProduct> getErpOrderProductlis() {
		return erpOrderProductlis;
	}

	public void setErpOrderProductlis(List<AdMESOrderProduct> erpOrderProductlis) {
		this.erpOrderProductlis = erpOrderProductlis;
	}

	/**
	 * 解析数据
	 * @param result
	 * @return
	 */
	public static AdMESOrderProductPage parse(String result)
	{
		JSONObject jsonBoardPage = null;
		JSONArray jsonBoardArray = null;
		JSONObject jsonBoard = null;
		
		List<AdMESOrderProduct> erpOrderProductlis = new ArrayList<AdMESOrderProduct>();
		AdMESOrderProductPage orderProductPage = new AdMESOrderProductPage();
		AdMESOrderProduct orderProduct = null;
		try {
			jsonBoardPage = new JSONObject(result);

			if (!jsonBoardPage.isNull("pageSize")) {
				orderProductPage.setPageSize(jsonBoardPage.getInt("pageSize"));
			}

			if (!jsonBoardPage.isNull("erpOrderProductlis")) {
				jsonBoardArray = jsonBoardPage.getJSONArray("erpOrderProductlis");

				for (int i = 0; i < jsonBoardArray.length(); ++i) {
					jsonBoard = jsonBoardArray.getJSONObject(i);
					orderProduct = new AdMESOrderProduct();

					orderProduct.setCusno(jsonBoard.getString("cusno"));
					orderProduct.setCusname(jsonBoard.getString("cusname"));
					orderProduct.setCusdeldate(jsonBoard.getString("cusdeldate"));
					if(!jsonBoard.isNull("jit_stringa")){
						orderProduct.setBoardno(jsonBoard.getString("jit_stringa"));
					}else{
						orderProduct.setBoardno("");
					}
					
					orderProduct.setOrdertotal(jsonBoard.getDouble("ordertotal"));
					orderProduct.setOrderown(jsonBoard.getDouble("orderown"));
					orderProduct.setSteelmaking(jsonBoard.getDouble("steelmaking"));
					orderProduct.setSteelrolling(jsonBoard.getDouble("steelrolling"));
					orderProduct.setFinishing(jsonBoard.getDouble("finishing"));
					orderProduct.setLastdec(jsonBoard.getDouble("lastdec"));
					orderProduct.setUnstorage(jsonBoard.getDouble("unstorage"));
					orderProduct.setInstorage(jsonBoard.getDouble("instorage"));
					orderProduct.setOnway(jsonBoard.getDouble("onway"));
					orderProduct.setWaitdeliver(jsonBoard.getDouble("waitdeliver"));
					orderProduct.setFinishdeliver(jsonBoard.getDouble("finishdeliver"));
					orderProduct.setFinishpercent(jsonBoard.getDouble("finishpercent"));
					orderProduct.setVisibity(View.GONE);
					erpOrderProductlis.add(orderProduct);
				}
				orderProductPage.setErpOrderProductlis(erpOrderProductlis);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return orderProductPage;
	}
	
}
