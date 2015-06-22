package com.android.dream.app.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdUserPermissionTemp;
import com.android.dream.app.bean.AdXTUser;
import com.android.dream.app.widget.PersonalHeaderView;

/**
 * @author zhaoj
 * 个人信息（我）功能模块
 *
 */
public class AdMyInfoFragment extends Fragment{
	
	private String[] strfro;
	private int[]  intto;
	private SimpleAdapter  simpleadapter;
	
	//协同用户基本信息
	private AdXTUser mXtUser;
	
	private AppContext appContext;
	
	/**
	 * 头像
	 */
	private PersonalHeaderView v_roundImage_myinfo_main;
	
	/**
	 * 姓名
	 */
	private TextView tv_name_myinfo_main;
	
	/**
	 * 工号
	 */
	private TextView tv_userno_myinfo_main;
	
	/**
	 * 单位名称（公司名称）
	 */
	private TextView tv_company_myinfo_main;
	
	private final static int MSG_SUCCESS = 0x01;  
	
	private final static int MSG_ERROR = -1;  
	//头像
	private Bitmap btFaceImage; 
	//用户名
	private String userno;
	//标识处理下载
	private final static int MSG_DWONLOAD_IMAGE = 0x02;
	//下载成功
	private final static int MSG_UPLOAD_SUCCESS = 0x03;
	/**
	 * 头像文件存放目录
	 */
	private String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dream/Portrait/";
	
	private View rootview;
	
	//用户权限列表
	private List<AdUserPermissionTemp> userPermissionTemplis = null;
	
	//功能图片
	private int[] drawables = null;
	//功能说明
	private String[] item;
	//功能列表
	private ArrayList<HashMap<String, Object>> listItems;
	
	/**
	 *  处理协同办用户基本信息
	 */
	private Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			
			if(msg.what == MSG_SUCCESS && mXtUser!=null){
				
				if (appContext != null)
				{
					tv_name_myinfo_main.setText(mXtUser.getUsername());
					tv_userno_myinfo_main.setText(mXtUser.getUserno());
					tv_company_myinfo_main.setText(mXtUser.getCompanyname());
					appContext.setXtUser(mXtUser);
				}
			}
			
