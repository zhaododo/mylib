package com.android.dream.app.api;

public interface AD_NISCO_LOCAL_URL {

	public final static String HOST = "http://10.0.2.2:8080/rest/service";
	
	public final static String ERP_LOGIN = HOST+"/ad/login/";
	
	public final static String ERP_BONUS = HOST+"/ad/bonus.json";
	
	public final static String ERP_GETPAY = HOST+"/ad/pay/getPay/";
	
	public final static String ERP_GET_VERSION = HOST+"/ad/update/getVersion";
	
	public final static String ERP_GET_ADMENU = HOST+"/ad/api/getAdMenu/";
	
	//public final static String ERP_GET_REPORT = HOST+"/ad/report/getRpV2/";
	
	public final static String ERP_GET_REPORT = HOST+"/ad/report/getRp/";
	
	public final static String ERP_GET_BOARD = HOST+"/ad/erpboard/getBoard/";
	
	public final static String ERP_APPLY_PER = HOST+"/ad/adpermissionapply/applyPermision/";

	public final static String ERP_APPLY_PER_POST = HOST+"/ad/adpermissionapply/applyPermisionpost";
	
	public final static String ERP_GET_BOARD_FILE = HOST+"/ad/erpboard/geterpfile/";

}
