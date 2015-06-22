package com.android.dream.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class NetUtil {
	
	private final static int _TIME_OUT = 5* 1000;
	public final static String  _POST_METHOD = "POST";
	public final static String  _GET_METHOD = "GET";
	public final static String  _PUT_METHOD = "PUT";
	public final static String  _UTF8 = "UTF-8";
	public final static String  _GBK = "GBK";
	private static final String TAG = "HttpRequestTest";
	
	public static byte[] sendXML(String path, String xml){
		byte[] data = xml.getBytes();
		
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod(_POST_METHOD);
			conn.setConnectTimeout(_TIME_OUT);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(data);
			outStream.flush();
			outStream.close();
			conn.connect();
			if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
				return readInputStream(conn.getInputStream());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (conn != null) conn.disconnect();
		}
		
		return null;
	}
	
	public static byte[] getMethod(String path, Map<String, String> params, String enc){
		
		HttpURLConnection conn = null;
		InputStreamReader in = null;
		try {
			StringBuilder sb = new StringBuilder(path);
			sb.append('?');
			for(Map.Entry<String, String> entry : params.entrySet()){
				sb.append(entry.getKey()).append('=')
					.append(URLEncoder.encode(entry.getValue(), enc)).append('&');
			}
			sb.deleteCharAt(sb.length()-1);
			
			URL url = new URL(sb.toString());
			Log.i(TAG, "url:"+sb.toString());
			conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(_TIME_OUT);
			in = new InputStreamReader(conn.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(in);
			
			StringBuffer strBuffer = new StringBuffer();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				strBuffer.append(line);
			}
			String result = strBuffer.toString();
			
			Log.i(TAG, "result:"+result);
			
			return result.getBytes();
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally
		{
			if (conn != null) conn.disconnect();
			
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
		return null;
	}
	
	
	public static byte[] postMethod(String path, Map<String, String> params, String enc) {
		
		HttpURLConnection conn = null;
		try {
			StringBuilder sb = new StringBuilder();
			if(params!=null && !params.isEmpty()){
				for(Map.Entry<String, String> entry : params.entrySet()){
					sb.append(entry.getKey()).append('=')
						.append(URLEncoder.encode(entry.getValue(), enc)).append('&');
				}
				sb.deleteCharAt(sb.length()-1);
			}
			byte[] entitydata = sb.toString().getBytes();//得到实体的二进制数据
			URL url = new URL(path);
		   conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod(_POST_METHOD);
			conn.setConnectTimeout(_TIME_OUT);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(entitydata.length));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(entitydata);
			outStream.flush();
			outStream.close();
			if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
				return readInputStream(conn.getInputStream());
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (conn != null) conn.disconnect();
		}
		
		return null;
	}
	
	public static byte[] postFromHttpClient(String path, Map<String, String> params, String encode) throws Exception{
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> entry : params.entrySet()){
			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, encode);
		HttpPost httppost = new HttpPost(path);
		httppost.setEntity(entity);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = httpclient.execute(httppost);	
		return readInputStream(response.getEntity().getContent());
	}
	
	
	private static byte[] readInputStream(InputStream inStream) throws Exception{
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) !=-1 ){
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
	
	private static String getContent(HttpURLConnection conn) throws Exception{
		InputStreamReader in = new InputStreamReader(conn.getInputStream());  
		String result = "";
		 
		BufferedReader bufferedReader = new BufferedReader(in);
		StringBuffer strBuffer = new StringBuffer();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			strBuffer.append(line);
		}
		result = strBuffer.toString();
		
		
		return result;
	}

}
