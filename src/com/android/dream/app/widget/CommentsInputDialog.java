package com.android.dream.app.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.dream.app.R;

public class CommentsInputDialog extends Dialog {
	
	private CommentsInputDialog mDialog;

	private Context context;
	
	private SmileyParser parser;
	
	private Resources res;
	
	private int grey;

	/**
	 * 文本状态 0-文字 (默认) 1-表情
	 */
	private int contentStatus = 0;

	/**
	 * 切换表情按钮
	 */
	private ImageView commentsinput_iv;

	/**
	 * 文本输入
	 */
	private EditText edittextinput_et;

	/**
	 * 发送按钮
	 */
	private TextView sendcontent_btn;

	/**
	 * 表情gridview
	 */
	private GridView commentdetails_gv;
	
	private PriorityListener listener;

	public PriorityListener getListener() {
		return listener;
	}

	

	public int getContentStatus() {
		return contentStatus;
	}

	public void setContentStatus(int contentStatus) {
		this.contentStatus = contentStatus;
	}
	
	public CommentsInputDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	
	public void setListener(PriorityListener listener) {
		this.listener = listener;
	}

	public CommentsInputDialog(Context context, int theme, PriorityListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}

	/**
	 * 隐藏对话框
	 */
	private void hideDialog() {
		
		if (edittextinput_et != null)
		{
			edittextinput_et.setText("");
		}
		if (mDialog != null) {
			mDialog.hide();
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ad_commentsinput_dialog);
		res = context.getResources();
		grey = res.getColor(R.color.sendbtntextcolor);
		mDialog = this;
		initView();
	}

	private void initView() {
		
		SmileyParser.init(context);		
		parser = SmileyParser.getInstance();

		initGridView();
		//发送按钮
		sendcontent_btn = (TextView) findViewById(R.id.sendcontent_btn);
		sendcontent_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//文本框有内容发送按钮状态变化
				sendcontent_btn.setBackgroundResource(R.drawable.comments_input_send);
				sendcontent_btn.setTextColor(grey);
				if (listener == null) throw new IllegalArgumentException("listener is null");
				listener.refreshPriorityUI(edittextinput_et.getText().toString()); 
				hideDialog();
			}
		});
		
		//文本输入
		edittextinput_et = (EditText) findViewById(R.id.edittextinput_et);
		edittextinput_et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if(edittextinput_et.getText().length()>0){
					//文本框有内容发送按钮状态变化
					sendcontent_btn.setBackgroundResource(R.drawable.search_btn_p);
					sendcontent_btn.setTextColor(Color.WHITE);
				}else{
					//文本框有内容发送按钮状态变化
					sendcontent_btn.setBackgroundResource(R.drawable.comments_input_send);
					sendcontent_btn.setTextColor(grey);
				}
			}
		});
		// 显示软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		commentsinput_iv = (ImageView) findViewById(R.id.commentsinput_iv);
		commentsinput_iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (contentStatus == 0) {
					// 隐藏软键盘
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					// 切换图片变成键盘
					commentsinput_iv.setBackgroundResource(R.drawable.keyboard);
					Log.i("BBBB", "11111");
					// 表情gridview显示
					commentdetails_gv.setVisibility(View.VISIBLE);
					// 状态变为1
					contentStatus = 1;
				} else {
					// 显示软键盘
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					// 切换图片变成笑脸
					commentsinput_iv.setBackgroundResource(R.drawable.phiz);
					// 表情gridview隐藏
					commentdetails_gv.setVisibility(View.GONE);
					// 状态变为0
					contentStatus = 0;
				}
			}
		});
	}

	private void initGridView() {

		commentdetails_gv = (GridView) findViewById(R.id.commentdetails_gv);
		int[] drawables = { R.drawable.aini, R.drawable.aoteman,
				R.drawable.baibai, R.drawable.baobao, R.drawable.beiju,
				R.drawable.beishang, R.drawable.bianbian, R.drawable.bishi,
				R.drawable.bizui, R.drawable.buyao, R.drawable.chanzui,
				R.drawable.comment_blank, R.drawable.comment_blank, R.drawable.backspace };

		List<HashMap<String, Object>> listItems = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 14; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", drawables[i]);
			listItems.add(map);
		}
		String[] strfro = new String[] { "image" };
		int[] intto = new int[] { R.id.comment_item_imageView };

		SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems,
				R.layout.comment_grid_item, strfro, intto);
		commentdetails_gv.setAdapter(simpleAdapter);
		commentdetails_gv
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long id) {
						switch (position) {
						case 0:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac爱你]"));
							break;
						case 1:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac机器人]"));
							break;
						case 2:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac拜拜]"));
							break;
						case 3:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac抱抱]"));
							break;
						case 4:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac悲剧]"));
							break;
						case 5:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac悲伤]"));
							break;
						case 6:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac便便]"));
							break;
						case 7:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac鄙视]"));
							break;
						case 8:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac闭嘴]"));
							break;
						case 9:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac不要]"));
							break;
						case 10:
							edittextinput_et.setText(parser.addSmileySpans(edittextinput_et.getText()+"[fac馋嘴]"));
							break;
						case 11:
							break;
						case 12:
							break;
						case 13:
							int selectionStart = edittextinput_et.getSelectionStart();// 获取光标的位置
							if (selectionStart > 0) {
								String body = edittextinput_et.getText().toString();
								String tempStr = body.substring(0,selectionStart);
								int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
								if (i != -1 && body.subSequence(body.length()-1, body.length()).equals("]")) {
									CharSequence cs = tempStr.subSequence(
											i, i+4);
									if (cs.equals("[fac")) {// 判断是否是一个表情
										edittextinput_et.getEditableText().delete(i, selectionStart);
										return;
									}
								}
								edittextinput_et.getEditableText().delete(tempStr.length() - 1,	selectionStart);
							}
							break;
						default:
							break;
						}
						edittextinput_et.setSelection(edittextinput_et.getText().toString().length());
					}
				});
	}
	/** 
     * 自定义Dialog监听器 
     */  
    public interface PriorityListener {  
        /** 
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示 
         */  
        public void refreshPriorityUI(String string);  
    } 
}
