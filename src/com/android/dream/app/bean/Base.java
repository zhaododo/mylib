package com.android.dream.app.bean;

import java.io.Serializable;

public abstract class Base implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8039448864683266496L;
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "oschina";
	
	
	protected Notice notice;

	public Notice getNotice() {
		return notice;
	}

	public void setNotice(Notice notice) {
		this.notice = notice;
	}
}
