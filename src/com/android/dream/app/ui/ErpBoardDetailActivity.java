package com.android.dream.app.ui;

import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.adapter.ErpBoardFileAdapter;
import com.android.dream.app.bean.AdErpBoard;

/**
 * @author zhaoj
 * 
 * ERP公告明细界面
 *
 */
public class ErpBoardDetailActivity extends BaseActivity {

	private AppContext appContext;
	private AdErpBoard erpBoard;
	private ListView lis_file;
	private float pressX1;
	private float pressX2;
	
	private ImageView title_left_btn;
	
	//标题
	public TextView erp_board_title;
	//内容
	public TextView erp_board_content;
	//建立日期
	public TextView erp_board_createdate;
	//公告分类名称
	public TextView erp_board_name;
	//发布者名称
	public TextView erp_board_publisher_name;
	//文件附件Layout
	public LinearLayout erp_board_file_linearlayout;
	
	public final static String TAG = "zhaoj";
	
	//DownloadTask数组
	private ErpBoardFileAdapter.DownloadTask[] downloadTasks;
	
	/**
	 *  工号
	 */
	private String mUserno = "DEFAULT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		erpBoard = (AdErpBoard)getIntent().getSerializableExtra("ERP_BOARD");     
		
		Log.i(TAG, "ErpBoardDetailActivity erpBoard:"+erpBoard);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_erp_board_detail);
		
		appContext = (AppContext) getApplication();
		
		if (appContext!= null)
		{
			mUserno = appContext.getCurrentUser();
		}
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ad_erp_board_detail_layout);
		mainLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下
				case MotionEvent.ACTION_DOWN:
					pressX1 = event.getX();
					break;
				// 移动
				case MotionEvent.ACTION_MOVE:
					pressX2 = event.getX();
					if(pressX1 - pressX2 > 0
							&& Math.abs(pressX1 - pressX2) > 100)
						ErpBoardDetailActivity.this.finish();
					break;
				// 松开
				case MotionEvent.ACTION_UP:
					break;
				default:
					break;
				}
				return true;
			}
		});
		initView();
	}
	
	private void initView()
	{
		erp_board_title = (TextView)findViewById(R.id.ad_erp_board_title);
		erp_board_content = (TextView)findViewById(R.id.ad_erp_board_content);
		erp_board_createdate = (TextView)findViewById(R.id.ad_erp_board_createdate);
		erp_board_name = (TextView)findViewById(R.id.ad_erp_board_name);
		erp_board_publisher_name = (TextView)findViewById(R.id.ad_erp_board_publisher_name);
		erp_board_file_linearlayout = (LinearLayout)findViewById(R.id.ad_erp_board_file_linearlayout);
		title_left_btn = (ImageView) findViewById(R.id.board_detail_title_left_btn);
		
		//标题栏位加粗
		TextPaint paint = erp_board_title.getPaint();
		paint.setFakeBoldText(true);
		
		title_left_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ErpBoardDetailActivity.this.finish();
			}
		});
		
		if (erpBoard != null)
		{
			erp_board_title.setText(erpBoard.getTitle());
			erp_board_createdate.setText(erpBoard.getCreateDate());
			erp_board_name.setText(erpBoard.getBoardName());
			erp_board_publisher_name.setText(erpBoard.getPublisherName());
			
			if (erpBoard.getBoardItem() != null)
			{
				erp_board_content.setText(erpBoard.getBoardItem().getContent());
			}
			
			if (!erpBoard.isHasFile())
			{
				erp_board_file_linearlayout.setVisibility(View.GONE);
			}
			else
			{
				//如果有文件附件才创建适配器
				erp_board_file_linearlayout.setVisibility(View.VISIBLE);
				
				//创建下载线程对象
				downloadTasks = new ErpBoardFileAdapter.DownloadTask[erpBoard.getBoardFiles().size()];
				
				lis_file = (ListView) findViewById(R.id.lis_file);
				lis_file.setAdapter(new ErpBoardFileAdapter(this,erpBoard,erpBoard.getBoardFiles(),R.layout.ad_erp_board_file,downloadTasks));
				this.setListViewHeightBasedOnChildren(lis_file);
			}
			
		}
		
	}
	
	

	/**
	 * 设置listview不滚动
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight * 2
				+ (listView.getDividerHeight() * (listAdapter.getCount()));
		listView.setLayoutParams(params);
	}
	
	
	
	
	
	@Override
	protected void onDestroy() {
		if (downloadTasks != null)
		{
			for (int  i =0 ;i < downloadTasks.length; ++i)
			{
				if (downloadTasks[i]!= null)
				{
					downloadTasks[i].exit();
					
					Log.i(TAG, "downloadTasks:" +i+" exit");
				}
			}
		}
		super.onDestroy();
	}

}
