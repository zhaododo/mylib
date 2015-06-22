package com.android.dream.app.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dream.app.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class MainActivityDebug extends SliderBaseActivity {

	private Fragment mCurrent,mFragment1,mFragment2,mFragment3,mFragment4;
	
	private final static String TAG="zhaoj";

	private ImageView mTab1, mTab2, mTab3, mTab4;
	private ImageView mTabImg;
	private int lastIndex;
	private int curIndex;
	private int zero = 0;
	Display currDisplay = null;
	int displayWidth = 0;
    int displayHeight = 0;
    int one = 0;
    int two = 0;
    int three = 0;
    private TextView txt_usual_fun;
    private TextView txt_busysys;
    private TextView txt_mybusy;
    private TextView txt_setting;
    private LinearLayout lina_tab1;
    private LinearLayout lina_tab2;
    private LinearLayout lina_tab3;
    private LinearLayout lina_tab4;
	

	public MainActivityDebug() {
		super(R.string.test, new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.scale(percentOpen, 1, 0, 0);
			}
		});
		
		lastIndex = R.id.img_weixin;
		curIndex =  R.id.img_weixin;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 
		Log.i(TAG, "MainActivity:onCreate");
		// set the Above View
		if (savedInstanceState != null)
		{
			mCurrent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mCurrent");
		}
		
		if (mFragment1 == null)
		{
			mFragment1 = new AdInfoManageFragment();
			mCurrent = mFragment1;
		}
		else
		{
			mFragment1 =getSupportFragmentManager().findFragmentByTag(mFragment1.getClass().getSimpleName());
			
		}
		
		if (mFragment2 == null)
		{
			mFragment2 = new PullToRefreshFragment();
		}
		
		if (mFragment3 == null)
		{
			mFragment3 = new AdBusinessSysFragment();
		}
		else
		{
			mFragment3 =getSupportFragmentManager().findFragmentByTag(mFragment3.getClass().getSimpleName());
		}
		
		if (mFragment4 == null)
		{
			mFragment4 = new AdMyInfoFragment();
		}
		
		Display currDisplay = getWindowManager().getDefaultDisplay();
		displayWidth = currDisplay.getWidth();
		displayHeight = currDisplay.getHeight();
		one = displayWidth / 4;
		two = one * 2;
		three = one * 3;
			
		setSlidingActionBarEnabled(false);
		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

		setContentView(R.layout.frame_content);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_content, mCurrent).commit();

		getSlidingMenu().setSecondaryMenu(R.layout.frame_right);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_right, new RightFragment()).commit();
		initButton();
		txt_usual_fun =(TextView) findViewById(R.id.txt_usual_fun);
		txt_busysys =(TextView) findViewById(R.id.txt_busysys);
		txt_mybusy =(TextView) findViewById(R.id.txt_mybusy);
		txt_setting =(TextView) findViewById(R.id.txt_setting);
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mCurrent", mCurrent);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "MainActivity:onStop");
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "MainActivity:onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "MainActivity:onPause");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (mFragment1 != null)
		{
			mFragment1.onDestroy();
		}
		
		if (mFragment2 != null)
		{
			mFragment2.onDestroy();
		}
		
		if (mFragment3 != null)
		{
			mFragment3.onDestroy();
		}
		
		if (mFragment4 != null)
		{
			mFragment4.onDestroy();
		}
		mCurrent = null;
		
	}

	public void switchContent(Fragment fragment) {
		mCurrent = fragment;
		FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		
		ft.replace(R.id.frame_content, fragment,fragment.getClass().getSimpleName());
		ft.commit();
		getSlidingMenu().showContent();
	}
	
	public  void  switchAnimation() {
		
		Animation animation = null;
		if (curIndex == R.id.img_weixin)
		{
			switch (lastIndex) {
			case R.id.img_address:
				animation = new TranslateAnimation(one, 0, 0, 0);
				break;
				
			case R.id.img_friends:
				animation = new TranslateAnimation(two, 0, 0, 0);
				break;
				
			case R.id.img_settings:
				animation = new TranslateAnimation(three, 0, 0, 0);
				break;

			}
			animation.setFillAfter(true);
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		else if (curIndex == R.id.img_address)
		{
			switch (lastIndex) {
			case R.id.img_weixin:
				animation = new TranslateAnimation(zero, one, 0, 0);
				break;
				
			case R.id.img_friends:
				animation = new TranslateAnimation(two, one, 0, 0);
				break;
				
			case R.id.img_settings:
				animation = new TranslateAnimation(three, one, 0, 0);
				break;

			}
			animation.setFillAfter(true);
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		else if (curIndex == R.id.img_friends)
		{
			switch (lastIndex) {
			case R.id.img_weixin:
				animation = new TranslateAnimation(zero, two, 0, 0);
				break;
				
			case R.id.img_address:
				animation = new TranslateAnimation(one, two, 0, 0);
				break;
				
			case R.id.img_settings:
				animation = new TranslateAnimation(three, two, 0, 0);
				break;

			}
			animation.setFillAfter(true);
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		else if (curIndex == R.id.img_settings)
		{
			switch (lastIndex) {
			case R.id.img_weixin:
				animation = new TranslateAnimation(zero, three, 0, 0);
				break;
				
			case R.id.img_address:
				animation = new TranslateAnimation(one, three, 0, 0);
				break;
				
			case R.id.img_friends:
				animation = new TranslateAnimation(two, three, 0, 0);
				break;

			}
			animation.setFillAfter(true);
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		
		
	}

	private void initButton() {

		mTab1 = (ImageView) findViewById(R.id.img_weixin);
		mTab2 = (ImageView) findViewById(R.id.img_address);
		mTab3 = (ImageView) findViewById(R.id.img_friends);
		mTab4 = (ImageView) findViewById(R.id.img_settings);
//		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		lina_tab1 = (LinearLayout) findViewById(R.id.lina_tab1);
		lina_tab2 = (LinearLayout) findViewById(R.id.lina_tab2);
		lina_tab3 = (LinearLayout) findViewById(R.id.lina_tab3);
		lina_tab4 = (LinearLayout) findViewById(R.id.lina_tab4);

		mTab1.setOnClickListener(new TabOnClickListener());
		mTab2.setOnClickListener(new TabOnClickListener());
		mTab3.setOnClickListener(new TabOnClickListener());
		mTab4.setOnClickListener(new TabOnClickListener());
		lina_tab1.setOnClickListener(new TabOnClickListener());
		lina_tab2.setOnClickListener(new TabOnClickListener());
		lina_tab3.setOnClickListener(new TabOnClickListener());
		lina_tab4.setOnClickListener(new TabOnClickListener());
		
	}

	public class TabOnClickListener implements View.OnClickListener {

		Animation animation = null;
		@Override
		public void onClick(View v) {
			
			curIndex = v.getId();
			
			if (curIndex != lastIndex)

			switch (v.getId()) {
			
				case R.id.img_weixin: {
					tab1switch();	
				}
				break;
				
				case R.id.lina_tab1: {
					tab1switch();	
				}
				break;
				
				case R.id.img_address:{
//					switchAnimation();
					tab2switch();
				}
			    break;
				case R.id.lina_tab2: {
					tab2switch();	
				}
				break;

				case R.id.img_friends:{
	//				switchAnimation();
					tab3switch ();
				}
				break;
				case R.id.lina_tab3: {
					tab3switch();	
				}
				break;
				case R.id.img_settings:{
	//				switchAnimation();
					tab4switch();
				}
				break;
				case R.id.lina_tab4: {
					tab4switch();	
				}
			default:
				break;
			}
		}
	};
	
	public void tab1switch (){
		mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_manage_pressed));
		mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_oa_normal));
		mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_bus_normal));
		mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_mybusy_normal));
		txt_usual_fun.setTextColor(getResources().getColor(R.color.col_usual_fun_press));
		txt_busysys.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_mybusy.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_setting.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		switchContent(mFragment1);
		lastIndex = R.id.img_weixin;
	}
	public void tab2switch (){
		mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_manage_normal));
		mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_oa_pressed));
		mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_bus_normal));
		mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_mybusy_normal));
		txt_usual_fun.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_busysys.setTextColor(getResources().getColor(R.color.col_usual_fun_press));
		txt_mybusy.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_setting.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		switchContent(mFragment2);
		lastIndex = R.id.img_address;
	}
	public void tab3switch (){
		mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_manage_normal));
		mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_oa_normal));
		mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_bus_pressed));
		mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_mybusy_normal));
		switchContent(mFragment3);
		lastIndex = R.id.img_friends;
		txt_usual_fun.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_busysys.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_mybusy.setTextColor(getResources().getColor(R.color.col_usual_fun_press));
		txt_setting.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
	}
	public void tab4switch (){
		mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_manage_normal));
		mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_oa_normal));
		mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_bus_normal));
		mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_mybusy_pressed));
		switchContent(mFragment4);
		lastIndex = R.id.img_settings;
		txt_usual_fun.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_busysys.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_mybusy.setTextColor(getResources().getColor(R.color.col_usual_fun_normal));
		txt_setting.setTextColor(getResources().getColor(R.color.col_usual_fun_press));
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return true;
		}
		return true;
	}
	
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
		new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//AccoutList.this.finish();
				//System.exit(1);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		builder.setNegativeButton("取消",
		new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

}
