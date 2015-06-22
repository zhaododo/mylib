package com.android.dream.app.widget;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dream.app.R;

/**
 * @author czh
 * 输入电话号码dialog
 * 
 */
public class PersonalInfoTextViewDialog extends Dialog{

	Context context;
	
	/**
	 *  回调函数，构造函数中必须传入
	 */
	public PersonalInfoBtnListener personalInfoBtnListener;
	/**
	 * 取消按钮
	 */
	private TextView cancelBtn;
	/**
	 * 保存按钮
	 */
	private TextView saveBtn;
	
	/**
	 * 输入的查询条件
	 */
	private EditText phoneNoET;
	
	
	/**
	 * 保存Dialog对象
	 */
	private PersonalInfoTextViewDialog mDialog;
	
	/**
	 * 标题
	 */
	public String diatitle;
	
	private TextView tv_title;
	
	public EditText getSearchEditText() {
		return phoneNoET;
	}

	public void setSearchEditText(EditText phoneNoET) {
		this.phoneNoET = phoneNoET;
	}

	/**
	 * 构造函数
	 * @param context
	 * @param theme 
	 * @param csListener 查询接口的实现
	 */
	public PersonalInfoTextViewDialog(Context context, int theme, PersonalInfoBtnListener personalInfoBtnListener,String diatitle) {
		super(context,theme);
		this.context = context;
		this.personalInfoBtnListener = personalInfoBtnListener;
		this.diatitle = diatitle;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ad_erp_personalinfo_tv_dialog);
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		mDialog = this;
		initView();
	}
	
	private void initView(){
		tv_title = (TextView) findViewById(R.id.tv_dia_updataphone_title);
		tv_title.setText(diatitle);
		phoneNoET = (EditText) findViewById(R.id.contact_dialog_phone_et);
		//获取本机号码的方法    因为这里取得是sim卡中存储的本机号码,如果运营商没有在sim中存储 则取不到
		TelephonyManager phoneMgr=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNo = phoneNoET.getText().toString();
		if("".equals(phoneNo)){
			phoneNoET.setText(phoneMgr.getLine1Number());
		}
		saveBtn = (TextView) findViewById(R.id.personalinfo_phonedialog_savebt);
		saveBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				personalInfoBtnListener.btnAction(view,phoneNoET);
			}
		});
		
		
		cancelBtn = (TextView) findViewById(R.id.personalinfo_phonedialog_cancelbt);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				personalInfoBtnListener.btnAction(view,phoneNoET);
			}
		});
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
	
	public interface PersonalInfoBtnListener{
		public void btnAction(View v,EditText et);
	}
	
 
}
