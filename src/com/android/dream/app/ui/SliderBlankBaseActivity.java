package com.android.dream.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.android.dream.app.AppManager;
import com.android.dream.app.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * @author zhaododo
 * 
 *
 */
public class SliderBlankBaseActivity extends SlidingFragmentActivity{
	
	private int mTitleRes;
	protected Fragment mFrag;
	public static SlidingMenu mSlidingMenu;
	private CanvasTransformer mTransformer;
	
	public SliderBlankBaseActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	public SliderBlankBaseActivity(int titleRes,CanvasTransformer transformer) {
		mTitleRes = titleRes;
		mTransformer=transformer;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSlidingMenu = getSlidingMenu();
		setTitle(mTitleRes);
		
		SlidingMenu sm = getSlidingMenu();
		//sm.setShadowWidthRes(R.dimen.shadow_width);
		//sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setFadeEnabled(true);
		if (mTransformer != null)
		{
			sm.setBehindCanvasTransformer(mTransformer);
		}
		
		AppManager.getAppManager().addActivity(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}

}
