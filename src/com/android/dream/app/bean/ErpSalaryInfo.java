package com.android.dream.app.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ErpSalaryInfo extends ErpPayBasicInfo{
//	工资信息
	private String wagesshouldPay;
	private String wagesrealPay;
	private String wagesdeductPay;
	private List<ErpPayItem> wagesshouldPayDetail;
	private List<ErpPayItem> wagesdeductPayDetail;
//	奖金信息
	private String bonusshouldPay;
	private String bonusrealPay;
	private String bonusdeductPay;
	private List<ErpPayItem> bonusshouldPayDetail;
	private List<ErpPayItem> bonusdeductPayDetail;
	
	public String getWagesshouldPay() {
		return wagesshouldPay;
	}
	public void setWagesshouldPay(String wagesshouldPay) {
		this.wagesshouldPay = wagesshouldPay;
	}
	public String getWagesrealPay() {
		return wagesrealPay;
	}
	public void setWagesrealPay(String wagesrealPay) {
		this.wagesrealPay = wagesrealPay;
	}
	public String getWagesdeductPay() {
		return wagesdeductPay;
	}
	public void setWagesdeductPay(String wagesdeductPay) {
		this.wagesdeductPay = wagesdeductPay;
	}
	public List<ErpPayItem> getWagesshouldPayDetail() {
		return wagesshouldPayDetail;
	}
	public void setWagesshouldPayDetail(List<ErpPayItem> wagesshouldPayDetail) {
		this.wagesshouldPayDetail = wagesshouldPayDetail;
	}
	public List<ErpPayItem> getWagesdeductPayDetail() {
		return wagesdeductPayDetail;
	}
	public void setWagesdeductPayDetail(List<ErpPayItem> wagesdeductPayDetail) {
		this.wagesdeductPayDetail = wagesdeductPayDetail;
	}
	public String getBonusshouldPay() {
		return bonusshouldPay;
	}
	public void setBonusshouldPay(String bonusshouldPay) {
		this.bonusshouldPay = bonusshouldPay;
	}
	public String getBonusrealPay() {
		return bonusrealPay;
	}
	public void setBonusrealPay(String bonusrealPay) {
		this.bonusrealPay = bonusrealPay;
	}
	public String getBonusdeductPay() {
		return bonusdeductPay;
	}
	public void setBonusdeductPay(String bonusdeductPay) {
		this.bonusdeductPay = bonusdeductPay;
	}
	public List<ErpPayItem> getBonusshouldPayDetail() {
		return bonusshouldPayDetail;
	}
	public void setBonusshouldPayDetail(List<ErpPayItem> bonusshouldPayDetail) {
		this.bonusshouldPayDetail = bonusshouldPayDetail;
	}
	public List<ErpPayItem> getBonusdeductPayDetail() {
		return bonusdeductPayDetail;
	}
	public void setBonusdeductPayDetail(List<ErpPayItem> bonusdeductPayDetail) {
		this.bonusdeductPayDetail = bonusdeductPayDetail;
	}
	
	public static ErpSalaryInfo parse(JSONObject jsonObject) throws JSONException {
		
		JSONArray jsonArray =null;
		JSONObject basicjsonObject =null;
		JSONObject wagsjsonObject =null;
		JSONObject bonusjsonObject =null;
		JSONObject datailjsonObject =null;
		int detailesize=0;
		List<ErpPayItem> WagesshouldPaylis=null;
		List<ErpPayItem> Wagesdeductlis=null;
		List<ErpPayItem> Bonusdeductlis=null;
		List<ErpPayItem> BonusshouldPaylis=null;
		ErpPayItem erpPayItem =null;
		
		ErpSalaryInfo  erpSalaryInfo  =new ErpSalaryInfo();
//		基本信息
		basicjsonObject  =  jsonObject.getJSONObject("basicInfo");
		
		erpSalaryInfo.setBasicSalary(String.format("%.2f", Double.parseDouble(basicjsonObject.getString("basicSalary"))));
		erpSalaryInfo.setDeptname(basicjsonObject.getString("deptname"));
		erpSalaryInfo.setJobType(basicjsonObject.getString("jobType"));
		erpSalaryInfo.setJoblevel(basicjsonObject.getString("joblevel"));
		erpSalaryInfo.setUsername(basicjsonObject.getString("username"));
		erpSalaryInfo.setUserno(basicjsonObject.getString("userno"));
		Log.v("wchtest", basicjsonObject.toString());
		
//		工资信息
//		工资信息主档
		wagsjsonObject=jsonObject.getJSONObject("mapWages");
		erpSalaryInfo.setWagesshouldPay(wagsjsonObject.getString("shouldPay"));
		erpSalaryInfo.setWagesdeductPay(wagsjsonObject.getString("deductPay"));
		erpSalaryInfo.setWagesrealPay(wagsjsonObject.getString("realPay"));
//		工资信息明细
//		扣费信
		jsonArray = jsonObject.getJSONObject("mapWages").getJSONArray("deductPayDetail");
		detailesize=jsonArray.length();
		Wagesdeductlis=new ArrayList<ErpPayItem>();
		for(int i=0;i<detailesize;i++){
			erpPayItem=new ErpPayItem();
			datailjsonObject = jsonArray.getJSONObject(i);
//			salamt":800.0000,"salcode":"51","salname":"养老金个人负担","strSalamt":"800.00"
			erpPayItem.setSalcode(datailjsonObject.getString("salcode"));
			erpPayItem.setSalname(datailjsonObject.getString("salname"));
			erpPayItem.setStrSalamt(datailjsonObject.getString("strSalamt"));
			erpPayItem.setSalamt(new BigDecimal(datailjsonObject.getString("salamt")));
			Wagesdeductlis.add(erpPayItem);
		}
		Collections.sort(Wagesdeductlis);
		erpSalaryInfo.setWagesdeductPayDetail(Wagesdeductlis);
		Log.v("wchtest", Wagesdeductlis.toString());
//		发放明细
		jsonArray = jsonObject.getJSONObject("mapWages").getJSONArray("shouldPayDetail");
		detailesize=jsonArray.length();
		WagesshouldPaylis=new ArrayList<ErpPayItem>();
		for(int i=0;i<detailesize;i++){
			datailjsonObject = jsonArray.getJSONObject(i);
			erpPayItem=new ErpPayItem();
//			salamt":800.0000,"salcode":"51","salname":"养老金个人负担","strSalamt":"800.00"
			erpPayItem.setSalcode(datailjsonObject.getString("salcode"));
			erpPayItem.setSalname(datailjsonObject.getString("salname"));
			erpPayItem.setStrSalamt(datailjsonObject.getString("strSalamt"));
			erpPayItem.setSalamt(new BigDecimal(datailjsonObject.getString("salamt")));
			WagesshouldPaylis.add(erpPayItem);
		}
		Collections.sort(WagesshouldPaylis);
		erpSalaryInfo.setWagesshouldPayDetail(WagesshouldPaylis);
		
//		奖金信息
//		奖金信息主档
		bonusjsonObject=jsonObject.getJSONObject("mapBonus");
		erpSalaryInfo.setBonusshouldPay(bonusjsonObject.getString("shouldPay"));
		erpSalaryInfo.setBonusdeductPay(bonusjsonObject.getString("deductPay"));
		erpSalaryInfo.setBonusrealPay(bonusjsonObject.getString("realPay"));
//		工资信息明细
//		扣费信
		jsonArray = jsonObject.getJSONObject("mapBonus").getJSONArray("deductPayDetail");
		detailesize=jsonArray.length();
		Bonusdeductlis=new ArrayList<ErpPayItem>();
		for(int i=0;i<detailesize;i++){
			datailjsonObject = jsonArray.getJSONObject(i);
			erpPayItem=new ErpPayItem();
//			salamt":800.0000,"salcode":"51","salname":"养老金个人负担","strSalamt":"800.00"
			erpPayItem.setSalcode(datailjsonObject.getString("salcode"));
			erpPayItem.setSalname(datailjsonObject.getString("salname"));
			erpPayItem.setStrSalamt(datailjsonObject.getString("strSalamt"));
			erpPayItem.setSalamt(new BigDecimal(datailjsonObject.getString("strSalamt")));
			Bonusdeductlis.add(erpPayItem);
		}
		Collections.sort(Bonusdeductlis);
		erpSalaryInfo.setBonusdeductPayDetail(Bonusdeductlis);
//		发放明细
		jsonArray = jsonObject.getJSONObject("mapBonus").getJSONArray("shouldPayDetail");
		detailesize=jsonArray.length();
		BonusshouldPaylis=new ArrayList<ErpPayItem>();
		for(int i=0;i<detailesize;i++){
			datailjsonObject = jsonArray.getJSONObject(i);
			erpPayItem=new ErpPayItem();
//			salamt":800.0000,"salcode":"51","salname":"养老金个人负担","strSalamt":"800.00"
			erpPayItem.setSalcode(datailjsonObject.getString("salcode"));
			erpPayItem.setSalname(datailjsonObject.getString("salname"));
			erpPayItem.setStrSalamt(datailjsonObject.getString("strSalamt"));
			erpPayItem.setSalamt(new BigDecimal(datailjsonObject.getString("strSalamt")));
			BonusshouldPaylis.add(erpPayItem);
		}
		Collections.sort(BonusshouldPaylis);
		erpSalaryInfo.setBonusshouldPayDetail(BonusshouldPaylis);
		
		
		return erpSalaryInfo;
	}
	
	

}
