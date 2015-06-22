package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.ErpPayItem;
import com.android.dream.app.bean.ErpSalaryInfo;
import com.android.dream.app.widget.LoadingDialog;
import com.android.dream.app.widget.TabPageIndicator;

public class ErpSalaryActivity extends BaseActivity {

	private AppContext appContext;
	private LoadingDialog loading;
	private Handler mHandler;
	private TextView txtusername;
	private TextView txtbasicsalary;
	private TextView txtjoblevel;
	private TextView tvSalarySP, tvSalarySD, tvSalarySG;
	private TextView tvBonusSP, tvBonusSD, tvBonusSG;
	private String userNo = "";
	private String strdate = "";
	private TextView year, month;
	private ImageView iv1, iv2, iv3, iv4;
	private LinearLayout salaryShouldPay, salaryShouldDeduct, bonusShouldPay,
			bonusShouldDeduct;
	private float yearmoveX;
	private float yearpressX;
	private float monthmoveX;
	private float monthpressX;
	private ViewPager pager;
	private TabPageIndicator indicator;

	private final static int MSG_GETDATA_SUCCESS = 0x01;
	private final static int MSG_NETWORK_EROOR = 0x02;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_salary_main);
		appContext = (AppContext) getApplication();

		Calendar c = Calendar.getInstance();
		String yearString = c.get(Calendar.YEAR) + "";
		String monthString = getNewMonth(c.get(Calendar.MONTH) + 1) + "";
		strdate = yearString + monthString;
		userNo = appContext.getCurrentUser();
		initDateView();
		initView();
		initData();
		loadUserInfoThread(userNo, strdate);
	}

	private void initView() {
		// title_left_btn
		ImageButton imageButton = (ImageButton) findViewById(R.id.title_left_btn);
		imageButton.setOnClickListener(new imgbtnclick());
		TextView textView = (TextView) findViewById(R.id.title_info);
		textView.setText("工资信息");
		LayoutInflater lf = getLayoutInflater();
		List<View> views = new ArrayList<View>();
		views.add(lf.inflate(R.layout.ad_salary_salary, null));
		views.add(lf.inflate(R.layout.ad_salary_bonus, null));

		tvSalarySP = (TextView) views.get(0).findViewById(R.id.salaryShouldPay);
		tvSalarySD = (TextView) views.get(0).findViewById(
				R.id.salaryShouldDeduct);
		tvSalarySG = (TextView) views.get(0).findViewById(R.id.salaryShouldGet);
		tvBonusSP = (TextView) views.get(1).findViewById(R.id.bonusShouldPay);
		tvBonusSD = (TextView) views.get(1)
				.findViewById(R.id.bonusShouldDeduct);
		tvBonusSG = (TextView) views.get(1).findViewById(R.id.bonusShouldGet);

		txtusername = (TextView) findViewById(R.id.tv_user_name);
		txtbasicsalary = (TextView) findViewById(R.id.balance);

		txtjoblevel = (TextView) findViewById(R.id.joblevel);

		List<String> titles = new ArrayList<String>();

		titles.add("工资");
		titles.add("奖金");
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new MypagerAdapter(views, titles));
		titles.get(0);
		titles.get(1);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager, 0);
	}

	public static class MypagerAdapter extends PagerAdapter {

		private List<View> view_lists;
		private List<String> title_lists;

		public MypagerAdapter(List<View> views, List<String> titles) {
			view_lists = views;
			title_lists = titles;
		}

		@Override
		public int getCount() {

			return view_lists.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {

			return view == obj;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView(view_lists.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return title_lists.get(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(view_lists.get(position));
			return view_lists.get(position);
		}

	}

	public class imgbtnclick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ErpSalaryActivity.this.finish();
			// Intent intent =new
			// Intent(SalaryActivity.this,MainActivity.class);
			// startActivity(intent);
		}
	}

	public void searchSalary(View view) {
		Toast.makeText(this, "查询信息", Toast.LENGTH_SHORT).show();
	}

	private void loadUserInfoThread(final String userNo, final String strdate) {
		loading = new LoadingDialog(this);
		loading.show();

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					if (!appContext.isNetworkConnected()) {
						msg.what = MSG_NETWORK_EROOR;
					} else {
						ErpSalaryInfo erpSalaryInfo = appContext.erpsalary(
								userNo, strdate);
						msg.what = MSG_GETDATA_SUCCESS;
						msg.obj = erpSalaryInfo;
					}

				} catch (AppException e) {
					e.printStackTrace();
				}

				finally {
					mHandler.sendMessage(msg);
				}

			}
		}.start();
	}

	private void initData() {
		mHandler = new Handler() {
			public void handleMessage(Message msg) {

				if (msg.what == MSG_GETDATA_SUCCESS) {
					if (msg.obj != null) {
						process(msg.obj);
					}
				} else if (msg.what == MSG_NETWORK_EROOR) {
					if (loading != null)
						loading.dismiss();
					Toast.makeText(ErpSalaryActivity.this, "无网络连接",
							Toast.LENGTH_LONG).show();
				} else {
					if (loading != null)
						loading.dismiss();
				}
			}
		};
	}

	private void process(Object object) {
		final Intent intent = new Intent(this, AdMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (loading != null)
			loading.dismiss();

		ErpSalaryInfo erpSalaryInfo = (ErpSalaryInfo) object;
		txtusername.setText(erpSalaryInfo.getUsername());
		txtbasicsalary.setText(erpSalaryInfo.getBasicSalary());
		txtjoblevel.setText(erpSalaryInfo.getJoblevel());

		Resources res = this.getResources();
		int orange = res.getColor(R.color.orange);
		tvSalarySP.setTextColor(orange);
		tvSalarySD.setTextColor(orange);
		tvSalarySG.setTextColor(orange);
		tvSalarySP.setText(erpSalaryInfo.getWagesshouldPay()); // 应付工资
		tvSalarySD.setText(erpSalaryInfo.getWagesdeductPay()); // 应扣工资
		tvSalarySG.setText(erpSalaryInfo.getWagesrealPay()); // 实绩工资

		// 应付工资明细 动态创建 --------------start------------------//
		salaryShouldPay = (LinearLayout) findViewById(R.id.originalsalary);
		List<ErpPayItem> esiSP = erpSalaryInfo.getWagesshouldPayDetail();

		for (int i = 0; i < esiSP.size(); i++) {

			if (i == 0) {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dividingLine.setLayoutParams(dlp);
				salaryShouldPay.addView(dividingLine);
			} else {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dlp.setMargins(45, 0, 15, 0);
				dividingLine.setLayoutParams(dlp);
				salaryShouldPay.addView(dividingLine);
			}

			ErpPayItem epi = (ErpPayItem) esiSP.get(i);
			LinearLayout salarySPDetail = createDetailLinearLayout(epi);
			salaryShouldPay.addView(salarySPDetail);
		}
		// 应付工资明细 动态创建 --------------end------------------//

		// 应扣工资明细 动态创建 --------------start------------------//
		salaryShouldDeduct = (LinearLayout) findViewById(R.id.deductsalary);
		List<ErpPayItem> esiSD = erpSalaryInfo.getWagesdeductPayDetail();

		for (int i = 0; i < esiSD.size(); i++) {

			if (i == 0) {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dividingLine.setLayoutParams(dlp);
				salaryShouldDeduct.addView(dividingLine);
			} else {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dlp.setMargins(45, 0, 15, 0);
				dividingLine.setLayoutParams(dlp);
				salaryShouldDeduct.addView(dividingLine);
			}

			ErpPayItem epi = (ErpPayItem) esiSD.get(i);
			LinearLayout salarySDDetail = createDetailLinearLayout(epi);
			salaryShouldDeduct.addView(salarySDDetail);
		}
		// 应扣工资明细 动态创建 --------------end------------------//

		tvBonusSP.setTextColor(orange);
		tvBonusSD.setTextColor(orange);
		tvBonusSG.setTextColor(orange);
		tvBonusSP.setText(erpSalaryInfo.getBonusshouldPay()); // 应付奖金
		tvBonusSD.setText(erpSalaryInfo.getBonusdeductPay()); // 应扣奖金
		tvBonusSG.setText(erpSalaryInfo.getBonusrealPay()); // 实付奖金

		// 应付奖金明细 动态创建 --------------start------------------//
		bonusShouldPay = (LinearLayout) findViewById(R.id.originalbonus);
		List<ErpPayItem> ebiSP = erpSalaryInfo.getBonusshouldPayDetail();

		for (int i = 0; i < ebiSP.size(); i++) {

			if (i == 0) {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dividingLine.setLayoutParams(dlp);
				bonusShouldPay.addView(dividingLine);
			} else {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dlp.setMargins(25, 0, 25, 0);
				dividingLine.setLayoutParams(dlp);
				bonusShouldPay.addView(dividingLine);
			}

			ErpPayItem epi = (ErpPayItem) ebiSP.get(i);
			LinearLayout bonusSPDetail = createDetailLinearLayout(epi);
			bonusShouldPay.addView(bonusSPDetail);
		}
		// 应付奖金明细 动态创建 --------------end------------------//

		// 应扣奖金明细 动态创建 --------------start------------------//
		bonusShouldDeduct = (LinearLayout) findViewById(R.id.deductbonus);
		List<ErpPayItem> ebiSD = erpSalaryInfo.getBonusdeductPayDetail();

		for (int i = 0; i < ebiSD.size(); i++) {

			if (i == 0) {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dividingLine.setLayoutParams(dlp);
				bonusShouldDeduct.addView(dividingLine);
			} else {
				View dividingLine = new View(this);
				dividingLine
						.setBackgroundResource(R.drawable.view_dividing_line);
				LayoutParams dlp = new LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, 1);
				dlp.setMargins(25, 0, 25, 0);
				dividingLine.setLayoutParams(dlp);
				bonusShouldDeduct.addView(dividingLine);
			}

			ErpPayItem epi = (ErpPayItem) ebiSD.get(i);
			LinearLayout bonusSDDetail = createDetailLinearLayout(epi);
			bonusShouldDeduct.addView(bonusSDDetail);
		}
		// 应扣奖金明细 动态创建 --------------end------------------//

		Log.v("SalaryActivity", appContext.getCurrentUser());

	}

	private void initDateView() {
		iv1 = (ImageView) findViewById(R.id.imageView1);
		iv2 = (ImageView) findViewById(R.id.imageView2);
		iv3 = (ImageView) findViewById(R.id.imageView3);
		iv4 = (ImageView) findViewById(R.id.imageView4);

		iv1.setOnTouchListener(new MyImageTouchListener());
		iv2.setOnTouchListener(new MyImageTouchListener());
		iv3.setOnTouchListener(new MyImageTouchListener());
		iv4.setOnTouchListener(new MyImageTouchListener());

		year = (TextView) findViewById(R.id.year);
		month = (TextView) findViewById(R.id.month);

		Calendar c = Calendar.getInstance();
		String yearString = c.get(Calendar.YEAR) + "";
		String monthString = getNewMonth(c.get(Calendar.MONTH) + 1) + "";
		year.setText(yearString);
		month.setText(monthString);

		year.setOnTouchListener(new OnTouchListener() {

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
						changeYearDate(year, direction);
						yearpressX = event.getX();
					} else if (yearmoveX - yearpressX < 0
							&& Math.abs(yearmoveX - yearpressX) > 30) {
						Log.i("message", "向左");
						int direction = 0;
						iv1.setImageResource(R.drawable.icon_sort_price_up);
						iv2.setImageResource(R.drawable.icon_sort_rate_down);
						changeYearDate(year, direction);
						yearpressX = event.getX();
					}
					break;
				// 松开
				case MotionEvent.ACTION_UP:
					iv1.setImageResource(R.drawable.icon_sort_rate_up);
					iv2.setImageResource(R.drawable.icon_sort_rate_down);
					clearLayout();
					indicator.setViewPager(pager, pager.getCurrentItem());
					String queryDate = year.getText().toString()
							+ month.getText().toString();
					loadUserInfoThread(userNo, queryDate);
					break;
				default:
					break;
				}
				return true;
			}
		});

		month.setOnTouchListener(new OnTouchListener() {

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
						changeMonthDate(month, direction);
						monthpressX = event.getX();
					} else if (monthmoveX - monthpressX < 0
							&& Math.abs(monthmoveX - monthpressX) > 30) {
						Log.i("message", "向左");
						iv3.setImageResource(R.drawable.icon_sort_price_up);
						iv4.setImageResource(R.drawable.icon_sort_rate_down);
						int direction = 0;
						changeMonthDate(month, direction);
						monthpressX = event.getX();
					}
					break;
				// 松开
				case MotionEvent.ACTION_UP:
					iv3.setImageResource(R.drawable.icon_sort_rate_up);
					iv4.setImageResource(R.drawable.icon_sort_rate_down);
					clearLayout();
					indicator.setViewPager(pager, pager.getCurrentItem());
					String queryDate = year.getText().toString()
							+ month.getText().toString();
					loadUserInfoThread(userNo, queryDate);
					break;
				default:
					break;
				}
				return true;
			}
		});

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

	public LinearLayout createDetailLinearLayout(ErpPayItem epi) {
		// 创建一个新的布局
		LinearLayout layout1 = new LinearLayout(this);

		// 定义LinearLayout布局的属性
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		layout1.setGravity(Gravity.CENTER);
		layout1.setLayoutParams(llp);
		layout1.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams t1lp = new LinearLayout.LayoutParams(0,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		t1lp.setMargins(45, 10, 15, 10);

		TextView tv1 = new TextView(this);
		tv1.setText(epi.getSalname());

		LayoutParams t2lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		t2lp.setMargins(45, 10, 15, 10);
		TextView tv2 = new TextView(this);
		Resources res = this.getResources();
		int orange = res.getColor(R.color.orange);
		tv2.setTextColor(orange);
		tv2.setGravity(Gravity.RIGHT);
		tv2.setLayoutParams(t2lp);
		tv2.setText(epi.getStrSalamt());

		layout1.addView(tv1, t1lp);
		layout1.addView(tv2);
		return layout1;
	}

	public class MyImageTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View imageView, MotionEvent mEvent) {
			switch (mEvent.getAction()) {
			// 按下
			case MotionEvent.ACTION_DOWN:
				if (imageView.getId() == R.id.imageView1) {
					iv1.setImageResource(R.drawable.icon_sort_price_up);
					changeYearDate(year, 0);
				} else if (imageView.getId() == R.id.imageView2) {
					iv2.setImageResource(R.drawable.icon_sort_price_down);
					changeYearDate(year, 1);
				} else if (imageView.getId() == R.id.imageView3) {
					iv3.setImageResource(R.drawable.icon_sort_price_up);
					changeMonthDate(month, 0);
				} else if (imageView.getId() == R.id.imageView4) {
					iv4.setImageResource(R.drawable.icon_sort_price_down);
					changeMonthDate(month, 1);
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

				clearLayout();
				indicator.setViewPager(pager, pager.getCurrentItem());
				String queryDate = year.getText().toString()
						+ month.getText().toString();
				loadUserInfoThread(userNo, queryDate);

				break;
			default:
				break;
			}
			return true;

		}

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

	private void clearLayout() {
		if (salaryShouldPay != null)
		{
			salaryShouldPay.removeAllViews();
		}
		
		if (salaryShouldDeduct != null)
		{
			salaryShouldDeduct.removeAllViews();
		}
		
		if (bonusShouldPay != null)
		{
			bonusShouldPay.removeAllViews();
		}
		
		if (bonusShouldDeduct != null)
		{
			bonusShouldDeduct.removeAllViews();
		}
	}
}
