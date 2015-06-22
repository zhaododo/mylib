package com.android.dream.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author zhaoj
 * ERP公告主档
 */
public class AdErpBoard implements Serializable{

	private static final long serialVersionUID = 9035261563430109816L;

	//主键（日期排序）
	private String msgid;
		
	//布告分类id
	private String boardid;
	
	//布告分类名称
    private String boardName;

	//建立日期（按主键编号转换）
	private String createDate;
	
	//发布人
	private String publisher;
	
	//发布人（姓名）
	private String publisherName;
	
	//发布人IP
	private String publichost;
	
	//标题
	private String title;
	
    //附件标识(info)
	private String fileTag;
	
	//是否有附件
	private boolean hasFile;
	
	//过期日期
	private String expireDate;
	
	//公告重要性
	private int importance;
	
	//是否已读
	private boolean hasReaded;

	//公告明细
	private AdErpBoardItem boardItem;
	
	//文件列表
	private List<AdErpBoardFile> boardFiles;
	
	public AdErpBoard()
	{
		boardItem = new AdErpBoardItem();
		boardFiles =new ArrayList<AdErpBoardFile>();
		fileTag = "";
		hasFile = false;
		hasReaded = false;
	}
	
	public boolean isHasReaded() {
		return hasReaded;
	}

	public void setHasReaded(boolean hasReaded) {
		this.hasReaded = hasReaded;
	}
	
	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public boolean isHasFile() {
		return hasFile;
	}


	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}


	public AdErpBoardItem getBoardItem() {
		return boardItem;
	}


	public void setBoardItem(AdErpBoardItem boardItem) {
		this.boardItem = boardItem;
	}


	public List<AdErpBoardFile> getBoardFiles() {
		return boardFiles;
	}


	public void setBoardFiles(List<AdErpBoardFile> boardFiles) {
		this.boardFiles = boardFiles;
	}
	
	
	
	public String getMsgid() {
		return msgid;
	}


	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}


	public String getBoardid() {
		return boardid;
	}


	public void setBoardid(String boardid) {
		this.boardid = boardid;
	}


	public String getBoardName() {
		return boardName;
	}


	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}


	public String getCreateDate() {
		return createDate;
	}


	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}


	public String getPublisher() {
		return publisher;
	}


	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}


	public String getPublisherName() {
		return publisherName;
	}


	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}


	public String getPublichost() {
		return publichost;
	}


	public void setPublichost(String publichost) {
		this.publichost = publichost;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getFileTag() {
		return fileTag;
	}


	public void setFileTag(String fileTag) {
		this.fileTag = fileTag;
		
		if (fileTag != null && fileTag.length() >0)
		{
			hasFile = true;
		}
	}


	public String getExpireDate() {
		return expireDate;
	}


	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
}
