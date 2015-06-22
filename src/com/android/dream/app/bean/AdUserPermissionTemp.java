package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;

/**
 * @author Wang Cheng
 *
 * @date 2014-10-20下午9:38:19
 */
public class AdUserPermissionTemp {

//	用户activity权限
	private String strPermission;
//	activity-action
	private String straction;
	public String getStrPermission() {
		return strPermission;
	}
	public void setStrPermission(String strPermission) {
		this.strPermission = strPermission;
	}
	public String getStraction() {
		return straction;
	}
	public void setStraction(String straction) {
		this.straction = straction;
	}
	
	/**
	 * 解析数据
	 * @param result
	 * @return
	 */
	public static List<AdUserPermissionTemp> parse(String result)
	{
		JSONArray jsonuserPermissionTemparray = null;
		JSONObject jsonuserPermissionTemp = null;
		
		List<AdUserPermissionTemp> userPermissionTemplis = new ArrayList<AdUserPermissionTemp>();
		AdUserPermissionTemp userPermissionTemp = null;
		try {
			jsonuserPermissionTemparray = new JSONArray(result);
				for (int i = 0; i < jsonuserPermissionTemparray.length(); ++i) {
					jsonuserPermissionTemp = jsonuserPermissionTemparray.getJSONObject(i);
					userPermissionTemp = new AdUserPermissionTemp();
					String permision = jsonuserPermissionTemp.getString("strPermission");
					userPermissionTemp.setStrPermission(permision.split(":")[0]);
					userPermissionTemp.setStraction(permision.split(":")[1]);
					userPermissionTemplis.add(userPermissionTemp);
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userPermissionTemplis;
	}
}
