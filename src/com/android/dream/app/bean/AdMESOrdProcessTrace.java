package com.android.dream.app.bean;

import java.util.List;

/**
 * 
 * @author Wang Cheng
 *  
 * @date 2014年12月5日上午8:48:15
 */
public class AdMESOrdProcessTrace {
  
	private String ins_date; //报表编制日期
	private String ord_no; //订单号
	private String ord_item;//订单序列号
	private String ord_wgt; //订单重量
	private String cust_no; //客户代码
	private String stdspec; //标准
	private String ord_size; //尺寸
	private List<AdMESOrdProcessTraceItem> mesOrdProcessTraceItemlis;
	private int visibity;
//	private String aea_end; //板坯设计结束时间
//	private String aea_fin; //板坯已完成的量
//	private String aea_bef; //板坯截止之前完成的量
//	private String aea_aft; //板坯截止之后完成的量
//	private String aeb_end; //报料结束时间
//	private String aeb_fin; //报料已完成的量
//	private String aeb_bef; //报料截止之前完成的量
//	private String aeb_aft; //报料截止之后完成的量
//	private String aec_end; //炼钢计划结束时间
//	private String aec_fin; //炼钢计划已完成的量
//	private String aec_bef; //炼钢计划截止之前完成的量
//	private String aec_aft; //炼钢计划截止之后完成的量
//	--start
//	private String bca_end; // 炼钢结束时间
//	private String bca_fin; //炼钢已完成的量
//	private String bca_bef; //炼钢截止之前完成的量
//	private String bca_aft; //炼钢截止之后完成的量
//	--end
//	private String caa_end; //板坯精整结束时间
//	private String caa_fin; //板坯精整已完成的量
//	private String caa_bef; //板坯精整截止之前完成的量
//	private String caa_aft; //板坯精整截止之后完成的量
//	private String cab_end; //板坯计划开始时间
//	private String cab_fin; //板坯精整已完成的量
//	private String cab_bef; //板坯精整截止之前完成的量
//	private String cab_aft; //板坯精整截止之后完成的量
//	--start
//	private String cba_end;  //轧钢结束时间
//	private String cba_fin;  //轧钢已完成的量
//	private String cba_bef;  //轧钢截止之前完成的量
//	private String cba_aft;  //轧钢截止之后完成的量
//	--end
//	private String dza_end;  //剪切结束时间
//	private String dza_fin;  //剪切已完成的量
//	private String dza_bef;  //剪切截止之前完成的量
//	private String dza_aft;  //剪切截止之后完成的量
//	private String dzb_end;  //矫直结束时间
//	private String dzb_fin;  //矫直已完成的量
//	private String dzb_bef;  //矫直截止之前完成的量
//	private String dzb_aft;  //矫直截止之后完成的量
//	private String dzc_end;  //探伤结束时间
//	private String dzc_fin;  //探伤已完成的量
//	private String dzc_bef;  //探伤截止之前完成的量
//	private String dzc_aft;  //探伤截止之后完成的量
//	private String dzd_end;  //火切结束时间
//	private String dzd_fin;  //火切已完成的量
//	private String dzd_bef;  //火切截止之前完成的量
//	private String dzd_aft;  //火切截止之后完成的量
//	private String ena_end;  //正火结束时间
//	private String ena_fin;  //正火已完成的量
//	private String ena_bef;  //正火截止之前完成的量
//	private String ena_aft;  //正火截止之后完成的量
//	private String eqa_end;  //淬火结束时间
//	private String eqa_fin;  //淬火已完成的量
//	private String eqa_bef;  //淬火截止之前完成的量
//	private String eqa_aft;  //淬火截止之后完成的量
//	private String eta_end;  //回火结束时间
//	private String eta_fin;  //回火已完成的量
//	private String eta_bef;  //回火截止之前完成的量
//	private String eta_aft;  //回火截止之后完成的量
//	--start
//	private String eaa_end;  //热处理结束时间
//	private String eaa_fin;  //热处理已完成的量
//	private String eaa_bef;  //热处理截止之前完成的量
//	private String eaa_aft;  //热处理截止之后完成的量
//	private String qab_end;  //表判开结束时间
//	private String qab_fin;  //表判已完成的量
//	private String qab_bef;  //表判截止之前完成的量
//	private String qab_aft;  //表判截止之后完成的量
//	private String raa_end;  //入库结束时间
//	private String raa_fin;  //入库已完成的量
//	private String raa_bef;  //入库截止之前完成的量
//	private String raa_aft;  //入库截止之后完成的量
//	--end
//	private String tac_end;  //试验结束时间
//	private String tac_fin;  //试验已完成的量
//	private String tac_bef;  //试验截止之前完成的量
//	private String tac_aft;  //试验截止之后完成的量
//	--start
//	private String xaa_end;  //综判结束时间
//	private String xaa_fin;  //综判已完成的量
//	private String xaa_bef;  //综判截止之前完成的量
//	private String xaa_aft;  //综判截止之后完成的量
//	--end
//	private String xab_end;  //准发结束时间
//	private String xab_fin;  //准发已完成的量
//	private String xab_bef;  //准发截止之前完成的量
//	private String xab_aft;  //准发截止之后完成的量
//	--start
//	private String xaf_end;  //发货结束时间
//	private String xaf_fin;  //发货已完成的量
//	private String xaf_bef;  //发货截止之前完成的量
//	private String xaf_aFT;  //发货截止之后完成的量
//	--end
	public String getIns_date() {
		return ins_date;
	}
	public void setIns_date(String ins_date) {
		this.ins_date = ins_date;
	}
	public String getOrd_no() {
		return ord_no;
	}
	public void setOrd_no(String ord_no) {
		this.ord_no = ord_no;
	}
	public String getOrd_item() {
		return ord_item;
	}
	public void setOrd_item(String ord_item) {
		this.ord_item = ord_item;
	}
	public String getOrd_wgt() {
		return ord_wgt;
	}
	public void setOrd_wgt(String ord_wgt) {
		this.ord_wgt = ord_wgt;
	}
	public List<AdMESOrdProcessTraceItem> getMesOrdProcessTraceItemlis() {
		return mesOrdProcessTraceItemlis;
	}
	public void setMesOrdProcessTraceItemlis(
			List<AdMESOrdProcessTraceItem> mesOrdProcessTraceItemlis) {
		this.mesOrdProcessTraceItemlis = mesOrdProcessTraceItemlis;
	}
	public int getVisibity() {
		return visibity;
	}
	public void setVisibity(int visibity) {
		this.visibity = visibity;
	}
	public String getCust_no() {
		return cust_no;
	}
	public void setCust_no(String cust_no) {
		this.cust_no = cust_no;
	}
	public String getStdspec() {
		return stdspec;
	}
	public void setStdspec(String stdspec) {
		this.stdspec = stdspec;
	}
	public String getOrd_size() {
		return ord_size;
	}
	public void setOrd_size(String ord_size) {
		this.ord_size = ord_size;
	}
	
}
