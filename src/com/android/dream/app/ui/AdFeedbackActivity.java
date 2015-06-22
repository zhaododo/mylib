package com.android.dream.app.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdAjaxOpt;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-9上午9:11:22
 */

public class AdFeedbackActivity extends BaseActivity {

	private AppContext appContext;
	private Context context;
	
	private TextView sendBtn;
	private ImageView backBtn;
	private EditText etContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_feedback);
		appContext = (AppContext) getApplication();
		context = this;		
		sendBtn = (TextView) findViewById(R.id.txt_feedback_send);
		backBtn = (ImageView) findViewById(R.id.img_feedback_back);
		etContent = (EditText) findViewById(R.id.et_feedback_dsc);
		sendBtn.setOnTouchListener(new FeedBackSendBtnOnTouchListener());
		backBtn.setOnClickListener(new optionclick());
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
	}

	class optionclick implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.img_feedback_back:
				AdFeedbackActivity.this.finish();
		    break;
			}
		}
		
	}
	
	class FeedBackSendBtnOnTouchListener implements OnTouchListener{
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			Resources res = context.getResources();
			int colorNormal = res.getColor(R.color.ad_personalinformation_detail_tv_search_title);
			int colorPress = res.getColor(R.color.ad_personalinformation_detail_title_tv);
			
			int bgNormal = res.getColor(R.color.ad_personalinformation_layout_btn_bg);
			int bgPress = res.getColor(R.color.ad_personalinformation_layout_bt_bg_press);
			
			switch (event.getAction()) {
			// 按下
			case MotionEvent.ACTION_DOWN:
				sendBtn.setTextColor(colorPress);
				sendBtn.setBackgroundColor(bgPress);
				if(!("".equals(etContent.getText().toString().trim()))){
					new Thread(new feedback()).start();
				}
				break;
			// 移动
			case MotionEvent.ACTION_MOVE:
				break;
			// 松开
			case MotionEvent.ACTION_UP:
				sendBtn.setTextColor(colorNormal);
				sendBtn.setBackgroundColor(bgNormal);
				break;
			default:
				break;
			}
			return true;
		}
		
	}
	class feedback implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String userno = appContext.getCurrentUser();
			String feedbackdesc = etContent.getText().toString().trim();
			AdAjaxOpt maprslt = null;
			try {
				maprslt = appContext.createOpinion(userno,feedbackdesc);
				Message msg =new Message();
				msg.obj = maprslt;
				msg.what = 1;
				handmsg.sendMessage(msg);
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	Handler handmsg =new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				//UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
				AdAjaxOpt maprslt = (AdAjaxOpt) msg.obj;
				String status = maprslt.getStatus();
				String mesg = maprslt.getMsg();
				
				if(("Y").equals(status)){
					Toast.makeText(AdFeedbackActivity.this, "您的意见已提交，感谢您的支持！", Toast.LENGTH_SHORT).show();
					AdFeedbackActivity.this.finish();
				}
				else
				{
					Toast.makeText(AdFeedbackActivity.this, mesg, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
}
