package com.android.dream.app.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdErpReport;
import com.android.dream.app.bean.AdErpReportRow;
import com.android.dream.app.bean.AdErpReportRowItem;
import com.android.dream.app.util.StringUtils;

/**
 * @author zhaoj
 * 
 */
public class ErpReportPaginationAdapter extends BaseAdapter {
	private Context context;// 运行上下文
	private List<AdErpReport> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private int itemViewResource;// 自定义项视图源
	private ErpReportOpenChartInterface erprepinter;

	static class ListItemView { // 自定义控件集合
		public TextView title;
		public ImageView flag;
		public LinearLayout prLayout;
		public ImageView dropDown;
	}

	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ErpReportPaginationAdapter(Context context, List<AdErpReport> data,
			int resource,ErpReportOpenChartInterface erprepinter) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
		this.erprepinter = erprepinter;
		
		Log.i("zhaoj", "ErpReportPaginationAdapter:"+data);
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
		// Log.d("method", "getView");

		
		
		// 自定义视图
		ListItemView listItemView = null;

		// 获得数据源
		AdErpReport report = listItems.get(position);
		

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.title = (TextView) convertView
					.findViewById(R.id.report_listitem_title);
			listItemView.flag = (ImageView) convertView
					.findViewById(R.id.report_listitem_flag);
			listItemView.dropDown = (ImageView) convertView.findViewById(R.id.prArrow);
			// 明细控件
			listItemView.prLayout = (LinearLayout) convertView
					.findViewById(R.id.productionreport_detail);
			
			Log.i("zhaoj", "getView ListView is null:postion:"+position);
			Log.i("zhaoj", "report:"+report);
			Log.i("zhaoj", "getRowItems:"+report.getRowItems());
			//创建reportRow数据
			createReportRowLayout(report,position,listItemView.prLayout);
			

			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			Log.i("zhaoj", "getView getTag:postion:"+position);
			//listItemView = (ListItemView) convertView.getTag();
			convertView.setTag(null);
			
			/**
			 *  以下代码为测试代码，以后需要删除
			 *  ######################start#####################################
			 */
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			// 获取控件对象
			listItemView.title = (TextView) convertView
					.findViewById(R.id.report_listitem_title);
			listItemView.flag = (ImageView) convertView
					.findViewById(R.id.report_listitem_flag);
			listItemView.dropDown = (ImageView) convertView.findViewById(R.id.prArrow);
			// 明细控件
			listItemView.prLayout = (LinearLayout) convertView
					.findViewById(R.id.productionreport_detail);
			
			Log.i("zhaoj", "getView ListView is null:postion:"+position);
			Log.i("zhaoj", "report:"+report);
			Log.i("zhaoj", "getRowItems:"+report.getRowItems());
			//创建reportRow数据
			createReportRowLayout(report,position,listItemView.prLayout);
			
