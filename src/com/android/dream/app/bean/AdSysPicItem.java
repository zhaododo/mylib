package com.android.dream.app.bean;

import java.io.Serializable;

/**
 * 一个图片对象
 * 
 * @author Administrator
 * 
 */
public class AdSysPicItem implements Serializable {
	private static final long serialVersionUID = -5384566046978389085L;
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
