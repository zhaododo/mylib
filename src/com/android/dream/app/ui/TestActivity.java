package com.android.dream.app.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.widget.LoginDialog;

public class TestActivity extends BaseActivity{

	private ImageView goodim;
	private LoginDialog loginDialog;
	private AppContext appContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
//		//初始化页面控件
//		initview();
		appContext = (AppContext) getApplication();
		loginDialog = new LoginDialog(this, R.style.LoginDialog, appContext);
		loginDialog.setContentView(R.layout.login_dialog);
		Window dialogWindow = loginDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.gravity = Gravity.CENTER;
		dialogWindow.setAttributes(lp);
		loginDialog.setCanceledOnTouchOutside(true);
		loginDialog.hide();
		
		goodim = (ImageView) findViewById(R.id.goodim);
		goodim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loginDialog.show();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(loginDialog != null) loginDialog.dismiss();
	}
	
	
	private void scaleAnimation(View v) {
		Animation scaleAnimation;
		scaleAnimation = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		
		scaleAnimation.setDuration(1000);
		scaleAnimation.setFillAfter(true);
		// 动画重复次数(-1 表示一直重复)
//		scaleAnimation.setRepeatCount(-1);
		scaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
		// 图片配置动画
		v.setAnimation(scaleAnimation);
		v.startAnimation(scaleAnimation);
		
	}
	
}