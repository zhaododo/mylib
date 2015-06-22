package com.android.dream.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class AdUpdate {
	
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getUpdateLog() {
		return updateLog;
	}
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}
	
	public static AdUpdate getInstance(String result)
	{
		JSONObject jsonObject;
		AdUpdate up = new AdUpdate();
		try {
			jsonObject = new JSONObject(result);
			up.setVersionCode(jsonObject.getInt("versionCode"));
			up.setDownloadUrl(jsonObject.getString("downloadUrl"));
			up.setVersionName(jsonObject.getString("versionName"));
			up.setUpdateLog(jsonObject.getString("updateLog"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return up;
	}
	
	private int versionCode;
	private String versionName;
	private String downloadUrl;
	private String updateLog;
}
