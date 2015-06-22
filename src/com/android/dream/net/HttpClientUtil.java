package com.android.dream.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;



public class HttpClientUtil {
	
	public final static String  _UTF8 = "UTF-8";
	public final static String  _GBK = "GBK";
	private static final String TAG = "HttpClientUtil";
	
	public String postMethod(String path, Map<String, String> params, String enc) throws Exception
	{
		List<NameValuePair> nvPairList = new ArrayList<NameValuePair>();
		if(params!=null && !params.isEmpty()){
			for(Map.Entry<String, String> entry : params.entrySet()){
				
				nvPairList.add(new BasicNameValuePair(entry.getKey(), URLEncoder.encode(entry.getValue(), enc)));
			}
		}
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(path);
		HttpEntity formEntity = new UrlEncodedFormEntity(nvPairList);
		post.setEntity(formEntity);
		HttpResponse response = client.execute(post);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        	InputStream is = response.getEntity().getContent();
        	return inStream2String(is);
		}
		return null;
	}
	
	
	public String getMethod(String path, Map<String, String> params, String enc) throws Exception
	{
		StringBuilder sb = new StringBuilder(path);
//		sb.append('?');
//		for(Map.Entry<String, String> entry : params.entrySet()){
//			sb.append(entry.getKey()).append('=')
//				.append(entry.getValue()).append('&');
//		}
//		sb.deleteCharAt(sb.length()-1);
		HttpClient client = new DefaultHttpClient();
    	HttpGet get = new HttpGet(sb.toString());
    	Log.i(TAG, "getMethod url:"+sb.toString());
    	HttpResponse response = client.execute(get);
    	
    	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        	InputStream is = response.getEntity().getContent();
        	return  inStream2String(is);
    	}
    	return null;
	}
		
	
	private  String inStream2String(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) !=-1 ){
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		
		return new String(outSteam.toByteArray());
	}

}
