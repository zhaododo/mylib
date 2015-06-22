package com.android.dream.app.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.util.ImageUtils;
import com.android.dream.app.widget.LoadingDialog;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * @author zhaoj
 * 用户基本信息
 * 
 *
 */
public class AdUserInfoActivity extends BaseActivity{	
	
	private ImageView face;
	private Button editer;
	private TextView name;
	private LoadingDialog loading;
	private Handler mHandler;
		
	private final static int CROP = 200;
	private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dream/Portrait/";
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_user_info);		
		
		//初始化视图控件
		this.initView();
		//初始化视图数据
	}
	
	private void initView(){
		editer = (Button)findViewById(R.id.ad_user_info_editer);
		editer.setOnClickListener(editerClickListener);
		face = (ImageView)findViewById(R.id.ad_user_info_userface);
	}
	
	
	
	private View.OnClickListener editerClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			CharSequence[] items = {
					getString(R.string.img_from_album),
					getString(R.string.img_from_camera)
			};
			imageChooseItem(items);
		}
	};
	
	
	/**
	 * 操作选择
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items )
	{
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("上传头像").setIcon(android.R.drawable.btn_star).setItems(items,
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int item)
				{	
					//判断是否挂载了SD卡
					String storageState = Environment.getExternalStorageState();		
					if(storageState.equals(Environment.MEDIA_MOUNTED)){
						File savedir = new File(FILE_SAVEPATH);
						if (!savedir.exists()) {
							savedir.mkdirs();
						}
					}					
					else{
						UIHelper.ToastMessage(AdUserInfoActivity.this, "无法保存上传的头像，请检查SD卡是否挂载");
						return;
					}

					//输出裁剪的临时文件
					String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					//照片命名
					String origFileName = "osc_" + timeStamp + ".jpg";
					String cropFileName = "osc_crop_" + timeStamp + ".jpg";
					
					//裁剪头像的绝对路径
					protraitPath = FILE_SAVEPATH + cropFileName;
					protraitFile = new File(protraitPath);
					
					origUri = Uri.fromFile(new File(FILE_SAVEPATH, origFileName));
					cropUri = Uri.fromFile(protraitFile);
					
					//相册选图
					if(item == 0) {
						startActionPickCrop(cropUri);
					}
					//手机拍照
					else if(item == 1){
						startActionCamera(origUri);
					}
				}}).create();
		
		 imageDialog.show();
	}
	
	/**
	 * 选择图片裁剪
	 * @param output
	 */
	private void startActionPickCrop(Uri output) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		startActivityForResult(Intent.createChooser(intent, "选择图片"),ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}
	/**
	 * 相机拍照
	 * @param output
	 */
	private void startActionCamera(Uri output) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}
	/**
	 * 拍照后裁剪
	 * @param data 原始图片
	 * @param output 裁剪后图片
	 */
	private void startActionCrop(Uri data, Uri output) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}
	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto() {
//		final Handler handler = new Handler(){
//			public void handleMessage(Message msg) {
//				if(loading != null)	loading.dismiss();
//				if(msg.what == 1 && msg.obj != null){
//					Result res = (Result)msg.obj;
//					//提示信息
//					UIHelper.ToastMessage(AdUserInfoActivity.this, res.getErrorMessage());
//					if(res.OK()){
//						//显示新头像
//						face.setImageBitmap(protraitBitmap);
//					}
//				}else if(msg.what == -1 && msg.obj != null){
//					((AppException)msg.obj).makeToast(AdUserInfoActivity.this);
//				}
//			}
//		};
			
		if(loading != null){
			loading.setLoadText("正在上传头像···");
			loading.show();	
		}
		
		new Thread(){
			public void run() 
			{
//	        	//获取头像缩略图
//	        	if(!StringUtils.isEmpty(protraitPath) && protraitFile.exists())
//	        	{
//	        		protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath, 200, 200);
//	        	}
//		        
				if(protraitBitmap != null)
				{	
//					Message msg = new Message();
//					try {
//						Result res = ((AppContext)getApplication()).updatePortrait(protraitFile);
//						if(res!=null && res.OK()){
//							//保存新头像到缓存
//							String filename = FileUtils.getFileName(user.getFace());
//							ImageUtils.saveImage(AdUserInfoActivity.this, filename, protraitBitmap);
//						}
//						msg.what = 1;
//						msg.obj = res;
//					} catch (AppException e) {
//						e.printStackTrace();
//						msg.what = -1;
//						msg.obj = e;
//					} catch(IOException e) {
//						e.printStackTrace();
//					}
//					handler.sendMessage(msg);
				}				
			};
		}.start();
    }
	
	@Override 
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
	{ 
    	if(resultCode != RESULT_OK) return;
		
    	switch(requestCode){
    		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
    			startActionCrop(origUri, cropUri);//拍照后裁剪
    			break;
    		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
    		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
    			uploadNewPhoto();//上传新照片
    			break;
    	}
	}
}
