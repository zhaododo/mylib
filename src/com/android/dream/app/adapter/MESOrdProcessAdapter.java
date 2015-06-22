package com.android.dream.app.adapter;

import java.util.List;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdMESOrdProcessTrace;
import com.android.dream.app.bean.AdMESOrdProcessTraceItem;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MESOrdProcessAdapter extends BaseAdapter{

	private Context context;
	private List<AdMESOrdProcessTrace> data;
	private int resource;
	private LayoutInflater listContainer;// 视图容器
	private AdMESOrdProcessTrace mesOrdProcessTrace;
	private MESOrderProInterace ordprointer;
	private String TAG = "wch";
	private View convertchildView;
	
	public MESOrdProcessAdapter(Context context, List<AdMESOrdProcessTrace> data,int resource,MESOrderProInterace ordprointer){
		this.context = context;
		this.resource = resource;
		this.data = data;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.ordprointer = ordprointer;
		Log.i(TAG, "ErpOrdEnterAdapter:"+data);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		mesOrdProcessTrace = data.get(position);
		Viewholder viewholder = null;
		if(convertView == null){
			viewholder = new Viewholder();
			convertView = listContainer.inflate(resource, null);
			viewholder.orderinfo = (TextView) convertView.findViewById(R.id.tv_process_orderno);
			viewholder.ordertotal = (TextView) convertView.findViewById(R.id.tv_process_ordtotal);
			viewholder.custno = (TextView) convertView.findViewById(R.id.tv_ordpro_custno);
			viewholder.stdspec = (TextView) convertView.findViewById(R.id.tv_ordpro_stdspec);
			viewholder.prodsize = (TextView) convertView.findViewById(R.id.tv_ordpro_size);
			viewholder.ivbtn = (ImageView) convertView.findViewById(R.id.iv_process_click);
			viewholder.table_process_item = (LinearLayout) convertView.findViewById(R.id.ll_processhiden);
			convertView.setTag(viewholder);
		}else{
			viewholder = (Viewholder) convertView.getTag();
		}
		viewholder.orderinfo.setText("("+mesOrdProcessTrace.getOrd_no()+"-"+mesOrdProcessTrace.getOrd_item()+")");
		viewholder.ordertotal.setText(mesOrdProcessTrace.getOrd_wgt());
		viewholder.custno.setText(mesOrdProcessTrace.getCust_no());
		viewholder.stdspec.setText("产品:"+mesOrdProcessTrace.getStdspec());
		viewholder.prodsize.setText(mesOrdProcessTrace.getOrd_size());
		if(mesOrdProcessTrace.getVisibity() == View.VISIBLE){
			viewholder.table_process_item.setVisibility(View.VISIBLE);
			viewholder.ivbtn.setBackgroundResource(R.drawable.drawable_expand_open_orange);
		}else{
			viewholder.table_process_item.setVisibility(View.GONE);
			viewholder.ivbtn.setBackgroundResource(R.drawable.drawable_expand_close_orange);
		}
		viewholder.table_process_item.removeAllViewsInLayout();
//		动态创建工序内容
		List<AdMESOrdProcessTraceItem> mesOrdProcessTraceItem = mesOrdProcessTrace.getMesOrdProcessTraceItemlis();
		Log.i("size", String.valueOf(mesOrdProcessTraceItem.size()));
		for(int i =0;i<mesOrdProcessTraceItem.size();i++){
			convertchildView = listContainer.inflate(R.layout.ad_mes_ordprocess_item_detail, null);
			TextView tv_process_name= (TextView)convertchildView.findViewById(R.id.tv_process_name);
			tv_process_name.setText(mesOrdProcessTraceItem.get(i).getPro_name());
			TextView tv_process_endtime= (TextView)convertchildView.findViewById(R.id.tv_process_endtime);
			tv_process_endtime.setText(mesOrdProcessTraceItem.get(i).getEnd_date());
			TextView tv_fin_weight= (TextView)convertchildView.findViewById(R.id.tv_fin_weight);
			tv_fin_weight.setText(mesOrdProcessTraceItem.get(i).getFin_weight());
			TextView tv_bef_weight= (TextView)convertchildView.findViewById(R.id.tv_bef_weight);
			tv_bef_weight.setText(mesOrdProcessTraceItem.get(i).getBef_weight());
			TextView tv_aft_weight= (TextView)convertchildView.findViewById(R.id.tv_aft_weight);
			tv_aft_weight.setText(mesOrdProcessTraceItem.get(i).getAft_weight());
			viewholder.table_process_item.addView(convertchildView);
		}
		
		viewholder.ivbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ordprointer.updateStatus(data.get(position).getOrd_no(), data.get(position).getOrd_item(),data.get(position).getVisibity());
				Log.i(TAG, "点击了按钮"+data.get(position).getVisibity()+"位置"+position+data.get(position).getOrd_no()+data.get(position).getOrd_item());
			}
		});
		return convertView;
	}
	
	public class Viewholder{
		//订单号
		public TextView orderinfo;
		//客户代码
		public TextView  custno;
		//尺寸
		public TextView prodsize;
		//标准
		public TextView stdspec;
		//订单总量
		public TextView ordertotal;
//	            展开按钮
		public ImageView ivbtn;
//		影藏的布局
		public LinearLayout table_process_item;
	}

}
