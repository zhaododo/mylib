package com.android.dream.app.bean;

public class ErpPayBasicInfo {
	
	/**
	 * 工号
	 */
	private String userno;
	
	/**
	 * 姓名
	 */
	private String username;

	/**
	 * 公司别
	 */
	private String compid;
	
	/**
	 * 部门名称
	 */
	private String deptname;

	/**
	 * 目前岗级
	 */
	private String joblevel;
	
	/**
	 * 工种
	 */
	private String jobType;
	
	/**
	 * 基本薪资
	 */
	private String basicSalary;

	public String getUserno() {
		return userno;
	}
	public void setUserno(String userno) {
		this.userno = userno;
	}
	public String getCompid() {
		return compid;
	}
	public void setCompid(String compid) {
		this.compid = compid;
	}
	
	public String getJoblevel() {
		return joblevel;
	}
	public void setJoblevel(String joblevel) {
		this.joblevel = joblevel;
	}
	
	public String getDeptname() {
		return deptname;
	}
	public void setDeptname(String deptname) {
		this.deptname = deptname;
	}
	
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
//	@Override
//	public String toString() {
//		String json = JSONObject.toJSONString(this);
//		return json;
//	}
	
	public String getBasicSalary() {
		return basicSalary;
	}
	public void setBasicSalary(String basicSalary) {
		this.basicSalary = basicSalary;
	}
}