			/**
			 *  ######################end#####################################
			 */
		}

		listItemView.title.setText(report.getReportTitle());
		listItemView.title.setTag(report);// 设置隐藏参数(实体类)
		listItemView.prLayout.setVisibility(View.VISIBLE);

		if (StringUtils.isToday(report.getReportDate())){
			listItemView.flag.setVisibility(View.VISIBLE);
			listItemView.dropDown.setBackgroundResource(R.drawable.pr_detail_introduce_arrow_up);
		
		}else{
			listItemView.flag.setVisibility(View.GONE);
			listItemView.dropDown.setImageResource(R.drawable.pr_detail_introduce_arrow_down);
		}
			

		return convertView;
	}
	
	
	/**
	 * 创建reportRow布局及数据
	 * @param erpReport
	 * @param reportLayout
	 */
	public void createReportRowLayout(AdErpReport erpReport,final int position,LinearLayout reportLayout)
	{
		List<AdErpReportRow> rpRowList = erpReport.getRowItems();
		
		LinearLayout rowLayout =null;
		for (int i = 0;i<rpRowList.size();i++) {
			final AdErpReportRow reportRow = rpRowList.get(i);
			if (reportRow.getItems() != null  && reportRow.getItems().size() >0)
			{
				rowLayout = new LinearLayout(context);
				LinearLayout.LayoutParams llpmain = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

				rowLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				rowLayout.setLayoutParams(llpmain);
				rowLayout.setOrientation(LinearLayout.VERTICAL);
//				rowLayout.setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						erprepinter.getSelectData(reportRow,null);
//					}
//				});

				LinearLayout titleLayout = new LinearLayout(context);
				
				LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				
				titleLayout.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				titleLayout.setLayoutParams(titleLayoutParams);
				titleLayout.setOrientation(LinearLayout.HORIZONTAL);
				
				//title旁的橙色竖线
				View titleVertical = new View(context); 
				titleVertical.setBackgroundResource(R.drawable.xzcxt);
				LayoutParams titleVerticalLayoutParams = new LayoutParams(
						10, 50);
				titleVerticalLayoutParams.setMargins(25, 5, 15, 5);
				titleVertical.setLayoutParams(titleVerticalLayoutParams);
				
				
				//文字信息
				LayoutParams rowTextLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				rowTextLayoutParams.setMargins(10, 10, 15, 10);
				TextView tvtitle = new TextView(context);
				tvtitle.setGravity(Gravity.LEFT);
				tvtitle.setLayoutParams(rowTextLayoutParams);
				tvtitle.setText(reportRow.getReportName());
				
				
				//总分割线view
				View dividingLineMain = new View(context);
				dividingLineMain.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams divLineLayoutParamsMain = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				divLineLayoutParamsMain.setMargins(0, 0, 0, 0);
				dividingLineMain.setLayoutParams(divLineLayoutParamsMain);
				
				//分割线view
				View dividingLine = new View(context);
				dividingLine.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams divLineLayoutParams = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				divLineLayoutParams.setMargins(25, 0, 25, 0);
				dividingLine.setLayoutParams(divLineLayoutParams);
				
				
				titleLayout.addView(titleVertical);
				titleLayout.addView(tvtitle);
				rowLayout.addView(titleLayout);
				rowLayout.addView(dividingLine);
				
				//创建reportRowItem数据
				createReportRowItemLayout(reportRow,rowLayout);
				
				reportLayout.addView(dividingLineMain);
				reportLayout.addView(rowLayout);
			}
		}
	}
	
	
	
	/**
	 * 创建reportRowItem布局及数据
	 * @param reportRow
	 * @param rowLayout
	 */
	public void createReportRowItemLayout(final AdErpReportRow reportRow,LinearLayout rowLayout)
	{
		List<AdErpReportRowItem> rpRowItemList = reportRow.getItems();
		
		LinearLayout rowItemLayout=null;
		
		for (final AdErpReportRowItem reportRowItem : rpRowItemList) {
			
			
			rowItemLayout = new LinearLayout(context);
			
			LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			llp.setMargins(100, 10, 15, 10);

			rowItemLayout.setGravity(Gravity.LEFT);
			rowItemLayout.setLayoutParams(llp);
			rowItemLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowItemLayout.setBackgroundResource(R.drawable.erp_sckb_content);
			rowItemLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					erprepinter.getSelectData(reportRow,reportRowItem);
				}
			});
			LinearLayout.LayoutParams t1lp = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.WRAP_CONTENT, 1);
			t1lp.setMargins(45, 10, 15, 10);

			TextView tv1 = new TextView(context);
			tv1.setText(reportRowItem.getTargetName()+StringUtils.unitFormat(reportRowItem.getUnit()));

			LayoutParams t2lp = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1);
			t2lp.setMargins(45, 10, 15, 10);
			TextView tv2 = new TextView(context);
			Resources res = context.getResources();
			int orange = res.getColor(R.color.orange);
			tv2.setTextColor(orange);
			tv2.setGravity(Gravity.RIGHT);
			tv2.setLayoutParams(t2lp);
			tv2.setText(reportRowItem.getTargetValue());

			rowItemLayout.addView(tv1);
			rowItemLayout.addView(tv2);
			rowLayout.addView(rowItemLayout);
			
		}
	}	
}