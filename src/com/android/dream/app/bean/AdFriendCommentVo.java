package com.android.dream.app.bean;

import java.io.Serializable;

/**
 * @author zhaoj
 * 朋友圈评论档
 *
 */
public class AdFriendCommentVo implements Serializable{

	private static final long serialVersionUID = -7995157201605982440L;
	//评论人工号
	private String userno;
    //论人姓名
	private String name;
    //评论内容
	private String comment;
	public String getUserno() {
		return userno;
	}
	public void setUserno(String userno) {
		this.userno = userno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
