package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.adapter.ErpReportMenueAdapter;
import com.android.dream.app.bean.AdReportLeftMenu;

/**
 * @author zhaoj
 * 快报的菜单显示列表
 *
 */
public class ErpReportLeftFragment extends Fragment{
	private View view;
	private ListView ad_left_menu_list_view;
	private List<AdReportLeftMenu> menus;
	private ErpReportMenueAdapter menuAdapter;
	
	private final static int MSG_OK = 0x01;
	private final static int MSG_ERROR = -1;
	private AppContext mContext = null;
	private Handler mHandler;
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.product_left_con, null);
		ad_left_menu_list_view =(ListView)view.findViewById(R.id.ad_left_menu_list_view);
		menus = new ArrayList<AdReportLeftMenu>();
		
//		//定义左菜单选项
//		AdReportLeftMenu menu = new AdReportLeftMenu();
//		menu.setNo("T00001");
//		menu.setName("生产");
//		menus.add(menu);
//		
//		menu = new AdReportLeftMenu();
//		menu.setNo("A1");
//		menu.setName("成本");
//		menus.add(menu);
//		
		
		menuAdapter =new ErpReportMenueAdapter(getActivity(),menus, R.layout.erp_sckb_menu);
		ad_left_menu_list_view.setAdapter(menuAdapter);
		ad_left_menu_list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View viewitem, int position,
					long arg3) {
				
				((TextView)viewitem.findViewById(R.id.txt_sckb_menu)).setTextColor(getResources().getColor(R.color.abs__background_holo_dark));
				((LinearLayout)viewitem.findViewById(R.id.lin_menu_sckb)).setBackgroundColor(getResources().getColor(R.color.col_list_sckb_normal));
				((ImageView) viewitem.findViewById(R.id.img_sckb_menu)).setImageResource(R.drawable.sckb_arrow);
				
				if (position >=0 && position < menus.size())
				{
					switchReport(menus.get(position));
				}
				
			}

			
		});
		
		mHandler = getHandler();
		
		return view;
	
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getErpLeftMenus();
	}
	
	private Handler getHandler()
	{
		return new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				
				if (msg.what == MSG_OK)
				{
					List<AdReportLeftMenu> list = (List<AdReportLeftMenu>)msg.obj;
					if (list != null)
					{
						menus.addAll(list);
						menuAdapter.notifyDataSetChanged();
					}
					
				}
				
			}
		};
	}
	
	private void getErpLeftMenus()
	{
		if (getActivity()!=null)
		{
			mContext = (AppContext)getActivity().getApplication();
			
			new Thread(new LeftMenuRunnable()).start();
			
		}
		
	}
	class LeftMenuRunnable implements Runnable{

		@Override
		public void run() {
			
			if (mContext != null)
			{
				List<AdReportLeftMenu> list = null;
				
				try {
					list = mContext.adGetReportLeftMenus(mContext.getCurrentUser(),ErpReportLeftFragment.class.getSimpleName());
					Message msg = new Message();
					msg.what = MSG_OK;
					msg.obj = list;
					if (mHandler != null)
					{
						mHandler.sendMessage(msg);
					}
					
				} catch (AppException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	 
	private void switchReport(AdReportLeftMenu menuItem) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof ErpReportActivity) {
			ErpReportActivity fmainac = (ErpReportActivity) getActivity();
			fmainac.onItemSelected(menuItem);
		} 
	}
}
