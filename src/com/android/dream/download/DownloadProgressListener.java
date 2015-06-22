package com.android.dream.download;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-3上午11:01:12
 */
public interface DownloadProgressListener {
	/**
	 * 下载进度监听方法 获取和处理下载点数据的大小
	 * @param size 数据大小
	 */
	public void onDownloadSize(int size);
	
	/**
	 * 下载完成通知
	 */
	public void onFinished();
}
