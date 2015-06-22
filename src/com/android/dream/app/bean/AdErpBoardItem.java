package com.android.dream.app.bean;

import java.io.Serializable;


/**
 * @author zhaoj
 * ERP公告明细档
 */
public class AdErpBoardItem implements Serializable{

	private static final long serialVersionUID = -6976175135604613061L;

	//主档主键（日期排序）
	private String msgid;
		
	//明细内容
	private String content;
	
	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
