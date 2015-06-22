package com.android.dream.app.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.AppManager;
import com.android.dream.app.R;
import com.android.dream.app.api.ApiClient;
import com.android.dream.app.bean.AdErpBoard;
import com.android.dream.app.util.FileOpenUtils;
import com.android.dream.app.util.FileUtils;
import com.android.dream.app.util.ImageUtils;
import com.android.dream.app.util.StringUtils;

/**
 * @author zhaoj
 * 常量定义
 *
 */
public class UIHelper {
	
	/**********************************************************************************************************
	 *  朋友圈相关常量定义
	 *  
	 ***********************************************************************************************************
	 */
	
	/**
	 *  纯文本
	 */
	public final static int  FRINDS_CICLE_TYPE_TEXT= 0;
	
	/**
	 *  图文
	 */
	public final static int  FRINDS_CICLE_TYPE_TEXT_PIC= 1;
	
	/**
	 * 贺卡
	 */
	public final static int  FRINDS_CICLE_TYPE_CARD= 2;
	
	/**
	 * 分享链接
	 */
	public final static int  FRINDS_CICLE_TYPE_SHARE= 3;
	
	
	/**
	 * 赞对应消息
	 */
	public final static int MSG_ZAN = 0x001;
	
	/**
	 * 评论对应消息
	 */
	public final static int MSG_COMMENT = 0x002;
	
	/**
	 *屏蔽消息
	 */
	public final static int MSG_PINGBI = 0x021;
	
	/**
	 *删除消息
	 */
	public final static int MSG_DELFC = 0x022;
	
	
	public final static int DATA_LOAD_ING = 0x001;
	public final static int DATA_LOAD_COMPLETE = 0x002;
	public final static int DATA_LOAD_FAIL = 0x003;
	
	
	/**********************************************************************************************************
	 *  MSG相关常量定义
	 *  
	 ***********************************************************************************************************
	 */
	public final static int MSG_SUCCESS = 0x001;
	public final static int MSG_FAIL = 0x002;
	public final static int MSG_ERROR = -1;
	
	public static final int PAGE_SIZE = 20;
	
	//数据加载状态
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	
	public final static int LISTVIEW_DATA_MORE = 0x05;
	public final static int LISTVIEW_DATA_LOADING = 0x06;
	public final static int LISTVIEW_DATA_FULL = 0x07;
	public final static int LISTVIEW_DATA_EMPTY = 0x08;
	
	public final static int LISTVIEW_DATATYPE_NEWS = 0x09;
	
	//ERP报表的类别
	public final static int LISTVIEW_ERP_REPORT = 0x10;
	
	//ERP公告类别
    public final static int LISTVIEW_ERP_BOARD = 0x11;
    
    public final static int  LISTVIEW_CONTACTS_SEARCH_RESULT = 0x12;
    
    //ERP订单录入量
    public final static int LISTVIEW_ERP_ORDENTER = 0x13;
    
    //朋友圈
    public final static int LISTVIEW_FIREND_CICLE = 0x14;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;
	
	private static AppContext appContext;
	
	/**
	 * 提示信息
	 * @param cont
	 * @param msg
	 */
	public static void ToastMessage(Context cont,String msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,int msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,String msg,int time)
	{
		Toast.makeText(cont, msg, time).show();
	}
	
