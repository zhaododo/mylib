package com.android.dream.app.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdReportLeftMenu;

/**
 * @author zhaoj
 * 
 * 生产快报左菜单适配器
 *
 */
public class ErpReportMenueAdapter extends BaseAdapter {

	private Context context;// 运行上下文
	private List<AdReportLeftMenu> items;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	 
	
	public ErpReportMenueAdapter(Context context, List<AdReportLeftMenu> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.items = data;
		
		Log.i("wch", "ErpReportPaginationAdapter:"+data);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 自定义视图
		ListItemView listItemView =new ListItemView();
		AdReportLeftMenu menuItem =items.get(position);
		if(convertView==null){
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			//listItemView =new ListItemView();
			listItemView.title = (TextView) convertView.findViewById(R.id.txt_sckb_menu);
			listItemView.arrow = (ImageView) convertView.findViewById(R.id.img_sckb_menu);
			listItemView.prLayout = (LinearLayout) convertView.findViewById(R.id.lin_menu_sckb);
			
			convertView.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
				
					case   MotionEvent.ACTION_UP: 
						Log.i("wch", "ACTION_UP:");
						((TextView)v.findViewById(R.id.txt_sckb_menu)).setTextColor(context.getResources().getColor(R.color.abs__background_holo_dark));
						return false;
					case  MotionEvent.ACTION_DOWN:
						Log.i("wch", "ACTION_DOWN:");
						((TextView)v.findViewById(R.id.txt_sckb_menu)).setTextColor(context.getResources().getColor(R.color.abs__background_holo_light));
						((LinearLayout) v.findViewById(R.id.lin_menu_sckb)).setBackgroundColor(context.getResources().getColor(R.color.col_usual_fun_press));
						((ImageView) v.findViewById(R.id.img_sckb_menu)).setImageResource(R.drawable.sckb_arrow_press);
						return false;
					default :
					((TextView)v.findViewById(R.id.txt_sckb_menu)).setTextColor(context.getResources().getColor(R.color.abs__background_holo_dark));
					  break;
					}
					Log.i("wch", "event:"+event.getAction());
					return false;
				}
			});
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		}
		else{
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.title.setText(menuItem.getName());
		return convertView;
	}
	
	static class ListItemView { // 自定义控件集合
		public TextView title;
		public ImageView arrow;
		public LinearLayout prLayout;
	}
}
