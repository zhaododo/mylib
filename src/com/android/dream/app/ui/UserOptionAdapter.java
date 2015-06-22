package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.android.dream.app.R;
import com.android.dream.app.bean.Person;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserOptionAdapter extends BaseAdapter{

	private List<Person> list = new ArrayList<Person>(); 
    private Activity activity = null; 
	private Handler handler;
	
	/**
	 * 自定义构造方法
	 * @param activity
	 * @param handler
	 * @param list
	 */
    public UserOptionAdapter(Activity activity,Handler handler,List<Person> personlist){
    	this.activity = activity;
    	this.handler = handler;
    	this.list = personlist;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null; 
        if (convertView == null) { 
            holder = new ViewHolder(); 
            //下拉项布局
            convertView = LayoutInflater.from(activity).inflate(R.layout.user_option_item, null); 
            holder.textView = (TextView) convertView.findViewById(R.id.item_text_u); 
            holder.imageView = (ImageView) convertView.findViewById(R.id.delImage); 
            
            convertView.setTag(holder); 
        } else { 
            holder = (ViewHolder) convertView.getTag(); 
        } 
        
        holder.textView.setText(list.get(position).getCode());
        
        //为下拉框选项文字部分设置事件，最终效果是点击将其文字填充到文本框
        holder.textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				Bundle data = new Bundle();
				//设置选中索引
				data.putInt("selIndex", position);
				msg.setData(data);
				msg.what = 101;
				//发出消息
				handler.sendMessage(msg);
			}
		});
        
        //为下拉框选项删除图标部分设置事件，最终效果是点击将该选项删除
        holder.imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				Bundle data = new Bundle();
				//设置删除索引
				data.putInt("delIndex", position);
				msg.setData(data);
				msg.what = 102;
				//发出消息
				handler.sendMessage(msg);
			}
		});
        
        return convertView; 
	}
	
	class ViewHolder { 
	    TextView textView; 
	    ImageView imageView; 
	} 
}
