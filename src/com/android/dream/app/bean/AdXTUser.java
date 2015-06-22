package com.android.dream.app.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhaoj
 * 
 *  协同办公用户基本信息
 *
 */
public class AdXTUser implements Serializable{

	private static final long serialVersionUID = 9011753823239237026L;

	/**
	 * 工号
	 */
	private String userno;
	
	/**
	 * 输入的密码
	 */
	private String password;
	
	/**
	 * 数据库中的加密密码
	 */
	private String cryptopw;
	
	/**
	 * 手机号
	 */
	private String mobilephone;
	//用户名
	private String username;
	//固话
	private String telephone;
	//单位编码
	private String companyno;
	//单位部门编码
	private String deppartmentno;
	//单位名称
	private String companyname;
	//单位部门名称
	private String deppartmentname;
	//是否公开手机号码
	private String isvisiblemobile;
	//是否公开固话
	private String isvisibletelephone;
	public String getUserno() {
		return userno;
	}
	public void setUserno(String userno) {
		this.userno = userno;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCryptopw() {
		return cryptopw;
	}
	public void setCryptopw(String cryptopw) {
		this.cryptopw = cryptopw;
	}
	 
	public String getMobilephone() {
		return mobilephone;
	}
	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getCompanyno() {
		return companyno;
	}
	public void setCompanyno(String companyno) {
		this.companyno = companyno;
	}
	public String getDeppartmentno() {
		return deppartmentno;
	}
	public void setDeppartmentno(String deppartmentno) {
		this.deppartmentno = deppartmentno;
	}
	public String getCompanyname() {
		return companyname;
	}
	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}
	public String getDeppartmentname() {
		return deppartmentname;
	}
	public void setDeppartmentname(String deppartmentname) {
		this.deppartmentname = deppartmentname;
	}
	public String getIsvisiblemobile() {
		return isvisiblemobile;
	}
	public void setIsvisiblemobile(String isvisiblemobile) {
		this.isvisiblemobile = isvisiblemobile;
	}
	public String getIsvisibletelephone() {
		return isvisibletelephone;
	}
	public void setIsvisibletelephone(String isvisibletelephone) {
		this.isvisibletelephone = isvisibletelephone;
	}
	public static AdXTUser parse(String result)
	{
		AdXTUser xtUser = new AdXTUser();
		
		try {
			JSONObject jsonUser = new JSONObject(result);
			xtUser.setUserno(jsonUser.getString("userno"));
			xtUser.setUsername(jsonUser.getString("username"));
			xtUser.setTelephone(jsonUser.getString("telephone"));
			xtUser.setMobilephone(jsonUser.getString("phone3"));
			xtUser.setCompanyno(jsonUser.getString("companyno"));
			xtUser.setDeppartmentno(jsonUser.getString("deppartmentno"));
			xtUser.setDeppartmentname(jsonUser.getString("deppartmentname"));
			xtUser.setCompanyname(jsonUser.getString("companyname"));
			xtUser.setIsvisiblemobile(jsonUser.getString("isvisiblemobile"));
			xtUser.setIsvisibletelephone(jsonUser.getString("isvisibletelephone"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return xtUser;
	}
}
