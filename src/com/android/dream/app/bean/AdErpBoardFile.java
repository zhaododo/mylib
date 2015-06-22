package com.android.dream.app.bean;

import java.io.Serializable;


/**
 * @author zhaoj
 * ERP公告文件附件
 *
 */
public class AdErpBoardFile implements Serializable {
	
	public AdErpBoardFile()
	{
		filelength = 0;
		downlength =0;
		percent="%0";
	}

	private static final long serialVersionUID = 4798744527161143579L;
	
	//1KB
	private static final  int FILE_SIZE_KB = 1024;
	//1MB
	private static final  int FILE_SIZE_MB = 1024*1024;
	//1GB
	private static final  int FILE_SIZE_GB = 1024*1024*1024;

	//主键
	private String fileHandle;
	
	//文件标识
	private String info;
	
	//文件名称
	private String fileName;
	
	//文件路径（ERP路径）
	private String fileLocation;
	
	//Android端Sd卡对应的路径
	private String sdLocation;
	
	//下载状态
	private String downloadStatus;
	
	//文件长度
	private int filelength;
	
	//文件长度说明
    private String  fileLengthDesc;
	
	//下载长度
	private int downlength;
	
	//下载百分比
	private String percent;

	//获得大小信息
	public String getFileLengthDesc() {
		
		if (filelength <= 0)
		{
			fileLengthDesc ="(未知)";
		}
		else 
		{
			if (filelength< FILE_SIZE_MB)
			{
				fileLengthDesc = String.format("(%.2f KB)", (float)((float)filelength/(float)FILE_SIZE_KB));
			}
			else 
			{
				fileLengthDesc = String.format("(%.2f MB)", (float)((float)filelength/(float)FILE_SIZE_MB));
			}
		}
		
		return fileLengthDesc;
	}

	public void setFileLengthDesc(String fileLengthDesc) {
		this.fileLengthDesc = fileLengthDesc;
	}
	
	public String getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public int getFilelength() {
		return filelength;
	}

	public void setFilelength(int filelength) {
		this.filelength = filelength;
	}

	public int getDownlength() {
		return downlength;
	}

	public void setDownlength(int downlength) {
		this.downlength = downlength;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}
	
	
	public String getFileHandle() {
		return fileHandle;
	}

	public void setFileHandle(String fileHandle) {
		this.fileHandle = fileHandle;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getSdLocation() {
		return sdLocation;
	}

	public void setSdLocation(String sdLocation) {
		this.sdLocation = sdLocation;
	}

}
