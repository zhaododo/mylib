package com.android.dream.app.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.android.dream.app.R;
import com.android.dream.app.adapter.AdSysPicDirAdapter;
import com.android.dream.app.bean.AdSysPicDir;
import com.android.dream.app.util.AlbumHelper;
import com.android.dream.app.widget.AdFCBimp;
import com.android.dream.app.widget.LoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

/**
 * @author zhaoj
 * 自定义图像相册的选择
 *
 */
public class AdSysPicDirActivity extends Activity {
	List<AdSysPicDir> dataList = new ArrayList<AdSysPicDir>();
	List<AdSysPicDir> nlist;
	GridView gridView;
	AdSysPicDirAdapter adapter;// 自定义的适配器
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelistid";
	public static Bitmap bimap;
	private int maxselect = 0;
	private LoadingDialog loading;
	private Handler handler;
	/**
	 * 返回按钮
	 */
	private ImageView backBtn;
	/**
	 * 标题
	 */
	private TextView titleTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_fc_sys_picdir);
//		清空选择内容
		AdFCBimp.drr.clear();
		AdFCBimp.bmp.clear();
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		handler = getLvHandler();
		initView();
		initData();
		
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
 
//		异步扫描文件
		getpiclis();
//		添加图片按钮+
		bimap=BitmapFactory.decodeResource(
				getResources(),
				R.drawable.icon_addpic_unfocused);
		try{
			maxselect = getIntent().getExtras().getInt("maxselect");
		}catch(NullPointerException  ex){
			maxselect = 9;
		}
	}

	/**
	 * 初始化view视图
	 */
	private void initView() {
		
		//点击返回按钮关闭activity
		backBtn = (ImageView) findViewById(R.id.ad_newyeargreetingcard_header_back);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AdSysPicDirActivity.this.finish();
			}
		});
		//初始化title
		titleTV = (TextView) findViewById(R.id.ad_newyeargreetingcard_header_tv);
		titleTV.setText("最近照片");
		
		gridView = (GridView) findViewById(R.id.gridview);
		adapter = new AdSysPicDirAdapter(AdSysPicDirActivity.this, dataList);
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (dataList != null && dataList.get(position) != null)
				{
					Intent intent = new Intent(AdSysPicDirActivity.this,AdSysPicDirDetailActivity.class);
					
					intent.putExtra(AdSysPicDirActivity.EXTRA_IMAGE_LIST,position);
					intent.putExtra("maxselect",maxselect);
					startActivity(intent);
					finish();
				}
	
				
			}

		});
	}
	
	private void getpiclis(){
//		初始化时显示
//		正在等待dialog
		loading = new LoadingDialog(this);		
		loading.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					nlist = helper.getImagesBucketList(false);
					dataList.addAll(nlist);
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = "error";
//					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
		
	}
	
	private Handler getLvHandler() {
		return new Handler() {

			public void handleMessage(Message msg) {
				// 关闭等待对话框
				if (loading != null) {
					loading.dismiss();
				}
				adapter.notifyDataSetChanged();
			}
		};
	}
}
