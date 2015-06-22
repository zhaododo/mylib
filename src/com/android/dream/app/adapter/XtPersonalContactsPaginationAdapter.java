package com.android.dream.app.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdXTUser;

/**
 * @author zhaoj
 * 
 * 按拼音、姓名搜索个人号码适配器
 * 
 */
public class XtPersonalContactsPaginationAdapter extends BaseAdapter {
	private Context context;// 运行上下文
	private List<AdXTUser> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	
	// 自定义控件集合
	static class ListItemView {
		//头像
		public ImageView contacts_search_result_icon;
		//姓名
		public TextView contacts_search_result_username;
		//部门
		public TextView contacts_search_result_deppartmentname;
		//二级单位
		public TextView contacts_search_result_companyname;
		//手机号码号码
		public TextView contacts_search_result_mobile;
		//部门电话
		public TextView contacts_search_result_departmenttel;
		//拨号按钮
		public ImageView contacts_search_result_ring;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public XtPersonalContactsPaginationAdapter(Context context, List<AdXTUser> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		
		Log.i("zhaoj", "ErpBoardPaginationAdapter:"+data);
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * ListView Item设置
	 */
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// 自定义视图
		ListItemView listItemView = null;
		
		// 获得数据源
		AdXTUser erpUser = listItems.get(position);
		

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			listItemView.contacts_search_result_icon = (ImageView) convertView.findViewById(R.id.contacts_search_result_icon);
			listItemView.contacts_search_result_username = (TextView) convertView.findViewById(R.id.contacts_search_result_username);
			listItemView.contacts_search_result_deppartmentname = (TextView) convertView.findViewById(R.id.contacts_search_result_deppartmentname);
			listItemView.contacts_search_result_companyname = (TextView) convertView.findViewById(R.id.contacts_search_result_companyname);
			listItemView.contacts_search_result_mobile = (TextView) convertView.findViewById(R.id.contacts_search_result_mobile);
			listItemView.contacts_search_result_departmenttel = (TextView) convertView.findViewById(R.id.contacts_search_result_departmenttel);
			listItemView.contacts_search_result_ring = (ImageView) convertView.findViewById(R.id.contacts_search_result_ring);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		listItemView.contacts_search_result_icon.setVisibility(View.VISIBLE);
		listItemView.contacts_search_result_username.setText(erpUser.getUsername());
		// 设定隐藏参数，暂存到erp_board_title中
		listItemView.contacts_search_result_username.setTag(erpUser);
		listItemView.contacts_search_result_deppartmentname.setText(erpUser.getDeppartmentname());
		listItemView.contacts_search_result_companyname.setText(erpUser.getCompanyname());
		final String mobile = erpUser.getMobilephone();
		final String telephone = erpUser.getTelephone();
		
		listItemView.contacts_search_result_mobile.setText(mobile);
		listItemView.contacts_search_result_departmenttel.setText(telephone);
		
		if ("未公开".equals(mobile))
		{
			listItemView.contacts_search_result_mobile.setText("("+mobile+")");
		}
		
		if ("未公开".equals(telephone))
		{
			listItemView.contacts_search_result_departmenttel.setText("("+telephone+")");
		}
		
		listItemView.contacts_search_result_ring.setVisibility(View.VISIBLE);
		listItemView.contacts_search_result_ring.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (mobile != null && mobile.length() >0 && !mobile.equals("未公开"))
				{
					Intent intent = new Intent(
							"android.intent.action.CALL", Uri.parse("tel:"
									+ mobile));
					context.startActivity(intent);
				}
			}
		});
		
		return convertView;
	}
}