package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.R;
import com.android.dream.app.R.color;
import com.android.dream.app.adapter.AdSysPicDirDetailAdapter;
import com.android.dream.app.adapter.AdSysPicDirDetailAdapter.TextCallback;
import com.android.dream.app.bean.AdSysPicItem;
import com.android.dream.app.widget.AdFCBimp;
import com.android.dream.app.widget.SystemPicList;

/**
 * @author zhaoj
 * 
 * 自定义图像相册明细选择
 *
 */
public class AdSysPicDirDetailActivity extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelistid";

	List<AdSysPicItem> dataList;
	GridView gridView;
	AdSysPicDirDetailAdapter adapter;// 带有图像文件夹列表
//	AlbumHelper helper;
	TextView bt;
	int positon;
	/**
	 * 返回按钮
	 */
	private ImageView backBtn;
	/**
	 * 标题
	 */
	private TextView titleTV;
	int maxselect; //最多选择图片

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(AdSysPicDirDetailActivity.this, "最多选择"+maxselect+"张图片", 400).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ad_fc_picdir_detail);

//		helper = AlbumHelper.getHelper();
//		helper.init(getApplicationContext());
		
		positon = getIntent().getExtras().getInt(EXTRA_IMAGE_LIST);
		dataList = SystemPicList.tmpList.get(positon).imageList;
		initView();
		bt = (TextView) findViewById(R.id.bt);
		bt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.getMap().values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();) {
					list.add(it.next());
				}

				if (AdFCBimp.act_bool) {
//					Intent intent = new Intent(AdSysPicDirDetailActivity.this,
//							AdFCPublishStutasActivity.class);
//					startActivity(intent);
					AdFCBimp.act_bool = false;
				}
				for (int i = 0; i < list.size(); i++) {
					if (AdFCBimp.drr.size() < 9) {
						AdFCBimp.drr.add(list.get(i));
					}
				}
				finish();
			}

		});
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		maxselect = getIntent().getExtras().getInt("maxselect");
		
		//点击返回按钮关闭activity
		backBtn = (ImageView) findViewById(R.id.ad_newyeargreetingcard_header_back);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AdSysPicDirDetailActivity.this.finish();
			}
		});
		//初始化title
		titleTV = (TextView) findViewById(R.id.ad_newyeargreetingcard_header_tv);
		titleTV.setText("上传照片");
		
		gridView = (GridView) findViewById(R.id.gridview);
//		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new AdSysPicDirDetailAdapter(AdSysPicDirDetailActivity.this, dataList,
				mHandler,maxselect);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				
				if(count > 0 ){
					changeBtnView(true);
				}else {
					changeBtnView(false);
				}
				bt.setText("确定" + "(" + count + ")");
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 刷新数据
				 */
				adapter.notifyDataSetChanged();
			}

		});

	}
	
	private void changeBtnView(boolean isChoice){
		
		if(isChoice){
			bt.setBackgroundResource(R.drawable.silver_btn_pressed);
			bt.setTextColor(color.white);
		}else {
			bt.setBackgroundResource(R.drawable.silver_btn_normal);
			bt.setTextColor(color.grey);
		}
	}
}
