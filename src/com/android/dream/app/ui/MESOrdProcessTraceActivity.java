package com.android.dream.app.ui;
 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.adapter.MESOrdProcessAdapter;
import com.android.dream.app.adapter.MESOrderProInterace;
import com.android.dream.app.bean.AdMESOrdProcessTrace;
import com.android.dream.app.bean.AdMESOrdProcessTracePage;
import com.android.dream.app.util.StringUtils;
import com.android.dream.app.widget.LoadingDialog;
import com.android.dream.app.widget.PullToRefreshListView;
import com.android.dream.app.widget.PullToRefreshListView.OnRefreshListener;

/**
 * 
 * @author Wang Cheng
 *  
 * @date 2014年12月5日下午1:53:43
 */
public class MESOrdProcessTraceActivity extends BaseActivity{

	private AppContext appContext;
	private View pull_footer;
	private TextView pull_foot_more;
	private ProgressBar pull_foot_progress;
	private PullToRefreshListView pull_listView;
	private MESOrdProcessAdapter pull_adapter;
	private EditText et_orderno;  //订单号
//	private EditText et_orderitem;  //订单项次
	private TextView tv_year;   //年份
	private TextView tv_month;   //月份
	private String orderno = "";
	private String editdate;  //编制日期
	private String deldate;
	private ImageView iv1;
	private ImageView iv2;
	private ImageView iv3;
	private ImageView iv4;
	private float yearmoveX;
	private float yearpressX;
	private float monthmoveX;
	private float monthpressX;
	private TextView tv_title;
	private LoadingDialog loading;
//	private EditText tv_deldate;   //交付日期
//	private EditText tv_editdate;  //编制日期
//	private Button bt_search_ordpro;  //查询按钮
	private int mYear;  
    private int mMonth;  
    private int mDay;
    private int activeEdt;
	// 每页的大小，必须与后台的PAGE_SIZE一致
	private final static int PAGE_SIZE = 10;
//	private final static int DATE_DIALOG_ID = 11;
	// 用于计算总行数
	private int pull_ord_enter_sum = 0;
	//用于存放订单录入量
	private List<AdMESOrdProcessTrace> lvErporderProcessData = new ArrayList<AdMESOrdProcessTrace>();
	 //Handler
	private Handler pull_handler = null;
	//工号
	private String mUserno = "DEFAULT";

