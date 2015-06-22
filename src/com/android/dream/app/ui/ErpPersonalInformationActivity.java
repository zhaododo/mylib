package com.android.dream.app.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdAjaxOpt;
import com.android.dream.app.bean.AdXTUser;
import com.android.dream.app.common.UpdateManager;
import com.android.dream.app.util.ImageUtils;
import com.android.dream.app.widget.OnChangedListener;
import com.android.dream.app.widget.PersonalHeaderView;
import com.android.dream.app.widget.PersonalInfoTextViewDialog;
import com.android.dream.app.widget.PersonalInfoTextViewDialog.PersonalInfoBtnListener;
import com.android.dream.app.widget.SlipButton;

/**
 * @author czh
 * 
 * 个人信息（我）明细页面
 *
 */
/**
 * @author zhaododo
 *
 */
public class ErpPersonalInformationActivity extends BaseActivity implements OnChangedListener {
	
	private AppContext appContext;
	
	private Context context;
	
	private ImageView headerBackBtn;		//header部分退出按钮
	
	private SlipButton telSlipButton;		//办公电话SlipButton
	private SlipButton mobileSlipButton;	//手机SlipButton
	
	
	private ImageView feedBackBtn;			//意见反馈按钮
	
	private TextView versionTextView;    //版本信息说明
	private ImageView versionBtn;			//版本信息按钮
	
	private PersonalHeaderView personHeaderView;		//任务头像View
	
	//意见反馈layout
	private LinearLayout feedBackLay;
	
	//版本信息layout
	private LinearLayout versionLay; 		
	
	//修改手机号或座机号码的对话框
	private PersonalInfoTextViewDialog personalInfoTvDialog;
	
	
	/**
	 * 头像文件存放目录
	 */
	private String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dream/Portrait/";
	
	private String FILE_ORA_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dream/";
	
	/**
	 * 头像图片文件对象
	 */
	private File protraitFile;
	
	/**
	 * 头像图片路径
	 */
	private String protraitPath;
	
	/**
	 * 原始图像
	 */
	private Uri origUri;
	
	/**
	 * 裁剪后的图像
	 */
	private Uri cropUri;
	
	/**
	 * 原始图像文件名
	 */
	private String origFileName = "";
	
	/**
	 * 裁剪后的图像文件名
	 */
	private String cropFileName = "";
	
	/**
	 * 裁剪区域（正方形）
	 */
	private final static int CROP = 200;
	
	
	/*************************************************************************************************************
	 * 
	 * Handler处理消息列表开始
	 * 
	 *************************************************************************************************************/
	
	/**
	 * 上传头像
	 */
	private final static int MSG_UPLOAD_IMAGE = 0x01;
	
	/**
	 * 上传头像成功
	 */
	private final static int MSG_UPLOAD_SUCCESS = 0x02;
	
	
	/**
	 *  网路异常、错误消息
	 */
	private final static int MSG_ERROR = -1;
	
	/**
	 * 上传头像成功后删除原头像
	 */
	private final static int MSG_DEL_IMAGE = 0x04;
	
	/**
	 * 下载头像
	 */
	private final static int MSG_DWONLOAD_IMAGE = 0x05;
	
	
	private final static int DELETENEWPIC = 0x06;
	
	
	/**
	 * 关闭Activity
	 */
	public final static int MSG_ERP_PERSON_INFO_FINISH_ACTIVITY = 0x07;
	
	
	/*************************************************************************************************************
	 * 
	 * Handler处理消息列表结束
	 * 
	 *************************************************************************************************************/
	
	
	/**
	 *  头像对象(Bitmap)
	 */
	private Bitmap btFaceImage; 
	

	
	/**
	 *  当前登录工号
	 */
	private String userno = "";
	
	/**
	 *  姓名
	 */
	private TextView tv_name_personal_info;
	
	/**
	 * 工号
	 */
	private TextView tv_userno_personal_info;
	