	public static void showLoginDialog(Context context)
	{
		ToastMessage(context,"认证信息失效，请重新登录！",Toast.LENGTH_LONG);
		Intent intent = new Intent(context,LoginActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	
	
	/**
	 * 加载显示用户头像
	 * @param imgFace
	 * @param faceURL
	 */
	public static void showUserFace(final ImageView imgFace,final String faceURL)
	{
		showLoadImage(imgFace,faceURL,imgFace.getContext().getString(R.string.msg_load_userface_fail));
	}
	
	/**
	 * 加载显示图片
	 * @param imgFace
	 * @param faceURL
	 * @param errMsg
	 */
	public static void showLoadImage(final ImageView imgView,final String imgURL,final String errMsg)
	{
		//读取本地图片
		if(StringUtils.isEmpty(imgURL) || imgURL.endsWith("portrait.gif")){
			Bitmap bmp = BitmapFactory.decodeResource(imgView.getResources(), R.drawable.widget_dface);
			imgView.setImageBitmap(bmp);
			return;
		}
		
		//是否有缓存图片
    	final String filename = FileUtils.getFileName(imgURL);
    	//Environment.getExternalStorageDirectory();返回/sdcard
    	String filepath = imgView.getContext().getFilesDir() + File.separator + filename;
		File file = new File(filepath);
		if(file.exists()){
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			imgView.setImageBitmap(bmp);
			return;
    	}
		
		//从网络获取&写入图片缓存
		String _errMsg = imgView.getContext().getString(R.string.msg_load_image_fail);
		if(!StringUtils.isEmpty(errMsg))
			_errMsg = errMsg;
		final String ErrMsg = _errMsg;
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1 && msg.obj != null){
					imgView.setImageBitmap((Bitmap)msg.obj);
					try {
                    	//写图片缓存
						ImageUtils.saveImage(imgView.getContext(), filename, (Bitmap)msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					ToastMessage(imgView.getContext(), ErrMsg);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = ApiClient.getNetBitmap(imgURL);
					msg.what = 1;
					msg.obj = bmp;
				} catch (AppException e) {
					e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	
	

	/**
	 * 传递AdErpBoard参数，显示明细记录
	 * @param context
	 * @param board
	 */
	public static void showErpBoardDetail(Context context, AdErpBoard board)
	{
		Intent intent = new Intent(context, ErpBoardDetailActivity.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("ERP_BOARD", board);
		intent.putExtras(mBundle);
		context.startActivity(intent);
	}
	

	/**
	 * 打开文件
	 * @param context
	 * @param currentPath
	 */
	public static void openFile(Context context, File currentPath)
	{
		FileOpenUtils fileUtils = new FileOpenUtils(context);
		
		if(currentPath!=null && currentPath.isFile())
        {
			fileUtils.openfile(currentPath);
		}
	}
	
	public static void sendAppCrashReport(final Context cont, final String crashReport)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//发送异常报告
				Intent i = new Intent(Intent.ACTION_SEND);
				//i.setType("text/plain"); //模拟器
				i.setType("message/rfc822") ; //真机
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"zhaododo@163.com"});
				i.putExtra(Intent.EXTRA_SUBJECT,"手机南钢客户端 - 错误报告");
				i.putExtra(Intent.EXTRA_TEXT,crashReport);
				//cont.startActivity(Intent.createChooser(i, "发送错误报告"));
				try
				{
					senderrortoServer(cont,crashReport);
				}
				finally
				{
					//退出
					AppManager.getAppManager().AppExit(cont);
				}
				
				
			}
		});
		builder.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.show();
	}
	/**
	 * 信息反馈服务器-wch
	 * @param cont
	 * @param crashReport
	 */
	public static void senderrortoServer(Context cont,String crashReport){
		int errorlength = 0;
		appContext = (AppContext) cont.getApplicationContext();
		Map<String, Object> paramters = new HashMap<String, Object>();
		paramters.put("userno", appContext.getCurrentUser());
		if(crashReport.length()>2000){
			errorlength = 2000;
		}else{
			errorlength = crashReport.length();
		}
		paramters.put("strloginfo", crashReport.substring(0, errorlength));
		PackageInfo pinfo = ((AppContext)cont.getApplicationContext()).getPackageInfo();
		paramters.put("strversion",pinfo.versionName+"("+pinfo.versionCode+")\n");
		paramters.put("strPhoneType",android.os.Build.VERSION.RELEASE+"("+android.os.Build.MODEL+")\n");
		try {
			appContext.saveClientError(paramters);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

}
