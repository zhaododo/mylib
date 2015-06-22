package com.android.dream.app.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.android.dream.app.R;
import com.android.dream.app.adapter.MESOrdEnterAdapter.Viewholder;
import com.android.dream.app.bean.AdMESOrdEnter;
import com.android.dream.app.bean.AdMESOrderProduct;

import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-26下午1:43:52
 */
public class MESOrdProductAdapter extends BaseAdapter{

	private Context context;
	private List<AdMESOrderProduct> data;
	private int resource;
	private LayoutInflater listContainer;// 视图容器
	private AdMESOrderProduct adErpOrdProduct;
	private MESOrderProInterace ordprointer;
	private String TAG = "wch";
	
	public MESOrdProductAdapter(Context context, List<AdMESOrderProduct> data,int resource,MESOrderProInterace ordprointer){
		this.context = context;
		this.resource = resource;
		this.data = data;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.ordprointer = ordprointer;
		Log.i(TAG, "ErpOrdEnterAdapter:"+data);
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		adErpOrdProduct = data.get(position);
		Viewholder viewholder = null;
		if (convertView == null) {
			convertView = listContainer.inflate(this.resource, null);
			viewholder = new Viewholder();
			viewholder.companyname = (TextView) convertView.findViewById(R.id.tv_companyname);
			viewholder.cusdeldate = (TextView) convertView.findViewById(R.id.tv_custdel_time);
			viewholder.boardno = (TextView) convertView.findViewById(R.id.tv_boardno);
			viewholder.ordertotal = (TextView) convertView.findViewById(R.id.tv_ordertotal);
			viewholder.orderown = (TextView) convertView.findViewById(R.id.tv_orderown);
			viewholder.steelmaking = (TextView) convertView.findViewById(R.id.tv_steelmaking);
			viewholder.steelrolling = (TextView) convertView.findViewById(R.id.tv_steelrolling);
			viewholder.finishing = (TextView) convertView.findViewById(R.id.tv_finishing);
			viewholder.lastdec = (TextView) convertView.findViewById(R.id.tv_lastdec);
			viewholder.unstorage = (TextView) convertView.findViewById(R.id.tv_unstorage);
			viewholder.instorage = (TextView) convertView.findViewById(R.id.tv_instorage);
			viewholder.onway = (TextView) convertView.findViewById(R.id.tv_onway);
			viewholder.waitdeliver = (TextView) convertView.findViewById(R.id.tv_waitdeliver);
			viewholder.finishdeliver = (TextView) convertView.findViewById(R.id.tv_finishdeliver);
			viewholder.finishpercent = (TextView) convertView.findViewById(R.id.tv_finishpercent);
			viewholder.ivbtn = (ImageView) convertView.findViewById(R.id.iv_btnclick);
			viewholder.table_product_item = (TableLayout) convertView.findViewById(R.id.table_product_item);
			viewholder.finishpercentmain = (TextView) convertView.findViewById(R.id.tv_finish_per);
			convertView.setTag(viewholder);
		}else{
			viewholder = (Viewholder) convertView.getTag();
		}
		viewholder.companyname.setText(adErpOrdProduct.getCusname().toString());
		viewholder.cusdeldate.setText("交货期:"+adErpOrdProduct.getCusdeldate().toString());
		viewholder.boardno.setText("船号:"+adErpOrdProduct.getBoardno().toString());
		viewholder.ordertotal.setText(adErpOrdProduct.getOrdertotal().toString());
		viewholder.orderown.setText(adErpOrdProduct.getOrderown().toString());
		viewholder.steelmaking.setText(adErpOrdProduct.getSteelmaking().toString());
		viewholder.steelrolling.setText(adErpOrdProduct.getSteelrolling().toString());
		viewholder.finishing.setText(adErpOrdProduct.getFinishing().toString());
		viewholder.lastdec.setText(adErpOrdProduct.getLastdec().toString());
		viewholder.unstorage.setText(adErpOrdProduct.getUnstorage().toString());
		viewholder.instorage.setText(adErpOrdProduct.getInstorage().toString());
		viewholder.onway.setText(adErpOrdProduct.getOnway().toString());
		viewholder.waitdeliver.setText(adErpOrdProduct.getWaitdeliver().toString());
		viewholder.finishdeliver.setText(adErpOrdProduct.getFinishdeliver().toString());
		viewholder.finishpercent.setText(adErpOrdProduct.getFinishpercent().toString()+"%");
		viewholder.finishpercentmain.setText("完成率:"+adErpOrdProduct.getFinishpercent().toString()+"%");
		if(adErpOrdProduct.getVisibity()==View.GONE)
		{
			viewholder.table_product_item.setVisibility(View.GONE);
			viewholder.ivbtn.setBackgroundResource(R.drawable.drawable_expand_close_orange);
		}
		else{
			viewholder.table_product_item.setVisibility(View.VISIBLE);
			viewholder.ivbtn.setBackgroundResource(R.drawable.drawable_expand_open_orange);
		}
		//判断报警功能，改变字体颜色
		int cusdeldate = Integer.parseInt(adErpOrdProduct.getCusdeldate());
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");  
		Calendar calendar = Calendar.getInstance();     
		calendar.setTime(new Date());  
		calendar.set(Calendar.DAY_OF_YEAR,calendar.get(Calendar.DAY_OF_YEAR) + 7);  
		int overdate = Integer.parseInt(df.format(calendar.getTime()));  
        if(cusdeldate < overdate){
        	if(adErpOrdProduct.getOrdertotal()>adErpOrdProduct.getFinishdeliver()){
        		viewholder.companyname.setTextColor(Color.RED);
        		viewholder.cusdeldate.setTextColor(Color.RED);
        		viewholder.boardno.setTextColor(Color.RED);
        		viewholder.finishpercentmain.setTextColor(Color.RED);
        	}
        	else{
        		viewholder.companyname.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
        		viewholder.cusdeldate.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
        		viewholder.boardno.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
        		viewholder.finishpercentmain.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
        	}
        	
        }else{
    		viewholder.companyname.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
    		viewholder.cusdeldate.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
    		viewholder.boardno.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
    		viewholder.finishpercentmain.setTextColor(context.getResources().getColor(R.color.mes_ord_pro_group));
    	}
		viewholder.ivbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ordprointer.updateStatus(data.get(position).getCusno(), data.get(position).getCusdeldate(),data.get(position).getVisibity());
				Log.i(TAG, "点击了按钮"+data.get(position).getVisibity()+"位置"+position+data.get(position).getCusno()+data.get(position).getCusdeldate());
			}
		});
		return convertView;
	}

	public class Viewholder{
		//客户名称
		public TextView companyname;
		//交货日期
		public TextView  cusdeldate;
		//船号
		public TextView boardno;
		//订单总量
		public TextView ordertotal;
		//订单欠量
		public TextView orderown;
		//炼钢
		public TextView steelmaking;
		//轧钢
		public TextView steelrolling;
		//精整
		public TextView finishing;
		//终判
		public TextView lastdec;
		//未入库 
		public TextView unstorage;
		//已入库
		public TextView instorage;
		//在途
		public TextView onway;
		//待发
		public TextView waitdeliver;
		//已完成
		public TextView finishdeliver;
		//完成率
		public TextView finishpercent;
		public ImageView ivbtn;
		public TableLayout table_product_item;
		//主档完成率
		public TextView finishpercentmain;
	}
	
}
