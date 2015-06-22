package com.android.dream.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 一个目录的相册对象
 * 
 * @author Administrator
 * 
 */
public class AdSysPicDir implements Serializable {
	private static final long serialVersionUID = -347331180459129583L;
	public int count = 0;
	public String bucketName;
	public List<AdSysPicItem> imageList;

}
