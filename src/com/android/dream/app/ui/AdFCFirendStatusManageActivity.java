package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.adapter.AdFCStatusManageListAdapter;
import com.android.dream.app.bean.AdFriendStatusPageVo;
import com.android.dream.app.bean.AdFriendStatusVo;
import com.android.dream.app.util.StringUtils;
import com.android.dream.app.widget.LoadingDialog;
import com.android.dream.app.widget.PullToRefreshListView;
import com.android.dream.app.widget.PullToRefreshListView.OnRefreshListener;

/**
 * @author zhaoj
 * 
 * 朋友圈
 *
 */
public class AdFCFirendStatusManageActivity extends BaseActivity {

	private PullToRefreshListView pullListView;
	private AdFCStatusManageListAdapter adapter;
	private View pull_footer_contacts_search_result;
	private TextView pull_foot_more_contacts_search_result;
	private ProgressBar pull_foot_progress_contacts_search_result;

	private final static int PAGE_SIZE = 10;
	private Handler pull_handler;
	private final static String TAG ="WCH";
	private LoadingDialog loading;
	private int pull_friend_sum = 0;
	private AppContext appContext;
	private List<AdFriendStatusVo> localfcstatus = new ArrayList<AdFriendStatusVo>();
	/**
	 * 退出按钮
	 */
	private ImageView backBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_fc_status_main);
		appContext = (AppContext) getApplication();
		
		//退出按钮
		backBtn = (ImageView) findViewById(R.id.ad_fc_header_back);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isTaskRoot()){
					Intent intent = new Intent();
					intent.setClass(AdFCFirendStatusManageActivity.this, AdMainActivity.class);
					startActivity(intent);
				}
				AdFCFirendStatusManageActivity.this.finish();
			}
		});
		
		pull_footer_contacts_search_result = getLayoutInflater().inflate(R.layout.listview_footer, null);

		pull_foot_more_contacts_search_result = (TextView) pull_footer_contacts_search_result
				.findViewById(R.id.listview_foot_more);
		pull_foot_progress_contacts_search_result = (ProgressBar) pull_footer_contacts_search_result
				.findViewById(R.id.listview_foot_progress);
		pull_foot_more_contacts_search_result.setText(R.string.load_empty);
		pull_foot_progress_contacts_search_result.setVisibility(View.GONE);

		pullListView = (PullToRefreshListView) findViewById(R.id.pullListView);
		pullListView.addFooterView(pull_footer_contacts_search_result);
		pullListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				pullListView.onScrollStateChanged(view, scrollState);
				//数据为空--不用继续下面代码了
				if (localfcstatus.isEmpty()){
					pull_foot_progress_contacts_search_result.setVisibility(View.GONE);
					return;
				}

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(pull_footer_contacts_search_result) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(pullListView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					pullListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					pull_foot_more_contacts_search_result.setText(R.string.load_ing);
					pull_foot_progress_contacts_search_result.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = (int) (pull_friend_sum / PAGE_SIZE) + 1;
					loadFCStatusData(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				pullListView.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);

			}
		});
		
		
		
		pullListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadFCStatusData(1, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});

		adapter = new AdFCStatusManageListAdapter(AdFCFirendStatusManageActivity.this,appContext,localfcstatus);
		pullListView.setAdapter(adapter);
		initFrameListViewData();
	}
	
	private void initFrameListViewData() {
		
		// 定义Handler,分页为10页
		pull_handler = this.getLvHandler(pullListView, adapter,pull_foot_more_contacts_search_result,
				pull_foot_progress_contacts_search_result,PAGE_SIZE);
		
		// 加载资讯数据
		if (adapter.isEmpty()) {
			loadFCStatusData(1, UIHelper.LISTVIEW_ACTION_INIT);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(isTaskRoot()){
				Intent intent = new Intent();
				intent.setClass(AdFCFirendStatusManageActivity.this, AdMainActivity.class);
				startActivity(intent);
			}
			AdFCFirendStatusManageActivity.this.finish();
			return true;
		}
		return true;
	}
	
	/**
	 * 创建Handler
	 * @param lv
	 * @param adapter
	 * @param more
	 * @param progress
	 * @param pageSize
	 * @return
	 */
	private Handler getLvHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress, final int pageSize) {
		
		Log.i(TAG, "getLvHandler");
		
		return new Handler() {
			
			public void handleMessage(Message msg) {
				Log.i(TAG, "handleMessage11111");
					if (msg.what >= 0 ) {
						handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
						if (msg.what < pageSize) {
							lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
							adapter.notifyDataSetChanged();
							more.setText(R.string.load_full);
						} else if (msg.what == pageSize) {
							lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
							adapter.notifyDataSetChanged();
							more.setText(R.string.load_more);
						}
					}
					else if (msg.what == -1) {
						// 有异常--显示加载出错 & 弹出错误消息
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						more.setText(R.string.load_error);
					}
					if (adapter.getCount() == 0 && msg.what != -1) {
						lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
						more.setText(R.string.load_empty);
					}
					progress.setVisibility(ProgressBar.GONE);
					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
								+ new Date().toLocaleString());
						lv.setSelection(0);
					} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
						lv.onRefreshComplete();
						lv.setSelection(0);
					}
					//关闭等待对话框
					if(loading != null){
						loading.dismiss();
					}
				}
		};
	}
	
	/**
	 * @param what  大小
	 * @param obj   list对象
	 * @param objtype  LISTVIEW_ERP_REPORT
	 * @param actiontype  类别对象
	 */
	@SuppressWarnings("unchecked")
	private void handleLvData(int what, Object obj, int objtype, int actiontype) {
		Log.i(TAG, "handleLvData22222");
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_FIREND_CICLE:
				List<AdFriendStatusVo> adFCStatuslis = new ArrayList<AdFriendStatusVo>();
				if(obj!=null && !obj.equals("error")){
					adFCStatuslis = (List<AdFriendStatusVo>) obj;
				}
				pull_friend_sum = what;
				
				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (localfcstatus.size() > 0) {
						if (adFCStatuslis != null) {
							for (AdFriendStatusVo friendStatusVo : adFCStatuslis) {
								boolean b = false;

								for (AdFriendStatusVo localfriendStatusVo : localfcstatus) {
									if (friendStatusVo.getMainStatuId().equals(localfriendStatusVo.getMainStatuId())) {
										b = true;
										break;
									}
								}
								if (!b)
									newdata++;
							}
						}
					} else {
						newdata = what;
					}
				}
				if (adFCStatuslis != null)
				{
					localfcstatus.clear();// 先清除原有数据
					localfcstatus.addAll(adFCStatuslis);
				}else{
					localfcstatus.clear();// 先清除原有数据
				}
				break;
			}
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {

			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			switch (objtype) {
			case UIHelper.LISTVIEW_FIREND_CICLE:
				List<AdFriendStatusVo> adFCStatuslis = new ArrayList<AdFriendStatusVo>();
				 
				pull_friend_sum += what;
				if(obj!=null && !obj.equals("error")){
					adFCStatuslis = (List<AdFriendStatusVo>) obj;
				}
				if (localfcstatus.size() > 0) {
					if (adFCStatuslis != null) {
						for (AdFriendStatusVo remotStatusVo : adFCStatuslis) {
							boolean b = false;
							for (AdFriendStatusVo localStatusVo : localfcstatus) {
								if (remotStatusVo.getMainStatuId().equals(localStatusVo.getMainStatuId())) {
									b = true;
									break;
								}
							}
							if (!b)
								localfcstatus.add(remotStatusVo);
						}
					}
				} else {
					if (adFCStatuslis != null)
					{
						localfcstatus.addAll(adFCStatuslis);
					}
				}
			}
			break;
		}
	}
	
	/**
	 * 创建线程加载数据
	 * @param pageIndex
	 * @param checkdate  日期
	 * @param companyname 公司名称
	 */
	private void loadFCStatusData(final int pageIndex, final int action) {
		pull_foot_progress_contacts_search_result.setVisibility(ProgressBar.VISIBLE);
//		初始化时显示
		if(action == UIHelper.LISTVIEW_ACTION_INIT){
//			正在等待dialog
			loading = new LoadingDialog(this);		
			loading.show();
		}
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
//					加载朋友状态
					AdFriendStatusPageVo rslt = appContext.getFriendStatusMansge(appContext.getCurrentUser(),pageIndex);				
					
					if (rslt != null)
					{
						msg.what = rslt.getPageSize();
						msg.obj = rslt.getFriendStatuslis();
					}
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = "error";
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_FIREND_CICLE;
				pull_handler.sendMessage(msg);
			}
		}.start();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adapter != null)
		{
			adapter.finished();
		}
		Log.i("czh", "AdFC onDestroy");
	}
	
}
