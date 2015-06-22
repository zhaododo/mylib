package com.android.dream.app.ui;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdErpReportChartVo;
import com.android.dream.app.widget.LoadingDialog;
import com.android.dream.chartdemo.AdErpReportChartView;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author Wang Cheng
 *
 * @date 2015-1-28下午2:02:46
 */
public class ErpReportChartActivity extends BaseActivity {

	private AppContext appContext;
	private LoadingDialog loading;
	private Handler pull_handler;
	private LinearLayout linearView;
	private Bundle dataBundle;
	private String userno;
	private String typeNo;
	private String reportProperty;
	private String targetField;
	private String reportDate;
	private String reportName;
	private AdErpReportChartView aErpReportChartView;
	private ImageButton title_left_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_erp_report_chart);
		pull_handler = this.getLvHandler();
		appContext = (AppContext) getApplication();
		aErpReportChartView = new AdErpReportChartView();
		//初始化页面控件
		initview();
		initdata();
	}
	
	/**
	 * 初始化控件
	 */
	private void initview(){
		
		linearView = (LinearLayout) findViewById(R.id.chart_show);
		title_left_btn = (ImageButton) findViewById(R.id.title_left_btn);
		title_left_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ErpReportChartActivity.this.finish();
			}
		});
	}

    public void initdata(){
    	
	    dataBundle = this.getIntent().getExtras();
	    userno = appContext.getCurrentUser();
	    typeNo = dataBundle.getString("typeNo");
	    reportProperty = dataBundle.getString("reportProperty");
	    targetField = dataBundle.getString("targetField");
	    reportDate = dataBundle.getString("reportDate");
	    reportName = dataBundle.getString("reportName");
//	        测试/015724/T00001/MR/000853/20141101
//	    userno ="015724";
//	    typeNo ="T00001";
//	    reportProperty ="MR";
//	    targetField = "000853";
//	    reportDate = "20141101";
		loadChartData();
    }
   

	/**
	 * 创建线程加载数据
	 */
	private void loadChartData() {
//		初始化时显示
//		正在等待dialog
		loading = new LoadingDialog(this);		
		loading.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
//					加载数据
					AdErpReportChartVo rslt = appContext.getTimelyReportChart(userno,typeNo,reportProperty,targetField,reportDate);	
					if (rslt!=null)
					{
						msg.what =  1;
						msg.obj = rslt;
					}
				} catch (Exception e) {
					msg.what = -1;
					msg.obj = "error";
//					e.printStackTrace();
				}
				pull_handler.sendMessage(msg);
			}
		}.start();

	}
	
	/**
	 * 创建Handler
	 * @return
	 */
	private Handler getLvHandler() {
		return new Handler() {

			public void handleMessage(Message msg) {
				// 关闭等待对话框
				if (loading != null) {
					loading.dismiss();
				}
				if (msg.what >= 0) {
					AdErpReportChartVo rslt = (AdErpReportChartVo) msg.obj;
					if(rslt != null){
//					   Toast.makeText(ErpReportChartActivity.this,
//								"加载数据成功", Toast.LENGTH_SHORT).show();
						linearView.removeAllViews();
						linearView.addView(aErpReportChartView.getview(ErpReportChartActivity.this,rslt,reportName));
						}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(ErpReportChartActivity.this,
							"加载数据异常", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		 if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			 Toast.makeText(ErpReportChartActivity.this,
						"当前屏幕为横屏", Toast.LENGTH_SHORT).show(); 
        }else{
        	Toast.makeText(ErpReportChartActivity.this,
					"当前屏幕为竖屏", Toast.LENGTH_SHORT).show();
        }
	}
	
	

}
