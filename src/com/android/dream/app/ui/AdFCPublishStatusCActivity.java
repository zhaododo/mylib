package com.android.dream.app.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdAjaxOpt;
import com.android.dream.app.util.FileUtils;
import com.android.dream.app.widget.AdFCBimp;
import com.android.dream.app.widget.AdFCOperaPicDialog;
import com.android.dream.app.widget.LoadingDialog;

/**
 * @author zhaoj
 * 发说说功能
 *
 */
public class AdFCPublishStatusCActivity extends BaseActivity{

	private static final String TAG = null;
	private AppContext appContext;
	private LoadingDialog loading;
	private Handler pull_handler;
	private GridView gv_picitem;
	private EditText txt_publish_content;
	private GridAdapter adapter;
	private AdFCOperaPicDialog fcOperaPicDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_fc_publishstutas);
		appContext = (AppContext) getApplication();
		//初始化页面控件
		initview();
		pull_handler = getLvHandler();
	}
	/**
	 * 初始化控件
	 * @author Wang Cheng
	 * 2014年12月30日下午12:57:34
	 */
	private void initview(){

		fcOperaPicDialog = new AdFCOperaPicDialog(AdFCPublishStatusCActivity.this, R.style.AdFCOperaPicDialog, this);
		fcOperaPicDialog.setContentView(R.layout.ad_fc_tapic_popupdialog);
		Window dialogWindow = fcOperaPicDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.BOTTOM;
		dialogWindow.setAttributes(lp);
		fcOperaPicDialog.setCanceledOnTouchOutside(true);
		fcOperaPicDialog.hide();
		
		gv_picitem = (GridView) findViewById(R.id.gv_picitem);
		txt_publish_content = (EditText) findViewById(R.id.txt_publish_content);
		gv_picitem.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		gv_picitem.setAdapter(adapter);
		gv_picitem.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//+的图标
				if (position == AdFCBimp.bmp.size()) {
					fcOperaPicDialog.show();
				} else {
					Intent intent = new Intent(AdFCPublishStatusCActivity.this,
							AdFCOperaPicActivity.class);
					intent.putExtra("ID", position);
					startActivity(intent);
				}
			}
		});
	}
	/**
	 * 创建线程发表状态
	 * @param pageIndex
	 * @param checkdate  日期
	 * @param companyname 公司名称
	 */
	private void loadPicData() {
		loading = new LoadingDialog(this);		
		loading.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Map<String, Object> paramters = new HashMap<String, Object>();
					paramters.put("publishcon", txt_publish_content.getText());
					paramters.put("userno", appContext.getCurrentUser());
					Map<String, File> filesMap = new HashMap<String, File>();

					//图片文件
					for(int j=0;j<AdFCBimp.drr.size();j++){
						filesMap.put("file"+j, new File(AdFCBimp.drr.get(j)));
					}
					//发表心情
					AdAjaxOpt rslt = appContext.publishstatus(paramters,filesMap);					
					
					if (rslt!=null)
					{
						msg.what =  1;
						msg.obj = rslt;
					}
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = "error";
				}
				pull_handler.sendMessage(msg);
			}
		}.start();

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
	private Handler getLvHandler() {
		
		Log.i(TAG, "getLvHandler");
		
		return new Handler() {
			
			public void handleMessage(Message msg) {
				if (msg.what >= 0 ) {
					AdAjaxOpt rslt = (AdAjaxOpt) msg.obj;
					Toast.makeText(AdFCPublishStatusCActivity.this, rslt.getMsg().toString(), Toast.LENGTH_SHORT).show();
				}
				else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(AdFCPublishStatusCActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
				}
				//关闭等待对话框
				if(loading != null){
					loading.dismiss();
				}
			}
		};
	}
	@SuppressLint("HandlerLeak")
	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			return (AdFCBimp.bmp.size() + 1);
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.ad_fc_item_published_grid,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == AdFCBimp.bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				if (position == 9) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(AdFCBimp.bmp.get(position));
			}

			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}

		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					adapter.notifyDataSetChanged();
					break;
				}
				super.handleMessage(msg);
			}
		};

		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (AdFCBimp.max == AdFCBimp.drr.size()) {
							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = AdFCBimp.drr.get(AdFCBimp.max);
								Bitmap bm = AdFCBimp.revitionImageSize(path);
								AdFCBimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								//FileUtils.saveBitmap(bm, "" + newStr);
								AdFCBimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}
	
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.ad_fc_tapic_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.ad_fc_fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.ad_fc_push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			TextView bt1 = (TextView) view
					.findViewById(R.id.item_popupwindows_camera);
			TextView bt2 = (TextView) view
					.findViewById(R.id.item_popupwindows_Photo);
			TextView bt3 = (TextView) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(AdFCPublishStatusCActivity.this,
							AdSysPicDirActivity.class);
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}
	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String strfiledir = Environment.getExternalStorageDirectory()+ "/dream/publishstatus";
		String filename = String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		File file = FileUtils.createFile(strfiledir, filename);
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (AdFCBimp.drr.size() < 9 && resultCode == -1) {
				AdFCBimp.drr.add(path);
			}
			break;
		}
	}
	public void publishstatus(View view){
		Log.i(TAG, "发表");
		loadPicData();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.i(TAG, "发表=onRestart");
		adapter.update();
		super.onRestart();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i(TAG, "发表=onResume");
		super.onResume();
	}
	
}
