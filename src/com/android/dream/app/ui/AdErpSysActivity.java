package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

import com.android.dream.app.R;

public class AdErpSysActivity extends BaseActivity{

	private ViewPager viewPager;
	private String[] titles;
	private int[] imageResId;
	private List<View> dots; 
	private List<ImageView> imageViews;
//	private TextView tv_title;
	private SimpleAdapter  simpleadapter;
	private String[] strfro;
	private int[]  intto;
	private int currentItem = 0;
	private ScheduledExecutorService scheduledExecutorService;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_erpsys);
		imageResId = new int[] { R.drawable.ad_1, R.drawable.ad_2, R.drawable.ad_5, R.drawable.ad_4, R.drawable.ad_5};
		titles = new String[imageResId.length];
		titles = getResources().getStringArray(R.array.pic_title);
		imageViews = new ArrayList<ImageView>();
//		for (int i = 0; i < imageResId.length; i++) {
		for (int i = 0; i < 3; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(imageResId[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
		}
		
		dots = new ArrayList<View>();
		dots.add(findViewById(R.id.v_dot0));
		dots.add(findViewById(R.id.v_dot1));
		dots.add(findViewById(R.id.v_dot2));
//		dots.add(findViewById(R.id.v_dot3));
//		dots.add(findViewById(R.id.v_dot4));
		
//		tv_title = (TextView)findViewById(R.id.tv_title);
//		tv_title.setText(titles[0]);
		
		viewPager = (ViewPager)findViewById(R.id.vp);
		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setOffscreenPageLimit(0);
		
		GridView lisv=(GridView)findViewById(R.id.grd_items_business_erp);
		
		String[] item = getResources().getStringArray(R.array.titles_bus_sys_erp);
		
		ArrayList<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();  
		
		int[] drawables = { R.drawable.salary_pic,
				R.drawable.f,
				R.drawable.g,
				R.drawable.h,
				R.drawable.i,
				R.drawable.vv,
				R.drawable.f,
				R.drawable.g,
				R.drawable.h,
				R.drawable.i,
				R.drawable.vv,
				R.drawable.f};
	   
//       for(int i=0; i < item.length ; i++){ 
       for(int i=0; i < 1 ; i++){
           HashMap<String, Object> map = new HashMap<String, Object>();   
           map.put("image", drawables[i]);   
           map.put("item", item[i]);   
           listItems.add(map);    
       }   
		strfro=new String[]{"image","item"};
		intto=new int[]{R.id.item_imageView,R.id.item_textView};
		simpleadapter =new SimpleAdapter(this, listItems, R.layout.grid_item, strfro, intto);
		lisv.setAdapter(simpleadapter);
		lisv.setOnItemClickListener(new onitemclicklistener());
	}
    
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			Log.i("wchtest", String.valueOf(position)+"%"+String.valueOf(imageViews.size())+"onPageSelected");
			currentItem = position;
//			tv_title.setText(titles[position%imageViews.size()]);
			dots.get(oldPosition%imageViews.size()).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position%imageViews.size()).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position%imageViews.size();
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
	
	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
//			return imageResId.length;
			return Integer.MAX_VALUE;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			Log.i("wchtest", String.valueOf(arg1)+"%"+String.valueOf(imageViews.size())+"instantiateItem");
			try {
				
			       ((ViewPager) arg0).addView(imageViews.get(arg1%imageViews.size()),0);
			    
				 }catch(Exception e){  
				         //handler something  
			} 
			return imageViews.get(arg1%imageViews.size());
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			Log.i("wchtest", String.valueOf(arg1)+"%"+String.valueOf(imageViews.size())+"destroyItem");
//			((ViewPager) arg0).removeView(imageViews.get(arg1%imageViews.size()));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
	
	public class onitemclicklistener implements OnItemClickListener{

		private Class clazz;

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
			switch(arg2){
			case 0:
//				try {
//					clazz = Class.forName("com.android.dream.app.ui."+userAuthortylis.get(0).getStrAuthory());
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				Intent salaryItent=new Intent(AdErpSysActivity.this,ErpSalaryActivity.class);
				startActivity(salaryItent);
			break;
			case 1:
 
			break;
			case 2:
 
			break;
			}
			

		}
	}
	
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				Log.i("wchtest", String.valueOf(currentItem)+"%"+String.valueOf(imageViews.size())+"ScrollTask");
				System.out.println("currentItem: " + currentItem);
				//currentItem = (currentItem + 1) % imageViews.size();
				currentItem=currentItem+1;
				handler.obtainMessage().sendToTarget(); 
			}
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);
		};
	};
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5, TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
		scheduledExecutorService.shutdown();
		super.onStop();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
