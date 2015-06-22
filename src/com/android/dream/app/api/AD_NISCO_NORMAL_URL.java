package com.android.dream.app.api;

public interface AD_NISCO_NORMAL_URL {
	
    //public final static String HOST = "http://wx.nisco.cn/rest/service";

    public final static String HOST1 = "http://172.18.43.37:8080/rest/service";
    
    //nginx 地址（移动南钢正式环境）
//    public final static String HOST3 = "http://58.213.134.168/rest/service";
    
    //域名地址（移动南钢正式环境）
    public final static String HOST = "http://mobile.nisco.cn/rest/service";
    
    public final static String HOST2 = "http://172.18.248.103/rest/service";
	
//    public final static String HOST = "http://172.18.58.189:8080/rest/service";
	
//	public final static String HOST = "http://192.168.1.104:8080/rest/service";
	
	public final static String ERP_LOGIN = HOST+"/ad/login/";
	
	public final static String ERP_BONUS = HOST+"/ad/bonus.json";
	
	public final static String ERP_GETPAY = HOST+"/ad/pay/getPay/";
	
	public final static String ERP_GET_VERSION = HOST+"/ad/update/getVersion";
	
	public final static String ERP_GET_ADMENU = HOST+"/ad/api/getAdMenu/";
	
	public final static String ERP_GET_REPORT = HOST+"/ad/report/getRp/";
	
	public final static String ERP_GET_BOARD = HOST+"/ad/erpboard/getBoard/";
	
	public final static String ERP_APPLY_PER = HOST+"/ad/adpermissionapply/applyPermision/";

	public final static String ERP_APPLY_PER_POST = HOST+"/ad/adpermissionapply/applyPermisionpost";
	
	public final static String ERP_GET_BOARD_FILE = HOST+"/ad/erpboard/geterpfile/";
	//根据用户获取部门和本部门用户信息
	public final static String ERP_GET_USER_DEP = HOST+"/ad/xtcontact/lodeuserdep/";
	//根据部门查询部门用户
	public final static String ERP_GET_DEP_USER = HOST+"/ad/xtcontact/lodedepuser/";
	//根据关键字查询用户信息
	public final static String ERP_GET_USERBYKEY = HOST+"/ad/xtcontact/lodeuserinfo/";
	
	//协同办公获得用户信息
	public final static String XT_GET_USERINFO = HOST+"/ad/xtcontact/findUserInfo/";
	//协同办公更新办公室电话
	public final static String XT_UPDATE_USER_TELEPHONE = HOST+"/ad/xtcontact/updateTelephone";
	//协同办公更新手机号码
	public final static String XT_UPDATE_USER_MOBILE = HOST+"/ad/xtcontact/updateMobile";
	//协同办公隐藏用户信息（办公室电话或手机号码）
	public final static String XT_HIDEUSERINFO = HOST+"/ad/xtuserInfo/hideUserInfo";
	//用户头像上传
	public final static String XT_USERHEADERPIC_UPLOAD = HOST+"/ad/xtuserInfo/headerpicupload";
	//用户头像下载
	public final static String XT_USERHEADERPIC_DOWNLOAD = HOST+"/ad/xtuserInfo/headerpicdownload/";
	//意见反馈
	public final static String OPINION_CREATE = HOST+"/ad/opinion/create";
	
	//用户订单录入量
	public final static String MES_ORDER_ENTER = HOST+"/ad/ordtrack/calordenter/";
	//用户订单生产情况
	public final static String MES_ORDER_PRODUCT = HOST+"/ad/ordtrack/ordproduct/";
	//获取用户权限列表
	public final static String GET_USER_PERMISION = HOST+"/ad/getuserPermision/";
	//订单各工序跟踪
	public final static String MES_ORDER_PROCESS_TRACE = HOST+"/ad/ordtrack/ordprocess/";
	
	//ERP生产快报左分类菜单
	public final static String GET_TIMELYREPORT_CLASS = HOST+"/ad/timelyReportClass/list/";
	//朋友圈-发表状态published status
	public final static String FIREND_PUBLISH_STATUS = HOST+"/ad/friendcircle/publishstatus";
	//获取朋友圈动态
	public final static String GET_FRIEND_STATUS = HOST+"/ad/friendcircle/getFriendStatus/";
	//获取朋友动态（图文形式，贺卡）对应的图像
	public final static String GET_FRIEND_ALBUM_PIC = HOST+"/ad/friendcircle/getfriendpic/";
	//赞功能
	public final static String FRIEND_ZAN = HOST+"/ad/friendcircle/zan/";
	//评论功能
	public final static String FRIEND_COMMENT = HOST+"/ad/friendcircle/comment/";
	//屏蔽功能
	public final static String FRIEND_MASKUSER = HOST+"/ad/friendcircle/maskuser/";
	//删帖功能
	public final static String FRIEND_DELSTATUS = HOST+"/ad/friendcircle/delCard/";
	//朋友圈管理
	public final static String FRIEND_FCMANAGE = HOST+"/ad/friendcircle/getFriendStatusManager/";
	//获取快报图表信息
	public final static String GET_TIMELYREPORT_CHART = HOST+"/ad/report/getRpByCondition/";
	//保存客户端错误信息
	public final static String SAVE_CLIENT_ERROR = HOST+"/ad/clienterrorlog/savelog/";
}
