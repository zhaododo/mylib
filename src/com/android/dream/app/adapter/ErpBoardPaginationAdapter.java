package com.android.dream.app.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdErpBoard;
import com.android.dream.app.bean.AdErpBoardFile;
import com.android.dream.dao.DBErpBoardMainDao;

/**
 * @author zhaoj
 * 
 * ERP公告适配器
 * 
 */
public class ErpBoardPaginationAdapter extends BaseAdapter {
	private Context context;// 运行上下文
	private List<AdErpBoard> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private DBErpBoardMainDao erpBoardMainDao;

	
	// 自定义控件集合
	static class ListItemView {
		//标题
		public TextView erp_board_title;
		//建立日期
		public TextView erp_board_createdate;
		//发布者名称
		public TextView erp_board_publisher_name;
		//公告分类名称
		public TextView erp_board_name;
		//新（未阅读）
		public ImageView erp_board_newflag;
		//附件
		public ImageView erp_board_annexflag;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ErpBoardPaginationAdapter(Context context, List<AdErpBoard> data,
			int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		erpBoardMainDao = new DBErpBoardMainDao(context);
		
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
		AdErpBoard erpBoard = listItems.get(position);

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			listItemView.erp_board_title = (TextView) convertView.findViewById(R.id.ad_erp_board_title);
			listItemView.erp_board_createdate = (TextView) convertView.findViewById(R.id.ad_erp_board_createdate);
			listItemView.erp_board_publisher_name = (TextView) convertView.findViewById(R.id.ad_erp_board_publisher_name);
			listItemView.erp_board_name = (TextView) convertView.findViewById(R.id.ad_erp_board_name);
			listItemView.erp_board_newflag = (ImageView) convertView.findViewById(R.id.ad_erp_board_newflag);
			listItemView.erp_board_annexflag = (ImageView) convertView.findViewById(R.id.ad_erp_board_annexflag);

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		
		listItemView.erp_board_title.setText(erpBoard.getTitle());
		// 设定隐藏参数，暂存到erp_board_title中
		listItemView.erp_board_title.setTag(erpBoard);
		listItemView.erp_board_createdate.setText(erpBoard.getCreateDate());
		listItemView.erp_board_publisher_name.setText(erpBoard.getPublisherName());
		listItemView.erp_board_newflag.setVisibility(View.VISIBLE);
		
		
		//判断文件是否已阅读，如果阅读了则影藏new图片
		Boolean isexit = erpBoardMainDao.isexit(erpBoard.getMsgid());
		if(isexit){
			if (erpBoard != null)
			{
				erpBoard.setHasReaded(true);
			}
		}
		
		if (erpBoard.isHasFile())
		{
			listItemView.erp_board_annexflag.setVisibility(View.VISIBLE);
		}
		else
		{
			listItemView.erp_board_annexflag.setVisibility(View.INVISIBLE);
		}
		
		String erp_board_name = erpBoard.getBoardName();
		
		if(erpBoard.isHasReaded()){
			listItemView.erp_board_newflag.setVisibility(View.INVISIBLE);
			listItemView.erp_board_name.setText(erp_board_name);
		}
		else
		{
			if(erp_board_name.length()<=5){
				listItemView.erp_board_name.setText(erp_board_name);
			}else{
				listItemView.erp_board_name.setText(erp_board_name.substring(0, 4)+"...");
			}
		}
		return convertView;
	}
	
}