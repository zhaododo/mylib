package com.android.dream.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.dream.app.api.AD_NISCO_NORMAL_URL;
import com.android.dream.app.api.ApiClient;
import com.android.dream.app.bean.AdAjaxBonus;
import com.android.dream.app.bean.AdAjaxLogin;
import com.android.dream.app.bean.AdAjaxOpt;
import com.android.dream.app.bean.AdErpBoardPage;
import com.android.dream.app.bean.AdErpReportChartVo;
import com.android.dream.app.bean.AdErpReportPage;
import com.android.dream.app.bean.AdFriendStatusPageVo;
import com.android.dream.app.bean.AdMESOrdEnterPage;
import com.android.dream.app.bean.AdMESOrdProcessTracePage;
import com.android.dream.app.bean.AdMESOrderProductPage;
import com.android.dream.app.bean.AdMenu;
import com.android.dream.app.bean.AdReportLeftMenu;
import com.android.dream.app.bean.AdUpdate;
import com.android.dream.app.bean.AdUserPermissionTemp;
import com.android.dream.app.bean.AdXTDepartment;
import com.android.dream.app.bean.AdXTUser;
import com.android.dream.app.bean.AdXTUserPageVo;
import com.android.dream.app.bean.CommentList;
import com.android.dream.app.bean.ErpSalaryInfo;
import com.android.dream.app.bean.MyInformation;
import com.android.dream.app.bean.News;
import com.android.dream.app.bean.NewsList;
import com.android.dream.app.bean.Post;
import com.android.dream.app.bean.PostList;
import com.android.dream.app.bean.Result;
import com.android.dream.app.bean.SearchList;
import com.android.dream.app.bean.Software;
import com.android.dream.app.bean.SoftwareCatalogList;
import com.android.dream.app.bean.SoftwareList;
import com.android.dream.app.ui.UIHelper;
import com.android.dream.app.util.CyptoUtils;
import com.android.dream.app.util.FileUtils;
import com.android.dream.app.util.ImageUtils;
import com.android.dream.app.util.MethodsCompat;
import com.android.dream.app.util.StringUtils;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	
	private static final String TAG = "AppContext";

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	//手机号码
	public static final String TYPE_MOBILE="MOBILE";
	//办公室号码
	public static final String TYPE_TELEPHONE="TELEPHONE";
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	private boolean login = false;	//登录状态
	//二次登录
	private boolean secondLogin = false;
	public boolean isSecondLogin() {
		return secondLogin;
	}

	public void setSecondLogin(boolean secondLogin) {
		this.secondLogin = secondLogin;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}
	private int loginUid = 0;	//登录用户的id
	
	//是否已启动主程序
	private boolean isMainStart = false;
	
	public boolean isMainStart() {
		return isMainStart;
	}

	public void setMainStart(boolean isMainStart) {
		this.isMainStart = isMainStart;
	}
	/**
	 *  协同办公用户基本信息
	 */
	private  AdXTUser xtUser;
	
	public AdXTUser getXtUser() {
		return xtUser;
	}

	public void setXtUser(AdXTUser xtUser) {
		this.xtUser = xtUser;
	}
	/**
	 *  当前登录用户
	 */
	private String currentUser ="";
	
	/**
	 * 当前密码
	 */
	private String currentPwd ="";
	
	/**
	 * 当前用户名
	 */
	private String currentUserName = "";
	
	/**
	 * 是否显示贺卡
	 */
	private String showCard = "";
	
	
	public String getShowCard() {
		return showCard;
	}

	public void setShowCard(String showCard) {
		this.showCard = showCard;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public String getCurrentPwd() {
		return currentPwd;
	}

	public void setCurrentPwd(String currentPwd) {
		this.currentPwd = currentPwd;
	}
	/**
	 *  token
	 */
	private String token="";
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


	public String getCurrentUser() {
		return currentUser;
	}
	

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private Handler unLoginHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				UIHelper.showLoginDialog(AppContext.this);
			}
		}		
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
        //注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}

	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 应用程序是否发出提示音
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 用户是否登录
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}
	
	/**
	 * 获取登录用户id
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUid;
	}
	
	/**
	 * 用户注销
	 */
	public void Logout() {
		//ApiClient.cleanCookie();
		//this.cleanCookie();
		this.loginUid = 0;
		this.login = false;
	}
	
	/**
	 * 未登录或修改密码后的处理
	 */
	public Handler getUnLoginHandler() {
		return this.unLoginHandler;
	}
	
	/**
	 * 初始化用户登录信息
	 */
	public void initLoginInfo() {
		User loginUser = getLoginInfo();
		if(loginUser!=null && loginUser.getUid()>0 && loginUser.isRememberMe()){
			this.loginUid = loginUser.getUid();
			this.login = true;
		}else{
			this.Logout();
		}
	}
	
	/**
	 * 用户登录验证
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User loginVerify(String account, String pwd) throws AppException {
		return ApiClient.login(this, account, pwd);
	}
	
	/**
	 * 我的个人资料
	 * @param isRefresh 是否主动刷新
	 * @return
	 * @throws AppException
	 */
	public MyInformation getMyInformation(boolean isRefresh) throws AppException {
		MyInformation myinfo = null;
		String key = "myinfo_"+loginUid;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				myinfo = ApiClient.myInformation(this, loginUid);
				if(myinfo != null && myinfo.getName().length() > 0){
					myinfo.setCacheKey(key);
					saveObject(myinfo, key);
				}
			}catch(AppException e){
				myinfo = (MyInformation)readObject(key);
				if(myinfo == null)
					throw e;
			}
		} else {
			myinfo = (MyInformation)readObject(key);
			if(myinfo == null)
				myinfo = new MyInformation();
		}
		return myinfo;
	}
	
	
	/**
	 * 更新用户之间关系（加关注、取消关注）
	 * @param uid 自己的uid
	 * @param hisuid 对方用户的uid
	 * @param newrelation 0:取消对他的关注 1:关注他
	 * @return
	 * @throws AppException
	 */
	public Result updateRelation(int uid, int hisuid, int newrelation) throws AppException {
		return ApiClient.updateRelation(this, uid, hisuid, newrelation);
	}
	
	/**
	 * 更新用户头像
	 * @param portrait 新上传的头像
	 * @return
	 * @throws AppException
	 */
	public Result updatePortrait(File portrait) throws AppException {
		return ApiClient.updatePortrait(this, loginUid, portrait);
	}
	
	/**
	 * 清空通知消息
	 * @param uid
	 * @param type 1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
	 * @return
	 * @throws AppException
	 */
	public Result noticeClear(int uid, int type) throws AppException {
		return ApiClient.noticeClear(this, uid, type);
	}
	

	/**
	 * 新闻列表
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws ApiException
	 */
	public synchronized NewsList getNewsList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		NewsList list = null;
		String key = "newslist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		Log.i(TAG,"isRefresh:"+ isRefresh);
		
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getNewsList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					list.setCacheKey(key);
					saveObject(list, key);
				}
			}catch(AppException e){
				list = (NewsList)readObject(key);
				if(list == null)
					throw e;
			}		
		} else {
			list = (NewsList)readObject(key);
			if(list == null)
				list = new NewsList();
		}
		return list;
	}
	
	/**
	 * 新闻详情
	 * @param news_id
	 * @return
	 * @throws ApiException
	 */
	public News getNews(int news_id, boolean isRefresh) throws AppException {		
		News news = null;
		String key = "news_"+news_id;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				news = ApiClient.getNewsDetail(this, news_id);
				if(news != null){
					news.setCacheKey(key);
					saveObject(news, key);
				}
			}catch(AppException e){
				news = (News)readObject(key);
				if(news == null)
					throw e;
			}
		} else {
			news = (News)readObject(key);
			if(news == null)
				news = new News();
		}
		return news;		
	}
	
	
	/**
	 * 软件列表
	 * @param searchTag 软件分类  推荐:recommend 最新:time 热门:view 国产:list_cn
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public SoftwareList getSoftwareList(String searchTag, int pageIndex, boolean isRefresh) throws AppException {
		SoftwareList list = null;
		String key = "softwarelist_"+searchTag+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getSoftwareList(this, searchTag, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					list.setCacheKey(key);
					saveObject(list, key);
				}
			}catch(AppException e){
				list = (SoftwareList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (SoftwareList)readObject(key);
			if(list == null)
				list = new SoftwareList();
		}
		return list;
	}
	
	/**
	 * 软件分类的软件列表
	 * @param searchTag 从softwarecatalog_list获取的tag
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public SoftwareList getSoftwareTagList(int searchTag, int pageIndex, boolean isRefresh) throws AppException {
		SoftwareList list = null;
		String key = "softwaretaglist_"+searchTag+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				list = ApiClient.getSoftwareTagList(this, searchTag, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					list.setCacheKey(key);
					saveObject(list, key);
				}
			}catch(AppException e){
				list = (SoftwareList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (SoftwareList)readObject(key);
			if(list == null)
				list = new SoftwareList();
		}
		return list;
	}
	
	/**
	 * 软件分类列表
	 * @param tag 第一级:0  第二级:tag
	 * @return
	 * @throws AppException
	 */
	public SoftwareCatalogList getSoftwareCatalogList(int tag) throws AppException {
		SoftwareCatalogList list = null;
		String key = "softwarecataloglist_"+tag;
		if(isNetworkConnected() && isCacheDataFailure(key)) {
			try{
				list = ApiClient.getSoftwareCatalogList(this, tag);
				if(list != null){
					list.setCacheKey(key);
					saveObject(list, key);
				}
			}catch(AppException e){
				list = (SoftwareCatalogList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (SoftwareCatalogList)readObject(key);
			if(list == null)
				list = new SoftwareCatalogList();
		}
		return list;
	}
	
	/**
	 * 软件详情
	 * @param soft_id
	 * @return
	 * @throws AppException
	 */
	public Software getSoftware(String ident, boolean isRefresh) throws AppException {
		Software soft = null;
		String key = "software_"+(URLEncoder.encode(ident));
		if(isNetworkConnected() && (isCacheDataFailure(key) || isRefresh)) {
			try{
				soft = ApiClient.getSoftwareDetail(this, ident);
				if(soft != null){
					soft.setCacheKey(key);
					saveObject(soft, key);
				}
			}catch(AppException e){
				soft = (Software)readObject(key);
				if(soft == null)
					throw e;
			}
		} else {
			soft = (Software)readObject(key);
			if(soft == null)
				soft = new Software();
		}
		return soft;
	}
	
	
	
	/**
	 * 帖子列表
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws ApiException
	 */
	public PostList getPostList(int catalog, int pageIndex, boolean isRefresh) throws AppException {
		PostList list = null;
		String key = "postlist_"+catalog+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {		
			try{
				list = ApiClient.getPostList(this, catalog, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					list.setNotice(null);
					list.setCacheKey(key);
					saveObject(list, key);
				}
			}catch(AppException e){
				list = (PostList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (PostList)readObject(key);
			if(list == null)
				list = new PostList();
		}
		return list;
	}
	
	
	
	/**
	 * Tag相关帖子列表
	 * @param tag
	 * @param pageIndex
	 * @return
	 * @throws ApiException
	 */
	public PostList getPostListByTag(String tag, int pageIndex, boolean isRefresh) throws AppException {
		PostList list = null;
		String key = "postlist_"+(URLEncoder.encode(tag))+"_"+pageIndex+"_"+PAGE_SIZE;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {		
			try{
				list = ApiClient.getPostListByTag(this, tag, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					list.setCacheKey(key);
					saveObject(list, key);
				}
			}catch(AppException e){
				list = (PostList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (PostList)readObject(key);
			if(list == null)
				list = new PostList();
		}
		return list;
	}
	
	/**
	 * 读取帖子详情
	 * @param post_id
	 * @return
	 * @throws ApiException
	 */
	public Post getPost(int post_id, boolean isRefresh) throws AppException {		
		Post post = null;
		String key = "post_"+post_id;
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {	
			try{
				post = ApiClient.getPostDetail(this, post_id);
				if(post != null){
					post.setCacheKey(key);
					saveObject(post, key);
				}
			}catch(AppException e){
				post = (Post)readObject(key);
				if(post == null)
					throw e;
			}
		} else {
			post = (Post)readObject(key);
			if(post == null)
				post = new Post();
		}
		return post;		
	}
	
	
	
	
	

	
	/**
	 * 评论列表
	 * @param catalog 1新闻 2帖子 3动弹 4动态
	 * @param id 某条新闻，帖子，动弹的id 或者某条留言的friendid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public CommentList getCommentList(int catalog, int id, int pageIndex, boolean isRefresh) throws AppException {
		CommentList list = null;
		String key = "commentlist_"+catalog+"_"+id+"_"+pageIndex+"_"+PAGE_SIZE;		
		if(isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try{
				list = ApiClient.getCommentList(this, catalog, id, pageIndex, PAGE_SIZE);
				if(list != null && pageIndex == 0){
					list.setCacheKey(key);
					saveObject(list, key);
				}
			}catch(AppException e){
				list = (CommentList)readObject(key);
				if(list == null)
					throw e;
			}
		} else {
			list = (CommentList)readObject(key);
			if(list == null)
				list = new CommentList();
		}
		return list;
	}
	
	/**
	 * 获取搜索列表
	 * @param catalog 全部:all 新闻:news  问答:post 软件:software 博客:blog 代码:code
	 * @param content 搜索的内容
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public SearchList getSearchList(String catalog, String content, int pageIndex, int pageSize) throws AppException {
		return ApiClient.getSearchList(this, catalog, content, pageIndex, pageSize);
	}
	
	/**
	 * 发帖子
	 * @param post （uid、title、catalog、content、isNoticeMe）
	 * @return
	 * @throws AppException
	 */
	public Result pubPost(Post post) throws AppException {
		return ApiClient.pubPost(this, post);
	}
	
	/**
	 * 发送留言
	 * @param uid 登录用户uid
	 * @param receiver 接受者的用户id
	 * @param content 消息内容，注意不能超过250个字符
	 * @return
	 * @throws AppException
	 */
	public Result pubMessage(int uid, int receiver, String content) throws AppException {
		return ApiClient.pubMessage(this, uid, receiver, content);
	}
	
	/**
	 * 转发留言
	 * @param uid 登录用户uid
	 * @param receiver 接受者的用户名
	 * @param content 消息内容，注意不能超过250个字符
	 * @return
	 * @throws AppException
	 */
	public Result forwardMessage(int uid, String receiver, String content) throws AppException {
		return ApiClient.forwardMessage(this, uid, receiver, content);
	}
	
	/**
	 * 删除留言
	 * @param uid 登录用户uid
	 * @param friendid 留言者id
	 * @return
	 * @throws AppException
	 */
	public Result delMessage(int uid, int friendid) throws AppException {
		return ApiClient.delMessage(this, uid, friendid);
	}
	
	/**
	 * 发表评论
	 * @param catalog 1新闻  2帖子  3动弹  4动态
	 * @param id 某条新闻，帖子，动弹的id
	 * @param uid 用户uid
	 * @param content 发表评论的内容
	 * @param isPostToMyZone 是否转发到我的空间  0不转发  1转发
	 * @return
	 * @throws AppException
	 */
	public Result pubComment(int catalog, int id, int uid, String content, int isPostToMyZone) throws AppException {
		return ApiClient.pubComment(this, catalog, id, uid, content, isPostToMyZone);
	}
	
	/**
	 * 
	 * @param id 表示被评论的某条新闻，帖子，动弹的id 或者某条消息的 friendid 
	 * @param catalog 表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态
	 * @param replyid 表示被回复的单个评论id
	 * @param authorid 表示该评论的原始作者id
	 * @param uid 用户uid 一般都是当前登录用户uid
	 * @param content 发表评论的内容
	 * @return
	 * @throws AppException
	 */
	public Result replyComment(int id, int catalog, int replyid, int authorid, int uid, String content) throws AppException {
		return ApiClient.replyComment(this, id, catalog, replyid, authorid, uid, content);
	}
	
	/**
	 * 删除评论
	 * @param id 表示被评论对应的某条新闻,帖子,动弹的id 或者某条消息的 friendid
	 * @param catalog 表示该评论所属什么类型：1新闻  2帖子  3动弹  4动态&留言
	 * @param replyid 表示被回复的单个评论id
	 * @param authorid 表示该评论的原始作者id
	 * @return
	 * @throws AppException
	 */
	public Result delComment(int id, int catalog, int replyid, int authorid) throws AppException {
		return ApiClient.delComment(this, id, catalog, replyid, authorid);
	}
	
	/**
	 * 发表博客评论
	 * @param blog 博客id
	 * @param uid 登陆用户的uid
	 * @param content 评论内容
	 * @return
	 * @throws AppException
	 */
	public Result pubBlogComment(int blog, int uid, String content) throws AppException {
		return ApiClient.pubBlogComment(this, blog, uid, content);
	}
	
	/**
	 * 发表博客评论
	 * @param blog 博客id
	 * @param uid 登陆用户的uid
	 * @param content 评论内容
	 * @param reply_id 评论id
	 * @param objuid 被评论的评论发表者的uid
	 * @return
	 * @throws AppException
	 */
	public Result replyBlogComment(int blog, int uid, String content, int reply_id, int objuid) throws AppException {
		return ApiClient.replyBlogComment(this, blog, uid, content, reply_id, objuid);
	}
	
	
	
	
	/**
	 * 删除博客评论
	 * @param uid 登录用户的uid
	 * @param blogid 博客id
	 * @param replyid 评论id
	 * @param authorid 评论发表者的uid
	 * @param owneruid 博客作者uid
	 * @return
	 * @throws AppException
	 */
	public Result delBlogComment(int uid, int blogid, int replyid, int authorid, int owneruid) throws AppException {
		return ApiClient.delBlogComment(this, uid, blogid, replyid, authorid, owneruid);
	}
	
	
	/**
	 * 用户添加收藏
	 * @param uid 用户UID
	 * @param objid 比如是新闻ID 或者问答ID 或者动弹ID
	 * @param type 1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @return
	 * @throws AppException
	 */
	public Result addFavorite(int uid, int objid, int type) throws AppException {
		return ApiClient.addFavorite(this, uid, objid, type);
	}
	
	/**
	 * 用户删除收藏
	 * @param uid 用户UID
	 * @param objid 比如是新闻ID 或者问答ID 或者动弹ID
	 * @param type 1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @return
	 * @throws AppException
	 */
	public Result delFavorite(int uid, int objid, int type) throws AppException { 	
		return ApiClient.delFavorite(this, uid, objid, type);
	}
	
	/**
	 * 保存登录信息
	 * @param username
	 * @param pwd
	 */
	public void saveLoginInfo(final User user) {
		this.loginUid = user.getUid();
		this.login = true;
		setProperties(new Properties(){{
			setProperty("user.uid", String.valueOf(user.getUid()));
			setProperty("user.name", user.getName());
			setProperty("user.face", FileUtils.getFileName(user.getFace()));//用户头像-文件名
			setProperty("user.account", user.getAccount());
			setProperty("user.pwd", CyptoUtils.encode("oschinaApp",user.getPwd()));
			setProperty("user.location", user.getLocation());
			setProperty("user.followers", String.valueOf(user.getFollowers()));
			setProperty("user.fans", String.valueOf(user.getFans()));
			setProperty("user.score", String.valueOf(user.getScore()));
			setProperty("user.isRememberMe", String.valueOf(user.isRememberMe()));//是否记住我的信息
		}});		
	}
	
	
	
	/**登录ERP
	 * @param userno
	 * @param password
	 * @return
	 * @throws AppException
	 */
	public AdAjaxLogin erpLogin(String userno,String password)  throws AppException 
	{
		AdAjaxLogin adLogin = null;
		if(isNetworkConnected())
		{
			//ApiClient.cleanCookie();
			//cleanCookie();
			adLogin=  ApiClient.adLoginErp(this, userno, password);
		}
		
		return adLogin;
		
	}
	
	
	/**获得
	 * @return
	 * @throws AppException
	 */
	public List<AdAjaxBonus>  getAdBonus()  throws AppException 
	{
		List<AdAjaxBonus> list = null;
		if(isNetworkConnected())
		{
			cleanCookie();
			list=  ApiClient.getAdBonus(this);
		}
		
		return list;
	}
	
	
	
	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.loginUid = 0;
		this.login = false;
		removeProperty("user.uid","user.name","user.face","user.account","user.pwd",
				"user.location","user.followers","user.fans","user.score","user.isRememberMe");
	}
	
	/**
	 * 获取登录信息
	 * @return
	 */
	public User getLoginInfo() {		
		User lu = new User();		
		lu.setUid(StringUtils.toInt(getProperty("user.uid"), 0));
		lu.setName(getProperty("user.name"));
		lu.setFace(getProperty("user.face"));
		lu.setAccount(getProperty("user.account"));
		lu.setPwd(CyptoUtils.decode("oschinaApp",getProperty("user.pwd")));
		lu.setLocation(getProperty("user.location"));
		lu.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
		lu.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
		lu.setScore(StringUtils.toInt(getProperty("user.score"), 0));
		lu.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
		return lu;
	}
	
	/**
	 * 保存用户头像
	 * @param fileName
	 * @param bitmap
	 */
	public void saveUserFace(String fileName,Bitmap bitmap) {
		try {
			ImageUtils.saveImage(this, fileName, bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户头像
	 * @param key
	 * @return
	 * @throws AppException
	 */
	public Bitmap getUserFace(String key) throws AppException {
		FileInputStream fis = null;
		try{
			fis = openFileInput(key);
			return BitmapFactory.decodeStream(fis);
		}catch(Exception e){
			throw AppException.run(e);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 是否加载显示文章图片
	 * @return
	 */
	public boolean isLoadImage()
	{
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		//默认是加载的
		if(StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}
	
	/**
	 * 设置是否加载文章图片
	 * @param b
	 */
	public void setConfigLoadimage(boolean b)
	{
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}
	
	/**
	 * 是否发出提示音
	 * @return
	 */
	public boolean isVoice()
	{
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		//默认是开启提示声音
		if(StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}
	
	/**
	 * 设置是否发出提示音
	 * @param b
	 */
	public void setConfigVoice(boolean b)
	{
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}
	
	/**
	 * 是否启动检查更新
	 * @return
	 */
	public boolean isCheckUp()
	{
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		//默认是开启
		if(StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}
	
	/**
	 * 设置启动检查更新
	 * @param b
	 */
	public void setConfigCheckUp(boolean b)
	{
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}
	
	/**
	 * 是否左右滑动
	 * @return
	 */
	public boolean isScroll()
	{
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		//默认是关闭左右滑动
		if(StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}
	
	/**
	 * 设置是否左右滑动
	 * @param b
	 */
	public void setConfigScroll(boolean b)
	{
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}
	
	/**
	 * 是否Https登录
	 * @return
	 */
	public boolean isHttpsLogin()
	{
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		//默认是http
		if(StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}
	
	/**
	 * 设置是是否Https登录
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b)
	{
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}
	
	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie()
	{
		removeProperty(AppConfig.CONF_COOKIE);
	}
	
	/**
	 * 判断缓存数据是否可读
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile)
	{
		return readObject(cachefile) != null;
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile)
	{
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;
	}
	
	/**
	 * 清除app缓存
	 */
	public void clearAppCache()
	{
		//清除webview缓存
//		File file = CacheManager.getCacheFileBaseDir();  
//		if (file != null && file.exists() && file.isDirectory()) {  
//		    for (File item : file.listFiles()) {  
//		    	item.delete();  
//		    }  
//		    file.delete();  
//		}  		  
		deleteDatabase("webview.db");  
		deleteDatabase("webview.db-shm");  
		deleteDatabase("webview.db-wal");  
		deleteDatabase("webviewCache.db");  
		deleteDatabase("webviewCache.db-shm");  
		deleteDatabase("webviewCache.db-wal");  
		//清除数据缓存
		clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		//2.2版本才有将应用缓存转移到sd卡的功能
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		}
		//清除编辑器保存的临时内容
		Properties props = getProperties();
		for(Object key : props.keySet()) {
			String _key = key.toString();
			if(_key.startsWith("temp"))
				removeProperty(_key);
		}
	}	
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	/**
	 * 将对象保存到内存缓存中
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}
	
	/**
	 * 从内存缓存中获取对象
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key){
		return memCacheRegion.get(key);
	}
	
	/**
	 * 保存磁盘缓存
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 获取磁盘缓存数据
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try{
			fis = openFileInput("cache_"+key+".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}

	public boolean containsProperty(String key){
		Properties props = getProperties();
		 return props.containsKey(key);
	}
	
	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}
	
	public ErpSalaryInfo erpsalary(String userno,String strdate)  throws AppException 
	{
		 
		ErpSalaryInfo  erpSalaryInfo=new ErpSalaryInfo();
		if(isNetworkConnected())
		{
			cleanCookie();
			erpSalaryInfo=  ApiClient.adGetSalaryInfo(this, userno, strdate);
		}
		
		return erpSalaryInfo;
		
	}
	
	/**
	 * 获得版本信息
	 * @return
	 * @throws AppException
	 */
	public  AdUpdate  getVersion() throws AppException {
		
		AdUpdate  update = new AdUpdate();
		update.setVersionCode(0);
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			update=  ApiClient.adGetVersion(this);
		}
		return update;
		
	}
	
	
	/**获得菜单树
	 * @param userno
	 * @return
	 * @throws AppException
	 */
	public  AdMenu  getAdMenu(String userno) throws AppException {
		
		AdMenu  menu = new AdMenu();
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			menu =  ApiClient.adGetMenu(this, userno);
		}
		return menu;
	}
	
	
	/**
	 * 生产快报API
	 * @param userno     工号
	 * @param resType    类别编号
	 * @param pageIndex  分页从1开始
	 * @return
	 * @throws AppException
	 */
	public  AdErpReportPage  getAdErpReport(String userno,String resType,int pageIndex) throws AppException {
		
		AdErpReportPage erpReportPage =null;
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			erpReportPage =  ApiClient.adGetErpReport(this, userno, resType,pageIndex);
		}
		return erpReportPage;
	}
	
	
	/**
	 * ERP公告API
	 * @param userno 工号
	 * @param pageIndex 分页从1开始
	 * @return
	 * @throws AppException
	 */
	public  AdErpBoardPage  getAdErpBoard(String userno,int pageIndex) throws AppException {
		
		AdErpBoardPage erpBoardPage =null;
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			erpBoardPage =  ApiClient.adGetErpBoard(this, userno, pageIndex);
		}
		return erpBoardPage;
	}
	
	/**
	 * 权限申请
	 * @param applyuserNo
	 * @param activtyNo
	 * @param activtydsc
	 * @param applyreasion
	 * @return
	 * @throws AppException
	 */
	public Map<String,String> AdApplyPermission(String applyuserNo,String activtyNo, String activtydsc, String applyreasion) throws AppException{
	
		Map<String,String>  maprslt =null;
		if(isNetworkConnected())
		{
			//cleanCookie();
//			maprslt =  ApiClient.AdApplyPermission(this, applyuserNo, activtyNo, activtydsc, applyreasion);
			maprslt =  ApiClient.AdApplyPermission_post(this, applyuserNo, activtyNo, activtydsc, applyreasion);
		}
		return maprslt;
	}
	
	
	/**
	 * 获得文件下载URL
	 * @param userno
	 * @param fileHandle
	 * @return
	 * @throws AppException
	 */
	public String  getAdErpBoardFileUrl(String userno, String fileHandle){
		
		String fileUrl="";
		if(isNetworkConnected())
		{
			fileUrl =  ApiClient.adGetErpBoardFileUrl(this,userno,fileHandle);
		}
		return fileUrl;
	}
	
	/**
	 * 记录异常
	 * @param excp
	 */
	public void saveErrorLog(Exception excp) {
		String errorlog = "errorlog.txt";
		String savePath = "";
		String logFilePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			//判断是否挂载了SD卡
			String storageState = Environment.getExternalStorageState();		
			if(storageState.equals(Environment.MEDIA_MOUNTED)){
				savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dream/Log/";
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdirs();
				}
				logFilePath = savePath + errorlog;
			}
			//没有挂载SD卡，无法写文件
			if(logFilePath == ""){
				return;
			}
			File logFile = new File(logFilePath);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			fw = new FileWriter(logFile,true);
			pw = new PrintWriter(fw);
			pw.println("--------------------"+(new Date().toLocaleString())+"---------------------");	
			excp.printStackTrace(pw);
			pw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();		
		}finally{ 
			if(pw != null){ pw.close(); } 
			if(fw != null){ try { fw.close(); } catch (IOException e) { }}
		}

	}

	/**
	  * 根据部门id获取用户列表
	 * @param appContext
	 * @param depno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public  AdXTUserPageVo  adGetXTUserbyDepNo(String userno,String depno,int pageIndex) throws AppException {
		
		AdXTUserPageVo erpuserPage =null;
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			erpuserPage =  ApiClient.adGetXTUserbyDepNo(this, userno,depno, pageIndex);
		}
		return erpuserPage;
	}
	
	/**
	  * 根据登陆者获取部门和部门人员
	 * @param appContext
	 * @param userno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public  List<AdXTDepartment>  adGetXTUserbyUserNo(String userno) throws AppException {
		
		List<AdXTDepartment> erpdeplis =null;
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			erpdeplis =  ApiClient.adGetXTUserbyUserNo(this, userno);
		}
		return erpdeplis;
	}
	
	/**
	 * 根据关键字查询用户信息
	 * @param appContext
	 * @param userno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 * @throws UnsupportedEncodingException 
	 */
	public  AdXTUserPageVo  adGetXTUserbykey(String userno,String keys,int pageIndex) throws AppException, UnsupportedEncodingException {
		
		AdXTUserPageVo erpuserPage =null;
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			erpuserPage =  ApiClient.adGetXTUserbykey(this,userno, keys, pageIndex);
		}
		return erpuserPage;
	}
	
	/**
	 * 获取订单投入量
	 * @param appContext
	 * @param checkdate
	 * @param companyname
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public AdMESOrdEnterPage AdOrderEnter(String userno,String checkdate,String companyname,int pageIndex) throws AppException{
		
		AdMESOrdEnterPage  erpordenterrslt =null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			erpordenterrslt =  ApiClient.AdOrderEnter(this,userno, checkdate, companyname, pageIndex);
		}
		return erpordenterrslt;
	}

	/**
	 * 获取订单过程参数
	 * @param appContext
	 * @param checkdate
	 * @param companyname
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public AdMESOrderProductPage getMESOrderProductPage(String userno,String checkdate,String companyname,int pageIndex) throws AppException{
		
		AdMESOrderProductPage  erpordproductrslt =null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			erpordproductrslt =  ApiClient.AdMESOrderProduct(this,userno, checkdate, companyname, pageIndex);
		}
		return erpordproductrslt;
	}
	
	/**
	 * 获取用户权限列表
	 * @param userno
	 * @return
	 * @throws AppException
	 */
	public List<AdUserPermissionTemp> getUserPermision(String userno) throws AppException{
		List<AdUserPermissionTemp>  userPermissionlis =null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			userPermissionlis =  ApiClient.getUserPermision(this, userno);
		}
		return userPermissionlis;
	}
	/**
	 * 测试文件上传
	 * @param paramters
	 * @param filesMap
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt uploaduserHeaderPic(Map<String, Object> paramters,Map<String, File> filesMap) throws AppException{
		
		AdAjaxOpt opt = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.uploaduserHeaderPic(this, paramters, filesMap);
		}
		return opt;
	}
	
	/**
	 * 测试下载图片
	 * @param userno
	 * @return
	 * @throws AppException
	 */
	public Bitmap downloaduserHeaderPic(String userno) throws AppException{
		
		Bitmap bitmap = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			bitmap =  ApiClient.downloaduserHeaderPic(this,userno);
		}
		return bitmap;
	}
	
	
	/**
	 * 获得协同办公用户信息
	 * @param userno
	 * @return
	 * @throws AppException
	 */
	public AdXTUser xtGetUserInfo(String userno) throws AppException{
		AdXTUser user = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			user =  ApiClient.xtGetUserInfo(this,userno);
		}
		return user;
	}
	
	

	/**
	 * 更新协同办公用户的手机号
	 * @param userno
	 * @param mobile
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt xtUpdateUserMobile(String userno,String mobile) throws AppException{
		AdAjaxOpt opt = null;
		
		Map<String, Object> paramters = new HashMap<String, Object>();
		//工号
		paramters.put("userno", userno);
		//手机号码
		paramters.put("phone3", mobile);
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.xtUpdateUserMobile(this, paramters);
		}
		return opt;
	}
	
	/**
	 * 更新协同办公用户的座机号码
	 * @param userno
	 * @param telephone
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt xtUpdateUserTelephone(String userno,String telephone) throws AppException{
		AdAjaxOpt opt = null;
		
		Map<String, Object> paramters = new HashMap<String, Object>();
		//工号
		paramters.put("userno", userno);
		//手机号码
		paramters.put("telephone", telephone);
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.xtUpdateUserTelephone(this, paramters);
		}
		return opt;
	}
	
	/**
	 * 隐藏手机号码或座机号码
	 * @param userno 工号
	 * @param phoneType TYPE_MOBILE：手机号码;TYPE_TELEPHONE:座机号码
	 * @param isHide   是否隐藏
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt xtHideUserInfo(String userno,String phoneType,boolean isHide) throws AppException{
		AdAjaxOpt opt = null;
		
		Map<String, Object> paramters = new HashMap<String, Object>();
		//工号
		paramters.put("userno", userno);
		
		//手机号码
		if (TYPE_MOBILE.equals(phoneType))
		{
			if (isHide)
			{
				paramters.put("ISVISIBLEMOBILE", 0);
			}
			else
			{
				paramters.put("ISVISIBLEMOBILE", 1);
			}
			
		}
		//座机号码
		else if (TYPE_TELEPHONE.equals(phoneType))
		{
			if (isHide)
			{
				paramters.put("ISVISIBLETELEPHONE", 0);
			}
			else
			{
				paramters.put("ISVISIBLETELEPHONE", 1);
			}
		}
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.xtHideUserInfo(this, paramters);
		}
		return opt;
	}
	
	
	

	/**
	 * 建议反馈
	 * @param userno  工号
	 * @param opinion 建议
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt createOpinion(String userno,String opinion) throws AppException{
		AdAjaxOpt opt = null;
		
		Map<String, Object> paramters = new HashMap<String, Object>();
		//工号
		paramters.put("userno", userno);
		//建议
		paramters.put("opinion", opinion);
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.dbHelper(this, AD_NISCO_NORMAL_URL.OPINION_CREATE, paramters);
		}
		return opt;
	}
	

	/**
	 * 按工号查找对应的生产快报左侧菜单列表(按工号、activityName权限管控）
	 * @param userno 工号
	 * @param activityName 对应activityName
	 * @return
	 * @throws AppException
	 */
	public  List<AdReportLeftMenu> adGetReportLeftMenus(String userno,String activityName) throws AppException {
		
		List<AdReportLeftMenu> list = null;
		
		if(isNetworkConnected())
		{
			//cleanCookie();
			list =  ApiClient.adGetReportLeftMenus(this, userno,activityName);
		}
		return list;
	}
	
	/**
	 * 订单工序跟踪
	 * @param appContext
	 * @param checkdate
	 * @param orderno
	 * @param orderitem
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public AdMESOrdProcessTracePage mesOrderProcessTrace(String userno,String checkdate,String orderinfo,String deldate,int pageIndex) throws AppException{
		
		AdMESOrdProcessTracePage  mesordertracerslt =null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			mesordertracerslt =  ApiClient.mesOrderProcessTrace(this,userno, checkdate,orderinfo,deldate, pageIndex);
		}
		return mesordertracerslt;
	}
	/**
	 * 朋友圈发表状态
	 * @author Wang Cheng
	 * 2014年12月30日上午8:24:53
	 * @param paramters
	 * @param filesMap
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt publishstatus(Map<String, Object> paramters,Map<String, File> filesMap) throws AppException{
		
		AdAjaxOpt opt = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.publishstatus(this, paramters, filesMap);
		}else{
			opt = new AdAjaxOpt();
			opt.setMsg("网络异常");
			opt.setStatus("-1");
		}
		return opt;
	}
	
	/**
	 * 获取朋友圈状态
	 * @author Wang Cheng
	 * 2015年1月6日下午4:45:30
	 * @param userno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public AdFriendStatusPageVo getFriendStatus(String userno,int pageIndex) throws AppException{
		
		AdFriendStatusPageVo  friendStatusPage =null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			friendStatusPage =  ApiClient.getFriendStatus(this,userno, pageIndex);
		}
		return friendStatusPage;
	}
	
	

	/**
	 * 朋友圈赞
	 * @param userno 工号
	 * @param friendsMainId mainid
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt addFriendZan(String userno,String friendsMainId) throws AppException{
		AdAjaxOpt opt = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.addFriendZan(this, userno, friendsMainId);
		}
		return opt;
	}
	

	/**
	 * 朋友圈评论
	 * @param userno 工号
	 * @param friendsMainId mainid
	 * @param comment 内容
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt addFriendComment(String userno,String friendsMainId, String comment) throws AppException{
		
		AdAjaxOpt opt = null;
		//评论内容
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.addFriendComment(this, userno,friendsMainId,comment);
		}
		return opt;
	}
	
	/**
	 * 返回朋友圈图像下载路径
	 * @param appContext
	 * @param userno 工号
	 * @param imdid 图片id编号
	 * @return
	 * @throws AppException
	 */
	public String getFCLoadPic(String userno,String imdid) throws AppException{
		
		String adFCLoadPicUrl = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			adFCLoadPicUrl =  ApiClient.getFCLoadPic(this, userno,imdid);
		}
		return adFCLoadPicUrl;
	}
	
	/**
	 * 获取快报报表内容
	 * @param appContext
	 * @param userno
	 * @param typeNo
	 * @param reportProperty
	 * @param targetField
	 * @param reportDate
	 * @return
	 * @throws AppException
	 */
	public AdErpReportChartVo getTimelyReportChart(String userno,String typeNo,String reportProperty
			,String targetField,String reportDate) throws AppException{
		
		AdErpReportChartVo adErpReportChartVo = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			adErpReportChartVo =  ApiClient.getTimelyReportChart(this, userno,typeNo,reportProperty,targetField,reportDate);
		}
		return adErpReportChartVo;
	}
	
	/**
	 * 保存客户错误信息
	 * @param appContext
	 * @param paramters
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt saveClientError(Map<String, Object> paramters) throws AppException {

		AdAjaxOpt opt = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt = ApiClient.saveClientError(this, paramters);
		}
		return opt;
	}
	
	/**
	 *  朋友圈屏蔽状态
	 * @param operauserno  操作工号
	 * @param userno  屏蔽工号
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt maskFCStatus(String operauserno,String userno,int intIsActive) throws AppException{
		AdAjaxOpt opt = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.maskFCStatus(this,operauserno, userno,intIsActive);
		}
		return opt;
	}
	
	/**
	 * 获取朋友圈状态
	 * @author Wang Cheng
	 * 2015年1月6日下午4:45:30
	 * @param userno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public AdFriendStatusPageVo getFriendStatusMansge(String userno,int pageIndex) throws AppException{
		
		AdFriendStatusPageVo  friendStatusPage =null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			friendStatusPage =  ApiClient.getFriendStatusMansge(this,userno, pageIndex);
		}
		return friendStatusPage;
	}
	
	/**
	 *  朋友圈删帖
	 * @param operauserno  操作工号
	 * @param userno  屏蔽工号
	 * @return
	 * @throws AppException
	 */
	public AdAjaxOpt delFCStatus(String operauserno,String id) throws AppException{
		AdAjaxOpt opt = null;
		if(isNetworkConnected())
		{
			//cleanCookie();
			opt =  ApiClient.delFCStatus(this,operauserno, id);
		}
		return opt;
	}
	
}
