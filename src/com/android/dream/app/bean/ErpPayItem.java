package com.android.dream.app.bean;

import java.math.BigDecimal;

public class ErpPayItem implements Comparable<Object>{

	/**
	 *  编码
	 */
	private String salcode;
	
	/**
	 *  名称
	 */
	private String salname;
	
	/**
	 *  金额
	 */
	private BigDecimal salamt;
	
	/**
	 * 金额字符串（保留两位小数）
	 */
	private String strSalamt;
	
	public String getSalcode() {
		return salcode;
	}
	public void setSalcode(String salcode) {
		this.salcode = salcode;
	}
	public String getSalname() {
		return salname;
	}
	public void setSalname(String salname) {
		this.salname = salname;
	}
	public BigDecimal getSalamt() {
		return salamt;
	}
	public void setSalamt(BigDecimal salamt) {
		this.salamt = salamt;
	}
	public String getStrSalamt() {
		return strSalamt;
	}
	public void setStrSalamt(String strSalamt) {
		this.strSalamt = strSalamt;
	}
	@Override
	public int compareTo(Object another) {
		
		if (another instanceof ErpPayItem)
		{
			return this.salcode.compareTo(((ErpPayItem) another).salcode);
		}
		
		return 0;
	}

}
