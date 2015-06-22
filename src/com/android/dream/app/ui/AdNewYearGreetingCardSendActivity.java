package com.android.dream.app.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdAjaxOpt;
import com.android.dream.app.util.FileUtils;
import com.android.dream.app.util.ImageUtils;
import com.android.dream.app.widget.AdFCBimp;
import com.android.dream.app.widget.LoadingDialog;

public class AdNewYearGreetingCardSendActivity extends BaseActivity{

	private int bgType = 0;
	
	private ImageView modImageView;
	private ImageView backBtn;
	private TextView headerTitle;
	private TextView sendBtn;
	private EditText publishcontent;
	private AppContext appContext;
	private LoadingDialog loading;
	private Handler pull_handler;
	private Bitmap bitm;
	private Bundle urlBundle;
	private String imgpath;
	private String tempfilepath ="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_newyeargreetingcard_editortext);
		appContext = (AppContext) getApplication();
		urlBundle = this.getIntent().getExtras();
		bgType = urlBundle.getInt("imageId");
		Log.i("NewYearGreedingCard", "模板类型"+bgType);
		//初始化页面控件
		initview();
		pull_handler = this.getLvHandler();
		
		Log.i("ok", "onCreate");
		
	}
	
	/**
	 *  返回业务
	 */
	private void backProcess()
	{
		Intent intent = new Intent();
		intent.setClass(AdNewYearGreetingCardSendActivity.this, AdNewYearGreetingCardTemplateChoiceActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			backProcess();
			return true;
		}
		return true;
	}
	
	/**
	 * 初始化控件
	 */
	private void initview(){
		
		backBtn = (ImageView) findViewById(R.id.ad_newyeargreetingcard_header_back);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				backProcess();
			}
		});
		
		headerTitle = (TextView) findViewById(R.id.ad_newyeargreetingcard_header_tv);
		headerTitle.setText("编辑祝福语");
		
		//header右上发送按钮
		sendBtn = (TextView) findViewById(R.id.ad_newyeargreetingcard_header_sendbtn);
		sendBtn.setVisibility(View.VISIBLE);
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				publishGreetingCard(view);
			}
		});
		
//		祝福内容
		publishcontent = (EditText) findViewById(R.id.txt_publish_content);
		
		modImageView = (ImageView) findViewById(R.id.ad_newyeargreetingcard_editortextbg);
		switch (bgType) {
		case 0:
			modImageView.setBackgroundResource(R.drawable.ad_newyeargreetingcard_mod_1);
			modImageView.setTag(R.drawable.ad_newyeargreetingcard_mod_1);
			break;
		case 1:
			modImageView.setBackgroundResource(R.drawable.ad_newyeargreetingcard_mod_2);
			modImageView.setTag(R.drawable.ad_newyeargreetingcard_mod_2);
			break;
		case 2:
			modImageView.setBackgroundResource(R.drawable.ad_newyeargreetingcard_mod_3);
			modImageView.setTag(R.drawable.ad_newyeargreetingcard_mod_3);
			break;
		case 3:
			modImageView.setBackgroundResource(R.drawable.ad_newyeargreetingcard_mod_4);
			modImageView.setTag(R.drawable.ad_newyeargreetingcard_mod_4);
			break;
		case 401:   //拍照代码
			
			
			imgpath = urlBundle.getString("imgpath");
			
			BitmapFactory.Options options1 = new BitmapFactory.Options();
			options1.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imgpath, options1);  
			// Calculate inSampleSize
			//options1.inSampleSize = calculateInSampleSize(options1, 1024, 768);
			
			// Decode bitmap with inSampleSize set
			options1.inJustDecodeBounds = false;
			options1.inSampleSize = 2;
			
			bitm = BitmapFactory.decodeFile(imgpath, options1);
			
			modImageView.setImageBitmap(bitm);
			break;
		case 404:  //选择系统图片
			 int sampleSize = 1; //默认缩放为1 
			 BitmapFactory.Options options = new BitmapFactory.Options();  
//			 options.inJustDecodeBounds = true;
//			 bitm =  BitmapFactory.decodeFile(AdFCBimp.drr.get(0),options);
//			//得到宽与高  
//			 int height = options.outHeight;  
//			 int width = options.outWidth;  
			//图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例  
//			 while ((height / sampleSize > Cache.IMAGE_MAX_HEIGHT)  
//			            || (width / sampleSize > Cache.IMAGE_MAX_WIDTH)) {  
//			        sampleSize *= 2;  
//			    }
		    //不再只加载图片实际边缘  
		    options.inJustDecodeBounds = false;  
		    //并且制定缩放比例  
		    options.inSampleSize = 2;
		    bitm = BitmapFactory.decodeFile(AdFCBimp.drr.get(0),options);
			modImageView.setImageBitmap(bitm);
