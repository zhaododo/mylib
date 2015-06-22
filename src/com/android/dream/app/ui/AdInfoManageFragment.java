package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdMenu;
import com.android.dream.app.widget.LoadingDialog;

/**
 * @author zhaoj 管理信息
 * 
 */
public class AdInfoManageFragment extends Fragment {
	
	private final static String TAG="zhaoj";

	// 广告页面显示
	private ViewPager viewPager;
	private List<ImageView> imageViews;
	// 圆点
	private List<View> dots;
	// 当前位置
	private int currentPosition = 0;

	// 图片资源数组
	private int[] imageResArray;
	private ScheduledExecutorService scheduledExecutorService;

	// 用于显示菜单
	private GridView gridView;
	private SimpleAdapter gridviewAdapter;
	private String[] strfro = new String[] { "image", "item"};
	private int[]  intto = new int[] { R.id.item_imageView, R.id.item_textView };
	List<HashMap<String, Object>> menuItems = new ArrayList<HashMap<String, Object>>();

	private AppContext appContext;
	private Handler mHandler;
	private LoadingDialog loading;

	private final static int MSG_GETDATA_SUCCESS = 0x01;
	private final static int MSG_NETWORK_EROOR = 0x02;
	
	private View rootview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		if (rootview  == null)
		{
			rootview = inflater.inflate(R.layout.activity_main, container,
					false);

			// 广告页面
			imageResArray = new int[] { R.drawable.ad_1, R.drawable.ad_2,
					R.drawable.ad_5, R.drawable.ad_4, R.drawable.ad_5 };
			imageViews = new ArrayList<ImageView>();

			for (int i = 0; i < 3; i++) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setImageResource(imageResArray[i]);
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(imageView);
			}

			dots = new ArrayList<View>();
			dots.add(rootview.findViewById(R.id.v_dot0));
			dots.add(rootview.findViewById(R.id.v_dot1));
			dots.add(rootview.findViewById(R.id.v_dot2));

			viewPager = (ViewPager) rootview.findViewById(R.id.vp);
			viewPager.setAdapter(new AdvertisementAdapter());
			viewPager
					.setOnPageChangeListener(new AdvertisementPageChangeListener());
			viewPager.setOffscreenPageLimit(0);
			SliderBaseActivity.mSlidingMenu.addIgnoredView(viewPager);

			gridView = (GridView) rootview.findViewById(R.id.grd_items);
			