	public final static String TAG = "wch";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_mes_ordprocess_main);
		appContext = (AppContext) getApplication();
		pull_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);

		pull_foot_more = (TextView) pull_footer.findViewById(R.id.listview_foot_more);
		pull_foot_progress = (ProgressBar) pull_footer.findViewById(R.id.listview_foot_progress);
		pull_listView = (PullToRefreshListView)findViewById(R.id.ptl_ordprocess);
		pull_listView.addFooterView(pull_footer);
		ImageView imageButton = (ImageView) findViewById(R.id.board_detail_title_left_btn);
		imageButton.setOnClickListener(new imgbtnclick());
		ImageView imageBtnQuestion = (ImageView) findViewById(R.id.board_detail_title_question);
		imageBtnQuestion.setOnClickListener(new imgbtnclick());
		ImageView imageBtnSearch = (ImageView) findViewById(R.id.mes_order_pro_search__btn);
		imageBtnSearch.setOnClickListener(new imgbtnclick());
		
		tv_title = (TextView)findViewById(R.id.txt_sckb_title);
		tv_title.setText(R.string.ordprocesstitle);
		initDateView();
		pull_listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				pull_listView.onScrollStateChanged(view, scrollState);
				getdate();
				// 数据为空--不用继续下面代码了
				if (lvErporderProcessData.isEmpty())
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
					int pageIndex = (int)(pull_ord_enter_sum / PAGE_SIZE)+1;
					loadLvOrderProcessData(pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL,editdate,orderno,deldate);
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
				getdate();
				loadLvOrderProcessData(1, UIHelper.LISTVIEW_ACTION_REFRESH,editdate,orderno,deldate);
			}
		});
		pull_adapter = new MESOrdProcessAdapter(this,lvErporderProcessData,R.layout.ad_mes_ordprocess_item,ordprointer);	
		pull_listView.setAdapter(pull_adapter);
		initFrameListViewData();
	}
	/**
	 * 创建线程加载数据
	 * @param pageIndex
	 * @param action
	 * @param userno
	 */
	private void loadLvOrderProcessData(final int pageIndex, final int action,
			final String editdate,final String orderinfo,final String deldate) {
		pull_foot_progress.setVisibility(ProgressBar.VISIBLE);
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
					AdMESOrdProcessTracePage erpOrdProcessPage = null;
					erpOrdProcessPage = appContext.mesOrderProcessTrace(mUserno,editdate, orderinfo,deldate, pageIndex);
					
					if (erpOrdProcessPage != null && erpOrdProcessPage.getMesOrdProcessTracelis() !=null)
					{
						msg.what = erpOrdProcessPage.getPagesize();
						msg.obj = erpOrdProcessPage.getMesOrdProcessTracelis();
						Log.i(TAG, "loadLvErpBoardData#list size:"+ erpOrdProcessPage.getMesOrdProcessTracelis().size());
					}
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = "error";
					//e.printStackTrace();
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_ERP_ORDENTER;
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
				Log.i(TAG, "handleMessage11111");
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
		orderno = et_orderno.getText().toString().trim();
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_ERP_ORDENTER:
				List<AdMESOrdProcessTrace> nlist;
				if(obj!=null && !obj.equals("error")){
					nlist = (List<AdMESOrdProcessTrace>) obj;
				}
				else{
					nlist = null;
				}
				pull_ord_enter_sum = what;
				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvErporderProcessData.size() > 0) {
						if (nlist != null) {
							for (AdMESOrdProcessTrace remoteErpordpro : nlist) {
								boolean b = false;

								for (AdMESOrdProcessTrace localErpordpro : lvErporderProcessData) {
									if (remoteErpordpro.getOrd_no().equals(localErpordpro.getOrd_no())
											&& remoteErpordpro.getOrd_item().equals(localErpordpro.getOrd_item())) {
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
					lvErporderProcessData.clear();// 先清除原有数据
					lvErporderProcessData.addAll(nlist);
				}else{
					lvErporderProcessData.clear();// 先清除原有数据
				}
				
				break;
			}
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {

			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			switch (objtype) {
			case UIHelper.LISTVIEW_ERP_ORDENTER:

				List<AdMESOrdProcessTrace> nlist = (List<AdMESOrdProcessTrace>) obj;
				pull_ord_enter_sum += what;
				
				if (lvErporderProcessData.size() > 0) {
					if (nlist != null) {
						for (AdMESOrdProcessTrace remoteMESordpro : nlist) {
							boolean b = false;
							for (AdMESOrdProcessTrace localErpMESordpro : lvErporderProcessData) {
								if (remoteMESordpro.getOrd_no().equals(localErpMESordpro.getOrd_no())
										&& remoteMESordpro.getOrd_item().equals(localErpMESordpro.getOrd_item())) {
									b = true;
									break;
								}
							}
							if (!b)
								lvErporderProcessData.add(remoteMESordpro);
						}

					}
				} else {
					if (nlist != null)
					{
						lvErporderProcessData.addAll(nlist);
					}
				}
			}
			break;
		}
	}

	private void initDateView() {
//		编制日期
//		tv_editdate = (EditText) findViewById(R.id.tv_editdate);
////		交付日期
//		tv_deldate = (EditText) findViewById(R.id.tv_deldate);
////		查询按钮
//		bt_search_ordpro = (Button) findViewById(R.id.bt_search_ordpro);
//		tv_deldate.setOnClickListener(new DateButtonOnClickListener());
//		tv_editdate.setOnClickListener(new DateButtonOnClickListener());
//		bt_search_ordpro.setOnClickListener(new DateButtonOnClickListener());
//		final Calendar c = Calendar.getInstance();
//	    mYear = c.get(Calendar.YEAR);
//	    mMonth = c.get(Calendar.MONTH);
//	    mDay = c.get(Calendar.DAY_OF_MONTH);
////	           初始化日期
//	    String  initdate = new StringBuilder().append(mYear).append(
//	              (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append(
//	              (mDay < 10) ? "0" + mDay : mDay).toString();
//	    tv_editdate.setText(initdate);
//	    tv_deldate.setText(initdate);
//	    tv_editdate.setText("20141215");  //测试默认
//	    tv_deldate.setText("201411");  //测试默认
		iv1 = (ImageView) findViewById(R.id.iv_ordprocess_1);
		iv2 = (ImageView) findViewById(R.id.iv_ordprocess_2);
		iv3 = (ImageView) findViewById(R.id.iv_ordprocess_3);
		iv4 = (ImageView) findViewById(R.id.iv_ordprocess_4);

		iv1.setOnTouchListener(new MyImageTouchListener());
		iv2.setOnTouchListener(new MyImageTouchListener());
		iv3.setOnTouchListener(new MyImageTouchListener());
		iv4.setOnTouchListener(new MyImageTouchListener());

		et_orderno = (EditText) findViewById(R.id.et_ordprocess_orderno);
//		et_orderitem = (EditText) findViewById(R.id.et_ordprocess_orderitem);
		tv_year = (TextView) findViewById(R.id.tv_ordprocess_year);
		tv_month = (TextView) findViewById(R.id.tv_ordprocess_month);
		//初始化年月
		SimpleDateFormat adf = new SimpleDateFormat("yyyyMMdd");
		String nowdate = adf.format(new Date());
		tv_year.setText(nowdate.substring(0, 4));
		tv_month.setText(nowdate.substring(4,6));
		
		tv_year.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下
				case MotionEvent.ACTION_DOWN:
					yearpressX = event.getX();
					break;
				// 移动
				case MotionEvent.ACTION_MOVE:
					yearmoveX = event.getX();
					if (yearmoveX - yearpressX > 0
							&& Math.abs(yearmoveX - yearpressX) > 30) {// &&											// < 50
						Log.i("message", "向右");
						int direction = 1;
						iv1.setImageResource(R.drawable.icon_sort_rate_up);
						iv2.setImageResource(R.drawable.icon_sort_price_down);
						changeYearDate(tv_year, direction);
						yearpressX = event.getX();
					} else if (yearmoveX - yearpressX < 0
							&& Math.abs(yearmoveX - yearpressX) > 30) {
						Log.i("message", "向左");
						int direction = 0;
						iv1.setImageResource(R.drawable.icon_sort_price_up);
						iv2.setImageResource(R.drawable.icon_sort_rate_down);
						changeYearDate(tv_year, direction);
						yearpressX = event.getX();
					}
					break;
				// 松开
				case MotionEvent.ACTION_UP:
					iv1.setImageResource(R.drawable.icon_sort_rate_up);
					iv2.setImageResource(R.drawable.icon_sort_rate_down);
					getdate();
					loadLvOrderProcessData(1, UIHelper.LISTVIEW_ACTION_INIT,editdate,orderno,deldate);
					break;
				default:
					break;
				}
				return true;
			}
		});

		tv_month.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下
				case MotionEvent.ACTION_DOWN:
					monthpressX = event.getX();
					break;
				// 移动
				case MotionEvent.ACTION_MOVE:
					monthmoveX = event.getX();
					if (monthmoveX - monthpressX > 0
							&& Math.abs(monthmoveX - monthpressX) > 30) {
						Log.i("message", "向右");
						iv3.setImageResource(R.drawable.icon_sort_rate_up);
						iv4.setImageResource(R.drawable.icon_sort_price_down);
						int direction = 1;
						changeMonthDate(tv_month, direction);
						monthpressX = event.getX();
					} else if (monthmoveX - monthpressX < 0
							&& Math.abs(monthmoveX - monthpressX) > 30) {
						Log.i("message", "向左");
						iv3.setImageResource(R.drawable.icon_sort_price_up);
						iv4.setImageResource(R.drawable.icon_sort_rate_down);
						int direction = 0;
						changeMonthDate(tv_month, direction);
						monthpressX = event.getX();
					}
					break;
				// 松开
				case MotionEvent.ACTION_UP:
					iv3.setImageResource(R.drawable.icon_sort_rate_up);
					iv4.setImageResource(R.drawable.icon_sort_rate_down);
					getdate();
					loadLvOrderProcessData(1, UIHelper.LISTVIEW_ACTION_INIT,editdate,orderno,deldate);
					break;
				default:
					break;
				}
				return true;
			}
		});

	}
	
	/**
	 * 根据滑动方向更改年份
	 * @param dateTV
	 * @param direction
	 */
	private void changeYearDate(TextView dateTV, int direction) {
		int dateTemp = Integer.parseInt(dateTV.getText().toString());
		String newDate = "";
		if (direction == 1) {
			dateTemp = dateTemp + 1;
			newDate = dateTemp + "";
		} else {
			dateTemp = dateTemp - 1;
			newDate = dateTemp + "";
		}
		dateTV.setText(newDate);
	}
	
	/**
	 * 根据滑动方向更改月份
	 * @param dateTV
	 * @param direction
	 */
	private void changeMonthDate(TextView dateTV, int direction) {
		int dateTemp = Integer.parseInt(dateTV.getText().toString());
		if (direction == 1) {
			dateTemp = dateTemp + 1;
			String newMonth = getNewMonth(dateTemp);
			dateTV.setText(newMonth);
		} else {
			dateTemp = dateTemp - 1;
			String newMonth = getNewMonth(dateTemp);
			dateTV.setText(newMonth);
		}
	}
	/**
	 * 月份自动补0
	 * @param dateTV
	 * @param direction
	 */
	public String getNewMonth(int month) {
		if (month > 12) {
			month = 1;
		} else if (month <= 0) {
			month = 12;
		}
		String monthTemp = month + "";
		if (monthTemp.length() < 2) {
			monthTemp = "0" + monthTemp;
		}

		return monthTemp;
	}
	
	public class MyImageTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View imageView, MotionEvent mEvent) {
			switch (mEvent.getAction()) {
			// 按下
			case MotionEvent.ACTION_DOWN:
				if (imageView.getId() == R.id.iv_ordprocess_1) {
					iv1.setImageResource(R.drawable.icon_sort_price_up);
					changeYearDate(tv_year, 0);
				} else if (imageView.getId() == R.id.iv_ordprocess_2) {
					iv2.setImageResource(R.drawable.icon_sort_price_down);
					changeYearDate(tv_year, 1);
				} else if (imageView.getId() == R.id.iv_ordprocess_3) {
					iv3.setImageResource(R.drawable.icon_sort_price_up);
					changeMonthDate(tv_month, 0);
				} else if (imageView.getId() == R.id.iv_ordprocess_4) {
					iv4.setImageResource(R.drawable.icon_sort_price_down);
					changeMonthDate(tv_month, 1);
				}
				break;
			// 移动
			case MotionEvent.ACTION_MOVE:
				break;
			// 松开
			case MotionEvent.ACTION_UP:
				iv1.setImageResource(R.drawable.icon_sort_rate_up);
				iv2.setImageResource(R.drawable.icon_sort_rate_down);
				iv3.setImageResource(R.drawable.icon_sort_rate_up);
				iv4.setImageResource(R.drawable.icon_sort_rate_down);
				getdate();
				loadLvOrderProcessData(1, UIHelper.LISTVIEW_ACTION_INIT,editdate,orderno,deldate);
				break;
			default:
				break;
			}
			return true;
		}
	}
	
	/**
	 * 获取输入条件订单和年月
	 * @param dateTV
	 * @param direction
	 */
	private void getdate(){
//		String enterordno ="";
//		String enterorditem ="";
		if(et_orderno.getText()!=null&&!et_orderno.getText().toString().isEmpty()){
			orderno = et_orderno.getText().toString().trim().toUpperCase();
		}else{
			orderno = "";
		}
//		if(et_orderitem.getText()!=null&&!et_orderitem.getText().toString().isEmpty()){
//			enterorditem = orderno+et_orderitem.getText().toString().trim();
//		}else{
//			enterorditem = "";
//		}
//		if(enterordno!=""){
//		   if(enterorditem!=""){
//			   orderno = enterordno+"-"+enterorditem;
//		   }
//		   else{
//			   orderno = enterordno+"-";
//		   }
//		}else{
//			orderno = "";
//		}
		
//		editdate = tv_editdate.getText().toString();
//		deldate = tv_deldate.getText().toString();
		SimpleDateFormat  adf = new SimpleDateFormat("yyyyMMdd");
		editdate = adf.format(new Date());
		deldate = tv_year.getText().toString()+tv_month.getText().toString();
//		editdate = "20141215";  //测试默认
//		deldate = "201411";  //测试默认
	}
	
	/**
	 * 退出当前画面
	 * @param dateTV
	 * @param direction
	 */
	public class imgbtnclick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			
			case R.id.board_detail_title_left_btn:
				MESOrdProcessTraceActivity.this.finish();
			break;
			
			case R.id.board_detail_title_question:
				 //添加页面功能说明
				Dialog dialog  = new Dialog(MESOrdProcessTraceActivity.this,R.style.MESOrderProductQuestionDialog);
				dialog.setTitle(R.string.ordenterdialogheader);
				View view = LayoutInflater.from(MESOrdProcessTraceActivity.this).inflate(R.layout.ad_mes_order_trace_question, null);
				dialog.setContentView(R.layout.ad_mes_order_trace_question);
				TextView dialogcontent = (TextView)dialog.findViewById(R.id.tv_mes_product_dialog_content);
				dialogcontent.setText(getResources().getText(R.string.ordprocessdialogcontent));
				dialog.show();
				
			break;
			case R.id.mes_order_pro_search__btn:
				getdate();
				loadLvOrderProcessData(1, UIHelper.LISTVIEW_ACTION_INIT,editdate,orderno,deldate);
			break;
			
			}
		}
	}
	/**
	 * 首次加载数据
	 * @param dateTV
	 * @param direction
	 */
	private void initFrameListViewData() {
		if (appContext!= null)
		{
			mUserno = appContext.getCurrentUser();
		}
//		mUserno ="860007";  //测试默认
		// 定义Handler,分页为10页
		pull_handler = this.getLvHandler(pull_listView, pull_adapter,
				pull_foot_more, pull_foot_progress,PAGE_SIZE);
		// 加载资讯数据
		if (pull_adapter.isEmpty()) {
			getdate();
			loadLvOrderProcessData(1, UIHelper.LISTVIEW_ACTION_INIT,editdate,orderno,deldate);
		}
	}
	
	/**
	 * 回调是否展开
	 */
	MESOrderProInterace ordprointer = new MESOrderProInterace() {
		
		@Override
		public void updateStatus(String orderno,String orderitem, int status) {
			// TODO Auto-generated method stub
			if(orderno !=null){
				for(int i =0;i<lvErporderProcessData.size();i++){
					AdMESOrdProcessTrace ordpro = lvErporderProcessData.get(i);
					if(ordpro.getOrd_no() == orderno && ordpro.getOrd_item() == orderitem){
						int visstatus = status == View.VISIBLE ? View.GONE:View.VISIBLE;
						ordpro.setVisibity(visstatus);
					}
				}
				pull_adapter.notifyDataSetChanged();
		}
	}};
	
	/**
	 * 日期dialog
	 * @author Wang Cheng
	 *  
	 * @date 2014年12月18日上午10:30:19
	 */
