package com.android.dream.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class AdAjaxLogin {

	// 状态码
	public final static String  STATUS_CODE_SUCCESS = "100";
	public final static String  STATUS_CODE_USER_ERROR = "300";
	public final static String  STATUS_CODE_PASSWORD_ERROR = "301";
	public final static String  STATUS_CODE_USER_PASSWORD_ERROR = "302";
	//用户停用
	public final static String  STATUS_CODE_USER_STOP = "303";
	
	//token验证
	public final static String  STATUS_TOKEN_VERTIFY_ERROR = "400";
	public final static String  STATUS_TOKEN_NOT_FOUND = "401";
	
	private String statusCode = STATUS_CODE_USER_PASSWORD_ERROR;
	
	private String message = "错误的用户名或密码！";
	
	/**
	 * 返回的token
	 */
	private String token;
	
	/**
	 * 工号
	 */
	private String userno;
	
	/**
	 * 姓名
	 */
	private String username;
	
	/**
	 * 是否显示发送贺卡
	 */
	private String showcard;
	
	public String getShowcard() {
		return showcard;
	}
	public void setShowcard(String showcard) {
		this.showcard = showcard;
	}
	public String getUserno() {
		return userno;
	}
	public void setUserno(String userno) {
		this.userno = userno;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static AdAjaxLogin getInstance(String result)
	{
		JSONObject jsonObject;
		AdAjaxLogin al = new AdAjaxLogin();
		try {
			jsonObject = new JSONObject(result);
			al.setStatusCode(jsonObject.getString("statusCode"));
			al.setMessage(jsonObject.getString("message"));
			al.setUserno(jsonObject.getString("userno"));
			al.setUsername(jsonObject.getString("username"));
			al.setShowcard(jsonObject.getString("showcard"));
			
			if (al.getStatusCode().equalsIgnoreCase(AdAjaxLogin.STATUS_CODE_SUCCESS))
			{
				al.setToken(jsonObject.getString("token"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return al;
	}
}
