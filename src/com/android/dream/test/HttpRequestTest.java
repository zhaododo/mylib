package com.android.dream.test;

import java.util.HashMap;
import java.util.Map;

import android.test.AndroidTestCase;
import android.util.Log;

import com.android.dream.net.HttpClientUtil;
import com.android.dream.net.NetUtil;


public class HttpRequestTest extends AndroidTestCase {
	private static final String TAG = "HttpRequestTest";
	
	private HttpClientUtil http_util = new HttpClientUtil();
	
//	public void testSendXMLRequest() throws Throwable{
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><persons><person id=\"23\"><name>eric</name><age>30</age></person></persons>";
//		String str  = new String(NetUtil.sendXML("http://192.168.73.1:8080/rest/service/login", xml));
//		Log.i(TAG, "testSendXMLRequest:"+str);
//	}
	
//	public void testPost() throws Throwable{
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("userno", "015724");
//		params.put("pwd", "goodday");
//		String str  = new String(http_util.postMethod("http://192.168.73.1:8080/rest/service/login", params, NetUtil._UTF8));
//		Log.i(TAG, "testPost:"+str);
//	}
//
//	public void testGet() throws Throwable{
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("timelength", "80");
//		String str = new String(http_util.getMethod("http://192.168.73.1:8080/rest/service/users", params, NetUtil._UTF8));
//		Log.i(TAG, "testGet:"+str);
//	}
	
//	public void testLogin() throws Throwable{
//		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("userno", "015724");
//		params.put("pwd", "goodday");
//		String str  = new String(http_util.postMethod("http://192.168.73.1:8080/rest/service/login", params, NetUtil._UTF8));
//		
//		Map<String, String> params2 = new HashMap<String, String>();
//		params2.put("key", str.replaceAll("\"", ""));
//		String strresult = new String(http_util.getMethod("http://192.168.73.1:8080/rest/service/users", params2, NetUtil._UTF8));
//		System.out.println("strresult:"+strresult);
//		Log.i(TAG, "testLogin:"+strresult);
//	}
	
	
	
	
}