		    if(msg.arg1 == MSG_DWONLOAD_IMAGE && msg.what == MSG_UPLOAD_SUCCESS){
					v_roundImage_myinfo_main.setmSrc(btFaceImage);
					v_roundImage_myinfo_main.invalidate();
		    }
		};
	};
	
	

	/**
	 *  跳转到用户基本信息界面
	 */
	public  void showPersonalInfoActivity()
	{
		Intent erpItent=new Intent(getActivity(),ErpPersonalInformationActivity.class);
		//100作为参数传递到ErpPersonalInformationActivity，被忽略。
		startActivityForResult(erpItent,100);
	}
	
	/**
	 * @author zhaoj
	 * 
	 * 跳转到用户基本信息click事件
	 *
	 */
	class  MyOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			showPersonalInfoActivity();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (rootview == null)
		{
			appContext = (AppContext) getActivity().getApplication();
			
			userno = appContext.getCurrentUser();
			
			rootview=inflater.inflate(R.layout.ad_myinfo_main, container, false);

			ImageView btnView=(ImageView) rootview.findViewById(R.id.btn_myinfo_main);
			
			View myinfo_main_layout = rootview.findViewById(R.id.ad_layout_id_myinfo_main);
			
			MyOnClickListener myClick = new MyOnClickListener();
			myinfo_main_layout.setOnClickListener(myClick);
			btnView.setOnClickListener(myClick);
			
			v_roundImage_myinfo_main = (PersonalHeaderView)rootview.findViewById(R.id.ad_roundImage_myinfo_main);
			tv_name_myinfo_main = (TextView)rootview.findViewById(R.id.ad_name_myinfo_main);
			tv_userno_myinfo_main = (TextView)rootview.findViewById(R.id.ad_userno_myinfo_main);
			tv_company_myinfo_main = (TextView)rootview.findViewById(R.id.ad_company_myinfo_main);
			
			//功能列表
			GridView lisv=(GridView) rootview.findViewById(R.id.grd_items_myinfo);
			//文字信息
			item = getResources().getStringArray(R.array.titles_bus_sys_erp);
			
			listItems = new ArrayList<HashMap<String, Object>>();  
			
			drawables =  new int[]{
					R.drawable.ad_newyeargreetingcard_logo,
					R.drawable.ad_fc_logo,
					R.drawable.salary_pic,
					R.drawable.xt_contact_pic,
					R.drawable.g,
					R.drawable.h,
					R.drawable.i,
					R.drawable.vv,
					R.drawable.f};
	       for(int i=0; i < 4 ; i++){   
	           HashMap<String, Object> map = new HashMap<String, Object>();   
	           map.put("image", drawables[i]);   
	           map.put("item", item[i]);   
	           listItems.add(map);    
	       }   
			strfro=new String[]{"image","item"};
			intto=new int[]{R.id.item_imageView,R.id.item_textView};
			simpleadapter =new SimpleAdapter(getActivity(), listItems, R.layout.grid_item, strfro, intto);
			lisv.setAdapter(simpleadapter);
			lisv.setOnItemClickListener(new onitemclicklistener());
			initXtUserInfo();
			//加载用户头像信息
			loadheaderPic();
			//开辟线程加载用户权限列表
			new Thread(new loaduserpermision()).start();
		}
		else
		{
			ViewGroup parent = (ViewGroup) rootview.getParent();   
			if (parent != null) {   
			    parent.removeView(rootview);   
			}
			
			loadheaderPic();

		}
		
		
		return rootview;
	}
	
	
	private void initXtUserInfo()
	{
		new Thread(new XtUserInfoRunnable()).start();
	}
	
	/**
	 * @author zhaoj
	 * 获得协同办公用户基本信息
	 */
	private class XtUserInfoRunnable implements Runnable{

		@Override
		public void run() {
			
			if (appContext != null)
			{
				Message msg = Message.obtain();
				msg.what = MSG_ERROR;
				try {
					
					mXtUser = appContext.xtGetUserInfo(appContext.getCurrentUser());
					if (mXtUser != null)
					{
						msg.what = MSG_SUCCESS;
					}
				} catch (Exception e) {
					msg.what = MSG_ERROR;
				}
				
				handler.sendMessage(msg);
			}
		}
		
	}

	
	/**
	 * @author zhaoj
	 * 功能模块
	 *
	 */
	public class onitemclicklistener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			switch(arg2){
			case 0:
				Intent salaryIntent1=new Intent(getActivity(),AdNewYearGreetingCardTemplateChoiceActivity.class);
				startActivity(salaryIntent1);
				break;
			case 1:
				 //朋友圈
				Intent friendCircleIntent=new Intent(getActivity(),AdFCFirendStatusActivity.class);
				startActivity(friendCircleIntent);
				break;
			case 2:
				Intent salaryIntent=new Intent(getActivity(),ErpSalaryActivity.class);
				startActivity(salaryIntent);
				break;
			case 3:
				Intent ErpContactsActivity2=new Intent(getActivity(),XtContactsActivity.class);
				startActivity(ErpContactsActivity2);
				break;
			case 4:
				Intent friendCircleIntent2=new Intent(getActivity(),AdFCFirendStatusManageActivity.class);
				startActivity(friendCircleIntent2);
				break;
			}
		}
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
				if(bmp !=null){
					v_roundImage_myinfo_main.setmSrc(bmp);
					v_roundImage_myinfo_main.invalidate();
				}
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch(resultCode)
		{
		    //当用户关闭ErpPersonalInformationActivity时获得
			case ErpPersonalInformationActivity.MSG_ERP_PERSON_INFO_FINISH_ACTIVITY:
				loadheaderPic();
				break;
		}
	}
	
	private class loaduserpermision implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Message msg = new Message();
			// TODO Auto-generated method stub
			try {
				userPermissionTemplis = appContext.getUserPermision(appContext.getCurrentUser());
				msg.what = 100;
			} catch (AppException e) {
				// TODO Auto-generated catch block
				msg.what = -1;
				//e.printStackTrace();
			}
			handlerpermision.sendMessage(msg);
		}
	}
	
	//处理权限
	private Handler handlerpermision = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 100 && userPermissionTemplis !=null){
				for(int i =0;i<userPermissionTemplis.size();i++){
					//如果在权限列表内则添加显示
					if(userPermissionTemplis.get(i).getStrPermission().equals("AdFCFirendStatusManageActivity")
							&&userPermissionTemplis.get(i).getStraction().equals("view")){
						HashMap<String, Object> map = new HashMap<String, Object>();   
				        map.put("image", drawables[4]);   
				        map.put("item", item[4]);
				        if(!listItems.contains(map)){
				        	listItems.add(4,map);
				        }
					}
				}
			}
			simpleadapter.notifyDataSetChanged();
		};
	};
	@Override
	public void onDestroy() {
		super.onDestroy();
	}


}