	/**
	 * 公司（单位）
	 */
	private TextView tv_company_personal_info;
	
	/**
	 * 部门
	 */
	private TextView tv_depname_personal_info;
	
	/**
	 * 座机号码
	 */
	private TextView tv_telephone_personal_info;
	
	/**
	 * 手机号码
	 */
	private TextView tv_mobile_personal_info;
	
	/**
	 * 标识更新保密信息
	 */
	private final static int MSG_CHANGE_SEC = 0x07;
	/**
	 * 标识更新保密信息成功
	 */
	private final static int MSG_CHANGE_SEC_SUCESS = 0x08;
	/**
	 * 标识更新电话
	 */
	private final static int MSG_CHANGE_USERPHONE = 0x09;
	/**
	 * 标识更新电话成功
	 */
	private final static int MSG_CHANGE_USERPHONE_SUCCESS = 0x10;
	
	//手机号码
	public static final String TYPE_MOBILE="MOBILE";
	//办公室号码
	public static final String TYPE_TELEPHONE="TELEPHONE";
	//要更改的号码
	private EditText et_dialogPersonhpone;
	//判断哪个按钮
	private int phonetypeid;
	//电话是否公开
	private TextView ad_telephone_property_personal_info;
	//手机是否公开
	private TextView ad_mobile_property_personal_info;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_erp_personalinfo_detail);	
		appContext = (AppContext) getApplication();
		context = this;
		//当前用户
		userno = appContext.getCurrentUser();
		
		initEnvironment();
		initViews();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	/**
	 * 更新用户基本信息
	 */
	private void loadUserInfo()
	{
		if (appContext != null)
		{
			AdXTUser xtUser = appContext.getXtUser();
			if (xtUser != null)
			{
				Resources res = this.getResources();
				int publicETColor = res.getColor(R.color.ad_personalinformation_et_publiccolor);
				int privateETColor = res.getColor(R.color.ad_personalinformation_et_privatecolor);
				tv_name_personal_info.setText(xtUser.getUsername());
				tv_userno_personal_info.setText(xtUser.getUserno());
				tv_company_personal_info.setText(xtUser.getCompanyname());
				tv_depname_personal_info.setText(xtUser.getDeppartmentname());
				tv_telephone_personal_info.setText(xtUser.getTelephone());
				tv_mobile_personal_info.setText(xtUser.getMobilephone());
				if(xtUser.getIsvisibletelephone().equals("0")){
					telSlipButton.setChecked(false);
					tv_telephone_personal_info.setTextColor(privateETColor);
					ad_telephone_property_personal_info.setTextColor(privateETColor);
					ad_telephone_property_personal_info.setText(R.string.aderpuserhiddeninfo);
					
				}else{
					telSlipButton.setChecked(true);
					tv_telephone_personal_info.setTextColor(publicETColor);
					ad_telephone_property_personal_info.setTextColor(publicETColor);
					ad_telephone_property_personal_info.setText(R.string.aderpuserdishiddeninfo);
				}
				if(xtUser.getIsvisiblemobile().equals("0")){
					mobileSlipButton.setChecked(false);
					tv_mobile_personal_info.setTextColor(privateETColor);
					ad_mobile_property_personal_info.setTextColor(privateETColor);
					ad_mobile_property_personal_info.setText(R.string.aderpuserhiddeninfo);
				}else{
					mobileSlipButton.setChecked(true);
					tv_mobile_personal_info.setTextColor(publicETColor);
					ad_mobile_property_personal_info.setTextColor(publicETColor);
					ad_mobile_property_personal_info.setText(R.string.aderpuserdishiddeninfo);
				}
			}
		}
	}
	
	private void initViews(){
		
		headerBackBtn = (ImageView) findViewById(R.id.personalinfo_header_back_btn);
		headerBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				ErpPersonalInformationActivity.this.finish();
			}
		});
		
		//办公电话SlipButton
		telSlipButton = (SlipButton) this.findViewById(R.id.personalinformation_detail_slipbutton_tel);
		telSlipButton.SetOnChangedListener(this, telSlipButton);
		
		
		//手机号码SlipButton
		mobileSlipButton = (SlipButton) this.findViewById(R.id.personalinformation_detail_slipbutton_mobile);
		mobileSlipButton.SetOnChangedListener(this, mobileSlipButton);
		
		//姓名
		tv_name_personal_info = (TextView) findViewById(R.id.ad_name_personal_info);
		//工号
		tv_userno_personal_info = (TextView) findViewById(R.id.ad_userno_personal_info);
		//单位
		tv_company_personal_info = (TextView) findViewById(R.id.ad_company_personal_info);
		//部门
		tv_depname_personal_info = (TextView) findViewById(R.id.ad_depname_personal_info);
		//办公电话是否公开
		ad_telephone_property_personal_info = (TextView) findViewById(R.id.ad_telephone_property_personal_info);
		//手机是否公开
		ad_mobile_property_personal_info = (TextView) findViewById(R.id.ad_mobile_property_personal_info);
		
		//办公电话TextView
		tv_telephone_personal_info = (TextView) findViewById(R.id.ad_telephone_personal_info);
		tv_telephone_personal_info.setOnClickListener(new PhoneTVClickListener());
		
		//手机号码TextView
		tv_mobile_personal_info = (TextView) findViewById(R.id.ad_mobile_personal_info);
		tv_mobile_personal_info.setOnClickListener(new PhoneTVClickListener());
		
		//根据SlipButton状态不同改变EditText的字体颜色
		changeETBgColor(telSlipButton.isChecked(), telSlipButton.getId());
		changeETBgColor(mobileSlipButton.isChecked(), mobileSlipButton.getId());
		
		//意见反馈按钮
		feedBackBtn = (ImageView) findViewById(R.id.personalinfo_ortherinfo_feedback_btn);
		feedBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent erpItent=new Intent(context,AdFeedbackActivity.class);
				startActivity(erpItent);
			}
		});
		
		
		//版本信息说明
		versionTextView = (TextView) findViewById(R.id.personalinfo_ortherinfo_version_tv);
		
		if (appContext != null)
		{
			versionTextView.setText(appContext.getPackageInfo().versionName);
		}
		
		//版本信息按钮
		versionBtn = (ImageView) findViewById(R.id.personalinfo_ortherinfo_version_btn);
		versionBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UpdateManager.getUpdateManager().checkAppUpdate(context, true);
			}
		});
		
		//人物头像View	  人物头像资源现保存在/Dream/Portrait/文件夹下面
		personHeaderView = (PersonalHeaderView) findViewById(R.id.ad_personalinfo_personalheader_iv);
		personHeaderView.setOnClickListener(new PersonalHeaderOnClickListener());

		//反馈信息点击以后layout变暗
		feedBackLay = (LinearLayout) findViewById(R.id.personalinformation_detail_feedBacklay);
		feedBackLay.setOnTouchListener(new LayoutTouchListener());
		
		//版本信息点击以后layout变暗
		versionLay = (LinearLayout) findViewById(R.id.personalinformation_detail_versionlay);
		versionLay.setOnTouchListener(new LayoutTouchListener());
				
		//加载用户头像信息
		loadheaderPic();
		
		//加载用户基本信息
		loadUserInfo();
	}
	
	
	/**
	 * mobile和tel的textview点击事件
	 * @author Administrator
	 *
	 */
	class PhoneTVClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			
			phonetypeid = view.getId();
			String diatitle = "";
			switch(phonetypeid){
			case R.id.ad_telephone_personal_info:
				diatitle = getResources().getString(R.string.aderpuserupdatetelephone);
			break;
			case R.id.ad_mobile_personal_info:
				diatitle = getResources().getString(R.string.aderpuserupdatemobilephone);
			break;
			}
			createUpdatePhoneDialog(diatitle);
			personalInfoTvDialog.show();
			
		}
		
	}
	
	/**
	 * slipbutton状态改变的事件
	 */
	public void OnChanged(boolean CheckState, View v) {
		
		//
		changeETBgColor(CheckState, v.getId());
		//根据SlipButton状态不同改变EditText的字体颜色
//		if (CheckState) {
//			Toast.makeText(this, "您的号码改为公开模式!", Toast.LENGTH_SHORT).show();
//		} else {
//			Toast.makeText(this, "您的号码改为隐私模式!", Toast.LENGTH_SHORT).show();
//		}
		changeUserSec(v.getId());
	}
	/**
	 * 根据SlipButton状态不同改变EditText的字体颜色
	 * @param checkState
	 */
	private void changeETBgColor(boolean checkState, int viewID){
		Resources res = this.getResources();
		
		int publicETColor = res.getColor(R.color.ad_personalinformation_et_publiccolor);
		int privateETColor = res.getColor(R.color.ad_personalinformation_et_privatecolor);
		
		if(viewID == R.id.personalinformation_detail_slipbutton_tel){
			if (checkState) {
				//公开模式号码颜色为#333333
				tv_telephone_personal_info.setTextColor(publicETColor);
				ad_telephone_property_personal_info.setTextColor(publicETColor);
			} else {
				//隐私模式号码颜色为#e4e4e4
				tv_telephone_personal_info.setTextColor(privateETColor);
				ad_telephone_property_personal_info.setTextColor(privateETColor);
			}
		}else if (viewID == R.id.personalinformation_detail_slipbutton_mobile) {
			if (checkState) {
				//公开模式号码颜色为#333333
				tv_mobile_personal_info.setTextColor(publicETColor);
				ad_mobile_property_personal_info.setTextColor(publicETColor);
			} else {
				//隐私模式号码颜色为#e4e4e4
				tv_mobile_personal_info.setTextColor(privateETColor);
				ad_mobile_property_personal_info.setTextColor(privateETColor);
			}
		}
		
	}
	
	/**
	 * @author zhaoj
	 * 
	 *  点击头像事件
	 */
	class PersonalHeaderOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			final CharSequence[] items = {
					getString(R.string.img_from_album),
					getString(R.string.img_from_camera)
			};
			imageChooseItem(items);
		}
		
	}
	
	/**
	 * 头像操作选择
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items )
	{
//		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("上传头像").setIcon(android.R.drawable.btn_star).setItems(items,
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("上传头像").setItems(items,
				new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int item)
				{	
					if(!isSDCardExist())
					{
						UIHelper.ToastMessage(context, "无法保存上传的头像，请检查SD卡是否挂载");
						return;
					}
						
					//输出裁剪的临时文件
					String timeStamp = getFileName();
					//照片命名
					origFileName = "osc_" + timeStamp;
					cropFileName = "osc_crop_" + timeStamp;
					
					//裁剪头像的绝对路径
					protraitPath = getUserDirectory()+"/"+ cropFileName;
					protraitFile = new File(protraitPath);
					
					origUri = Uri.fromFile(new File(getUserOraDirectory(), origFileName));
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
		intent.putExtra("return-data", false);
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
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
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
    			if (data != null){
                    sentPicToNext(data);	
    			}
    			//上传新照片
    			uploadNewPhoto();
    			break;
    	}
	}
	
	 // 将进行剪裁后的图片传递到界面上
    private void sentPicToNext(Intent picdata) {
    	
    	Bitmap photo = BitmapFactory.decodeFile(cropUri.getPath());
        if (photo!=null) {
        	personHeaderView.setmSrc(photo);
        	personHeaderView.invalidate();
        }
//        Bundle bundle = picdata.getExtras();
//        if (bundle != null) {
////          Bitmap photo = bundle.getParcelable("data");
//            
//        }
    }
	
	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto() {
		new Thread(){
			public void run() 
			{	
					Message msg = new Message();
					msg.arg1 = MSG_UPLOAD_IMAGE;
					try {
						AdAjaxOpt opt =null;
						Map<String,Object> params = new HashMap<String,Object>();
						params.put("userno", appContext.getCurrentUser());
						Map<String, File> filesMap = new HashMap<String,File>();
						filesMap.put("headerpic", protraitFile);
						opt = appContext.uploaduserHeaderPic(params,filesMap);
						
						if (opt != null && opt.getStatus().equals(AdAjaxOpt.OK_STATUS))
						{
							msg.what = MSG_UPLOAD_SUCCESS;
							msg.obj = opt;
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg.what = MSG_ERROR;
						msg.obj = e;
					}
					handler.sendMessage(msg);				
			};
		}.start();
    }
	
	/**
	 * 加载头像图片
	 */
	private void loadheaderPic() {
		
		//获得用户对应的文件目录
		String userDirectory = getUserDirectory();
		
		File filedir = new File(userDirectory);
		
		File[] filelist = null;
		File fileheaderpic = null;
		if(filedir.exists())
		{
			filelist = filedir.listFiles();
			
			if(filelist != null  &&  filelist.length>0)
			{
				//取其中的一笔
				fileheaderpic = filelist[0];
				Bitmap bmp = BitmapFactory.decodeFile(fileheaderpic.getPath());
				personHeaderView.setmSrc(bmp);
				personHeaderView.invalidate();
				return;
			}
		}
		
		downloadFile();
	}
	
	/**
	 * 下载头像
	 */
	private void downloadFile() {

		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.arg1 = MSG_DWONLOAD_IMAGE;
				FileOutputStream fos =null;
				try {
					
					//头像缓存对象
					btFaceImage = appContext.downloaduserHeaderPic(userno);
					
					if (btFaceImage != null)
					{
						msg.what = MSG_UPLOAD_SUCCESS;
						msg.obj = btFaceImage;
						
						//下载成功缓存到文件
						String fileName = "osc_crop_" +getFileName();
						String basefiledir = getUserDirectory();
						String filePath = basefiledir + "/" +fileName;  
						fos = new FileOutputStream(filePath);  
						btFaceImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
					}
				} catch (Exception e) {
					msg.what = MSG_ERROR;
					msg.obj = e;
				}finally{
					if(fos!=null){
						try {
							fos.flush();
							fos.close();
						} catch (IOException e) {
							msg.what = MSG_ERROR;
							msg.obj = e;
						}
					}
				}
				handler.sendMessage(msg);
			}
		}.start();

	}
	
	
	/**
	 * 
	 */
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.arg1){
			case MSG_DWONLOAD_IMAGE:
				if (msg.what == MSG_UPLOAD_SUCCESS) {
					personHeaderView.setmSrc(btFaceImage);
					personHeaderView.invalidate();
					Toast.makeText(ErpPersonalInformationActivity.this, "头像加载成功", Toast.LENGTH_SHORT).show();

				} else if (msg.what == MSG_ERROR) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(ErpPersonalInformationActivity.this, "头像加载失败", Toast.LENGTH_SHORT).show();

				}
			break;
			case MSG_UPLOAD_IMAGE:
				if (msg.what == MSG_UPLOAD_SUCCESS) {
					Toast.makeText(ErpPersonalInformationActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
					//上传成功时删除文件原始文件，保留最新文件
					removeheaderpic(MSG_DEL_IMAGE);
					
					//成功后更新
					setResult(MSG_ERP_PERSON_INFO_FINISH_ACTIVITY);
				} else if (msg.what == MSG_ERROR) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(ErpPersonalInformationActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
					//上传失败时删除新生成文件，保留原文件
					removeheaderpic(DELETENEWPIC);
					
					//成功后更新
					setResult(MSG_ERP_PERSON_INFO_FINISH_ACTIVITY);
				}
			break;
			case MSG_CHANGE_SEC:
				if (msg.what == MSG_CHANGE_SEC_SUCESS) {
//					appContext.getXtUser().setIsvisiblemobile(isvisiblemobile);
					Boolean ishiden = msg.getData().getBoolean("ishiden");
					switch (msg.arg2){
					case R.id.personalinformation_detail_slipbutton_tel:
						if(ishiden){
							appContext.getXtUser().setIsvisibletelephone("0");
							ad_telephone_property_personal_info.setText(R.string.aderpuserhiddeninfo);
							Toast.makeText(ErpPersonalInformationActivity.this, "您的固话改为隐私模式!", Toast.LENGTH_SHORT).show();
						}else{
							appContext.getXtUser().setIsvisibletelephone("1");
							ad_telephone_property_personal_info.setText(R.string.aderpuserdishiddeninfo);
							Toast.makeText(ErpPersonalInformationActivity.this, "您的固话改为公开模式!", Toast.LENGTH_SHORT).show();
						}
					break;
					case R.id.personalinformation_detail_slipbutton_mobile:
						if(ishiden){
							appContext.getXtUser().setIsvisiblemobile("0");
							ad_mobile_property_personal_info.setText(R.string.aderpuserhiddeninfo);
							Toast.makeText(ErpPersonalInformationActivity.this, "您的手机改为隐私模式!", Toast.LENGTH_SHORT).show();
						}else{
							appContext.getXtUser().setIsvisiblemobile("1");
							ad_mobile_property_personal_info.setText(R.string.aderpuserdishiddeninfo);
							Toast.makeText(ErpPersonalInformationActivity.this, "您的手机改为公开模式!", Toast.LENGTH_SHORT).show();
						}
					break;
					}
				} else if (msg.what == MSG_ERROR) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(ErpPersonalInformationActivity.this, "更新失败!", Toast.LENGTH_SHORT).show();
				}
			break;
			case MSG_CHANGE_USERPHONE:
				if (msg.what == MSG_CHANGE_USERPHONE_SUCCESS) {
//					personalInfoTvDialog.dismiss();
					String phoneno = msg.getData().getString("phoneno");
					switch (msg.arg2){
					case R.id.ad_telephone_personal_info:
						appContext.getXtUser().setTelephone(phoneno);
						tv_telephone_personal_info.setText(phoneno);
						Toast.makeText(ErpPersonalInformationActivity.this, "您的固话更新成功!", Toast.LENGTH_SHORT).show();
						
					break;
					case R.id.ad_mobile_personal_info:
						appContext.getXtUser().setMobilephone(phoneno);
						tv_mobile_personal_info.setText(phoneno);
						Toast.makeText(ErpPersonalInformationActivity.this, "您的手机更新成功!", Toast.LENGTH_SHORT).show();
						
					break;
					}
				} else if (msg.what == MSG_ERROR) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(ErpPersonalInformationActivity.this, "号码更新失败!", Toast.LENGTH_SHORT).show();
				}
			break;
			}
			
		}
	};
	
	/**
	 * 根据上传是否成功删除相应的文件
	 * @param flg
	 */
	private void removeheaderpic(int flg){
		File filedir = new File(getUserDirectory());
		File[] filelist = null;
		switch(flg){
		//删除历史文件，保留最新文件
		case  MSG_DEL_IMAGE:
			filelist = filedir.listFiles();
			for(File file : filelist){
				if(!file.getName().equals(cropFileName)){
					file.delete();
					}
				}
			//删除拍照文件，因位置改变不在此文件夹下
			File orafile = new File(origUri.getPath());
			if(orafile.exists()){
				orafile.delete();
			}
		break;
		//删除新文件
		case  DELETENEWPIC:
			filelist = filedir.listFiles();
			for(File file : filelist){
				if(file.getName().equals(origFileName)||
						file.getName().equals(cropFileName)){
					file.delete();
					}
				}
			//删除拍照文件，因位置改变不在此文件夹下
			File orafile_d = new File(origUri.getPath());
			if(orafile_d.exists()){
				orafile_d.delete();
			}
		break;
		}
	}
	
	
	/**
	 * 初始化目录环境
	 */
	private void initEnvironment()
	{
		String[] dirPaths = {
				FILE_ORA_SAVEPATH,
				FILE_ORA_SAVEPATH+"/Portrait/" };
		for(int i=0; i<dirPaths.length; i++){
			File filedir = new File(dirPaths[i]);
	        if(!filedir.exists()){
	        	filedir.mkdir();
	        }
		}
		
	}
	
	
	/**
	 * 获得用户对应的文件目录
	 */
	private String getUserDirectory()
	{
		String userDirectory = FILE_SAVEPATH+userno;
		File filedir = new File(userDirectory);
        if(!filedir.exists()){
        	filedir.mkdirs();
        }
        return userDirectory;
	}
	/**
	 * 拍照临时文件
	 * @return
	 */
	private String getUserOraDirectory()
	{
		String userDirectory = FILE_ORA_SAVEPATH;
		File filedir = new File(userDirectory);
        if(!filedir.exists()){
        	filedir.mkdirs();
        }
        return userDirectory;
	}
	
	
	/**
	 * 判断是否能访问存储设备（包括内置的或扩展的SDCARD）
	 */
	private boolean isSDCardExist()
	{
		return  Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * 用于生成新的文件名称
	 */
	private String getFileName()
	{
		String filename = userno;
    	//输出裁剪的临时文件
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		//照片命名
		filename =userno+"_"+timeStamp + ".jpg";
		
		return filename;
	}
    
	/**
	 * 反馈信息和版本信息layout点击事件
	 * layout点击以后background变暗
	 *
	 */
	class LayoutTouchListener implements OnTouchListener {
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			
			Resources res = context.getResources();
			int bgColor = res.getColor(R.color.ad_personalinformation_layout_click_bg);
			
			switch (event.getAction()) {
			// 按下
			case MotionEvent.ACTION_DOWN:
				if(view.getId() == R.id.personalinformation_detail_feedBacklay) {
					feedBackLay.setBackgroundColor(bgColor);
					feedBackBtn.setImageResource(R.drawable.drawable_expand_close);
				}else if(view.getId() == R.id.personalinformation_detail_versionlay) {
					versionLay.setBackgroundColor(bgColor);
					versionBtn.setImageResource(R.drawable.drawable_expand_close);
				}
					
				break;
			// 移动
			case MotionEvent.ACTION_MOVE:
				break;
			// 松开
			case MotionEvent.ACTION_UP:
				if(view.getId() == R.id.personalinformation_detail_feedBacklay) {
					feedBackLay.setBackgroundResource(0);
					feedBackBtn.setImageResource(R.drawable.drawable_expand_close_orange);
					Intent erpItent=new Intent(context,AdFeedbackActivity.class);
					startActivity(erpItent);
				}else if(view.getId() == R.id.personalinformation_detail_versionlay) {
					versionLay.setBackgroundResource(0);
					versionBtn.setImageResource(R.drawable.drawable_expand_close_orange);
					UpdateManager.getUpdateManager().checkAppUpdate(context, true);
				}
				feedBackLay.setBackgroundResource(0);
				break;
			default:
				break;
			}
			return true;
		}
	}
	
	/**
	 * 更改保密信息
	 */
	private void changeUserSec(final int viewid) {
		
		new Thread(){
			public void run() 
			{	
				String phonetype = "";
				Boolean isHide = false;
				//根据id判断更改是电话还是手机
				switch (viewid){
				case R.id.personalinformation_detail_slipbutton_tel:
					phonetype = TYPE_TELEPHONE;
					isHide = !telSlipButton.isChecked();
				break;
				case R.id.personalinformation_detail_slipbutton_mobile:
					phonetype = TYPE_MOBILE;
					isHide = !mobileSlipButton.isChecked();
				break;
				}
				Message msg = new Message();
				msg.arg1 = MSG_CHANGE_SEC;
				try {
					AdAjaxOpt opt =null; 
					Bundle bundle = new Bundle();
					opt = appContext.xtHideUserInfo(userno,phonetype,isHide);
					
					if (opt != null && opt.getStatus().equals(AdAjaxOpt.OK_STATUS))
					{
						msg.what = MSG_CHANGE_SEC_SUCESS;
						msg.obj = opt;
						msg.arg2 = viewid;
						bundle.putBoolean("ishiden", isHide);
						msg.setData(bundle);
					}
				} catch (Exception e) {
					//e.printStackTrace();
					msg.what = MSG_ERROR;
					msg.obj = e;
				}
				handler.sendMessage(msg);				
			};
		}.start();
    }
	
	/**
	 * 更新号码
	 * @param viewid
	 */
	private void updateuserphone(final int viewid,final String phoneno) {
		personalInfoTvDialog.dismiss();
		new Thread(){
			public void run() 
			{	
				Message msg = new Message();
				msg.arg1 = MSG_CHANGE_USERPHONE;
				try {
					AdAjaxOpt opt =null;
					Bundle bundle = new Bundle();
					//根据id判断更改是电话还是手机
					switch (viewid){
					case R.id.ad_telephone_personal_info: 
						opt = appContext.xtUpdateUserTelephone(userno,phoneno);
					break;
					case R.id.ad_mobile_personal_info:
						opt = appContext.xtUpdateUserMobile(userno,phoneno);
					break;
					}
					if (opt != null && opt.getStatus().equals(AdAjaxOpt.OK_STATUS))
					{
						msg.what = MSG_CHANGE_USERPHONE_SUCCESS;
						msg.obj = opt;
						msg.arg2 = viewid;
						bundle.putString("phoneno", phoneno);
						msg.setData(bundle);
					}
				} catch (Exception e) {
					//e.printStackTrace();
					msg.what = MSG_ERROR;
					msg.obj = e;
				}
				handler.sendMessage(msg);				
			};
		}.start();
    }
	/**
	 * 更新号码对话框
	 * @param diatitle 对话框标题
	 */
	private void createUpdatePhoneDialog(String diatitle){
		//办公电话和手机号码的EditText点击后弹出dialog的按钮监听
		PersonalInfoBtnListener personalInfoBtnListener = new PersonalInfoBtnListener() {

			@Override
			public void btnAction(View v,EditText et) {
				String phoneno = "";
				switch (v.getId()) {
				case R.id.personalinfo_phonedialog_savebt:
					if(et.getText() != null && et.getText().length() >0){
						phoneno = et.getText().toString();
					}else{
						return;
					}
					//更新用户号码
					switch(phonetypeid){
					case R.id.ad_telephone_personal_info:
						 updateuserphone(phonetypeid,phoneno);
					break;
					case R.id.ad_mobile_personal_info:
						updateuserphone(phonetypeid,phoneno);
					break;
					}
					break;
				case R.id.personalinfo_phonedialog_cancelbt:
					personalInfoTvDialog.dismiss();
					break;
				default:
					break;
				}
			}
			
		};
		//创建办公电话和手机号码的EditText点击后弹出dialog
		personalInfoTvDialog = new PersonalInfoTextViewDialog(ErpPersonalInformationActivity.this, R.style.PersonalInfoTVDialog, personalInfoBtnListener, diatitle);
		Window dialogWindow = personalInfoTvDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL;
		dialogWindow.setAttributes(lp);
		personalInfoTvDialog.setCanceledOnTouchOutside(true);
		//personalInfoTvDialog.hide();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (personalInfoTvDialog != null)
		{
			personalInfoTvDialog.dismiss();
		}
		
	}
}
