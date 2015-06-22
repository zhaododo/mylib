package com.android.dream.app.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.adapter.ErpBoardPaginationAdapter;
import com.android.dream.app.bean.AdErpBoard;
import com.android.dream.app.bean.AdErpBoardPage;
import com.android.dream.app.util.StringUtils;
import com.android.dream.app.widget.PullToRefreshListView;
import com.android.dream.app.widget.PullToRefreshListView.OnRefreshListener;
import com.android.dream.dao.DBErpBoardMainDao;

/**
 * @author zhaoj
 * 
 * ERP公告主界面
 *
 */
public class ErpBoardActivity extends BaseActivity {
		
	/**
	 * 适配器
	 */
	private ErpBoardPaginationAdapter pull_adapter;
	private View pull_footer;
	private TextView pull_foot_more;
	private ProgressBar pull_foot_progress;
	private PullToRefreshListView pull_listView;
	private AppContext appContext;
	private DBErpBoardMainDao erpBoardMainDao; //阅读时存储文件
	
	
	/**
	 * 每页的大小，必须与后台的PAGE_SIZE一致
	 */
	private final static int PAGE_SIZE = 10;

	/**
	 *  用于计算总行数
	 */
	private int pull_erp_board_sum = 0;
	
	/**
	 *  用于存放erpBoard
	 */
	private List<AdErpBoard> lvErpBoardData = new ArrayList<AdErpBoard>();
	
	
	/**
	 *  Handler
	 */
	private Handler pull_handler = null;
	
	/**
	 *  工号
	 */
	private String mUserno = "DEFAULT";

