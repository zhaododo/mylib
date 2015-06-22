package com.android.dream.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.dream.app.R;
import com.android.dream.app.bean.News;

public class PaginationAdapter extends BaseAdapter {
	
	static class ListItemView{				//自定义控件集合  
        public TextView title;  
	    public TextView content;
    }  


	private Context 					context;//运行上下文
	private List<News> 					newsItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 

	public PaginationAdapter(Context context, List<News> data,int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context);
		this.newsItems = data;
		this.itemViewResource = resource;
	}

	@Override
	public int getCount() {
		return newsItems.size();
	}

	@Override
	public Object getItem(int position) {
		return newsItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		ListItemView  listItemView = null;
		
		if (view == null) {
			view = listContainer.inflate(this.itemViewResource, null);
			listItemView = new ListItemView();
			
			listItemView.title =(TextView) view.findViewById(R.id.newsTitle);
			listItemView.content = (TextView) view.findViewById(R.id.newsContent);
			
			view.setTag(listItemView);
		}
		else
		{
			listItemView = (ListItemView)view.getTag();
		}
		
		News news =newsItems.get(position);
		if (news != null)
		{
			listItemView.title.setText(news.getTitle());
			listItemView.content.setText(news.getContent());
		}

		return view;
	}

}
