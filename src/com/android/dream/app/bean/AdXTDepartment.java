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
 * @date 2014-9-23上午午09:11:18
 */
public class AdXTDepartment {

	private String depno;
//	部门名称
	private String depname;
	private List<AdXTUser> depuser;
//	部门人员数量
	private int depusernum;
	private String isfull;
	public String getDepno() {
		return depno;
	}
	public void setDepno(String depno) {
		this.depno = depno;
	}
	public String getDepname() {
		return depname;
	}
	public void setDepname(String depname) {
		this.depname = depname;
	}
	public List<AdXTUser> getDepuser() {
		return depuser;
	}
	public void setDepuser(List<AdXTUser> depuser) {
		this.depuser = depuser;
	}

	public String getIsfull() {
		return isfull;
	}
	public void setIsfull(String isfull) {
		this.isfull = isfull;
	}
	public int getDepusernum() {
		return depusernum;
	}
	public void setDepusernum(int depusernum) {
		this.depusernum = depusernum;
	}
	/**
	 * json转换
	 * @param result
	 * @return
	 */
	public static List<AdXTDepartment> parse(String result)
	{
		JSONArray jsonDepartmentArray = null;
		JSONArray jsonDepUserArray = null;
		JSONObject jsonDepartment = null;
		JSONObject jsonerpuser = null;

		List<AdXTDepartment> erpdeplis = new ArrayList<AdXTDepartment>();
		List<AdXTUser> depUserList = null;
		
		AdXTDepartment adErpDep = null;
		AdXTUser adErpuser = null;
		try 
		{
			jsonDepartmentArray = new JSONArray(result);
			if (jsonDepartmentArray != null)
			{
				for (int i=0; i <jsonDepartmentArray.length(); ++i)
				{
					jsonDepartment = jsonDepartmentArray.getJSONObject(i);
					adErpDep = new AdXTDepartment();
					adErpDep.setDepname(jsonDepartment.getString("depname"));
					adErpDep.setDepno(jsonDepartment.getString("depno"));
					if(!jsonDepartment.isNull("depusernum")){
						adErpDep.setDepusernum(jsonDepartment.getInt("depusernum"));
					}
					else{
						adErpDep.setDepusernum(0);
					}
					
					//验证是否有效depusernum
					if (!jsonDepartment.isNull("depuser"))
					{
						jsonDepUserArray = jsonDepartment.getJSONArray("depuser");
						depUserList = new ArrayList<AdXTUser>();

						//获得行记录对象
						for (int j=0;j <jsonDepUserArray.length();++j)
						{
							jsonerpuser = jsonDepUserArray.getJSONObject(j);
							
							adErpuser = new AdXTUser();
							if(!jsonerpuser.isNull("userno")){
								adErpuser.setUserno(jsonerpuser.getString("userno"));
							}
							else{
								adErpuser.setUserno("");
							}
							if(!jsonerpuser.isNull("username")){
								adErpuser.setUsername(jsonerpuser.getString("username"));
							}
//							adErpuser.setUserno(jsonerpuser.getString("userno"));
//							adErpuser.setUsername(jsonerpuser.getString("username"));
//							adErpuser.setCompanyno(jsonerpuser.getString("companyno"));
//							adErpuser.setCompanyname(jsonerpuser.getString("companyname"));
//							adErpuser.setDeppartmentno(jsonerpuser.getString("deppartmentno"));
//							adErpuser.setDeppartmentname(jsonerpuser.getString("deppartmentname"));
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
							
							depUserList.add(adErpuser);
						}
						adErpDep.setDepuser(depUserList);
						adErpDep.setIsfull("N");
					}
					erpdeplis.add(adErpDep);
				}
			}		

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return erpdeplis;
	}
}
