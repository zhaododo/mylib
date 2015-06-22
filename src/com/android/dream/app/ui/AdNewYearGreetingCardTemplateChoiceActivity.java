package com.android.dream.app.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.util.FileUtils;
import com.android.dream.app.util.ImageUtils;
import com.android.dream.app.widget.AdFCBimp;
import com.android.dream.app.widget.AdNewYearGreedingCardPicDialog;

public class AdNewYearGreetingCardTemplateChoiceActivity extends BaseActivity{

	private AppContext appContext;
	private ImageView headerBackBtn;
	private TextView uploadBtn;
	private AdNewYearGreedingCardPicDialog fcOperaPicDialog;
	private ImageView mod1ImageView;
	private ImageView mod2ImageView;
	private ImageView mod3ImageView;
	private ImageView mod4ImageView;
	private static final int TAKE_PICTURE = 0x000000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("ok", "ChoiceActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_newyeargreetingcard_templatechoice);
		appContext = (AppContext) getApplication();
		initview();
	}
	
	/**
	 *  返回业务
	 */
	private void backProcess()
	{
		if (appContext != null)
		{
			if (!appContext.isMainStart())
			{
				Intent intent = new Intent();
				intent.setClass(AdNewYearGreetingCardTemplateChoiceActivity.this, AdMainActivity.class);
				startActivity(intent);
				appContext.setMainStart(true);
			}
		}
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			backProcess();
			return true;
		}
		return true;
	}
	
	/**
	 * 初始化控件
	 */
	private void initview(){
		
		headerBackBtn = (ImageView) findViewById(R.id.ad_newyeargreetingcard_header_back);
		headerBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				backProcess();
			}
		});
		
		//上传本地图片dialog
		fcOperaPicDialog = new AdNewYearGreedingCardPicDialog(AdNewYearGreetingCardTemplateChoiceActivity.this, R.style.AdFCOperaPicDialog, this);
		fcOperaPicDialog.setContentView(R.layout.ad_fc_tapic_popupdialog);
		Window dialogWindow = fcOperaPicDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.BOTTOM;
		dialogWindow.setAttributes(lp);
		fcOperaPicDialog.setCanceledOnTouchOutside(true);
		fcOperaPicDialog.hide();
		
		uploadBtn = (TextView) findViewById(R.id.ad_newyeargreetingcard_upload);
		uploadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				fcOperaPicDialog.show();
			}
		});
		
		BGChoiceClickListener bgClickListener = new BGChoiceClickListener();
		
		mod1ImageView = (ImageView) findViewById(R.id.ad_newyeargreetingcard_mod1);
		mod1ImageView.setOnClickListener(bgClickListener);
		
		mod2ImageView = (ImageView) findViewById(R.id.ad_newyeargreetingcard_mod2);
		mod2ImageView.setOnClickListener(bgClickListener);
		
		mod3ImageView = (ImageView) findViewById(R.id.ad_newyeargreetingcard_mod3);
		mod3ImageView.setOnClickListener(bgClickListener);
		
		mod4ImageView = (ImageView) findViewById(R.id.ad_newyeargreetingcard_mod4);
		mod4ImageView.setOnClickListener(bgClickListener);
	}
	
	
	class BGChoiceClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			
			int type = 0;
			switch (view.getId()) {
				case R.id.ad_newyeargreetingcard_mod1:
					type = 0;
					break;
				case R.id.ad_newyeargreetingcard_mod2:
					type = 1;
					break;
				case R.id.ad_newyeargreetingcard_mod3:
					type = 2;
					break;
				case R.id.ad_newyeargreetingcard_mod4:
					type = 3;
					break;
				default :
					type = 0;
					break;
			}
			Intent intent = new Intent();
			Bundle idBundle = new Bundle();
			idBundle.putInt("imageId", type);
			intent.putExtras(idBundle);
			intent.setClass(AdNewYearGreetingCardTemplateChoiceActivity.this, AdNewYearGreetingCardSendActivity.class);
			startActivity(intent);
			finish();
		}
		
	}
	
	@Override
	protected void onRestart() {
		Log.i("ok", "AdNewYearGreetingCardTemplateChoiceActivity onRestart");
		super.onRestart();
		
		try {
			if (AdFCBimp.drr.size() >0) {
				Intent intent = new Intent();
				Bundle idBundle = new Bundle();
				idBundle.putInt("imageId", 404);
				intent.putExtras(idBundle);
				intent.setClass(AdNewYearGreetingCardTemplateChoiceActivity.this, AdNewYearGreetingCardSendActivity.class);
				startActivity(intent);
				this.finish();
			}
		} catch (Exception e) {
			Log.i("ok", " onRestart error:"+e.getMessage());
		}
		
	}
	

	/**
	 * 拍照后裁剪
	 * @param data 原始图片
	 * @param output 裁剪后图片
	 */
	private void startActionCrop(Uri data, Uri output) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 1024);// 输出图片大小
		intent.putExtra("outputY", 768);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}
	
	
	@Override 
	protected void onActivityResult(final int requestCode,final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA) {
			startActionCrop(ImageUtils.cameraUri,ImageUtils.cropUri);
		}
		else if (requestCode == ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP)
		{
				
			Intent intent = new Intent();
			Bundle idBundle = new Bundle();
			idBundle.putInt("imageId", 401);
			idBundle.putString("imgpath", ImageUtils.cropUri.getPath());
			intent.putExtras(idBundle);
			intent.setClass(AdNewYearGreetingCardTemplateChoiceActivity.this, AdNewYearGreetingCardSendActivity.class);
			startActivity(intent);
			this.finish();
			
		}
	}
	
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
        super.onSaveInstanceState(outState);
        
        Log.i("ok", "onSaveInstanceState");
    } 
    
	@Override
	protected void onDestroy() {
		if (fcOperaPicDialog != null) {
			fcOperaPicDialog.dismiss();
		}
		//AdFCBimp.drr.clear();
		//AdFCBimp.bmp.clear();
		
		super.onDestroy();
		Log.i("ok", "ChoiceActivity onDestroy");
		
	}
	
	
}
