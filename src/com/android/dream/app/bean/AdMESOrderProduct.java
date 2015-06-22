package com.android.dream.app.bean;

/**
 * @author wangCheng
 * @date 2014-09-24 08:39
 * 订单生产过程跟踪
 */
public class AdMESOrderProduct {

	//客户编码
	private String  cusno;
	//客户名称
	private String  cusname;
	//交货日期
	private String  cusdeldate;
	//船号
	private String boardno;
	//订单总量
	private Double  ordertotal;
	//订单欠量
	private Double	orderown;
	//炼钢
	private Double  steelmaking;
	//轧钢
	private Double  steelrolling;
	//精整
	private Double  finishing;
	//终判
	private Double  lastdec;
	//未入库 
	private Double  unstorage;
	//已入库
	private Double  instorage;
	//在途
	private Double  onway;
	//待发
	private Double  waitdeliver;
	//已完成
	private Double  finishdeliver;
	//完成率
	private Double  finishpercent;
	private int visibity;
	public String getCusno() {
		return cusno;
	}
	public void setCusno(String cusno) {
		this.cusno = cusno;
	}
	public String getCusname() {
		return cusname;
	}
	public void setCusname(String cusname) {
		this.cusname = cusname;
	}
	public Double getOrdertotal() {
		return ordertotal;
	}
	public void setOrdertotal(Double ordertotal) {
		this.ordertotal = ordertotal;
	}
	public Double getOrderown() {
		return orderown;
	}
	public void setOrderown(Double orderown) {
		this.orderown = orderown;
	}
	public Double getSteelmaking() {
		return steelmaking;
	}
	public void setSteelmaking(Double steelmaking) {
		this.steelmaking = steelmaking;
	}
	public Double getSteelrolling() {
		return steelrolling;
	}
	public void setSteelrolling(Double steelrolling) {
		this.steelrolling = steelrolling;
	}
	public Double getFinishing() {
		return finishing;
	}
	public void setFinishing(Double finishing) {
		this.finishing = finishing;
	}
	public Double getLastdec() {
		return lastdec;
	}
	public void setLastdec(Double lastdec) {
		this.lastdec = lastdec;
	}
	public Double getUnstorage() {
		return unstorage;
	}
	public void setUnstorage(Double unstorage) {
		this.unstorage = unstorage;
	}
	public Double getInstorage() {
		return instorage;
	}
	public void setInstorage(Double instorage) {
		this.instorage = instorage;
	}
	public Double getOnway() {
		return onway;
	}
	public void setOnway(Double onway) {
		this.onway = onway;
	}
	public Double getWaitdeliver() {
		return waitdeliver;
	}
	public void setWaitdeliver(Double waitdeliver) {
		this.waitdeliver = waitdeliver;
	}
	public Double getFinishdeliver() {
		return finishdeliver;
	}
	public void setFinishdeliver(Double finishdeliver) {
		this.finishdeliver = finishdeliver;
	}
	public Double getFinishpercent() {
		return finishpercent;
	}
	public void setFinishpercent(Double finishpercent) {
		this.finishpercent = finishpercent;
	}
	public int getVisibity() {
		return visibity;
	}
	public void setVisibity(int visibity) {
		this.visibity = visibity;
	}
	public String getCusdeldate() {
		return cusdeldate;
	}
	public void setCusdeldate(String cusdeldate) {
		this.cusdeldate = cusdeldate;
	}
	public String getBoardno() {
		return boardno;
	}
	public void setBoardno(String boardno) {
		this.boardno = boardno;
	}
	
}
