package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaoj
 * 
 * 生产快报左侧菜单解析
 *
 */
public class AdReportLeftMenuPage {
	
	public static List<AdReportLeftMenu> parse(String result)
	{
		List<AdReportLeftMenu> menus = new ArrayList<AdReportLeftMenu>();
		JSONArray menuJsonArray = null;
		JSONObject menuJsonObject = null;
		AdReportLeftMenu menuItem = null;
		
		try {
			menuJsonArray = new JSONArray(result);
				for (int i = 0; i < menuJsonArray.length(); ++i) {
					menuJsonObject = menuJsonArray.getJSONObject(i);
					menuItem = new AdReportLeftMenu();
					menuItem.setNo(menuJsonObject.getString("strClass"));
					menuItem.setName(menuJsonObject.getString("strContentName"));
					menuItem.setTitle(menuJsonObject.getString("strTitle"));
					menus.add(menuItem);
				}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return menus;
	}

}