//    class DateButtonOnClickListener implements android.view.View.OnClickListener {  
//		@Override  
//		public void onClick(View v) {  
//		    
//			switch(v.getId()){
//			
//			case R.id.bt_search_ordpro:
//				getdate();
//				loadLvOrderProcessData(1, UIHelper.LISTVIEW_ACTION_INIT,editdate,orderno,deldate);
//			break;
//			case R.id.tv_deldate:
//			case R.id.tv_editdate:
//				activeEdt = v.getId();
//				showDialog(DATE_DIALOG_ID);
//			break;
//			}
//		}
//	}  
    /**
     * 更新日期
     * @author Wang Cheng
     * 2014年12月18日上午10:30:39
     */
//	private void updateDisplay(){
//		CharSequence text = "";
//		if(activeEdt == tv_deldate.getId()){
//			
//			tv_deldate.setText(new StringBuilder().append(mYear).append(  
//					  
//		              (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)));
//			text = tv_deldate.getText();
//		}else if(activeEdt == tv_editdate.getId()){
//			
//			tv_editdate.setText(new StringBuilder().append(mYear).append(  
//					  
//		              (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append(  
//		  
//		              (mDay < 10) ? "0" + mDay : mDay)); 
//			text = tv_editdate.getText();
//		}
//		 //Debug.asserts(text instanceof Spannable);
//		 if (text instanceof Spannable) {
//		     Spannable spanText = (Spannable)text;
//		     Selection.setSelection(spanText, text.length());
//		 }
//	}
	
	/**
	 * 创建对话框
	 */
//	@Override
//    protected Dialog onCreateDialog(int id) {  
//		
//       return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay);  
//  
//    }
	/**
	 * 准备创建对话框
	 */
//	@Override
//    protected void onPrepareDialog(int id, Dialog dialog) { 
//		
//       ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);  
//  
//    }
	/**
	 * 日期对话框
	 */
//	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {  
//		  
//	       public void onDateSet(DatePicker view, int year, int monthOfYear,  
//	  
//	              int dayOfMonth) {  
//	  
//	           mYear = year;  
//	  
//	           mMonth = monthOfYear;  
//	  
//	           mDay = dayOfMonth;  
//	  
//	           updateDisplay();  
//	  
//	       }  
//	  
//	    };  
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
