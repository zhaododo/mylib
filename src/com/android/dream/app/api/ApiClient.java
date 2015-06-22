package com.android.dream.app.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.User;
import com.android.dream.app.bean.ActiveList;
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
import com.android.dream.app.bean.AdReportLeftMenuPage;
import com.android.dream.app.bean.AdUpdate;
import com.android.dream.app.bean.AdUserPermissionTemp;
import com.android.dream.app.bean.AdXTDepartment;
import com.android.dream.app.bean.AdXTUser;
import com.android.dream.app.bean.AdXTUserPageVo;
import com.android.dream.app.bean.CommentList;
import com.android.dream.app.bean.ErpSalaryInfo;
import com.android.dream.app.bean.FavoriteList;
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
import com.android.dream.app.bean.Update;

/**
 * API客户端接口：用于访问网络数据
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

	private static final String TAG = "ApiClient";
	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}

	private static String getCookie(AppContext appContext) {
		if (appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		
		Log.i("zhaoj", "appCookie:"+appCookie);
		
		return appCookie;
	}

	private static String getUserAgent(AppContext appContext) {
		if (appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/' + appContext.getPackageInfo().versionName + '_'
					+ appContext.getPackageInfo().versionCode);// App版本
			ua.append("/Android");// 手机系统平台
			ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
			ua.append("/" + android.os.Build.MODEL); // 手机型号
			ua.append("/" + appContext.getAppId());// 客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

	private static GetMethod getHttpGet(String url, String cookie,
			String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		
		return httpGet;
	}

	private static PostMethod getHttpPost(String url, String cookie,
			String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", NISCO_URLs.HOST);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}

	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	private static InputStream http_get(AppContext appContext, String url)
			throws AppException {
		// System.out.println("get_url==> "+url);

		//Log.i(TAG, "http_get url:" + url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				else if(statusCode == HttpStatus.SC_OK) 
		        {
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //保存cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setProperty("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}
		        }
				responseBody = httpGet.getResponseBodyAsString();

				//Log.i(TAG, "http_get responseBody:" + responseBody);
				// System.out.println("XMLDATA=====>"+responseBody);
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		responseBody = responseBody.replaceAll("\\p{Cntrl}", "");

		if (responseBody.contains("statusCode")  && appContext.isLogin())
		{
			try {
				
				JSONObject jsonObject;
				String statusCode = "";
				try {
					jsonObject = new JSONObject(responseBody);
					statusCode = jsonObject.getString("statusCode");
					
				} catch (JSONException e) {
					statusCode = "";
				}
				
				if (statusCode != null )
				{
					if (statusCode.equalsIgnoreCase(AdAjaxLogin.STATUS_TOKEN_VERTIFY_ERROR)
							|| statusCode.equalsIgnoreCase(AdAjaxLogin.STATUS_TOKEN_NOT_FOUND))
					{
						appContext.setSecondLogin(true);
						appContext.Logout();
						appContext.getUnLoginHandler().sendEmptyMessage(1);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ByteArrayInputStream(responseBody.getBytes());
	}

	/**
	 * 公用post方法
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static InputStream _post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {
		// System.out.println("post_url==> "+url);

		//Log.i(TAG, "_post url:" + url);

		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;

		// post表单参数处理
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
				// System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// System.out.println("post_key_file==> "+file);
			}

		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);
				httpPost.setRequestEntity(new MultipartRequestEntity(parts,
						httpPost.getParams()));
				int statusCode = httpClient.executeMethod(httpPost);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				} else if (statusCode == HttpStatus.SC_OK) {
					Cookie[] cookies = httpClient.getState().getCookies();
					String tmpcookies = "";
					for (Cookie ck : cookies) {
						tmpcookies += ck.toString() + ";";
					}
					// 保存cookie
					if (appContext != null && tmpcookies != "") {
						appContext.setProperty("cookie", tmpcookies);
						appCookie = tmpcookies;
					}
				}
				responseBody = httpPost.getResponseBodyAsString();
				// System.out.println("XMLDATA=====>"+responseBody);
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		//Log.i(TAG, "_post responseBody:" + responseBody);

		if (responseBody.contains("result")
				&& responseBody.contains("errorCode")
				&& appContext.containsProperty("user.uid")) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody
						.getBytes()));
				if (res.getErrorCode() == 0) {
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ByteArrayInputStream(responseBody.getBytes());
	}

	/**
	 * post请求URL
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 * @throws IOException
	 * @throws
	 */
	private static Result http_post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException, IOException {
		return Result.parse(_post(appContext, url, params, files));
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		// System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				//此处会内存溢出
				InputStream inStream = null;
				try{
					inStream = httpGet.getResponseBodyAsStream();
					BitmapFactory.Options opts=new BitmapFactory.Options();
					opts.inTempStorage = new byte[100 * 1024];
					opts.inPreferredConfig = Bitmap.Config.RGB_565;
					opts.inSampleSize = 1;
					opts.inInputShareable = true; 
					bitmap =BitmapFactory.decodeStream(inStream,null, opts);    

				}catch(Exception ex){
					System.out.println("下载出错了");
					bitmap = null;
				}
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				bitmap = null;
				//throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return bitmap;
	}

	/**
	 * 检查版本更新
	 * 
	 * @param url
	 * @return
	 */
	public static Update checkVersion(AppContext appContext)
			throws AppException {
		try {
			return Update.parse(http_get(appContext, URLs.UPDATE_VERSION));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 登录， 自动处理cookie
	 * 
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, String username, String pwd)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		params.put("pwd", pwd);
		params.put("keep_login", 1);

		String loginurl = URLs.LOGIN_VALIDATE_HTTP;
		if (appContext.isHttpsLogin()) {
			loginurl = URLs.LOGIN_VALIDATE_HTTPS;
		}

		try {
			return User.parse(_post(appContext, loginurl, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 我的个人资料
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static MyInformation myInformation(AppContext appContext, int uid)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);

		try {
			return MyInformation.parse(_post(appContext, URLs.MY_INFORMATION,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 更新用户头像
	 * 
	 * @param appContext
	 * @param uid
	 *            当前用户uid
	 * @param portrait
	 *            新上传的头像
	 * @return
	 * @throws AppException
	 */
	public static Result updatePortrait(AppContext appContext, int uid,
			File portrait) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);

		Map<String, File> files = new HashMap<String, File>();
		files.put("portrait", portrait);

		try {
			return http_post(appContext, URLs.PORTRAIT_UPDATE, params, files);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 更新用户之间关系（加关注、取消关注）
	 * 
	 * @param uid
	 *            自己的uid
	 * @param hisuid
	 *            对方用户的uid
	 * @param newrelation
	 *            0:取消对他的关注 1:关注他
	 * @return
	 * @throws AppException
	 */
	public static Result updateRelation(AppContext appContext, int uid,
			int hisuid, int newrelation) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("hisuid", hisuid);
		params.put("newrelation", newrelation);

		try {
			return Result.parse(_post(appContext, URLs.USER_UPDATERELATION,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 清空通知消息
	 * 
	 * @param uid
	 * @param type
	 *            1:@我的信息 2:未读消息 3:评论个数 4:新粉丝个数
	 * @return
	 * @throws AppException
	 */
	public static Result noticeClear(AppContext appContext, int uid, int type)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("type", type);

		try {
			return Result.parse(_post(appContext, URLs.NOTICE_CLEAR, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取资讯列表
	 * 
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static NewsList getNewsList(AppContext appContext,
			final int catalog, final int pageIndex, final int pageSize)
			throws AppException {
		String newUrl = _MakeURL(URLs.NEWS_LIST, new HashMap<String, Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("catalog", catalog);
				put("pageIndex", pageIndex);
				put("pageSize", pageSize);
			}
		});

		try {
			return NewsList.parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取资讯的详情
	 * 
	 * @param url
	 * @param news_id
	 * @return
	 * @throws AppException
	 */
	public static News getNewsDetail(AppContext appContext, final int news_id)
			throws AppException {
		String newUrl = _MakeURL(URLs.NEWS_DETAIL,
				new HashMap<String, Object>() {
					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					{
						put("id", news_id);
					}
				});

		try {
			return News.parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取帖子列表
	 * 
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static PostList getPostList(AppContext appContext,
			final int catalog, final int pageIndex, final int pageSize)
			throws AppException {
		String newUrl = _MakeURL(URLs.POST_LIST, new HashMap<String, Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("catalog", catalog);
				put("pageIndex", pageIndex);
				put("pageSize", pageSize);
			}
		});

		try {
			return PostList.parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 通过Tag获取帖子列表
	 * 
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static PostList getPostListByTag(AppContext appContext,
			final String tag, final int pageIndex, final int pageSize)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tag", tag);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		try {
			return PostList.parse(_post(appContext, URLs.POST_LIST, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取帖子的详情
	 * 
	 * @param url
	 * @param post_id
	 * @return
	 * @throws AppException
	 */
	public static Post getPostDetail(AppContext appContext, final int post_id)
			throws AppException {
		String newUrl = _MakeURL(URLs.POST_DETAIL,
				new HashMap<String, Object>() {
					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					{
						put("id", post_id);
					}
				});
		try {
			return Post.parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 发帖子
	 * 
	 * @param post
	 *            （uid、title、catalog、content、isNoticeMe）
	 * @return
	 * @throws AppException
	 */
	public static Result pubPost(AppContext appContext, Post post)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", post.getAuthorId());
		params.put("title", post.getTitle());
		params.put("catalog", post.getCatalog());
		params.put("content", post.getBody());
		params.put("isNoticeMe", post.getIsNoticeMe());

		try {
			return http_post(appContext, URLs.POST_PUB, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取动态列表
	 * 
	 * @param uid
	 * @param catalog
	 *            1最新动态 2@我 3评论 4我自己
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static ActiveList getActiveList(AppContext appContext,
			final int uid, final int catalog, final int pageIndex,
			final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.ACTIVE_LIST,
				new HashMap<String, Object>() {
					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					{
						put("uid", uid);
						put("catalog", catalog);
						put("pageIndex", pageIndex);
						put("pageSize", pageSize);
					}
				});

		try {
			return ActiveList.parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 发送留言
	 * 
	 * @param uid
	 *            登录用户uid
	 * @param receiver
	 *            接受者的用户id
	 * @param content
	 *            消息内容，注意不能超过250个字符
	 * @return
	 * @throws AppException
	 */
	public static Result pubMessage(AppContext appContext, int uid,
			int receiver, String content) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("receiver", receiver);
		params.put("content", content);

		try {
			return http_post(appContext, URLs.MESSAGE_PUB, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 转发留言
	 * 
	 * @param uid
	 *            登录用户uid
	 * @param receiver
	 *            接受者的用户名
	 * @param content
	 *            消息内容，注意不能超过250个字符
	 * @return
	 * @throws AppException
	 */
	public static Result forwardMessage(AppContext appContext, int uid,
			String receiverName, String content) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("receiverName", receiverName);
		params.put("content", content);

		try {
			return http_post(appContext, URLs.MESSAGE_PUB, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 删除留言
	 * 
	 * @param uid
	 *            登录用户uid
	 * @param friendid
	 *            留言者id
	 * @return
	 * @throws AppException
	 */
	public static Result delMessage(AppContext appContext, int uid, int friendid)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("friendid", friendid);

		try {
			return http_post(appContext, URLs.MESSAGE_DELETE, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 发表博客评论
	 * 
	 * @param blog
	 *            博客id
	 * @param uid
	 *            登陆用户的uid
	 * @param content
	 *            评论内容
	 * @return
	 * @throws AppException
	 */
	public static Result pubBlogComment(AppContext appContext, int blog,
			int uid, String content) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("blog", blog);
		params.put("uid", uid);
		params.put("content", content);

		try {
			return http_post(appContext, URLs.BLOGCOMMENT_PUB, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 发表博客评论
	 * 
	 * @param blog
	 *            博客id
	 * @param uid
	 *            登陆用户的uid
	 * @param content
	 *            评论内容
	 * @param reply_id
	 *            评论id
	 * @param objuid
	 *            被评论的评论发表者的uid
	 * @return
	 * @throws AppException
	 */
	public static Result replyBlogComment(AppContext appContext, int blog,
			int uid, String content, int reply_id, int objuid)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("blog", blog);
		params.put("uid", uid);
		params.put("content", content);
		params.put("reply_id", reply_id);
		params.put("objuid", objuid);

		try {
			return http_post(appContext, URLs.BLOGCOMMENT_PUB, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 删除博客评论
	 * 
	 * @param uid
	 *            登录用户的uid
	 * @param blogid
	 *            博客id
	 * @param replyid
	 *            评论id
	 * @param authorid
	 *            评论发表者的uid
	 * @param owneruid
	 *            博客作者uid
	 * @return
	 * @throws AppException
	 */
	public static Result delBlogComment(AppContext appContext, int uid,
			int blogid, int replyid, int authorid, int owneruid)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("blogid", blogid);
		params.put("replyid", replyid);
		params.put("authorid", authorid);
		params.put("owneruid", owneruid);

		try {
			return http_post(appContext, URLs.BLOGCOMMENT_DELETE, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取评论列表
	 * 
	 * @param catalog
	 *            1新闻 2帖子 3动弹 4动态
	 * @param id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static CommentList getCommentList(AppContext appContext,
			final int catalog, final int id, final int pageIndex,
			final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.COMMENT_LIST,
				new HashMap<String, Object>() {
					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					{
						put("catalog", catalog);
						put("id", id);
						put("pageIndex", pageIndex);
						put("pageSize", pageSize);
					}
				});

		try {
			return CommentList.parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 发表评论
	 * 
	 * @param catalog
	 *            1新闻 2帖子 3动弹 4动态
	 * @param id
	 *            某条新闻，帖子，动弹的id
	 * @param uid
	 *            用户uid
	 * @param content
	 *            发表评论的内容
	 * @param isPostToMyZone
	 *            是否转发到我的空间 0不转发 1转发
	 * @return
	 * @throws AppException
	 */
	public static Result pubComment(AppContext appContext, int catalog, int id,
			int uid, String content, int isPostToMyZone) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("isPostToMyZone", isPostToMyZone);

		try {
			return http_post(appContext, URLs.COMMENT_PUB, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 
	 * @param id
	 *            表示被评论的某条新闻，帖子，动弹的id 或者某条消息的 friendid
	 * @param catalog
	 *            表示该评论所属什么类型：1新闻 2帖子 3动弹 4动态
	 * @param replyid
	 *            表示被回复的单个评论id
	 * @param authorid
	 *            表示该评论的原始作者id
	 * @param uid
	 *            用户uid 一般都是当前登录用户uid
	 * @param content
	 *            发表评论的内容
	 * @return
	 * @throws AppException
	 */
	public static Result replyComment(AppContext appContext, int id,
			int catalog, int replyid, int authorid, int uid, String content)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("catalog", catalog);
		params.put("id", id);
		params.put("uid", uid);
		params.put("content", content);
		params.put("replyid", replyid);
		params.put("authorid", authorid);

		try {
			return http_post(appContext, URLs.COMMENT_REPLY, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 删除评论
	 * 
	 * @param id
	 *            表示被评论对应的某条新闻,帖子,动弹的id 或者某条消息的 friendid
	 * @param catalog
	 *            表示该评论所属什么类型：1新闻 2帖子 3动弹 4动态&留言
	 * @param replyid
	 *            表示被回复的单个评论id
	 * @param authorid
	 *            表示该评论的原始作者id
	 * @return
	 * @throws AppException
	 */
	public static Result delComment(AppContext appContext, int id, int catalog,
			int replyid, int authorid) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("catalog", catalog);
		params.put("replyid", replyid);
		params.put("authorid", authorid);

		try {
			return http_post(appContext, URLs.COMMENT_DELETE, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 用户收藏列表
	 * 
	 * @param uid
	 *            用户UID
	 * @param type
	 *            0:全部收藏 1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @param pageIndex
	 *            页面索引 0表示第一页
	 * @param pageSize
	 *            每页的数量
	 * @return
	 * @throws AppException
	 */
	public static FavoriteList getFavoriteList(AppContext appContext,
			final int uid, final int type, final int pageIndex,
			final int pageSize) throws AppException {
		String newUrl = _MakeURL(URLs.FAVORITE_LIST,
				new HashMap<String, Object>() {
					/**
			 * 
			 */
					private static final long serialVersionUID = 1L;

					{
						put("uid", uid);
						put("type", type);
						put("pageIndex", pageIndex);
						put("pageSize", pageSize);
					}
				});

		try {
			return FavoriteList.parse(http_get(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 用户添加收藏
	 * 
	 * @param uid
	 *            用户UID
	 * @param objid
	 *            比如是新闻ID 或者问答ID 或者动弹ID
	 * @param type
	 *            1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @return
	 * @throws AppException
	 */
	public static Result addFavorite(AppContext appContext, int uid, int objid,
			int type) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("objid", objid);
		params.put("type", type);

		try {
			return http_post(appContext, URLs.FAVORITE_ADD, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 用户删除收藏
	 * 
	 * @param uid
	 *            用户UID
	 * @param objid
	 *            比如是新闻ID 或者问答ID 或者动弹ID
	 * @param type
	 *            1:软件 2:话题 3:博客 4:新闻 5:代码
	 * @return
	 * @throws AppException
	 */
	public static Result delFavorite(AppContext appContext, int uid, int objid,
			int type) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("objid", objid);
		params.put("type", type);

		try {
			return http_post(appContext, URLs.FAVORITE_DELETE, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取搜索列表
	 * 
	 * @param catalog
	 *            全部:all 新闻:news 问答:post 软件:software 博客:blog 代码:code
	 * @param content
	 *            搜索的内容
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SearchList getSearchList(AppContext appContext,
			String catalog, String content, int pageIndex, int pageSize)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("catalog", catalog);
		params.put("content", content);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		try {
			return SearchList.parse(_post(appContext, URLs.SEARCH_LIST, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 软件列表
	 * 
	 * @param searchTag
	 *            软件分类 推荐:recommend 最新:time 热门:view 国产:list_cn
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SoftwareList getSoftwareList(AppContext appContext,
			final String searchTag, final int pageIndex, final int pageSize)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("searchTag", searchTag);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		try {
			return SoftwareList.parse(_post(appContext, URLs.SOFTWARE_LIST,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 软件分类的软件列表
	 * 
	 * @param searchTag
	 *            从softwarecatalog_list获取的tag
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static SoftwareList getSoftwareTagList(AppContext appContext,
			final int searchTag, final int pageIndex, final int pageSize)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("searchTag", searchTag);
		params.put("pageIndex", pageIndex);
		params.put("pageSize", pageSize);

		try {
			return SoftwareList.parse(_post(appContext, URLs.SOFTWARETAG_LIST,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 软件分类列表
	 * 
	 * @param tag
	 *            第一级:0 第二级:tag
	 * @return
	 * @throws AppException
	 */
	public static SoftwareCatalogList getSoftwareCatalogList(
			AppContext appContext, final int tag) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tag", tag);

		try {
			return SoftwareCatalogList.parse(_post(appContext,
					URLs.SOFTWARECATALOG_LIST, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取软件详情
	 * 
	 * @param ident
	 * @return
	 * @throws AppException
	 */
	public static Software getSoftwareDetail(AppContext appContext,
			final String ident) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ident", ident);

		try {
			return Software.parse(_post(appContext, URLs.SOFTWARE_DETAIL,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static String inputStream2String(InputStream is) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int i = -1;
		try {
			while ((i = is.read()) != -1) {
				byteStream.write(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteStream.toString();
	}

	/**
	 * 登录至ERP系统
	 * 
	 * @param appContext
	 * @param username
	 *            用户名
	 * @param pwd
	 *            密码
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxLogin adLoginErp(AppContext appContext, String userno,
			String password) throws AppException {
		String erpLoginUrl = AD_NISCO_NORMAL_URL.ERP_LOGIN + userno + "/"
				+ password;

		String result = null;
		AdAjaxLogin adLogin = null;

		try {
			result = inputStream2String(http_get(appContext, erpLoginUrl));
			adLogin = AdAjaxLogin.getInstance(result);

			if (adLogin != null
					&& adLogin.getStatusCode().equalsIgnoreCase(
							AdAjaxLogin.STATUS_CODE_SUCCESS)) {
				appContext.setToken(adLogin.getToken());
				appContext.setCurrentUser(userno);
				appContext.setCurrentPwd(password);
				appContext.setCurrentUserName(adLogin.getUsername());
				appContext.setShowCard(adLogin.getShowcard());
				appContext.setLogin(true);
			}

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return adLogin;
	}

	/**
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static List<AdAjaxBonus> getAdBonus(AppContext appContext)
			throws AppException {

		// String bonusUrl = AD_NISCO_LOCAL_URL.ERP_BONUS;
		String bonusUrl = AD_NISCO_NORMAL_URL.ERP_BONUS;
		String result = null;
		List<AdAjaxBonus> bonus = null;

		try {
			result = inputStream2String(http_get(appContext, bonusUrl));
			bonus = AdAjaxBonus.getInstance(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return bonus;
	}

	public static String login2(AppContext appContext, String username,
			String pwd) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userno", username);
		params.put("pwd", pwd);
		params.put("keep_login", 1);

		// return PostList.parse(http_get(appContext, newUrl));

		// String loginurl = URL_CONSTANTS.LOGIN_URL;

		String jsonUrl = URL_CONSTANTS.GET_JSONS;

		String json_array_url = URL_CONSTANTS.GET_JSONS_ARRAY;

		String result = "";
		JSONObject jsonObject = null;

		JSONArray jsonArray = null;

		try {
			// result = inputStream2String(http_get(appContext, usersUrl));

			result = inputStream2String(http_get(appContext, jsonUrl));
			jsonObject = new JSONObject(result);
			String name = jsonObject.getString("statusCode");

			result = inputStream2String(http_get(appContext, json_array_url));

			jsonObject = new JSONObject(result);

			jsonArray = jsonObject.getJSONObject("users").getJSONArray("users");
			int length = jsonArray.length();
			jsonObject = jsonArray.getJSONObject(0);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return result;
	}

	/**
	 * 用于验证Token是否失效，失效后重新获得
	 * 
	 * @param appContext
	 * @param result
	 */
	public static void refreshToken(final AppContext appContext, String result) {
		AdAjaxLogin adlg = AdAjaxLogin.getInstance(result);

		if (adlg != null && adlg.getStatusCode().contains("40")) {
			String erpLoginUrl = AD_NISCO_NORMAL_URL.ERP_LOGIN
					+ appContext.getCurrentUser() + "/"
					+ appContext.getCurrentPwd();

			try {
				String tmpResult = inputStream2String(http_get(appContext,
						erpLoginUrl));
				AdAjaxLogin tmpLogin = AdAjaxLogin.getInstance(tmpResult);

				if (tmpLogin != null
						&& tmpLogin.getStatusCode().equalsIgnoreCase(
								AdAjaxLogin.STATUS_CODE_SUCCESS)) {
					appContext.setToken(tmpLogin.getToken());
				}

			} catch (Exception e) {

				Log.v(TAG, e.toString());
			}

		}

	}

	/**
	 * 获取薪资信息
	 * 
	 * @param appContext
	 * @param userno
	 * @param password
	 * @return
	 * @throws AppException
	 */
	public static ErpSalaryInfo adGetSalaryInfo(final AppContext appContext,
			String userno, String strdate) throws AppException {

		String getPayUrl = _MakeURL(AD_NISCO_NORMAL_URL.ERP_GETPAY + userno
				+ "/" + strdate, new HashMap<String, Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("_tk", appContext.getToken());
			}
		});

		String result = null;
		JSONObject jsonObject = null;
		ErpSalaryInfo erpSalaryInfo = new ErpSalaryInfo();

		try {
			result = inputStream2String(http_get(appContext, getPayUrl));
			refreshToken(appContext, result);
			jsonObject = new JSONObject(result);
			erpSalaryInfo = ErpSalaryInfo.parse(jsonObject);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpSalaryInfo;
	}

	/**
	 * 获得版本信息
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static AdUpdate adGetVersion(AppContext appContext)
			throws AppException {
		String erpLoginUrl = AD_NISCO_NORMAL_URL.ERP_GET_VERSION;
		String result = null;

		AdUpdate update = null;
		try {
			result = inputStream2String(http_get(appContext, erpLoginUrl));

			update = AdUpdate.getInstance(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return update;
	}

	/**
	 * 获得菜单树
	 * @param appContext
	 * @param userno  工号
	 * @return
	 * @throws AppException
	 */
	public static AdMenu adGetMenu(final AppContext appContext, String userno)
			throws AppException {

		String admenuUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.ERP_GET_ADMENU + userno,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;

					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;

		AdMenu menu = null;
		try {
			result = inputStream2String(http_get(appContext, admenuUrl));

			menu = AdMenu.getInstance(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return menu;
	}
	
	
	/**
	 * 生产快报分页
	 * @param appContext
	 * @param userno
	 * @param resType
	 * @param pageIndex 从1开始分页
	 * @return
	 * @throws AppException
	 */
	public static AdErpReportPage adGetErpReport(final AppContext appContext, String userno,String resType,int pageIndex)
			throws AppException {

		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.ERP_GET_REPORT + userno+"/"+resType+"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		AdErpReportPage  erpReportPage = new AdErpReportPage();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			erpReportPage = AdErpReportPage.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpReportPage;
	}
	
	/**
	 *  权限申请
	 * @param appContext
	 * @param applyuserNo
	 * @param activtyNo
	 * @param activtydsc
	 * @param applyreasion
	 * @return
	 * @throws AppException
	 */
	public static Map<String,String> AdApplyPermission_post(final AppContext appContext, String applyuserNo,String activtyNo, String activtydsc, String applyreasion) throws AppException{
		//http://localhost:8080/rest/service/ad/adpermissionapply/applyPermision/860007/asqwqw/kb/apply
		Map<String,String>  maprslt =new HashMap<String,String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", applyuserNo);
		params.put("activtyNo", activtyNo);
		params.put("activtydsc", activtydsc);
		params.put("applyreasion", applyreasion);
		try {
			String ApplyUrl = _MakeURL(
				AD_NISCO_LOCAL_URL.ERP_APPLY_PER_POST,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
			String result = null;
			result = inputStream2String(_post(appContext, ApplyUrl, params, null));
			JSONObject jsonReportPage = new JSONObject(result);
			maprslt.put("status", jsonReportPage.getString("status"));
			maprslt.put("mesg",jsonReportPage.getString("mesg"));

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return maprslt;
	}
	
	/**
	 * 按FileHandle获得ErpBoardFileUrl
	 * @param appContext
	 * @param userno
	 * @param fileHandle
	 * @return
	 * @throws AppException
	 */
	public static String adGetErpBoardFileUrl(final AppContext appContext,String userno,String fileHandle) {

		String erpBoardFileUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.ERP_GET_BOARD_FILE +userno+"/"+fileHandle,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});


		return erpBoardFileUrl;
	}
	
	/**
	 * 获取部门和本部门用户
	 * @param appContext
	 * @param userno    工号
	 * @param pageIndex 从1开始分页
	 * @return
	 * @throws AppException
	 */
	public static AdErpBoardPage adGetErpBoard(final AppContext appContext, String userno,int pageIndex)
			throws AppException {

		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.ERP_GET_BOARD + userno+"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		AdErpBoardPage  erpBoardPage = new AdErpBoardPage();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			erpBoardPage = AdErpBoardPage.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpBoardPage;
	}
	
	/**
	 * 根据部门id获取用户列表
	 * @param appContext
	 * @param depno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static AdXTUserPageVo adGetXTUserbyDepNo(final AppContext appContext, String userno,String depno,int pageIndex)
			throws AppException {

		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.ERP_GET_DEP_USER +userno+"/"+ depno+"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		AdXTUserPageVo  erpUserPage = new AdXTUserPageVo();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			erpUserPage = AdXTUserPageVo.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpUserPage;
	}
	
	/**
	 * 根据登陆者获取部门和部门人员
	 * @param appContext
	 * @param userno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static List<AdXTDepartment> adGetXTUserbyUserNo(final AppContext appContext, String userno)
			throws AppException {

		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.ERP_GET_USER_DEP + userno,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		List<AdXTDepartment>  erpDeplis = new ArrayList<AdXTDepartment>();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			erpDeplis = AdXTDepartment.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpDeplis;
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
	public static AdXTUserPageVo adGetXTUserbykey(final AppContext appContext, String userno,String keys,int pageIndex)
			throws AppException, UnsupportedEncodingException {
		keys = URLEncoder.encode(keys,"utf-8");
		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.ERP_GET_USERBYKEY + keys+"/"+userno+"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		AdXTUserPageVo  erpUserPage = new AdXTUserPageVo();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			erpUserPage = AdXTUserPageVo.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpUserPage;
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
	public static AdMESOrdEnterPage AdOrderEnter(final AppContext appContext,String userno, String checkdate,String companyname,int pageIndex) throws AppException{
		//http://localhost:8080/rest/service/ad/adpermissionapply/applyPermision/860007/asqwqw/kb/apply
		if(companyname==null || companyname==""){
			companyname ="empty";
		}
		try {
			companyname = URLEncoder.encode(companyname,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.MES_ORDER_ENTER +userno +"/"+checkdate+"/"+companyname+"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		AdMESOrdEnterPage  erpOrderEnterPage = new AdMESOrdEnterPage();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			erpOrderEnterPage = AdMESOrdEnterPage.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpOrderEnterPage;
	}
	
	/**
	 * 订单生产情况
	 * @param appContext
	 * @param checkdate
	 * @param companyname
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static AdMESOrderProductPage AdMESOrderProduct(final AppContext appContext, String userno,String checkdate,String companyname,int pageIndex) throws AppException{
		//http://localhost:8080/rest/service/ad/adpermissionapply/applyPermision/860007/asqwqw/kb/apply
		if(companyname==null || companyname==""){
			companyname ="empty";
		}
		try {
			companyname = URLEncoder.encode(companyname,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.MES_ORDER_PRODUCT +userno +"/"+checkdate+"/"+companyname+"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		AdMESOrderProductPage  erpOrderProductPage = new AdMESOrderProductPage();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			erpOrderProductPage = AdMESOrderProductPage.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return erpOrderProductPage;
	}
	
	/**
	 * 获取用户权限列表
	 * @param appContext
	 * @param userno
	 * @return
	 * @throws AppException
	 */
	public static List<AdUserPermissionTemp> getUserPermision(final AppContext appContext, String userno) throws AppException{
		//http://localhost:8080/rest/service/ad/adpermissionapply/applyPermision/860007/asqwqw/kb/apply
		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.GET_USER_PERMISION + userno,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		List<AdUserPermissionTemp>  userPermisionlis = new ArrayList<AdUserPermissionTemp>();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			userPermisionlis = AdUserPermissionTemp.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return userPermisionlis;
	}
	
	/**
	 * 测试头像上传
	 * @param appContext
	 * @param paramters
	 * @param filesMap
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt uploaduserHeaderPic(final AppContext appContext, Map<String, Object> paramters,
            Map<String, File> filesMap) throws AppException{
		
		String fileuploadtUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.XT_USERHEADERPIC_UPLOAD,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(_post(appContext, fileuploadtUrl, paramters, filesMap));
			opt =AdAjaxOpt.parse(result);
			return opt;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
	/**
	 * 测试文件下载
	 * @param appContext
	 * @param userno
	 * @return
	 * @throws AppException
	 */
	public static Bitmap downloaduserHeaderPic(final AppContext appContext, String userno) throws AppException{
		
		String fileuploadtUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.XT_USERHEADERPIC_DOWNLOAD+userno,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		return getNetBitmap(fileuploadtUrl);

	}
	
	
	/**
	 * 获得协同办公用户基本信息
	 * @param appContext
	 * @param userno
	 * @return
	 * @throws AppException
	 */
	public static AdXTUser xtGetUserInfo(final AppContext appContext,
			String userno) throws AppException {

		String getUserInfoUrl = _MakeURL(AD_NISCO_NORMAL_URL.XT_GET_USERINFO
				+ userno, new HashMap<String, Object>() {
			/**
			 * 序列化
			 */
			private static final long serialVersionUID = 1L;
			{
				put("_tk", appContext.getToken());
			}
		});

		String result = null;
		AdXTUser xtUser = new AdXTUser();
		try {
			result = inputStream2String(http_get(appContext, getUserInfoUrl));

			xtUser = AdXTUser.parse(result);
			
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return xtUser;
	}
	
	
	/**
	 * 更新协同办公用户手机号
	 * @param appContext
	 * @param paramters
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt xtUpdateUserMobile(final AppContext appContext,Map<String, Object> paramters) throws AppException {

		String updateUserMobileUrl = _MakeURL(AD_NISCO_NORMAL_URL.XT_UPDATE_USER_MOBILE, new HashMap<String, Object>() {
			/**
			 * 序列化
			 */
			private static final long serialVersionUID = 1L;
			{
				put("_tk", appContext.getToken());
			}
		});
		
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(_post(appContext, updateUserMobileUrl, paramters, null));
			opt =AdAjaxOpt.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return opt;
	}
		
	/**
	 * 更新协同办公用户办公室号码
	 * @param appContext
	 * @param paramters
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt xtUpdateUserTelephone(final AppContext appContext,Map<String, Object> paramters) throws AppException {

		String updateUserTelephoneUrl = _MakeURL(AD_NISCO_NORMAL_URL.XT_UPDATE_USER_TELEPHONE, new HashMap<String, Object>() {
			/**
			 * 序列化
			 */
			private static final long serialVersionUID = 1L;
			{
				put("_tk", appContext.getToken());
			}
		});
		
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(_post(appContext, updateUserTelephoneUrl, paramters, null));
			opt =AdAjaxOpt.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return opt;
	}
	
	
	
	/**
	 * 隐藏用户的手机号码或办公室号码
	 * @param appContext
	 * @param paramters
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt xtHideUserInfo(final AppContext appContext,Map<String, Object> paramters) throws AppException {

		String hideUserInfoUrl = _MakeURL(AD_NISCO_NORMAL_URL.XT_HIDEUSERINFO, new HashMap<String, Object>() {
			/**
			 * 序列化
			 */
			private static final long serialVersionUID = 1L;
			{
				put("_tk", appContext.getToken());
			}
		});
		
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(_post(appContext, hideUserInfoUrl, paramters, null));
			opt =AdAjaxOpt.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return opt;
	}
	
	
	/**
	 * 统一数据库的增、删、改操作
	 * @param appContext
	 * @param commUrl  AD_NISCO_NORMAL_URL对应的参数
	 * @param paramters  提交的参数
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt dbHelper(final AppContext appContext,final String commUrl,final Map<String, Object> paramters) throws AppException {

		String url = _MakeURL(commUrl, new HashMap<String, Object>() {
			/**
			 * 序列化
			 */
			private static final long serialVersionUID = 1L;
			{
				put("_tk", appContext.getToken());
			}
		});
		
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(_post(appContext, url, paramters, null));
			opt =AdAjaxOpt.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		return opt;
	}
	
	
	
	
	/**
	 * 获得生产快报菜单列表，有授权管控（按userno，activityName）
	 * @param appContext
	 * @param userno
	 * @param activityName
	 * @return
	 * @throws AppException
	 */
	public static List<AdReportLeftMenu> adGetReportLeftMenus(
			final AppContext appContext, String userno,String activityName) throws AppException {

		String reportLeftMenuUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.GET_TIMELYREPORT_CLASS + userno+"/"+activityName,
				new HashMap<String, Object>() {
					/**
					 * 序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});

		String result = null;
		List<AdReportLeftMenu> list = new ArrayList<AdReportLeftMenu>();
		try {

			result = inputStream2String(http_get(appContext, reportLeftMenuUrl));

			list = AdReportLeftMenuPage.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
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
	public static AdMESOrdProcessTracePage mesOrderProcessTrace(final AppContext appContext, String userno,String checkdate,String orderinfo,String deldate,int pageIndex) throws AppException{
		if(orderinfo ==""){
			orderinfo = "empty";
		}
		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.MES_ORDER_PROCESS_TRACE +userno +"/"+checkdate +"/"+orderinfo+"/"+deldate+"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		Log.i("url", reportUrl);
		String result = null;
		AdMESOrdProcessTracePage  mesOrdProcessTracePage = new AdMESOrdProcessTracePage();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			mesOrdProcessTracePage = AdMESOrdProcessTracePage.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return mesOrdProcessTracePage;
	}
	
	/**
	 * 发表状态
	 * @author Wang Cheng
	 * 2014年12月29日下午10:33:52
	 * @param appContext
	 * @param paramters
	 * @param filesMap
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt publishstatus(final AppContext appContext, Map<String, Object> paramters,
            Map<String, File> filesMap) throws AppException{
		
		String fileuploadtUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.FIREND_PUBLISH_STATUS,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(_post(appContext, fileuploadtUrl, paramters, filesMap));
			opt =AdAjaxOpt.parse(result);
			return opt;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
	
	/**
	 * 获取朋友状态  
	 * @author Wang Cheng
	 * 2015年1月6日下午4:41:36
	 * @param appContext
	 * @param userno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static AdFriendStatusPageVo getFriendStatus(final AppContext appContext, String userno,int pageIndex) throws AppException{
		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.GET_FRIEND_STATUS +userno +"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		String result = null;
		AdFriendStatusPageVo  friendStatusPage = new AdFriendStatusPageVo();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			friendStatusPage = AdFriendStatusPageVo.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return friendStatusPage;
	}
	
	

	/**
	 * 朋友圈点赞
	 * @param appContext
	 * @param userno  工号
	 * @param friendsMainid mainid
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt addFriendZan(final AppContext appContext, String userno,String friendsMainid) throws AppException{
		
		String addFriendZanUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.FRIEND_ZAN+userno+"/"+friendsMainid,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(http_get(appContext, addFriendZanUrl));
			opt =AdAjaxOpt.parse(result);
			return opt;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
		

	/**
	 * 朋友圈评论
	 * @param appContext
	 * @param userno 工号
	 * @param friendsMainid mainid
	 * @param comment 内容
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt addFriendComment(final AppContext appContext,String userno,String friendsMainid,String comment) throws AppException{

		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try {
			String utf8_comment =  URLEncoder.encode(comment,"UTF-8");
			utf8_comment=utf8_comment.replaceAll("\\+","%20"); //处理空格
			String addFriendCommentUrl = _MakeURL(
					AD_NISCO_NORMAL_URL.FRIEND_COMMENT+userno+"/"+friendsMainid+"/"+utf8_comment,
					new HashMap<String, Object>() {
						/**
						 *  序列化
						 */
						private static final long serialVersionUID = 1L;
						{
							put("_tk", appContext.getToken());
						}
					});
			
			result = inputStream2String(http_get(appContext, addFriendCommentUrl));
			opt =AdAjaxOpt.parse(result);
			return opt;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
	
	/**
	 * 返回朋友圈图像下载路径
	 * @param appContext
	 * @param userno 工号
	 * @param imdid 图片id编号
	 * @return
	 * @throws AppException
	 */
	public static String getFCLoadPic(final AppContext appContext,String userno,String imdid) throws AppException{
 
		try { 
			String adFCLoadPicUrl = _MakeURL(
					AD_NISCO_NORMAL_URL.GET_FRIEND_ALBUM_PIC+userno+"/"+imdid,
					new HashMap<String, Object>() {
						/**
						 *  序列化
						 */
						private static final long serialVersionUID = 1L;
						{
							put("_tk", appContext.getToken());
						}
					});
			return adFCLoadPicUrl;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

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
	public static AdErpReportChartVo getTimelyReportChart(final AppContext appContext,String userno,String typeNo,String reportProperty
			,String targetField,String reportDate) throws AppException{
		 
		try {
			targetField = URLEncoder.encode(targetField,"UTF-8");
			String adErpReportChartUrl = _MakeURL(
					AD_NISCO_NORMAL_URL.GET_TIMELYREPORT_CHART+userno+"/"+typeNo+"/"+reportProperty+"/"+targetField+"/"+reportDate,
					new HashMap<String, Object>() {
						/**
						 *  序列化
						 */
						private static final long serialVersionUID = 1L;
						{
							put("_tk", appContext.getToken());
						}
					});
			
			String result = null;
			AdErpReportChartVo  adErpReportChartVo = new AdErpReportChartVo();
			result = inputStream2String(http_get(appContext, adErpReportChartUrl));
			adErpReportChartVo = AdErpReportChartVo.parse(result);
			
			return adErpReportChartVo;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 保存客户错误信息
	 * @param appContext
	 * @param paramters
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt saveClientError(final AppContext appContext,Map<String, Object> paramters) throws AppException {

		AdAjaxOpt opt = new AdAjaxOpt();
		String  result = "";
		String updateUserTelephoneUrl = _MakeURL(AD_NISCO_NORMAL_URL.SAVE_CLIENT_ERROR, new HashMap<String, Object>() {
			/**
			 * 序列化
			 */
			private static final long serialVersionUID = 1L;
			{
				put("_tk", appContext.getToken());
			}
		});
		try { 
			result = inputStream2String(_post(appContext, updateUserTelephoneUrl, paramters, null));
			opt =AdAjaxOpt.parse(result);
			return opt;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 朋友圈屏蔽状态
	 * @param appContext
	 * @param operauserno  操作工号
	 * @param userno  屏蔽工号
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt maskFCStatus(final AppContext appContext, String operauserno,String userno,int intIsActive) throws AppException{
		
		String addFriendMaskUserUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.FRIEND_MASKUSER+operauserno+"/"+userno+"/"+intIsActive,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(http_get(appContext, addFriendMaskUserUrl));
			opt =AdAjaxOpt.parse(result);
			return opt;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取朋友状态-管理界面  
	 * @author Wang Cheng
	 * 2015年1月6日下午4:41:36
	 * @param appContext
	 * @param userno
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static AdFriendStatusPageVo getFriendStatusMansge(final AppContext appContext, String userno,int pageIndex) throws AppException{
		String reportUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.FRIEND_FCMANAGE +userno +"/"+pageIndex,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		String result = null;
		AdFriendStatusPageVo  friendStatusPage = new AdFriendStatusPageVo();

		try {
			result = inputStream2String(http_get(appContext, reportUrl));

			friendStatusPage = AdFriendStatusPageVo.parse(result);

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

		return friendStatusPage;
	}
	
	
	/**
	 * 朋友圈 删帖
	 * @param appContext
	 * @param operauserno  操作工号
	 * @param id  帖子id
	 * @return
	 * @throws AppException
	 */
	public static AdAjaxOpt delFCStatus(final AppContext appContext, String operauserno,String id) throws AppException{
		
		String addFriendMaskUserUrl = _MakeURL(
				AD_NISCO_NORMAL_URL.FRIEND_DELSTATUS+operauserno+"/"+id,
				new HashMap<String, Object>() {
					/**
					 *  序列化
					 */
					private static final long serialVersionUID = 1L;
					{
						put("_tk", appContext.getToken());
					}
				});
		String  result = "";
		AdAjaxOpt opt = new AdAjaxOpt();
		try { 
			result = inputStream2String(http_get(appContext, addFriendMaskUserUrl));
			opt =AdAjaxOpt.parse(result);
			return opt;

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
}
