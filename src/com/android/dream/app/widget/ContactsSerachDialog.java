package com.android.dream.app.widget;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.dream.app.R;

/**
 * @author zhaoj
 * 搜索对话框
 * 
 */
public class ContactsSerachDialog extends Dialog{

	Context context;
	
	/**
	 *  回调函数，构造函数中必须传入
	 */
	public TextChangedSearchListener tcListener;
	
	/**
	 * 取消按钮
	 */
	private ImageView cancelBtn;
	/**
	 * 查询按钮
	 */
	private ImageView searchBtn;
	
	/**
	 * 输入的查询条件
	 */
	private EditText searchEditText;
	
	
	/**
	 * 保存Dialog对象
	 */
	private ContactsSerachDialog mDialog;
	
	
	public EditText getSearchEditText() {
		return searchEditText;
	}

	public void setSearchEditText(EditText searchEditText) {
		this.searchEditText = searchEditText;
	}

	/**
	 * 构造函数
	 * @param context
	 * @param theme      layout
	 * @param csListener 查询接口的实现
	 */
	public ContactsSerachDialog(Context context, int theme, TextChangedSearchListener tcListener) {
		super(context,theme);
		this.tcListener = tcListener;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ad_erp_contacts_search_dialog);
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
	
	private void initView()
	{
		searchEditText = (EditText) findViewById(R.id.contact_dialog_search_et);
		searchEditText.addTextChangedListener(new SearchDialogTextWatcher());
		cancelBtn = (ImageView) findViewById(R.id.contact_dialog_cancel__btn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideDialog();
			}
		});
		
		searchBtn = (ImageView) findViewById(R.id.contact_dialog_search__btn);
		searchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//与侦听文本变化的事件相同
				tcListener.searchAction(searchEditText);
			}
		});
		
	}
	/**
	 * @author zhaoj
	 * 外部接口，侦听查询对话框中的查询
	 *
	 */
	public interface TextChangedSearchListener{
		
		/**
		 * 处理查询数据
		 * @param et
		 * @param dialog
		 */
		public void searchAction(EditText et);
	}
	
	/**
	 * @author zhaoj
	 * 文本改变监测器
	 */
	class SearchDialogTextWatcher implements TextWatcher {
		// 文字变化后
		@Override
		public void afterTextChanged(Editable s) {
			tcListener.searchAction(searchEditText);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}
	}
 
}
