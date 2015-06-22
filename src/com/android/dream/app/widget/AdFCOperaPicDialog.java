package com.android.dream.app.widget;



import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.ui.AdFCPublishStatusCActivity;
import com.android.dream.app.ui.AdSysPicDirActivity;
import com.android.dream.app.util.FileUtils;

/**
 * @author zhaoj
 * 搜索对话框
 * 
 */
public class AdFCOperaPicDialog extends Dialog{

	Context context;
	
	/**
	 * 取消按钮
	 */
	private TextView bt1;
	/**
	 * 查询按钮
	 */
	private TextView bt2;
	
	/**
	 * 输入的查询条件
	 */
	private TextView bt3;
	
	
	/**
	 * 保存Dialog对象
	 */
	private AdFCOperaPicDialog mDialog;
	
	private AdFCPublishStatusCActivity activity;
	/**
	 * 构造函数
	 * @param context
	 * @param theme      layout
	 * @param csListener 查询接口的实现
	 */
	public AdFCOperaPicDialog(Context context, int theme, AdFCPublishStatusCActivity fcpActivity) {
		super(context,theme);
		this.context = context;
		this.activity = fcpActivity;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ad_fc_tapic_popupdialog);
		mDialog = this;
		initView();
	}
	/**
	 *  隐藏对话框
	 */
	private void hideDialog()
	{
		if (mDialog != null)
		{
			mDialog.hide();
		}
	}
	
	private void initView()
	{
		
		bt1 = (TextView) findViewById(R.id.item_popupwindows_camera);
		bt2 = (TextView) findViewById(R.id.item_popupwindows_Photo);
		bt3 = (TextView) findViewById(R.id.item_popupwindows_cancel);
		bt1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				photo();
				dismiss();
			}
		});
		bt2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context,
						AdSysPicDirActivity.class);
				context.startActivity(intent);
				dismiss();
			}
		});
		bt3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String strfiledir = Environment.getExternalStorageDirectory()+ "/dream/publishstatus";
		String filename = String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		File file = FileUtils.createFile(strfiledir, filename);
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		activity.startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
 
}