			Log.i(TAG, "AdInfoManageFragment:onCreateView");
			Log.i(TAG, "AdInfoManageFragment:container:"+container);
		}
		
		
		ViewGroup parent = (ViewGroup) rootview.getParent();   
		if (parent != null) {   
		    parent.removeView(rootview);   
		}  

		return rootview;
	}

	/**
	 * @author zhaoj GridView的Click事件
	 * 
	 */
	public class GridViewOnTtemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			if (menuItems != null && position<= menuItems.size())
			{
				HashMap<String, Object> map = menuItems.get(position);
				
				String activity = (String)map.get("activity");
				try {
					Class<?> clazz = Class.forName("com.android.dream.app.ui."+activity);
					Intent intent = new Intent(getActivity(),
							clazz);
					startActivity(intent);
					
				} catch (ClassNotFoundException e) {
					
				}
			}
		}
	}

	/**
	 * @author zhaoj 广告条事件
	 * 
	 */
	private class AdvertisementPageChangeListener implements
			OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			Log.i("wchtest",
					String.valueOf(position) + "%"
							+ String.valueOf(imageViews.size())
							+ "onPageSelected");
			currentPosition = position;
			// tv_title.setText(titles[position%imageViews.size()]);
			dots.get(oldPosition % imageViews.size()).setBackgroundResource(
					R.drawable.dot_normal);
			dots.get(position % imageViews.size()).setBackgroundResource(
					R.drawable.dot_focused);
			oldPosition = position % imageViews.size();
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

	}

	/**
	 * 
	 * @author zhaoj 广告条适配器
	 * 
	 */
	private class AdvertisementAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object instantiateItem(View arg0, final int arg1) {
			Log.i("wchtest",
					String.valueOf(arg1) + "%"
							+ String.valueOf(imageViews.size())
							+ "instantiateItem");
			try {
				View view = imageViews.get(arg1 % imageViews.size());
				((ViewPager) arg0).addView(
						imageViews.get(arg1 % imageViews.size()), 0);
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.e("xl", "xl:arrive here.");
						if (arg1 % imageViews.size() == 1) {
							Uri uri = Uri.parse("http://ilife.nisco.cn");
							Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(intent);
						}
					}
				});

			} catch (Exception e) {
			}
			return imageViews.get(arg1 % imageViews.size());
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			Log.i("wchtest",
					String.valueOf(arg1) + "%"
							+ String.valueOf(imageViews.size()) + "destroyItem");
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "AdInfoManageFragment:onPause");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "AdInfoManageFragment:onResume");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.i(TAG, "AdInfoManageFragment:onDestroyView");
	}

	@Override
	public void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 5,
				TimeUnit.SECONDS);
		super.onStart();
		Log.i(TAG, "AdInfoManageFragment:onStart");
	}

	@Override
	public void onStop() {
		scheduledExecutorService.shutdown();
		super.onStop();
		Log.i(TAG, "AdInfoManageFragment:onStop");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		
		Log.i(TAG, "AdInfoManageFragment:onActivityCreated");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity != null) {
			appContext = (AppContext) activity.getApplication();
		}
	}

	private void initData() {
		
		if (mHandler == null)
		{
			mHandler = new Handler() {
				public void handleMessage(Message msg) {

					if (msg.what == MSG_GETDATA_SUCCESS) {
						if (msg.obj != null) {
							process(msg.obj);
						}
					} else if (msg.what == MSG_NETWORK_EROOR) {
						if (loading != null)
							loading.dismiss();
						if (getActivity() != null) {
							Toast.makeText(getActivity(), "无网络连接",
									Toast.LENGTH_LONG).show();
						}

					} else {
						if (loading != null)
							loading.dismiss();
					}
				}
			};
			
			
			Log.i(TAG, "mHandler:"+mHandler);
			
			if (appContext != null) {
				loadMenuThread(appContext.getCurrentUser());
			}
			
		}
		
		
	}

	/**
	 * 解析分析
	 * @param obj
	 */
	private void process(Object obj) {
		
		AdMenu parentMenu = null;
		AdMenu curMenu = null;
		
		// 菜单图标数组
		int[] drawables = { R.drawable.erp_kb, R.drawable.f, R.drawable.g,
				R.drawable.h, R.drawable.i, R.drawable.vv, R.drawable.f,
				R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.vv,
				R.drawable.f };
		
		if (obj instanceof AdMenu)
		{
			parentMenu = (AdMenu)obj;
		
			if (parentMenu != null)
			{
				
				menuItems.clear();
				//获得当前节点
				curMenu =parentMenu.findActivity(AdInfoManageFragment.class.getSimpleName());
				
				if (curMenu != null && curMenu.getChildren() != null)
				{
					List<AdMenu> menuList = curMenu.getChildren();
					
					for ( int i=0; i< menuList.size(); ++i)
					{
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("image", drawables[i]);
						map.put("item", menuList.get(i).getStrName());
						map.put("activity",menuList.get(i).getProgramURL());
						menuItems.add(map);
					}
					
					gridviewAdapter = new SimpleAdapter(getActivity(), menuItems,
							R.layout.grid_item, strfro, intto);
					gridView.setAdapter(gridviewAdapter);
					gridView.setOnItemClickListener(new GridViewOnTtemClickListener());
					
				}
			}
		}

		if (loading != null)
			loading.dismiss();
	}

	private void loadMenuThread(final String userNo) {
		loading = new LoadingDialog(this.getActivity());
		loading.show();

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					if (!appContext.isNetworkConnected()) {
						msg.what = MSG_NETWORK_EROOR;
					} else {
						if (appContext != null) {
							AdMenu admenu = appContext.getAdMenu(userNo);
							msg.what = MSG_GETDATA_SUCCESS;
							msg.obj = admenu;
						}
					}

				} catch (AppException e) {
					e.printStackTrace();
				}

				finally {
					mHandler.sendMessage(msg);
				}

			}
		}.start();
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (viewPager) {
				Log.i("wchtest",
						String.valueOf(currentPosition) + "%"
								+ String.valueOf(imageViews.size())
								+ "ScrollTask");
				currentPosition = currentPosition + 1;
				handler.obtainMessage().sendToTarget();
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentPosition);
		};
	};

}
