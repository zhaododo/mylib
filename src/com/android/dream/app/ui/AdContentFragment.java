package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdUserPermissionTemp;

public class AdContentFragment extends Fragment {
	private AppContext appContext;
	private ViewPager viewPager;
	private int[] imageResId;
	private List<View> dots; 
	private List<ImageView> imageViews; 
	private int currentItem = 0;
	private SimpleAdapter  simpleadapter;
	private String[] strfro;
	private int[]  intto;
	//功能说明
	private String[] item;
	//用户权限列表
	private List<AdUserPermissionTemp> userPermissionTemplis = null;
	private GridView lisv;
	//功能列表
	private ArrayList<HashMap<String, Object>> listItems;
	//功能图片
	private int[] drawables = null;
	
	private View rootview;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		if (rootview == null)
		{
			appContext = (AppContext) getActivity().getApplication();
			rootview=inflater.inflate(R.layout.activity_main, container, false);
			//广告图片资源
			imageResId = new int[] { R.drawable.ad_1, R.drawable.ad_2, R.drawable.ad_5, R.drawable.ad_4, R.drawable.ad_5};
			//广告imgview,目前只有一个异彩生活网
			imageViews = new ArrayList<ImageView>();
			for (int i = 0; i < 1; i++) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setImageResource(imageResId[1]);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(imageView);
			}
			//图片下面的点
			dots = new ArrayList<View>();
			dots.add(rootview.findViewById(R.id.v_dot0));
			dots.add(rootview.findViewById(R.id.v_dot1));
			dots.add(rootview.findViewById(R.id.v_dot2));
			//装载广告图片的ViewPager
			viewPager = (ViewPager) rootview.findViewById(R.id.vp);
			viewPager.setAdapter(new MyAdapter());
			//只有一张图片不用改变事件
			//viewPager.setOnPageChangeListener(new MyPageChangeListener());
			viewPager.setOffscreenPageLimit(0);
			SliderBaseActivity.mSlidingMenu.addIgnoredView(viewPager);
			
			lisv=(GridView) rootview.findViewById(R.id.grd_items);
			//功能说明
			item = getResources().getStringArray(R.array.titles_manage_info);
			//功能列表
			listItems = new ArrayList<HashMap<String, Object>>();   
			//功能图片
			drawables = new int[]{ R.drawable.erp_kb,
					R.drawable.ad_erp_board_pic,
					R.drawable.boat_deck_icon,
					R.drawable.ad_mes_ordproduct,
					R.drawable.ad_mes_ordprocess,
					R.drawable.f,
					R.drawable.g,
					R.drawable.h,
					R.drawable.i,
					R.drawable.vv,
					R.drawable.f};
			//默认不显示MES订单功能
	       for(int i=0; i < 2 ; i++){   
	           HashMap<String, Object> map = new HashMap<String, Object>();   
	           map.put("image", drawables[i]);   
	           map.put("item", item[i]);   
	           listItems.add(map);    
	        }
			strfro=new String[]{"image","item"};
			intto=new int[]{R.id.item_imageView,R.id.item_textView};
			//开辟线程加载用户权限列表
			new Thread(new loaduserpermision()).start();
			simpleadapter =new SimpleAdapter(getActivity(), listItems, R.layout.grid_item, strfro, intto);
			lisv.setAdapter(simpleadapter);
			lisv.setOnItemClickListener(new onitemclicklistener());
		}
		else
		{
			ViewGroup parent = (ViewGroup) rootview.getParent();   
			if (parent != null) {   
			    parent.removeView(rootview);   
			}  

			
		}
		
		return rootview;
		
	}
	
	public class onitemclicklistener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			switch(arg2){
			
			case 0:
				Intent erpquickinfo=new Intent(getActivity(),ErpReportActivity.class);
				startActivity(erpquickinfo);
			break;
			case 1:
				Intent erpBoardIntent=new Intent(getActivity(),ErpBoardActivity.class);
				startActivity(erpBoardIntent);
			break;
			case 2:
				Intent adBoatIntent=new Intent(getActivity(),AdBoatDeckActivity.class);
				startActivity(adBoatIntent);
			break;
			}
		}
	}
	
	private class loaduserpermision implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Message msg = new Message();
			// TODO Auto-generated method stub
			try {
				userPermissionTemplis = appContext.getUserPermision(appContext.getCurrentUser());
				msg.what = 100;
			} catch (AppException e) {
				// TODO Auto-generated catch block
				msg.what = -1;
				//e.printStackTrace();
			}
			handlerpermision.sendMessage(msg);
		}}
	
	
	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
//			return imageResId.length;
//			return Integer.MAX_VALUE;
			return 1;
		}

		@Override
		public Object instantiateItem(View arg0, final int arg1) {
			Log.i("wchtest", String.valueOf(arg1)+"%"+String.valueOf(imageViews.size())+"instantiateItem");
			try {
				   View view = imageViews.get(arg1%imageViews.size());
			       ((ViewPager) arg0).addView(imageViews.get(arg1%imageViews.size()),0);
			       view.setOnClickListener(new OnClickListener() {
			    	    
			    	    @Override
			    	    public void onClick(View v) {
			    	     // TODO Auto-generated method stub
			    	     Log.e("xl", "xl:arrive here.");
			    	     if(arg1%imageViews.size()==0){
			    	    	 Uri  uri = Uri.parse("http://ilife.nisco.cn");
				    	     Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
				    	     startActivity(intent);
			    	     }
			    	    }
			    	   });
			    
				 }catch(Exception e){  
				         //handler something  
			} 
			return imageViews.get(arg1%imageViews.size());
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			Log.i("wchtest", String.valueOf(arg1)+"%"+String.valueOf(imageViews.size())+"destroyItem");
			//((ViewPager) arg0).removeView(imageViews.get(arg1%imageViews.size()));
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

	@Override
	public void onStart() {
//		thread =new Thread(new ScrollTask());
//		thread.start();
		// TODO Auto-generated method stub
		//循环图片
//		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5, TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	public void onStop() {
//		scheduledExecutorService.shutdown();
		super.onStop();
	}
	

//	
//	private Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			viewPager.setCurrentItem(currentItem);
//		};
//	};

	//处理权限
	private Handler handlerpermision = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 100 && userPermissionTemplis !=null){
				for(int i =0;i<userPermissionTemplis.size();i++){
					//如果在权限列表内则添加显示
					if(userPermissionTemplis.get(i).getStrPermission().equals("AdBoatDeckActivity")
							&&userPermissionTemplis.get(i).getStraction().equals("view")){
						HashMap<String, Object> map = new HashMap<String, Object>();   
				        map.put("image", drawables[2]);   
				        map.put("item", item[2]);
				        //防止多个角色存在权限重叠，页面加载多次
				        if(!listItems.contains(map)){
				        	listItems.add(2,map);
				        }
					}
				}
			}
			simpleadapter.notifyDataSetChanged();
		};
	};
}
