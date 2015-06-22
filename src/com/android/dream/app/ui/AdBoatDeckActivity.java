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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdUserPermissionTemp;

/**
 * @author zhaoj
 * 船板定制配送界面
 *
 */
public class AdBoatDeckActivity extends BaseActivity{

	private ViewPager viewPager;
	//广告对应的图像资源
	private int[] adImageResources;
	private List<View> dots;
	//广告显示数组
	private List<ImageView> adImageViews;
	
	//功能列表
	private ArrayList<HashMap<String, Object>> listItems;
	//功能列表对应图片
	private int[] drawables;
	//GridView布局适配器
	private SimpleAdapter  gridviewAdapter;
	private GridView gridView;
	private String[] strfro;
	private int[]  intto;
	//GridView图片对应的文字
	private String[] item_info_array;
	
	private AppContext appContext;

	//权限列表
	private List<AdUserPermissionTemp> userPermissionTemplis = null;
	//标题
	private TextView tv_title;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_boat_main_layout);
		appContext = (AppContext)getApplication();
		//广告图片数组
		adImageResources = new int[] { R.drawable.boat_deck_banner, R.drawable.ad_1};
		adImageViews = new ArrayList<ImageView>();
		for (int i = 0; i < 1; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setImageResource(adImageResources[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			adImageViews.add(imageView);
		}
		
//		dots = new ArrayList<View>();
//		dots.add(findViewById(R.id.v_dot0));
//		dots.add(findViewById(R.id.v_dot1));
//		dots.add(findViewById(R.id.v_dot2));
		
		viewPager = (ViewPager)findViewById(R.id.vp);
		viewPager.setAdapter(new AdAdapter());
		//viewPager.setOnPageChangeListener(new MyPageChangeListener());
		viewPager.setOffscreenPageLimit(0);
		
		//GridView对象
		gridView=(GridView)findViewById(R.id.grd_items);
		
		//功能列表初始化
		listItems = new ArrayList<HashMap<String, Object>>();
		
		item_info_array = getResources().getStringArray(R.array.ad_boat_deck_info);
		
		//船板定制配送（图标数组）
		drawables = new int[]{
	    		//入单跟踪
	    		R.drawable.ad_mes_ordenter,
				//进程跟踪
	    		R.drawable.ad_mes_ordproduct,
	    		//工序跟踪
				R.drawable.ad_mes_ordprocess};
	   
		//SimpleAdapter初始化
		strfro=new String[]{"image","item"};
		intto=new int[]{R.id.item_imageView,R.id.item_textView};
		gridviewAdapter =new SimpleAdapter(this, listItems, R.layout.grid_item, strfro, intto);
		gridView.setAdapter(gridviewAdapter);
		gridView.setOnItemClickListener(new GridViewItemClickListener());
		
		ImageView imageButton = (ImageView) findViewById(R.id.board_detail_title_left_btn);
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				AdBoatDeckActivity.this.finish();
			}
		});
		
		tv_title = (TextView)findViewById(R.id.txt_sckb_title);
		tv_title.setText(R.string.boat_deck_name);
		
		new Thread(new LoadUserPermission()).start();
	}
    
    
  //处理权限
  	private Handler handlerpermision = new Handler() {
  		public void handleMessage(android.os.Message msg) {
  			if(msg.what == 100 && userPermissionTemplis !=null){
  				for(int i =0;i<userPermissionTemplis.size();i++){
  					//如果在权限列表内则添加显示
  					if(userPermissionTemplis.get(i).getStrPermission().equals("MESOrdProcessTraceActivity")
  							&&userPermissionTemplis.get(i).getStraction().equals("view")){
  						HashMap<String, Object> map = new HashMap<String, Object>();   
  				        map.put("image", drawables[2]);   
  				        map.put("item", item_info_array[2]);
  				        if(!listItems.contains(map)){
  				        	listItems.add(2,map);
  				        }
  					}
  					if(userPermissionTemplis.get(i).getStrPermission().equals("MESOrderProductActivity")
  							&&userPermissionTemplis.get(i).getStraction().equals("view")){
  						HashMap<String, Object> map = new HashMap<String, Object>();   
  				        map.put("image", drawables[1]);   
  				        map.put("item", item_info_array[1]);
  				        if(!listItems.contains(map)){
  				        	listItems.add(1,map);
  				        }
  					}
  					if(userPermissionTemplis.get(i).getStrPermission().equals("MESOrderEnterActivity")
  							&&userPermissionTemplis.get(i).getStraction().equals("view")){
  						HashMap<String, Object> map = new HashMap<String, Object>();   
  				        map.put("image", drawables[0]);   
  				        map.put("item", item_info_array[0]);
  				        //防止多个角色存在权限重叠，页面加载多次
  				        if(!listItems.contains(map)){
  				        	listItems.add(0,map);
  				        }
  					}
  				}
  				if (gridviewAdapter != null)
  				{
  					gridviewAdapter.notifyDataSetChanged();
  				}
  				
  			}
  			
  		};
  	};

    
    
	/**
	 * @author zhaoj
	 * 用户权限列表（获得用户的所有权限列表。以后需改造为：获得当前activity下的权限列表）
	 */
	private class LoadUserPermission implements Runnable {
		@Override
		public void run() {
			Message msg = new Message();
			try {
				if (appContext != null) {
					userPermissionTemplis = appContext
							.getUserPermision(appContext.getCurrentUser());
					msg.what = 100;
				}

			} catch (AppException e) {
				msg.what = -1;
			}
			handlerpermision.sendMessage(msg);
		}}

	/**
	 * @author zhaoj
	 * 广告条适配器，用于初始化
	 */
	private class AdAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}
		
		@Override
		public Object instantiateItem(View view, final int position) {
			try {
				//初始化
				((ViewPager) view).addView(adImageViews.get(position%adImageViews.size()),0);
				if (position == 0) {
					view.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							
								Uri uri = Uri.parse("http://ilife.nisco.cn");
								Intent intent = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
						}
					});
				}
				
				

			} catch (Exception e) {
			}
			
			return adImageViews.get(0);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
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
	
	/**
	 * @author zhaoj
	 * 点击GridView的Click事件
	 *
	 */
	public class GridViewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			switch (position) {
			case 0:
				Intent erpOrderEnterIntent=new Intent(AdBoatDeckActivity.this,MESOrderEnterActivity.class);
				startActivity(erpOrderEnterIntent);
				break;
			case 1:
				Intent erpOrderProductIntent=new Intent(AdBoatDeckActivity.this,MESOrderProductActivity.class);
				startActivity(erpOrderProductIntent);
				break;
			case 2:
				Intent erpOrderProcessIntent=new Intent(AdBoatDeckActivity.this,MESOrdProcessTraceActivity.class);
				startActivity(erpOrderProcessIntent);
				break;

			}
		}
	}
	
	
	
}
