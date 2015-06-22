package com.android.dream.app.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaoj
 * 
 * 操作状态（用于处理增、删、改操作）
 *
 */
public class AdAjaxOpt {
	
	/**
	 * 成功状态
	 */
	public final static String OK_STATUS="Y";
	
	/**
	 * 错误状态
	 */
	public final static String ERROR_STATUS="N";
	
	/**
	 * 错误信息
	 */
	public final static String ERROR_MSG="操作失败！";
	
	
	/**
	 * 信息
	 */
	private String msg = ERROR_MSG;
	
	/**
	 * 状态
	 */
	private String status = ERROR_STATUS;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	/**
	 * 获得操作实例
	 * @param result
	 * @return
	 */
	public static AdAjaxOpt parse(String result)
	{
		JSONObject jsonOpt;
		AdAjaxOpt opt = new AdAjaxOpt();
		try {
			jsonOpt = new JSONObject(result);
			
			opt.setStatus(jsonOpt.getString("status"));
			opt.setMsg(jsonOpt.getString("msg"));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return opt;
	}

	
}
