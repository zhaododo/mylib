package com.android.dream.app.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdReportLeftMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class ErpReportActivity extends SliderBlankBaseActivity {
	
	private Fragment mCurrent;
	private ImageView img_erpreport_menue;
	private ImageView img_back_home;
	private TextView txt_sckb_title;
	
	private OnReportTypeSelectedListener mReportTypeSelectedListener;
	
	public void setmReportTypeSelectedListener(
			OnReportTypeSelectedListener mReportTypeSelectedListener) {
		this.mReportTypeSelectedListener = mReportTypeSelectedListener;
	}

	/**
	 * @author zhaododo
	 * 
	 * 用于处理报表分类
	 *
	 */
	public interface OnReportTypeSelectedListener
	{
		 public void loadReport(AdReportLeftMenu menuItem); 
	}
	
	public ErpReportActivity() {
		super(R.string.test, new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.scale(percentOpen, 1, 0, 0);
			}
		});
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setBehindContentView(R.layout.frame_left);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			mFrag = new ErpReportLeftFragment();
			t.replace(R.id.frame_left, mFrag);
			t.commit();
		} else {
			mFrag = (Fragment)this.getSupportFragmentManager().findFragmentById(R.id.frame_left);
		}
		
		setContentView(R.layout.fram_product_main);
		if (savedInstanceState != null) {
			mCurrent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mCurrent");
		} else {
			mCurrent = new ErpReportContentFragment();
		}
		txt_sckb_title = (TextView) findViewById(R.id.txt_sckb_title);
		img_erpreport_menue = (ImageView) findViewById(R.id.img_erpreport_menue);
		img_erpreport_menue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSlidingMenu().showMenu();
			}
		});
		img_back_home = (ImageView) findViewById(R.id.img_back_home);
		img_back_home.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ErpReportActivity.this.finish();
			}
		});
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_product_main, mCurrent).commit();
		getSlidingMenu().setMode(SlidingMenu.LEFT);
		
	}
	
	public void switchContent(Fragment fragment,String tagName) {
		mCurrent = fragment;
		FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.replace(R.id.frame_product_main, fragment,tagName);
		ft.commit();
		getSlidingMenu().showContent();
	}
	
	
	/**
	 * ErpReprtLeftFragment调用该方法传值
	 */
	public void onItemSelected(AdReportLeftMenu menuItem) {
		
		if (mReportTypeSelectedListener !=null)
		{
			if (menuItem != null)
			{
				txt_sckb_title.setText(menuItem.getTitle());
				mReportTypeSelectedListener.loadReport(menuItem);
				getSlidingMenu().showContent();
			}
			
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mCurrent", mCurrent);
	}

}
