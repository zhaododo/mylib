package com.android.dream.app.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdMESOrderProduct;
import com.android.dream.app.bean.AdMESOrderProductPage;
import com.android.dream.app.widget.LoadingDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wang Cheng
 *
 * @date 2014-10-7下午11:01:52
 */
public class MESOrderProForCusActivity extends BaseActivity{

	protected static final String TAG = "wch";
	private AppContext appContext;
	private TextView tv_title;
	private ImageView iv1;
	private ImageView iv2;
	private ImageView iv3;
	private ImageView iv4;
	private EditText et_company;  //公司名称
	private TextView tv_year;   //年份
	private TextView tv_month;   //月份
	private String companyname = "";
	private String enterdate;
	private float yearmoveX;
	private float yearpressX;
	private float monthmoveX;
	private float monthpressX;
	private Handler handler;
	private LoadingDialog loading;
	private TextView tv_ordercustotal;
	private TextView tv_ordercusown;//订单欠量
	private TextView tv_steelmaking_cus; //炼钢/连铸
	private TextView tv_steelrolling_cus;  //轧钢
	private TextView tv_finishing_cus; //精整
	private TextView tv_lastdec_cus;  //综判:
	private TextView tv_unstorage_cus; //未入库
	private TextView tv_instorage_cus;  //已入库
	private TextView tv_onway_cus;  //在途
	private TextView tv_waitdeliver_cus; //待发
	private TextView tv_finishdeliver_cus; //已完成
	private TextView tv_finishpercent_cus; //完成率
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_mes_ordproduct_cus);
		appContext = (AppContext) getApplication();
		initview();
		initDateView();
	}

	private void initview(){
		ImageView imageButton = (ImageView) findViewById(R.id.board_detail_title_left_btn);
		imageButton.setOnClickListener(new imgbtnclick());
		tv_title = (TextView)findViewById(R.id.txt_sckb_title);
		tv_title.setText("客户订单跟踪信息");
		tv_ordercustotal = (TextView)findViewById(R.id.tv_ordercustotal);
		tv_ordercusown = (TextView)findViewById(R.id.tv_ordercusown); //订单欠量
		tv_steelmaking_cus = (TextView)findViewById(R.id.tv_steelmaking_cus); //炼钢/连铸
		tv_steelrolling_cus = (TextView)findViewById(R.id.tv_steelrolling_cus); //轧钢
		tv_finishing_cus = (TextView)findViewById(R.id.tv_finishing_cus); //精整
		tv_lastdec_cus = (TextView)findViewById(R.id.tv_lastdec_cus);  //综判:
		tv_unstorage_cus = (TextView)findViewById(R.id.tv_unstorage_cus); //未入库
		tv_instorage_cus = (TextView)findViewById(R.id.tv_instorage_cus); //已入库
		tv_onway_cus = (TextView)findViewById(R.id.tv_onway_cus); //在途
		tv_waitdeliver_cus = (TextView)findViewById(R.id.tv_waitdeliver_cus);//待发
		tv_finishdeliver_cus = (TextView)findViewById(R.id.tv_finishdeliver_cus); //已完成
		tv_finishpercent_cus = (TextView)findViewById(R.id.tv_finishpercent_cus); //完成率:
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (msg.what >0) {
					if (msg.obj != null) {
						process(msg.obj);
					}
				}else {
					if (loading != null)
						loading.dismiss();
					    Toast.makeText(MESOrderProForCusActivity.this, "查无资料",
							Toast.LENGTH_LONG).show();
				}
			}
			
		};
	}
	private void process(Object rslt){
		if (loading != null)
			loading.dismiss();
		List<AdMESOrderProduct>  lisrslt = (List<AdMESOrderProduct>) rslt;
		tv_ordercustotal.setText(lisrslt.get(0).getOrdertotal().toString());
		tv_ordercusown.setText(lisrslt.get(0).getOrderown().toString());//订单欠量
		tv_steelmaking_cus.setText(lisrslt.get(0).getSteelmaking().toString()); //炼钢/连铸
		tv_steelrolling_cus.setText(lisrslt.get(0).getSteelrolling().toString());  //轧钢
		tv_finishing_cus.setText(lisrslt.get(0).getFinishing().toString()); //精整
		tv_lastdec_cus.setText(lisrslt.get(0).getLastdec().toString());  //综判:
		tv_unstorage_cus.setText(lisrslt.get(0).getUnstorage().toString()); //未入库
		tv_instorage_cus.setText(lisrslt.get(0).getInstorage().toString());  //已入库
		tv_onway_cus.setText(lisrslt.get(0).getOnway().toString());  //在途
		tv_waitdeliver_cus.setText(lisrslt.get(0).getWaitdeliver().toString()); //待发
		tv_finishdeliver_cus.setText(lisrslt.get(0).getFinishdeliver().toString()); //已完成
		tv_finishpercent_cus.setText(lisrslt.get(0).getFinishpercent().toString()+"%");//完成率
	}
	public class imgbtnclick implements OnClickListener {
		@Override
		public void onClick(View v) {
			MESOrderProForCusActivity.this.finish();
		}
	}

	
	/**
	 * 创建线程加载数据
	 * @param pageIndex
	 * @param action
	 * @param userno
	 */
	private void loadLvOrderEnterData(final String checkdate,final String companyname) {
		loading = new LoadingDialog(this);
		loading.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					
					AdMESOrderProductPage erpOrdProductPage = appContext.getMESOrderProductPage(appContext.getCurrentUser(),checkdate, companyname, 1);
					
					if (erpOrdProductPage != null && erpOrdProductPage.getErpOrderProductlis() !=null)
					{
						msg.what = erpOrdProductPage.getPageSize();
						msg.obj = erpOrdProductPage.getErpOrderProductlis();
						Log.i(TAG, "loadLvErpBoardData#list size:"+ erpOrdProductPage.getErpOrderProductlis().size());
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}
	
	private void initDateView() {
		iv1 = (ImageView) findViewById(R.id.iv_ordprocus_1);
		iv2 = (ImageView) findViewById(R.id.iv_ordprocus_2);
		iv3 = (ImageView) findViewById(R.id.iv_ordprocus_3);
		iv4 = (ImageView) findViewById(R.id.iv_ordprocus_4);

		iv1.setOnTouchListener(new MyImageTouchListener());
		iv2.setOnTouchListener(new MyImageTouchListener());
		iv3.setOnTouchListener(new MyImageTouchListener());
		iv4.setOnTouchListener(new MyImageTouchListener());

		et_company = (EditText) findViewById(R.id.et_ordprocus_company);
		tv_year = (TextView) findViewById(R.id.tv_ordprocus_year);
		tv_month = (TextView) findViewById(R.id.tv_ordprocus_month);
		//初始化年月
		SimpleDateFormat adf = new SimpleDateFormat("yyyyMM");
		String nowdate = adf.format(new Date());
		tv_year.setText(nowdate.substring(0, 4));
		tv_month.setText(nowdate.substring(4,6));
		enterdate = nowdate+"00";
		tv_year.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下
				case MotionEvent.ACTION_DOWN:
					yearpressX = event.getX();
					break;
				// 移动
				case MotionEvent.ACTION_MOVE:
					yearmoveX = event.getX();
					if (yearmoveX - yearpressX > 0
							&& Math.abs(yearmoveX - yearpressX) > 30) {// &&
																		// Math.abs(yearmoveY
																		// -
																		// yearpressY)
																		// < 50
						Log.i("message", "向右");
						int direction = 1;
						iv1.setImageResource(R.drawable.icon_sort_rate_up);
						iv2.setImageResource(R.drawable.icon_sort_price_down);
						changeYearDate(tv_year, direction);
						yearpressX = event.getX();
					} else if (yearmoveX - yearpressX < 0
							&& Math.abs(yearmoveX - yearpressX) > 30) {
						Log.i("message", "向左");
						int direction = 0;
						iv1.setImageResource(R.drawable.icon_sort_price_up);
						iv2.setImageResource(R.drawable.icon_sort_rate_down);
						changeYearDate(tv_year, direction);
						yearpressX = event.getX();
					}
					break;
				// 松开
				case MotionEvent.ACTION_UP:
					iv1.setImageResource(R.drawable.icon_sort_rate_up);
					iv2.setImageResource(R.drawable.icon_sort_rate_down);
					getdate();
					loadLvOrderEnterData(enterdate,companyname);
					break;
				default:
					break;
				}
				return true;
			}
		});

		tv_month.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				// 按下
				case MotionEvent.ACTION_DOWN:
					monthpressX = event.getX();
					break;
				// 移动
				case MotionEvent.ACTION_MOVE:
					monthmoveX = event.getX();
					if (monthmoveX - monthpressX > 0
							&& Math.abs(monthmoveX - monthpressX) > 30) {
						Log.i("message", "向右");
						iv3.setImageResource(R.drawable.icon_sort_rate_up);
						iv4.setImageResource(R.drawable.icon_sort_price_down);
						int direction = 1;
						changeMonthDate(tv_month, direction);
						monthpressX = event.getX();
					} else if (monthmoveX - monthpressX < 0
							&& Math.abs(monthmoveX - monthpressX) > 30) {
						Log.i("message", "向左");
						iv3.setImageResource(R.drawable.icon_sort_price_up);
						iv4.setImageResource(R.drawable.icon_sort_rate_down);
						int direction = 0;
						changeMonthDate(tv_month, direction);
						monthpressX = event.getX();
					}
					break;
				// 松开
				case MotionEvent.ACTION_UP:
					iv3.setImageResource(R.drawable.icon_sort_rate_up);
					iv4.setImageResource(R.drawable.icon_sort_rate_down);
					getdate();
					loadLvOrderEnterData(enterdate,companyname);
					break;
				default:
					break;
				}
				return true;
			}
		});
		getdate();
		loadLvOrderEnterData(companyname,enterdate);
	}
	
	private void changeYearDate(TextView dateTV, int direction) {
		int dateTemp = Integer.parseInt(dateTV.getText().toString());
		String newDate = "";
		if (direction == 1) {
			dateTemp = dateTemp + 1;
			newDate = dateTemp + "";
		} else {
			dateTemp = dateTemp - 1;
			newDate = dateTemp + "";
		}
		dateTV.setText(newDate);
	}

	private void changeMonthDate(TextView dateTV, int direction) {
		int dateTemp = Integer.parseInt(dateTV.getText().toString());
		if (direction == 1) {
			dateTemp = dateTemp + 1;
			String newMonth = getNewMonth(dateTemp);
			dateTV.setText(newMonth);
		} else {
			dateTemp = dateTemp - 1;
			String newMonth = getNewMonth(dateTemp);
			dateTV.setText(newMonth);
		}
	}
	
	public String getNewMonth(int month) {
		if (month > 12) {
			month = 1;
		} else if (month <= 0) {
			month = 12;
		}
		String monthTemp = month + "";
		if (monthTemp.length() < 2) {
			monthTemp = "0" + monthTemp;
		}

		return monthTemp;
	}
	
	public class MyImageTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View imageView, MotionEvent mEvent) {
			switch (mEvent.getAction()) {
			// 按下
			case MotionEvent.ACTION_DOWN:
				if (imageView.getId() == R.id.iv_ordprocus_1) {
					iv1.setImageResource(R.drawable.icon_sort_price_up);
					changeYearDate(tv_year, 0);
				} else if (imageView.getId() == R.id.iv_ordprocus_2) {
					iv2.setImageResource(R.drawable.icon_sort_price_down);
					changeYearDate(tv_year, 1);
				} else if (imageView.getId() == R.id.iv_ordprocus_3) {
					iv3.setImageResource(R.drawable.icon_sort_price_up);
					changeMonthDate(tv_month, 0);
				} else if (imageView.getId() == R.id.iv_ordprocus_4) {
					iv4.setImageResource(R.drawable.icon_sort_price_down);
					changeMonthDate(tv_month, 1);
				}
				break;
			// 移动
			case MotionEvent.ACTION_MOVE:
				break;
			// 松开
			case MotionEvent.ACTION_UP:
				iv1.setImageResource(R.drawable.icon_sort_rate_up);
				iv2.setImageResource(R.drawable.icon_sort_rate_down);
				iv3.setImageResource(R.drawable.icon_sort_rate_up);
				iv4.setImageResource(R.drawable.icon_sort_rate_down);
				getdate();
				loadLvOrderEnterData(enterdate,companyname);
				break;
			default:
				break;
			}
			return true;
		}
	}
	
	private void getdate(){
		if(et_company.getText()!=null&&!et_company.getText().toString().isEmpty()){
			companyname = et_company.getText().toString().trim();
		}else{
			companyname = "";
		}
		enterdate = tv_year.getText().toString()+tv_month.getText().toString()+"00";
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}
