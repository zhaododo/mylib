package com.android.dream.app.adapter;

import java.util.List;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdMESOrdEnter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-18下午4:21:21
 */
public class MESOrdEnterAdapter extends BaseAdapter {

	private Context context;
	private List<AdMESOrdEnter> data;
	private int resource;
	private LayoutInflater listContainer;// 视图容器
	private AdMESOrdEnter adErpOrdEnter;
	private String TAG = "wch";
	
	public MESOrdEnterAdapter(Context context, List<AdMESOrdEnter> data,int resource){
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.resource = resource;
		this.data = data;
		
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
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		adErpOrdEnter = data.get(position);
		Viewholder viewholder = null;
		if (convertView == null) {
			convertView = listContainer.inflate(resource, null);
			viewholder = new Viewholder();
			viewholder.companyid = (TextView) convertView.findViewById(R.id.tv_companyid);
			viewholder.companyname = (TextView) convertView.findViewById(R.id.tv_companyname);
			viewholder.ordnum = (TextView) convertView.findViewById(R.id.tv_ordnum);
			convertView.setTag(viewholder);
		}else{
			viewholder = (Viewholder) convertView.getTag();
		}
		viewholder.companyid.setText(adErpOrdEnter.getConpanyid());
		viewholder.companyname.setText(adErpOrdEnter.getConpanyname());
		viewholder.ordnum.setText(adErpOrdEnter.getEnterno().toString());
		return convertView;
	}
	
	public class Viewholder{
		//公司编号
		public TextView companyid;
		//公司名称
		public TextView companyname;
		//当月入单量
		public TextView ordnum;
	}

}
