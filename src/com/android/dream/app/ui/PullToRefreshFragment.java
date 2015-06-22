package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.adapter.NewsPaginationAdapter;
import com.android.dream.app.bean.News;
import com.android.dream.app.bean.NewsList;
import com.android.dream.app.util.StringUtils;
import com.android.dream.app.widget.PullToRefreshListView;
import com.android.dream.app.widget.PullToRefreshListView.OnRefreshListener;

public class PullToRefreshFragment extends Fragment{
	
	private NewsPaginationAdapter pull_adapter;
	private View pull_footer;
	private TextView pull_foot_more;
	private ProgressBar pull_foot_progress;
	private PullToRefreshListView pull_listView;
	private Handler pull_handler = null;
	private int pull_NewsSumData = 0;
	public final static  String TAG="PullToRefreshFragment";
	private int curNewsCatalog = NewsList.CATALOG_ALL;

	
	private List<News> lvNewsData = new ArrayList<News>();
	private AppContext appContext;//全局Context
	
	private View rootview;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (rootview  == null)
		{
			rootview=inflater.inflate(R.layout.pull_list_content, container, false);
			
			appContext = (AppContext)getActivity().getApplication();
			
			pull_footer = inflater.inflate(R.layout.listview_footer,null);
			
			pull_foot_more = (TextView)pull_footer.findViewById(R.id.listview_foot_more);
			pull_foot_progress = (ProgressBar)pull_footer.findViewById(R.id.listview_foot_progress);
			
			pull_listView = (PullToRefreshListView) rootview.findViewById(R.id.pull_content_listview);
			pull_listView.addFooterView(pull_footer);

			pull_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(position == 0 || view == pull_footer) return;
	        		
	        		News news = null;        		
	        		//判断是否是TextView
	        		if(view instanceof TextView){
	        			news = (News)view.getTag();
	        		}else{
	        			TextView tv = (TextView)view.findViewById(R.id.news_listitem_title);
	        			news = (News)tv.getTag();
	        		}
	        		if(news == null) return;
	        		
	        		Log.i(TAG, "showNewsRedirect");
					
				}
			});
			
			pull_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					pull_listView.onScrollStateChanged(view, scrollState);
					
					//数据为空--不用继续下面代码了
					if(lvNewsData.isEmpty()) return;
					
					//判断是否滚动到底部
					boolean scrollEnd = false;
					try {
						if(view.getPositionForView(pull_footer) == view.getLastVisiblePosition())
							scrollEnd = true;
					} catch (Exception e) {
						scrollEnd = false;
					}
					
					int lvDataState = StringUtils.toInt(pull_listView.getTag());
					if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
					{
						pull_listView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						pull_foot_more.setText(R.string.load_ing);
						pull_foot_progress.setVisibility(View.VISIBLE);
						//当前pageIndex
						int pageIndex = pull_NewsSumData/UIHelper.PAGE_SIZE;
						loadLvNewsData(curNewsCatalog,pageIndex, pull_handler, UIHelper.LISTVIEW_ACTION_SCROLL);
					}
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					pull_listView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
					
				}
			});
			
			pull_listView.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					
					loadLvNewsData(curNewsCatalog,0, pull_handler, UIHelper.LISTVIEW_ACTION_REFRESH);
					
				}
			});
			
			pull_adapter = new NewsPaginationAdapter(getActivity(),lvNewsData,R.layout.news_listitem);	
			pull_listView.setAdapter(pull_adapter);
			initFrameListViewData();
		}
		
		ViewGroup parent = (ViewGroup) rootview.getParent();   
		if (parent != null) {   
		    parent.removeView(rootview);   
		}  
		
		return rootview;
	}
	
	
	
	private void loadLvNewsData(final int catalog,final int pageIndex,final Handler handler,final int action){
		pull_foot_progress.setVisibility(ProgressBar.VISIBLE);
		new Thread(){
			public void run() {				
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					
					if (action == UIHelper.LISTVIEW_ACTION_REFRESH)
					{
						Log.i(TAG, "action == UIHelper.LISTVIEW_ACTION_REFRESH");
					}
					else if (action == UIHelper.LISTVIEW_ACTION_SCROLL)
					{
						Log.i(TAG, "action == UIHelper.LISTVIEW_ACTION_SCROLL");
					}
					isRefresh = true;
				try {
					NewsList list = appContext.getNewsList(catalog, pageIndex, isRefresh);				
					msg.what = list.getPageSize();
					msg.obj = list;
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
                handler.sendMessage(msg);
			}
		}.start();
		
	} 
	
	private void initFrameListViewData() {

		pull_handler= this.getLvHandler(pull_listView, pull_adapter, pull_foot_more, pull_foot_progress, UIHelper.PAGE_SIZE);
		
		// 加载资讯数据
		if (pull_adapter.isEmpty()) {
			loadLvNewsData(curNewsCatalog,0, pull_handler,
					UIHelper.LISTVIEW_ACTION_INIT);
		}
	}

	public  static class NewsFactory
	{
		public final static int TOTAL = 54; 
		public final static int PAGE_SIZE = 20; 
		public List<News> getNewsList(final int pageIndex,boolean isRefresh)
		{
			
			List<News> list = new ArrayList<News>();
			int pages = TOTAL/PAGE_SIZE;
			if (TOTAL%PAGE_SIZE != 0)
			{
				pages++;
			}
			int start = pageIndex * PAGE_SIZE;
			int end =start +PAGE_SIZE;
			
			if (pageIndex == pages -1)
			{
				end = TOTAL;
			}
			
			while(start < end)
			{
				News item = new News();
				item.setTitle("Title"+start);
				item.setContent("This is News Content"+start);
				list.add(item);
				++start;
			}
			return list;
		}
	}
	
	private Handler getLvHandler(final PullToRefreshListView lv,final BaseAdapter adapter,final TextView more,final ProgressBar progress,final int pageSize){
    	return new Handler(){
			public void handleMessage(Message msg) {
				
				handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
				if(msg.what >= 0){
					if(msg.what < pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					}else if(msg.what == pageSize){
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
				}
				else if(msg.what == -1){
					//有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
				}
				if(adapter.getCount()==0){
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					Log.i(TAG, "pull_to_refresh_update:"+getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					lv.setSelection(0);
				}else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG){
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
    }
	
	
	 private  void handleLvData(int what,Object obj,int objtype,int actiontype){
			switch (actiontype) {
				case UIHelper.LISTVIEW_ACTION_INIT:
				case UIHelper.LISTVIEW_ACTION_REFRESH:
				case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
					int newdata = 0;//新加载数据-只有刷新动作才会使用到
					switch (objtype) {
						case UIHelper.LISTVIEW_DATATYPE_NEWS:
							Log.i(TAG, "LISTVIEW_ACTION_CHANGE_CATALOG:"+"objtype:UIHelper.LISTVIEW_DATATYPE_NEWS");

							NewsList nlist = (NewsList)obj;
							pull_NewsSumData = what;
							
							if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
								if(lvNewsData.size() > 0){
									for(News news1 : nlist.getNewslist()){
										boolean b = false;
										for(News news2 : lvNewsData){
											if(news1.getId() == news2.getId()){
												b = true;
												break;
											}
										}
										if(!b) newdata++;
									}
								}else{
									newdata = what;
								}
							}
							lvNewsData.clear();//先清除原有数据
							lvNewsData.addAll(nlist.getNewslist());
							break;
					}
					if(actiontype == UIHelper.LISTVIEW_ACTION_REFRESH){
						
					}
					break;
				case UIHelper.LISTVIEW_ACTION_SCROLL:
					switch (objtype) {
						case UIHelper.LISTVIEW_DATATYPE_NEWS:
							
							Log.i(TAG, "LISTVIEW_ACTION_SCROLL:"+"objtype:UIHelper.LISTVIEW_DATATYPE_NEWS");

							NewsList list = (NewsList)obj;
							pull_NewsSumData += what;
							if(lvNewsData.size() > 0){
								for(News news1 : list.getNewslist()){
									boolean b = false;
									for(News news2 : lvNewsData){
										if(news1.getId() == news2.getId()){
											b = true;
											break;
										}
									}
									if(!b) lvNewsData.add(news1);
								}
							}else{
								lvNewsData.addAll(list.getNewslist());
							}
					}
					break;
			}
	    }

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		Log.i(TAG, "onDestroy()");

	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()");
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "onPause()");
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

}
