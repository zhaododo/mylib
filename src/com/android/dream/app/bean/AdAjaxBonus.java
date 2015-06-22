package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdAjaxBonus {
	
	private String userno;
	private String pwd;
	private String keyCode;
	
	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}


	public String getPwd() {
		return pwd;
	}


	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	
	public static List<AdAjaxBonus> getInstance(String result)
	{
		JSONObject jsonObject;
		JSONArray jsonArray;
		JSONObject tJsonObject;
		AdAjaxBonus bonus;
		
		List<AdAjaxBonus> list = new ArrayList<AdAjaxBonus>();
		try {
			jsonObject = new JSONObject(result).getJSONObject("users");
			jsonArray = jsonObject.getJSONArray("users");
			
			for (int i=0; i<jsonArray.length(); ++i)
			{
				tJsonObject = jsonArray.getJSONObject(i);
				if (tJsonObject != null)
				{
					bonus = new AdAjaxBonus();
					bonus.setUserno(tJsonObject.getString("userno"));
					bonus.setPwd(tJsonObject.getString("pwd"));
					bonus.setKeyCode(tJsonObject.getString("keyCode"));
					list.add(bonus);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
