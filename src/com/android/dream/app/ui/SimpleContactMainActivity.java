package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdXTDepartment;
import com.android.dream.app.bean.AdXTUser;
import com.android.dream.app.bean.AdXTUserPageVo;
import com.android.dream.app.widget.PullToRefreshListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleContactMainActivity extends BaseActivity{

	private ListView lv_contactsimple;
	private AppContext appContext;
	private String TAG = "wch";
	private int SCUCESS = 1;
	private Handler pull_handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simplecontactmain);
		appContext = (AppContext) getApplication();;
		pull_handler = this.getLvHandler();
		lv_contactsimple = (ListView) findViewById(R.id.lv_contactsimple);
		
		loadLvErpUserDepData("860007");
	}

	/**
	 * 创建线程加载数据
	 * @param pageIndex
	 * @param action
	 * @param userno
	 */
	private void loadLvErpUserDepData(final String userno) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					//根据用户查询部门及本部门用户
//					 List<AdXTDepartment>  erpdep = appContext.adGetXTUserbyUserNo("860007");
//					 
//					 AdXTUserPageVo xtdepuser = appContext.adGetXTUserbyDepNo("927",1);
					 
					 AdXTUserPageVo xtuser = appContext.adGetXTUserbykey("860007","wc",1);
					
					if (xtuser != null)
					{
						msg.what = SCUCESS;
						//msg.obj = xtdepuser;
						//msg.obj = erpdep;
						msg.obj = xtuser;
						Log.i(TAG, "xtuser:"+ xtuser);
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
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
	private Handler getLvHandler() {
		
		Log.i(TAG, "getLvHandler");
		
		return new Handler() {
	
			public void handleMessage(Message msg) {
				if (msg.what == SCUCESS) {
					 //更新数据
//					 List<AdErpDepartment>  erpdep = (List<AdErpDepartment>) msg.obj;
//					 AdErpDepartment dep;
//					 List<Map<String,String>>  deplis = new ArrayList<Map<String,String>>();
//					 Map<String,String> depmap = null;
//					 for(int j = 0;j<erpdep.size();j++){
//						 dep = erpdep.get(j);
//						 depmap = new HashMap<String,String>();
//						 depmap.put("depno", dep.getDepno());
//						 depmap.put("depname", dep.getDepname());
//						 deplis.add(depmap);
//					 }
//					 SimpleAdapter sa=new SimpleAdapter(SimpleContactMainActivity.this,deplis,R.layout.simplecontactmain_item,new String[]{"depno","depname"},new int[]{R.id.tv_comno,R.id.tv_comname});
					 
					 AdXTUserPageVo xtuser = (AdXTUserPageVo)msg.obj;
					 AdXTUser user;
					 Map<String,String> usermap = null;
					 List<Map<String,String>>  usermaplis = new ArrayList<Map<String,String>>();
					 List<AdXTUser> userlis = xtuser.getErpdeplis();
					 for(int i = 0;i<userlis.size();i++){
						 user = userlis.get(i);
						 usermap = new HashMap<String,String>();
						 usermap.put("username",  user.getUsername()+"("+user.getUserno()+")");
						 usermap.put("userphone", user.getMobilephone());
						 usermaplis.add(usermap);
					 }
					SimpleAdapter sa=new SimpleAdapter(SimpleContactMainActivity.this,usermaplis,R.layout.simplecontactmain_item,new String[]{"username","userphone"},new int[]{R.id.tv_comno,R.id.tv_comname});
					lv_contactsimple.setAdapter(sa);
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(SimpleContactMainActivity.this, "", Toast.LENGTH_SHORT).show();;
				}
			}
		};
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	

}
