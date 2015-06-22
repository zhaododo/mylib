package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdMenu {

	/**
	 * 菜单编号
	 */
	private String strSn;
	/**
	 * 菜单名称
	 */
	private String strName;
	
	/**
	 * 模块地址
	 */
	private String strActivity;
	
	/**
	 * 程序编号
	 */
	private String programNo;
	/**
	 * 程序URL
	 */
	private String programURL;
	/**
	 * 是否是叶子节点
	 */
	private boolean leaf = false;

	/**
	 * 优先级
	 */
	private Integer intPriority = 99;

	public String getStrSn() {
		return strSn;
	}

	public void setStrSn(String strSn) {
		this.strSn = strSn;
	}

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	public String getProgramNo() {
		return programNo;
	}

	public void setProgramNo(String programNo) {
		this.programNo = programNo;
	}

	public String getProgramURL() {
		return programURL;
	}

	public void setProgramURL(String programURL) {
		this.programURL = programURL;
	}

	public Integer getIntPriority() {
		return intPriority;
	}

	public void setIntPriority(Integer intPriority) {
		this.intPriority = intPriority;
	}

	public boolean isLeaf() {
		if (children.isEmpty()) {
			leaf = true;
		} else {
			leaf = false;
		}
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	private List<AdMenu> children;

	public List<AdMenu> getChildren() {
		return children;
	}

	public void setChildren(List<AdMenu> children) {
		this.children = children;
	}
	
	
	public String getStrActivity() {
		return strActivity;
	}

	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}


	public AdMenu() {
		children = new ArrayList<AdMenu>();
	}
	
	
	private boolean find(AdMenu menu,final String activityName)
	{
		boolean isfind = false;
		if (menu != null)
		{
			if (activityName !=null && activityName.equals(menu.getStrActivity()))
			{
				isfind = true;
			}
		}
		
		return isfind;
	}
	
	/**
	 * 按activityName查找菜单
	 * @param parentMenu    根菜单
	 * @param activityName  Activity名称
	 * @return
	 */
	private AdMenu findMenu(AdMenu parentMenu,String activityName)
	{
		if (find(parentMenu,activityName))
		{
			return parentMenu;
		}
		else
		{
			List<AdMenu> menuList = parentMenu.getChildren();
			if (menuList != null)
			{
				
				for (AdMenu adMenu : menuList) {
						return findMenu(adMenu,activityName);
				}
			}
			
		}
		
		return null;
			
	}
	
	
	/**
	 * 查找Menu
	 * @param activityName
	 * @return
	 */
	public AdMenu findActivity(String activityName)
	{
		return findMenu(this,activityName);
		
	}
	
	/**
	 * 按json格式构建对象
	 * @param json
	 */
	protected void build(JSONObject json) {
		try {
			
			strSn = json.getString("strSn");
			strName = json.getString("strName");
			intPriority = json.getInt("intPriority");
			leaf = json.getBoolean("leaf");
			
			if (!json.isNull("strActivity"))
			{
				strActivity = json.getString("strActivity");
			}
			
			if (!json.isNull("programNo"))
			{
				programNo = json.getString("programNo");
			}
			
			if (!json.isNull("programURL"))
			{
				programURL = json.getString("programURL");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 递归生成
	 * @param parent
	 * @param json
	 */
	public static  void makeTree(AdMenu parent,JSONObject json)
	{
		JSONArray jsonArray = null;
		AdMenu tmpMenu = null;
		try {
			jsonArray = json.getJSONArray("children");
			
			if (jsonArray == null ) return;

			//如果存在子节点
			for (int i =0;i < jsonArray.length(); ++i)
			{
				//按子节点生成Menu
				tmpMenu = new AdMenu();
				tmpMenu.build(jsonArray.getJSONObject(i));
				parent.getChildren().add(tmpMenu);
				
				makeTree(tmpMenu,jsonArray.getJSONObject(i));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static AdMenu getInstance(String result)
	{
		JSONObject jsonObject;
		AdMenu adMenu = new AdMenu();
		try 
		{
			jsonObject = new JSONObject(result);
			adMenu.build(jsonObject);
			
			makeTree(adMenu,jsonObject);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return adMenu;
	}
	
	

}
