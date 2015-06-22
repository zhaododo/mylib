package com.android.dream.app.widget;



import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import com.android.dream.app.ui.AdMainActivity;
import com.android.dream.app.ui.UserOptionAdapter;
import com.android.dream.dao.PersonDao;

/**
 * @author czh
 * 登陆对话框
 * 
 */
public class LoginDialog extends Dialog{

	Context context;
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
	private ImageView cancalBtn;
	/**
	 * 保存Dialog对象
	 */
	private LoginDialog mDialog;
	
	/**
	 * 构造函数
	 * @param context
	 * @param theme      layout
	 */
	public LoginDialog(Context context, int theme, AppContext appContext) {
		super(context,theme);
		this.context = context;
		this.appContext = appContext;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login_dialog);
		UpdateManager.getUpdateManager().checkAppUpdate(context, false);
		mDialog = this;
		initView();
	}
	/**
	 *  隐藏对话框
	 */
	private void hideDialog()
	{
		if (mDialog != null)
		{
			mDialog.hide();
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		while(!flag){
		
			initView();
			flag = true;
		}
	}

	public void listloginhis(){
		personDao =new PersonDao(context);
		personlis=personDao.query();
	}
	
	private void initView(){
		img_login_isrember = (ImageView) findViewById(R.id.image_login_isrember_dialog);
		btn_login = (Button) findViewById(R.id.btn_login_dialog);
		et_user = (EditText) findViewById(R.id.et_user_dialog);
		//et_user.setText("ICSC01");
		et_password = (EditText) findViewById(R.id.et_pwd_dialog);
		cancalBtn = (ImageView) findViewById(R.id.btn_cancel_dialog);
		cancalBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hideDialog();
			}
		});
		btn_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_login_dialog:
					if(!appContext.isNetworkConnected()){
						Toast.makeText(context, "无网络连接", Toast.LENGTH_LONG).show();
						return;
					}
					loadUserInfoThread();

					break;
				}
				
			}
		});
		lina_login_isrember = (LinearLayout) findViewById(R.id.lina_login_isrember_dialog);
		img_login_isrember.setOnClickListener(new changeimgclickdialog());
		lina_login_isrember.setOnClickListener(new changeimgclickdialog());
		//初始化界面组件
		parent = (LinearLayout)findViewById(R.id.parent_dialog);
		image = (ImageView)findViewById(R.id.btn_select_dialog);
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
	
    @SuppressWarnings("deprecation")
	private void initPopuWindow(){ 
    	
    	//initDatas();
    	listloginhis();
    	//PopupWindow浮动下拉框布局
        View loginwindow = (View)this.getLayoutInflater().inflate(R.layout.user_options, null); 
        listviewuser = (ListView) loginwindow.findViewById(R.id.list_user); 
        
        //设置自定义Adapter
        userOptionAdapter = new UserOptionAdapter((Activity) context, mHandler,personlis); 
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
			final Intent intent;
			intent = new Intent(context, AdMainActivity.class);
			appContext.setMainStart(true);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			new Thread(new Runnable() {
				@Override
				public void run() {
					appContext.startActivity(intent);
					Activity activity = (Activity) context;
					activity.finish();
					mDialog.dismiss();
				}
			}).start();
		}
		
	}
	
	private void initData()
	{
		mHandler = new Handler(){
			public void handleMessage(Message msg) {
				Bundle data = msg.getData();
				if(msg.what == Integer.valueOf(AdAjaxLogin.STATUS_CODE_SUCCESS)){
					
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
					personDao =new PersonDao(context);
					personDao.deleteOldPerson(personlis.get(delIndex));
					personlis.remove(delIndex);
					//刷新下拉列表
					userOptionAdapter.notifyDataSetChanged();
					 
				}
				else 
				{
					if (loading != null) loading.dismiss();
					Toast.makeText(context, (String)msg.obj, Toast.LENGTH_LONG).show();
				}
			}
		};		
	}
	
	private void loadUserInfoThread(){
		loading = new LoadingDialog(context);		
		loading.show();
		
		new Thread(){
			public void run() {
				Message msg = new Message();
				
				try {
					
					String userNo = et_user.getText().toString();
					String password = et_password.getText().toString();
					
					AdAjaxLogin adLogin = appContext.erpLogin(userNo, password);
					
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog();
			return true;
		}
		return true;
	}
	
	
	protected void dialog() {
		hideDialog();
	}
	
	class changeimgclickdialog implements View.OnClickListener{

		@Override
		public void onClick(View v) {
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
