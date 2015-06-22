package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdXTUserPageVo {

	private int pageSize;
	private List<AdXTUser> erpdeplis;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public List<AdXTUser> getErpdeplis() {
		return erpdeplis;
	}
	public void setErpdeplis(List<AdXTUser> erpdeplis) {
		this.erpdeplis = erpdeplis;
	}
	/**
	 * json转换
	 * @param result
	 * @return
	 */
	public static AdXTUserPageVo parse(String result)
	{
		JSONObject jsonErpuserPage = null;
		JSONArray jsonErpUserArray = null;
		JSONObject jsonerpuser = null;
		AdXTUserPageVo erpuserpagevo = new AdXTUserPageVo();
		List<AdXTUser> erpuserlis = new ArrayList<AdXTUser>();
		AdXTUser adErpuser = null;
		try 
		{
			jsonErpuserPage = new JSONObject(result);

			if (!jsonErpuserPage.isNull("pageSize")) {
				erpuserpagevo.setPageSize(jsonErpuserPage.getInt("pageSize"));
			}

			if (!jsonErpuserPage.isNull("erpdeplis")) {
				jsonErpUserArray = jsonErpuserPage.getJSONArray("erpdeplis");
				if (jsonErpUserArray != null)
				{
					for (int i=0; i <jsonErpUserArray.length(); ++i)
					{
						jsonerpuser = jsonErpUserArray.getJSONObject(i);
						adErpuser = new AdXTUser();
//						adErpuser.setUserno(jsonerpuser.getString("userno"));
//						adErpuser.setUsername(jsonerpuser.getString("username"));
						if(!jsonerpuser.isNull("userno")){
							adErpuser.setUserno(jsonerpuser.getString("userno"));
						}
						if(!jsonerpuser.isNull("username")){
							adErpuser.setUsername(jsonerpuser.getString("username"));
						}
						if(!jsonerpuser.isNull("companyno")){
							adErpuser.setCompanyno(jsonerpuser.getString("companyno"));
						}
						if(!jsonerpuser.isNull("companyname")){
							adErpuser.setCompanyname(jsonerpuser.getString("companyname"));
						}
						if(!jsonerpuser.isNull("deppartmentno")){
							adErpuser.setDeppartmentno(jsonerpuser.getString("deppartmentno"));
						}
						if(!jsonerpuser.isNull("deppartmentname")){
							adErpuser.setDeppartmentname(jsonerpuser.getString("deppartmentname"));
						}
						if(!jsonerpuser.isNull("phone3")){
							adErpuser.setMobilephone(jsonerpuser.getString("phone3"));
						}
						else{
							adErpuser.setMobilephone("");
						}
						if(!jsonerpuser.isNull("telephone")){
							adErpuser.setTelephone(jsonerpuser.getString("telephone"));
						}
						else{
							adErpuser.setTelephone("");
						}
						erpuserlis.add(adErpuser);
					}
					erpuserpagevo.setErpdeplis(erpuserlis);
				}
			}
				

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return erpuserpagevo;
	}
}
