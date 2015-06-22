package com.android.dream.app.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;

/**
 * @author Wang Cheng
 *
 * @date 2014-10-27下午11:43:51
 */
public class TestdownloadPicActivity extends BaseActivity{

	private AppContext appContext;
	private String TAG = "wch";
	private int SCUCESS = 1;
	private Handler upload_handler;
	private Button  btn_file_download;
	private ImageView iv_head_pic;
	private Bitmap downloadrslt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_file_download);
		appContext = (AppContext) getApplication();;
		upload_handler = this.getuploadHandler();
		btn_file_download = (Button) findViewById(R.id.btn_file_download);
		iv_head_pic = (ImageView) findViewById(R.id.iv_head_pic);
		btn_file_download.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadFile();
			}
		});
		
	}
	/**
	 * 创建线程加载数据
	 * @param pageIndex
	 * @param action
	 * @param userno
	 */
	private void downloadFile() {
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					downloadrslt = appContext.downloaduserHeaderPic("860008");
					
					if (downloadrslt != null)
					{
						msg.what = SCUCESS;
						msg.obj = downloadrslt;
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				upload_handler.sendMessage(msg);
			}
		}.start();

	}
	
	/**
	 * 创建Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @param more
	 * @param progress
	 * @param pageSize
	 * @return
	 */
	private Handler getuploadHandler() {
		
		Log.i(TAG, "getLvHandler");
		
		return new Handler() {
	
			public void handleMessage(Message msg) {
				if (msg.what == SCUCESS) {
					Toast.makeText(TestdownloadPicActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
					iv_head_pic.setImageBitmap(downloadrslt);
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(TestdownloadPicActivity.this, "", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
