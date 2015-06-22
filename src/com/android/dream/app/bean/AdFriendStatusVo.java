package com.android.dream.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoj
 * 朋友圈主档
 *
 */
public class AdFriendStatusVo implements Serializable{

private static final long serialVersionUID = -3849759016797122119L;
	
	//朋友圈主档编号
	private String mainStatuId;
	//工号
	private String userno;
	//姓名
	private String username;
	//公司名称
	private String companyname;
	//部门名称
	private String depname;
	//类型
	private int type;
	//是否有赞
	private int intHasZan;
	//是否有评论
	private int intHasComment;
	// 图像连接(分享)
	private String strImageUrl; 
	// 分享内容文字
	private String strShareContent;
	// 分享内容连接
	private String strShareUrl;
	//发表标题
	private String strTitle;
	// 发表内容
	private String strContent;
	// 发表内容是否有图片
	private int intHaspic;
	//显示时间（新增加）
	private String displayTime;

	//创建日期（新增加）
	private String createDate;
	
	//图片列表，这里存放图片的id编号
	private List<String> piclis = new ArrayList<String>();

	//点赞人员列表
	private List<AdFriendZanVo> zanlis = new ArrayList<AdFriendZanVo>();
	//评论列表
	private List<AdFriendCommentVo> commentlis = new ArrayList<AdFriendCommentVo>();
	
	//是否被禁用
	private int  intIsActive = 1;
	//贺卡是否禁用（1：贺卡存在，0：贺卡禁用）
	private int  intCardActive = 1;
	
	public int getIntCardActive() {
		return intCardActive;
	}
	public void setIntCardActive(int intCardActive) {
		this.intCardActive = intCardActive;
	}
	public int getIntIsActive() {
		return intIsActive;
	}
	public void setIntIsActive(int intIsActive) {
		this.intIsActive = intIsActive;
	}

	public String getDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(String displayTime) {
		this.displayTime = displayTime;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	public List<String> getPiclis() {
		return piclis;
	}

	public void setPiclis(List<String> piclis) {
		this.piclis = piclis;
	}
	
	public String getStrTitle() {
		return strTitle;
	}

	public void setStrTitle(String strTitle) {
		this.strTitle = strTitle;
	}
	
	public String getMainStatuId() {
		return mainStatuId;
	}

	public void setMainStatuId(String mainStatuId) {
		this.mainStatuId = mainStatuId;
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIntHasZan() {
		return intHasZan;
	}

	public void setIntHasZan(int intHasZan) {
		this.intHasZan = intHasZan;
	}

	public int getIntHasComment() {
		return intHasComment;
	}

	public void setIntHasComment(int intHasComment) {
		this.intHasComment = intHasComment;
	}

	public String getStrImageUrl() {
		return strImageUrl;
	}

	public void setStrImageUrl(String strImageUrl) {
		this.strImageUrl = strImageUrl;
	}

	public String getStrShareContent() {
		return strShareContent;
	}

	public void setStrShareContent(String strShareContent) {
		this.strShareContent = strShareContent;
	}

	public String getStrShareUrl() {
		return strShareUrl;
	}

	public void setStrShareUrl(String strShareUrl) {
		this.strShareUrl = strShareUrl;
	}

	public String getStrContent() {
		return strContent;
	}

	public void setStrContent(String strContent) {
		this.strContent = strContent;
	}

	public int getIntHaspic() {
		return intHaspic;
	}

	public void setIntHaspic(int intHaspic) {
		this.intHaspic = intHaspic;
	}


	public List<AdFriendZanVo> getZanlis() {
		return zanlis;
	}

	public void setZanlis(List<AdFriendZanVo> zanlis) {
		this.zanlis = zanlis;
	}

	public List<AdFriendCommentVo> getCommentlis() {
		return commentlis;
	}

	public void setCommentlis(List<AdFriendCommentVo> commentlis) {
		this.commentlis = commentlis;
	}
	
	
	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getDepname() {
		return depname;
	}

	public void setDepname(String depname) {
		this.depname = depname;
	}
	
	/**
	 * 更新评论
	 * @param commentVo
	 */
	public void addComment(AdFriendCommentVo commentVo)
	{
		if (commentlis != null)
		{
			this.commentlis.add(commentVo);
		}
	}
	
	/**
	 * 更新赞
	 * @param zanVo
	 */
	public void addZan(AdFriendZanVo zanVo)
	{
		if (zanlis != null)
		{
			if (zanlis.contains(zanVo))
			{
				zanlis.remove(zanVo);
			}
			else
			{
				zanlis.add(zanVo);
			}
			
		}
	}


}