	public final static String TAG = "zhaoj";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_erp_board_main);
		
		appContext = (AppContext) getApplication();

		pull_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);

		pull_foot_more = (TextView) pull_footer
				.findViewById(R.id.listview_foot_more);
		pull_foot_progress = (ProgressBar) pull_footer
				.findViewById(R.id.listview_foot_progress);

		pull_listView = (PullToRefreshListView)findViewById(R.id.pull_content_listview);
		pull_listView.addFooterView(pull_footer);
		
		ImageButton imageButton = (ImageButton) findViewById(R.id.title_left_btn);
		imageButton.setOnClickListener(new imgbtnclick());
		
		erpBoardMainDao = new DBErpBoardMainDao(this);

		pull_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (position == 0 || view == pull_footer)
							return;
						
						AdErpBoard board = null;        		
		        		//判断是否是TextView
		        		if(view instanceof TextView){
		        			board = (AdErpBoard)view.getTag();
		        		}else{
		        			TextView tv = (TextView)view.findViewById(R.id.ad_erp_board_title);
		        			board = (AdErpBoard)tv.getTag();
		        		}
		        		if(board == null) return;
		        		
		        		
		        		
						Log.i(TAG, "pull_listView clicked board:"+board);
						
						//数据库存储该条信息
						erpBoardMainDao.adddata(board.getMsgid());
						
						board.setHasReaded(true);
						
						UIHelper.showErpBoardDetail(view.getContext(), board);

					}
				});

		pull_listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				pull_listView.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvErpBoardData.isEmpty())
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
					int pageIndex = (int)(pull_erp_board_sum / PAGE_SIZE)+1;
					loadLvErpBoardData(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL,mUserno);
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
				loadLvErpBoardData(1, UIHelper.LISTVIEW_ACTION_REFRESH, mUserno);
			}
		});
		
		pull_adapter = new ErpBoardPaginationAdapter(this,lvErpBoardData,R.layout.ad_erp_board_listview);	
		pull_listView.setAdapter(pull_adapter);
		initFrameListViewData();
		initEnvironment();
	}
	
	
	public class imgbtnclick implements OnClickListener {
		@Override
		public void onClick(View v) {
			ErpBoardActivity.this.finish();
		}
	}
	
	
	/**
	 * 创建线程加载数据
	 * @param pageIndex
	 * @param action
	 * @param userno
	 */
	private void loadLvErpBoardData(final int pageIndex, final int action,
			final String userno) {
		pull_foot_progress.setVisibility(ProgressBar.VISIBLE);
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					
					AdErpBoardPage erpBoardPage = appContext.getAdErpBoard(userno, pageIndex);
					
					if (erpBoardPage != null && erpBoardPage.getBoards() !=null)
					{
						msg.what = erpBoardPage.getPageSize();
						msg.obj = erpBoardPage.getBoards();
						Log.i(TAG, "loadLvErpBoardData#list size:"+ erpBoardPage.getBoards().size());
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_ERP_BOARD;
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
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_ERP_BOARD:

				List<AdErpBoard> nlist = (List<AdErpBoard>) obj;
				pull_erp_board_sum = what;
				

				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvErpBoardData.size() > 0) {
						if (nlist != null) {
							for (AdErpBoard remoteErpBoard : nlist) {
								boolean b = false;

								for (AdErpBoard localErpBoard : lvErpBoardData) {
									if (remoteErpBoard.getMsgid().equals(
											localErpBoard.getMsgid())) {
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
					lvErpBoardData.clear();// 先清除原有数据
					lvErpBoardData.addAll(nlist);
				}
				break;
			}
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {

			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			switch (objtype) {
			case UIHelper.LISTVIEW_ERP_BOARD:

				List<AdErpBoard> nlist = (List<AdErpBoard>) obj;
				pull_erp_board_sum += what;
				
				if (lvErpBoardData.size() > 0) {
					if (nlist != null) {
						for (AdErpBoard remoteErpBoard : nlist) {
							boolean b = false;
							for (AdErpBoard localErpBoard : lvErpBoardData) {
								if (remoteErpBoard.getMsgid().equals(
										localErpBoard.getMsgid())) {
									b = true;
									break;
								}
							}
							if (!b)
								lvErpBoardData.add(remoteErpBoard);
						}

					}
				} else {
					if (nlist != null)
					{
						lvErpBoardData.addAll(nlist);
					}
				}
			}
			break;
		}
	}

	
	private void initFrameListViewData() {
		if (appContext!= null)
		{
			mUserno = appContext.getCurrentUser();
		}
		
		// 定义Handler,分页为10页
		pull_handler = this.getLvHandler(pull_listView, pull_adapter,
				pull_foot_more, pull_foot_progress,PAGE_SIZE);

		// 加载资讯数据
		if (pull_adapter.isEmpty()) {
			loadLvErpBoardData(1, UIHelper.LISTVIEW_ACTION_INIT, mUserno);
		}
	}
	
	
	/**
	 *  初始化存储环境
	 */
	private void initEnvironment() {
		
		// 获取SDCard是否存在，当SDCard存在时
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
			
			// 获取SDCard根目录文件
			String saveDirsbsolute = Environment.getExternalStorageDirectory().getAbsolutePath();
			
			
			try
			{
				
				File filesDir = new File(saveDirsbsolute + "/Dream/Files/");
				// 临时文件保存目录
				File saveDir = new File(saveDirsbsolute + "/Dream/Files/TempFile/");
				// 正式文件保存目录
				File realDir = new File(saveDirsbsolute + "/Dream/Files/BoardFile/");
				
				if (!filesDir.exists()) {
					filesDir.mkdirs();
				}
				if (!saveDir.exists()) {
					saveDir.mkdirs();
				}
				if (!realDir.exists()) {
					realDir.mkdirs();
				}
			}
			
			catch(Exception e)
			{
				
				if (appContext != null)
				{
					appContext.saveErrorLog(e);
				}
			}
			
			
			
		} else { // 当SDCard不存在时
			Toast.makeText(this, R.string.sdcarderror, Toast.LENGTH_LONG).show(); // 提示用户SDCard不存在
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		if (pull_adapter != null)
		{
			pull_adapter.notifyDataSetChanged();
		}
	}
	

}
