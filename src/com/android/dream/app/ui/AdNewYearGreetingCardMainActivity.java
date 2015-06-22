package com.android.dream.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;

public class AdNewYearGreetingCardMainActivity extends BaseActivity{

	private TextView sendView;
	private TextView backView;
	private AppContext appContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_newyeargreetingcard_main);
		appContext = (AppContext) getApplication();
		//初始化页面控件
		initview();
	}
	
	/**
	 *  返回业务
	 */
	private void backProcess()
	{
		Intent intent = new Intent();
		intent.setClass(AdNewYearGreetingCardMainActivity.this, AdMainActivity.class);
		startActivity(intent);
		if (appContext != null)
		{
			appContext.setMainStart(true);
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
	 * @author Wang Cheng
	 * 2014年12月30日下午12:57:34
	 */
	private void initview(){
		
		sendView = (TextView) findViewById(R.id.ad_newyeargreetingcard_send);
		sendView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(AdNewYearGreetingCardMainActivity.this, AdNewYearGreetingCardTemplateChoiceActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		backView = (TextView) findViewById(R.id.ad_newyeargreetingcard_back);
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				backProcess();
			}
		});
		
		
	}
	
}
