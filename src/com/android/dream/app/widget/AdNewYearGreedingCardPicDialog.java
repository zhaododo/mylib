package com.android.dream.app.widget;



import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.ui.AdNewYearGreetingCardTemplateChoiceActivity;
import com.android.dream.app.ui.AdSysPicDirActivity;
import com.android.dream.app.util.ImageUtils;

/**
 * @author zhaoj
 * 搜索对话框
 * 
 */
public class AdNewYearGreedingCardPicDialog extends Dialog{
	
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
	
	private LinearLayout layout;
	
	
	/**
	 * 保存Dialog对象
	 */
	private AdNewYearGreedingCardPicDialog mDialog;
	
	private AdNewYearGreetingCardTemplateChoiceActivity activity;
	/**
	 * 构造函数
	 * @param context
	 * @param theme      layout
	 * @param csListener 查询接口的实现
	 */
	public AdNewYearGreedingCardPicDialog(Context context, int theme, AdNewYearGreetingCardTemplateChoiceActivity fcpActivity) {
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
				startActionCamera();
				dismiss();
			}
		});
		bt2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(context,
						AdSysPicDirActivity.class);
				//设置最大选择图片数量
				intent.putExtra("maxselect", 1);
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
	
	/**
	 * 相机拍照
	 * @param output
	 */
	private void startActionCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtils.cameraUri);
		activity.startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}
 
}
