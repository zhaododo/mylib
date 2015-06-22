package com.android.dream.app.ui;

import java.util.Map;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-9上午9:11:22
 */

public class ErpApplyPermissionActivity extends BaseActivity {

	private ImageView img_apply_back;
	private Button btn_apply_per;
	private TextView et_apply_person;
	private TextView et_activitydec;
	private TextView et_person_info;
	private EditText et_apply_dsc;
	private AppContext appContext;
	private Intent intent;
	private String activityname;
	private String activitydsc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.applypermission);
		appContext = (AppContext) this.getApplication();
		intent = getIntent();
		activityname = intent.getStringExtra("activityname");
		activitydsc = intent.getStringExtra("activitydsc");
		img_apply_back = (ImageView) findViewById(R.id.img_apply_back);
		btn_apply_per = (Button) findViewById(R.id.btn_apply_per);
		et_apply_person = (TextView) findViewById(R.id.et_apply_person);
		et_activitydec = (TextView) findViewById(R.id.et_activitydec);
		et_apply_dsc = (EditText) findViewById(R.id.et_apply_dsc);
		img_apply_back.setOnClickListener(new optionclick());
		btn_apply_per.setOnClickListener(new optionclick());
		et_apply_person.setText(appContext.getCurrentUser());
		et_activitydec.setText(activitydsc);
		
		
	}

	class optionclick implements OnClickListener{

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.img_apply_back:
				ErpApplyPermissionActivity.this.finish();
		    break;
			case R.id.btn_apply_per:
				new Thread(new applyPermission()).start();
		    break;
			
			}
		}
		
	}
	
	class applyPermission implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String applyuserno = et_apply_person.getText().toString().trim();
			String activitydec = et_activitydec.getText().toString().trim();
			String applyreason = et_apply_dsc.getText().toString().trim();
			Map<String ,String> maprslt = null;
			try {
				maprslt = appContext.AdApplyPermission(applyuserno,activityname,activitydec,applyreason);
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
				Map<String ,String> maprslt = (Map<String, String>) msg.obj;
				String status = maprslt.get("status");
				String mesg = maprslt.get("mesg");
				Toast.makeText(ErpApplyPermissionActivity.this, mesg, Toast.LENGTH_SHORT).show();
			}
		}
	};
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
}