//			modImageView.setBackgroundResource(R.drawable.ad_newyeargreetingcard_mod_4);
			break;
		default:
			break;
		}
		
	}
	
	
	/**
	 * 点击发送贺卡
	 * @param view
	 */
	public void publishGreetingCard(View view){
		loadPicData();
		
	}
	
	/**
	 * 创建线程发表状态
	 * @param pageIndex
	 * @param checkdate  日期
	 * @param companyname 公司名称
	 */
	private void loadPicData() {
//		初始化时显示
//		正在等待dialog
		loading = new LoadingDialog(this);		
		loading.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Map<String, Object> paramters = new HashMap<String, Object>();
					paramters.put("publishcon", publishcontent.getText());
					paramters.put("userno", appContext.getCurrentUser());
//					测试
//					paramters.put("userno", "015724");
					paramters.put("bgType", bgType);
					paramters.put("contentType", UIHelper.FRINDS_CICLE_TYPE_CARD);
					Map<String, File> filesMap = new HashMap<String, File>();
//					图片文件,如果用模版就不用上传，如果选择图片就上传文件
					if(bgType == 404){
						filesMap.put("file", new File(AdFCBimp.drr.get(0)));
//						发表前图片压缩，暂未上线
//						String tempath = getTempfilePath(AdFCBimp.drr.get(0));
//						filesMap.put("file", new File(tempath));
					}else if(bgType == 401){
						filesMap.put("file", new File(imgpath)); 
					}else{
//						读取文件保存临时文件
						int imgid = Integer.parseInt(modImageView.getTag().toString());
						Bitmap bitmap =  BitmapFactory.decodeResource(getResources(), imgid);
						String strfiledir = Environment.getExternalStorageDirectory()
								+ "/dream/temfile";
						String filename = String.valueOf(System.currentTimeMillis())
								+ ".jpg";
						File file = FileUtils.createFile(strfiledir, filename);
						tempfilepath= file.getPath();
						ImageUtils.saveImageToSD(tempfilepath,bitmap,100);
						filesMap.put("file", new File(tempfilepath));
					}
					
//					发表心情
					AdAjaxOpt rslt = appContext.publishstatus(paramters,filesMap);					
					Log.i("ok", rslt.getStatus());
					if (rslt!=null)
					{
						msg.what =  1;
						msg.obj = rslt;
					}
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = "error";
					Log.i("ok", "loadPicData error:"+e.getMessage());
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
		return new Handler() {

			public void handleMessage(Message msg) {
				// 关闭等待对话框
				if (loading != null) {
					loading.dismiss();
				}
				if (msg.what == 1) {
					AdAjaxOpt rslt = (AdAjaxOpt) msg.obj;
					if(rslt != null && rslt.getStatus().equals(AdAjaxOpt.OK_STATUS)){
						Toast.makeText(AdNewYearGreetingCardSendActivity.this,
								rslt.getMsg().toString(), Toast.LENGTH_SHORT)
								.show();
						//发表成功删除临时文件
						try {
							File temfile = new File(tempfilepath);
							if(temfile.exists()){
								temfile.delete();
							}
							
							if (appContext != null)
							{
								if (!appContext.isMainStart())
								{
									Intent intent = new Intent();
									intent.setClass(AdNewYearGreetingCardSendActivity.this, AdFCFirendStatusActivity.class);
									startActivity(intent);
									appContext.setMainStart(true);
								}
							}
							
							finish();
							
						} catch (Exception e) {
							Log.i("ok", e.getMessage());
						}
					}else if(rslt != null && rslt.getStatus().equals(AdAjaxOpt.ERROR_STATUS)){
						Toast.makeText(AdNewYearGreetingCardSendActivity.this,
								rslt.getMsg().toString(), Toast.LENGTH_SHORT)
								.show();
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(AdNewYearGreetingCardSendActivity.this,
							"发表失败", Toast.LENGTH_SHORT).show();
				}
				

			}
		};
	}
	@Override
	protected void onDestroy() {
		// 关闭等待对话框
		if (loading != null) {
			loading.dismiss();
		}
		//清空选择内容
		AdFCBimp.drr.clear();
		AdFCBimp.bmp.clear();
		super.onDestroy();
		
		Log.i("ok", "AdNewYearGreetingCardSendActivity onDestroy");
	}
	/**
	 * 上传时对文件压缩，暂未上线
	 * @param orapath 原路径
	 * @return
	 */
	private String getTempfilePath(String orapath){
		Display currentDisplay = getWindowManager().getDefaultDisplay();  
	    int dw = currentDisplay.getWidth();  
	    int dh = currentDisplay.getHeight();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;  
		Bitmap bmptemp = BitmapFactory.decodeFile(orapath,options); 
        
		int heightRatio = (int)Math.ceil(options.outHeight/(float)dh);  
	    int widthRatio = (int)Math.ceil(options.outWidth/(float)dw);
	    if (heightRatio > 1 && widthRatio > 1)  
        {  
	    	options.inSampleSize =  heightRatio > widthRatio ? heightRatio:widthRatio;  
        }
	    options.inJustDecodeBounds = false;
	    bmptemp = BitmapFactory.decodeFile(AdFCBimp.drr.get(0),options);
		String strfiledir = Environment.getExternalStorageDirectory()
					+ "/dream/temfile";
		String filename = String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		File file = FileUtils.createFile(strfiledir, filename);
		tempfilepath= file.getPath();
		try {
			ImageUtils.saveImageToSD(tempfilepath,bmptemp,100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tempfilepath = AdFCBimp.drr.get(0);
		}
		return tempfilepath;
	}
}
