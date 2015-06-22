package com.android.dream.app.bean;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.android.dream.app.AppException;

public class OptMessage extends Base{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6254743195161785643L;
	public final static String SUCCESS = "00001";
	public final static String ERROR = "00002";
	public final static String FAULT = "00003";


	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	

	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public boolean OK()
	{
		boolean isSuccess = false;
		if (code != null && code.equalsIgnoreCase(SUCCESS))
		{
			isSuccess =true;
		}
		
		return isSuccess;
	}
	private String code;
	private String result;
	
	
	public static OptMessage parse(InputStream stream) throws IOException, AppException {
		OptMessage message = null;
		// 获得XmlPullParser解析器
		XmlPullParser xmlParser = Xml.newPullParser();
		try {
			xmlParser.setInput(stream, Base.UTF8);
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = xmlParser.getEventType();
			// 一直循环，直到文档结束
			while (evtType != XmlPullParser.END_DOCUMENT) {
				String tag = xmlParser.getName();
				switch (evtType) {
				
				case XmlPullParser.START_DOCUMENT:
					message = null;
					break;
					
				case XmlPullParser.START_TAG:
					// 如果是标签开始，则说明需要实例化对象了
					if (tag.equalsIgnoreCase("message")) {
						message = new OptMessage();
					} else if (message != null) {
						if(tag.equalsIgnoreCase("code")){
							message.setCode(xmlParser.nextText());
						}else if(tag.equalsIgnoreCase("result")){
							message.setResult(xmlParser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					//如果遇到标签结束，则把对象添加进集合中
			       	if (tag.equalsIgnoreCase("message") && message != null) {
			       		
			       	}
					break;
				}
				// 如果xml没有结束，则导航到下一个节点
				evtType = xmlParser.next();
			}

		} catch (XmlPullParserException e) {
			throw AppException.xml(e);
		} finally {
			stream.close();
		}
		return message;
	}
}
