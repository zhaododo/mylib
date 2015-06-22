package com.android.dream.app.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdAjaxLogin;
import com.android.dream.app.bean.Person;
import com.android.dream.app.common.UpdateManager;
import com.android.dream.app.widget.LoadingDialog;
import com.android.dream.dao.PersonDao;
public class LoginActivity extends BaseActivity implements OnClickListener {
	
	private Button btn_login;
	private AppContext appContext;
	private LoadingDialog loading;
	private Handler mHandler;
	private EditText et_user;
	private EditText et_password;
	private ImageView img_login_isrember;
	private PersonDao personDao;
	//下拉框依附组件
	private LinearLayout parent;
	private ImageView image;
	//下拉框依附组件宽度，也将作为下拉框的宽度
	private int pwidth;
	//PopupWindow对象
	private PopupWindow selectPopupWindow= null;
	private ListView listviewuser;
	List<Person> personlis;
	private UserOptionAdapter userOptionAdapter;
	private boolean flag=false;
	private LinearLayout lina_login_isrember;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		UpdateManager.getUpdateManager().checkAppUpdate(this, false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_1);
		appContext = (AppContext)getApplication();
		
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		while(!flag){
		
			initView();
			flag = true;
		}
	}

	public void listloginhis(){
		personDao =new PersonDao(LoginActivity.this);
		personlis=personDao.query();
	}
	
	private void initView(){
		img_login_isrember = (ImageView) findViewById(R.id.image_login_isrember);
		btn_login = (Button) findViewById(R.id.btn_login);
		et_user = (EditText) findViewById(R.id.et_user);
		//et_user.setText("ICSC01");
		et_password = (EditText) findViewById(R.id.et_pwd);
		btn_login.setOnClickListener(this);
		lina_login_isrember = (LinearLayout) findViewById(R.id.lina_login_isrember);
		img_login_isrember.setOnClickListener(new changeimgclick());
		lina_login_isrember.setOnClickListener(new changeimgclick());
		//初始化界面组件
		parent = (LinearLayout)findViewById(R.id.parent);
		image = (ImageView)findViewById(R.id.btn_select);
		//获取下拉框依附的组件宽度
        int width = parent.getWidth();
        pwidth = width;
        //设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
        image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(true){
					//显示PopupWindow窗口
					popupWindwShowing();
				}
			}
		});
        
        initData();
      //初始化PopupWindow
        initPopuWindow();
	}
	
	/**
     * 显示PopupWindow窗口
     * 
     * @param popupwindow
     */ 
    public void popupWindwShowing() { 
       //将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
       //这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
       //（是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
       selectPopupWindow.showAsDropDown(parent,0,-3); 
    } 
	
    private void initPopuWindow(){ 
    	
    	//initDatas();
    	listloginhis();
    	//PopupWindow浮动下拉框布局
        View loginwindow = (View)this.getLayoutInflater().inflate(R.layout.user_options, null); 
        listviewuser = (ListView) loginwindow.findViewById(R.id.list_user); 
        
        //设置自定义Adapter
        userOptionAdapter = new UserOptionAdapter(this, mHandler,personlis); 
        listviewuser.setAdapter(userOptionAdapter); 
        
        selectPopupWindow = new PopupWindow(loginwindow, pwidth,LayoutParams.WRAP_CONTENT, true); 
        
        selectPopupWindow.setOutsideTouchable(true); 
        
        //这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
        //没有这一句则效果不能出来，但并不会影响背景
        //本人能力极其有限，不明白其原因，还望高手、知情者指点一下
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());  
    }
	
    /**
     * PopupWindow消失
     */ 
    public void dismiss(){ 
        selectPopupWindow.dismiss(); 
    }
    
	
	private void process() {
		
		if (appContext != null)
		{
			//不是二次登录
			if (!appContext.isSecondLogin())
			{
				final Intent intent;
				//是否显示贺卡
				if ("Y".equals(appContext.getShowCard()))
				{
					intent = new Intent(this, AdNewYearGreetingCardMainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				else
				{
					if (!appContext.isMainStart())
					{
						intent = new Intent(this, AdMainActivity.class);
						appContext.setMainStart(true);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				}
				appContext.setLogin(true);
			}
			
			finish();
			
		}
		
	}
	
	private void initData()
	{
		mHandler = new Handler(){
			public void handleMessage(Message msg) {
				Bundle data = msg.getData();
				if(msg.what == Integer.valueOf(AdAjaxLogin.STATUS_CODE_SUCCESS)){
					
					//Toast.makeText(LoginActivity.this,(String)msg.obj, Toast.LENGTH_LONG).show();
					appContext.setCurrentUser(et_user.getText().toString().toUpperCase());
//					保存用户名
					saveuserinfo();
					process();
				}
				else if(msg.what == 101){
					 
					//选中下拉项，下拉框消失
					int selIndex = data.getInt("selIndex");
					et_user.setText(personlis.get(selIndex).getCode());
					et_password.setText(personlis.get(selIndex).getPwd());
					dismiss();
					 
				}
				else if(msg.what == 102){
					 
					//移除下拉项数据
					int delIndex = data.getInt("delIndex");
//					删除数据
					personDao =new PersonDao(LoginActivity.this);
					personDao.deleteOldPerson(personlis.get(delIndex));
					personlis.remove(delIndex);
					//刷新下拉列表
					userOptionAdapter.notifyDataSetChanged();
					 
				}
				else 
				{
					if (loading != null) loading.dismiss();
					Toast.makeText(LoginActivity.this,(String)msg.obj, Toast.LENGTH_LONG).show();
//					if(msg.obj != null){
//						((AppException)msg.obj).makeToast(LoginActivity.this);
//					}
				}
			}
		};		
	}
	
	private void loadUserInfoThread(){
		loading = new LoadingDialog(this);		
		loading.show();
		
		new Thread(){
			public void run() {
				Message msg = new Message();
				
				try {
					
					String userNo = et_user.getText().toString();
					String password = et_password.getText().toString();
					
					AdAjaxLogin adLogin = appContext.erpLogin(userNo, password);
					
					//AdMenu adMenu = appContext.getAdMenu(userNo);
					
					 //Log.v("LoginActivity", adMenu.toString());
					msg.what = Integer.valueOf(adLogin.getStatusCode());
					msg.obj = adLogin.getMessage();
					
	            } catch (AppException e) {
					e.printStackTrace();
				} 
				
				finally
				{
					mHandler.sendMessage(msg);
				}
				
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if(!appContext.isNetworkConnected()){
				Toast.makeText(LoginActivity.this,"无网络连接", Toast.LENGTH_LONG).show();
				return;
			}
			loadUserInfoThread();

			break;
		}
		
	}
	
	public void saveuserinfo(){
		Person person  =new Person();
		String userNo = et_user.getText().toString();
		String password = et_password.getText().toString();
		person.setCode(userNo);
		person.setPwd(password);
		String tag=img_login_isrember.getTag().toString();
		if(tag.endsWith("1")){
			if(personDao.findbycode(person)==0){
				personDao.addone(person);
			}else {
				personDao.updatePwd(person);
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (loading != null) loading.dismiss();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return true;
		}
		return true;
	}
	
	
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
		new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//AccoutList.this.finish();
				//System.exit(1);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
		builder.setNegativeButton("取消",
		new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	class changeimgclick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag=img_login_isrember.getTag().toString();
			if(tag.endsWith("0")){
				img_login_isrember.setTag("1");
				img_login_isrember.setImageResource(R.drawable.login_rember);
			}else{
				img_login_isrember.setTag("0");
				img_login_isrember.setImageResource(R.drawable.login_unrember);
			}
		}
		
	}

}
