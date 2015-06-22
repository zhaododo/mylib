package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.adapter.ErpReportOpenChartInterface;
import com.android.dream.app.adapter.ErpReportPaginationAdapter;
import com.android.dream.app.bean.AdErpReport;
import com.android.dream.app.bean.AdErpReportPage;
import com.android.dream.app.bean.AdErpReportRow;
import com.android.dream.app.bean.AdErpReportRowItem;
import com.android.dream.app.bean.AdReportLeftMenu;
import com.android.dream.app.ui.ErpReportActivity.OnReportTypeSelectedListener;
import com.android.dream.app.util.StringUtils;
import com.android.dream.app.widget.PullToRefreshListView;
import com.android.dream.app.widget.PullToRefreshListView.OnRefreshListener;

/**
 * @author zhaoj
 * 
 */
public class ErpReportContentFragment extends Fragment implements
		OnReportTypeSelectedListener {

	/**
	 * 适配器
	 */
	private ErpReportPaginationAdapter pull_adapter;
	private View pull_footer;
	private TextView pull_foot_more;
	private LinearLayout listview_foot_layout;
	private ProgressBar pull_foot_progress;
	private PullToRefreshListView pull_listView;
	private AppContext appContext;
	
	private final static int PAGE_SIZE = 2;

	/**
	 * 用于计算总行数
	 */
	private int pull_ReportSum = 0;
	private List<AdErpReport> lvReportData = new ArrayList<AdErpReport>();
	private Handler pull_handler = null;

	public final static String TAG = "zhaoj";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity != null) {
			appContext = (AppContext) activity.getApplication();

			ErpReportActivity tmp = (ErpReportActivity) activity;
			tmp.setmReportTypeSelectedListener(this);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootview = inflater.inflate(R.layout.pull_list_content, container,
				false);

		appContext = (AppContext) getActivity().getApplication();

		pull_footer = inflater.inflate(R.layout.listview_footer, null);

		pull_foot_more = (TextView) pull_footer
				.findViewById(R.id.listview_foot_more);
		listview_foot_layout = (LinearLayout) pull_footer.findViewById(R.id.listview_foot_layout);
		listview_foot_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				pull_listView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
				pull_foot_more.setText(R.string.load_ing);
				pull_foot_progress.setVisibility(View.VISIBLE);
				// 当前pageIndex
				int pageIndex = (int)(pull_ReportSum / PAGE_SIZE)+1;
				loadLvNewsData(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL,
						mUserno, mResType);
			}
		});
		pull_foot_progress = (ProgressBar) pull_footer
				.findViewById(R.id.listview_foot_progress);

		pull_listView = (PullToRefreshListView) rootview
				.findViewById(R.id.pull_content_listview);
		pull_listView.addFooterView(pull_footer);

		pull_listView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (position == 0 || view == pull_footer)
							return;

						AdErpReport adReport = null;
//						// 判断是否是TextView
//						if (view instanceof TextView) {
//							adReport = (AdErpReport) view.getTag();
//						} else {
//							TextView tv = (TextView) view
//									.findViewById(R.id.news_listitem_title);
//							adReport = (AdErpReport) tv.getTag();
//						}
						if (adReport == null)
							return;
					}
				});

		pull_listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				pull_listView.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvReportData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(pull_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(pull_listView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					pull_listView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					pull_foot_more.setText(R.string.load_ing);
					pull_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					int pageIndex = (int)(pull_ReportSum / PAGE_SIZE)+1;
					loadLvNewsData(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL,
							mUserno, mResType);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				pull_listView.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);

			}
		});

		pull_listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadLvNewsData(1, UIHelper.LISTVIEW_ACTION_REFRESH, mUserno,
						mResType);
			}
		});
		return rootview;
	}

	private String mUserno = "DEFAULT";
	private String mResType = "T00001";

	private void loadLvNewsData(final int pageIndex, final int action,
			final String userno, final String restype) {
		pull_foot_progress.setVisibility(ProgressBar.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					
					
					AdErpReportPage erpReportPage = appContext.getAdErpReport(userno,
							restype,pageIndex);
					
					if (erpReportPage != null && erpReportPage.getReportItem() !=null)
					{
						msg.what = erpReportPage.getPageSize();
						msg.obj = erpReportPage.getReportItem();
						Log.i(TAG, "loadLvNewsData#list size:"+ erpReportPage.getReportItem().size());
					}
					
					// NewsList list =
					// appContext.getNewsList(NewsList.CATALOG_ALL, pageIndex,
					// isRefresh);
					// msg.what = list.getPageSize();
					// msg.obj = list;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_ERP_REPORT;
				pull_handler.sendMessage(msg);
			}
		}.start();

	}

	/**
	 * 创建Handler
	 * 
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
				handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
				if (msg.what >= 0) {
					if (msg.what < pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
					if(msg.arg1 == UIHelper.LISTVIEW_ACTION_INIT){
						pull_listView.setSelection(0);
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
				}
				if (adapter.getCount() == 0) {
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
		if (what == -1) {
			// 有异常--显示加载出错 & 弹出错误消息
			return;
		}
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_ERP_REPORT:

				List<AdErpReport> nlist = (List<AdErpReport>) obj;
				pull_ReportSum = what;
				

				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvReportData.size() > 0) {
						if (nlist != null) {
							for (AdErpReport remoteReport : nlist) {
								boolean b = false;

								for (AdErpReport localReport : lvReportData) {
									if (remoteReport.getReportDate().equals(
											localReport.getReportDate())) {
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
				if (nlist != null)
				{
					lvReportData.clear();// 先清除原有数据
					lvReportData.addAll(nlist);
				}
				break;
			}
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {

			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			switch (objtype) {
			case UIHelper.LISTVIEW_ERP_REPORT:

				List<AdErpReport> nlist = (List<AdErpReport>) obj;
				pull_ReportSum += what;
				
				if (lvReportData.size() > 0) {
					if (nlist != null) {
						for (AdErpReport remoteReport : nlist) {
							boolean b = false;
							for (AdErpReport localReport : lvReportData) {
								if (remoteReport.getReportDate().equals(
										localReport.getReportDate())) {
									b = true;
									break;
								}
							}
							if (!b)
								lvReportData.add(remoteReport);
						}

					}
				} else {
					if (nlist != null)
					{
						lvReportData.addAll(nlist);
					}
					
				}
			}
			break;
		}
	}

	
	private void initFrameListViewData() {

		
		// 定义Handler,分页为10页
		pull_handler = this.getLvHandler(pull_listView, pull_adapter,
				pull_foot_more, pull_foot_progress,PAGE_SIZE);

		// 加载资讯数据
		if (pull_adapter.isEmpty()) {
			loadLvNewsData(1, UIHelper.LISTVIEW_ACTION_INIT, mUserno, mResType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 * Fragment的状态保存
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 * 
	 * 在onCreateView之后调用，此时activity已经绑定。 此时进行数据的初始化。
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		pull_adapter = new ErpReportPaginationAdapter(getActivity(),
				lvReportData, R.layout.erp_report_listitem,erprepinter);
		pull_listView.setAdapter(pull_adapter);
		
		if (appContext!= null)
		{
			mUserno = appContext.getCurrentUser();
		}
		initFrameListViewData();

	}

	@Override
	public void loadReport(AdReportLeftMenu menuItem) {
//		pull_listView.setSelectionAfterHeaderView();
		
		mResType = menuItem.getNo();
		if (!pull_adapter.isEmpty()) {
			lvReportData.clear();
		}
		loadLvNewsData(1, UIHelper.LISTVIEW_ACTION_INIT, mUserno, mResType);
	}
	
//	接口打开图表activity
	ErpReportOpenChartInterface erprepinter = new ErpReportOpenChartInterface() {
		
		@Override
		public void getSelectData(AdErpReportRow reportRow,AdErpReportRowItem reportRowItem) {
			// TODO Auto-generated method stub
//			 根据位置打开activity
			Intent intent = new Intent();
			Bundle idBundle = new Bundle();
			idBundle.putString("reportName", reportRow.getReportName());
			idBundle.putString("reportDate", reportRow.getReportDate());
			idBundle.putString("typeNo", reportRow.getTypeNo());
			if(reportRow.getReportProperty() ==null ||reportRow.getReportProperty().equals("")){
				Toast.makeText(getActivity(), "数据异常,"+ reportRow.getReportNo() + " ReportProperty不存在", Toast.LENGTH_SHORT).show();
				return;
			}
			idBundle.putString("reportProperty", reportRow.getReportProperty());
			if(reportRowItem != null){
				idBundle.putString("targetField", reportRowItem.getTargetField());
			}else{
				idBundle.putString("targetField", "empty");
			}
			
			intent.putExtras(idBundle);
			intent.setClass(getActivity(), ErpReportChartActivity.class);
			getActivity().startActivity(intent);
		}
	};
}
