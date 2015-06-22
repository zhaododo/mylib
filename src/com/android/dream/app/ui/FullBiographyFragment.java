package com.android.dream.app.ui;

import java.util.Calendar;

import com.android.dream.app.R;
import com.android.dream.app.widget.ExperienceDialog;
import com.android.dream.app.widget.SalaryDialog;
import com.android.dream.app.widget.ExperienceDialog.OnExperClickListener;
import com.android.dream.app.widget.SalaryDialog.OnSureClickListener;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FullBiographyFragment extends Fragment{

	private EditText et_personName, et_birthday, et_salary, et_experience;
	private TextView sex_Man, sex_Woman, isMarriedY, isMarriedN;
	private int customColor_green, customColor_grey;
	private int choseSex, choseMarried;
	private Button commitBtn_fbf;
	
	private String TAG="FullBiographyFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		
		Log.i(TAG, "onCreateView()");
		
		View rootview=inflater.inflate(R.layout.fullbiography, container, false);

		et_personName = (EditText) rootview.findViewById(R.id.et_personName); // 姓名

		// 使用Resources对象获得自定义的颜色，否则取得颜色值有问题
		Resources res = this.getResources();
		customColor_green = res.getColor(R.color.green);
		customColor_grey = res.getColor(R.color.grey);

		// 性别选项用textView实现radioBox的效果
		sex_Man = (TextView) rootview.findViewById(R.id.sex_Man);
		sex_Woman = (TextView) rootview.findViewById(R.id.sex_Woman);

		sex_Man.setBackgroundColor(customColor_green);
		sex_Woman.setBackgroundColor(customColor_grey);

		sex_Man.setOnClickListener(new ChoseOnClickListener());
		sex_Woman.setOnClickListener(new ChoseOnClickListener());

		// 是否已婚选项用textView实现radioBox的效果
		isMarriedN = (TextView) rootview.findViewById(R.id.isMarriedN);
		isMarriedY = (TextView) rootview.findViewById(R.id.isMarriedY);

		isMarriedN.setBackgroundColor(customColor_green);
		isMarriedY.setBackgroundColor(customColor_grey);

		isMarriedN.setOnClickListener(new ChoseOnClickListener());
		isMarriedY.setOnClickListener(new ChoseOnClickListener());

		et_birthday = (EditText) rootview.findViewById(R.id.et_birthday); // 出生日期
		// 在editText的点击事件中增加日期选择器
		et_birthday.setOnClickListener(new DatePackClickListener());

		et_salary = (EditText) rootview.findViewById(R.id.et_salary); // 期望薪资
		et_salary.clearFocus();
		et_salary.setOnClickListener(new SalaryDialogClickListener());

		et_experience = (EditText) rootview.findViewById(R.id.et_workExperience); //工作经验
		et_experience.setOnClickListener(new ExperDialogClickListener());
		
		commitBtn_fbf = (Button) rootview.findViewById(R.id.commitBtn_fbf);	//提交按钮
		commitBtn_fbf.setOnClickListener(new CommitClickListener());
		
		return rootview;

	}

	// 选择性别和是否已婚的点击事件
	private class ChoseOnClickListener implements
			android.view.View.OnClickListener {

		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.sex_Man:
				choseSex = 0;
				sex_Man.setBackgroundColor(customColor_green);
				sex_Woman.setBackgroundColor(customColor_grey);
				break;

			case R.id.sex_Woman:
				choseSex = 1;
				sex_Man.setBackgroundColor(customColor_grey);
				sex_Woman.setBackgroundColor(customColor_green);
				break;

			case R.id.isMarriedN:
				choseMarried = 0;
				isMarriedN.setBackgroundColor(customColor_green);
				isMarriedY.setBackgroundColor(customColor_grey);
				break;

			case R.id.isMarriedY:
				choseMarried = 1;
				isMarriedN.setBackgroundColor(customColor_grey);
				isMarriedY.setBackgroundColor(customColor_green);
				break;

			default:
				break;
			}

		}
	}

	// 添加日期选择器的事件
	private class DatePackClickListener implements OnClickListener {

		private final int DATE_DIALOG = 1;

		@Override
		public void onClick(View v) {
			
			getActivity().showDialog(DATE_DIALOG);
		}

	}

	// 在dialog中添加日期选择器
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub

		Calendar calendar = Calendar.getInstance();

		Dialog dialog = null;
		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month,
					int dayOfMonth) {

				// Calendar月份是从0开始,所以month要加1
				et_birthday.setText(year + "年" + (month + 1) + "月" + dayOfMonth
						+ "日");

			}
		};
		dialog = new DatePickerDialog(getActivity(), dateListener,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		// 点击dialog外关闭dialog
		dialog.setCanceledOnTouchOutside(true);
		return dialog;

	}

	// salary的点击事件
	private class SalaryDialogClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			OnSureClickListener osClickListener = new OnSureClickListener() {

				@Override
				public void cancelClick(View v, Dialog dialog) {
					switch (v.getId()) {
					case R.id.dialogBtn:
						dialog.dismiss();
						break;

					case R.id.salaryChoose01:
						et_salary.setText(R.string.dialogSel1);
						dialog.dismiss();
						break;

					case R.id.salaryChoose02:
						et_salary.setText(R.string.dialogSel2);
						dialog.dismiss();
						break;

					case R.id.salaryChoose03:
						et_salary.setText(R.string.dialogSel3);
						dialog.dismiss();
						break;

					case R.id.salaryChoose04:
						et_salary.setText(R.string.dialogSel4);
						dialog.dismiss();
						break;

					case R.id.salaryChoose05:
						et_salary.setText(R.string.dialogSel5);
						dialog.dismiss();
						break;

					case R.id.salaryChoose06:
						et_salary.setText(R.string.dialogSel6);
						dialog.dismiss();
						break;

					case R.id.salaryChoose07:
						et_salary.setText(R.string.dialogSel7);
						dialog.dismiss();
						break;
					}
				}
			};

			SalaryDialog dialog = new SalaryDialog(getActivity(),
					R.style.SalaryDialogStyle, osClickListener);

			dialog.setContentView(R.layout.salary_dialog);
			// 点击dialog外不会关闭dialog
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
	}

	// experience的点击事件
	private class ExperDialogClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			OnExperClickListener osClickListener = new OnExperClickListener() {

				@Override
				public void cancelClick(View v, Dialog dialog) {
					switch (v.getId()) {
					case R.id.exDialogBtn:
						dialog.dismiss();
						break;

					case R.id.exper01:
						et_experience.setText(R.string.experChoose01);
						dialog.dismiss();
						break;

					case R.id.exper02:
						et_experience.setText(R.string.experChoose02);
						dialog.dismiss();
						break;

					case R.id.exper03:
						et_experience.setText(R.string.experChoose03);
						dialog.dismiss();
						break;

					case R.id.exper04:
						et_experience.setText(R.string.experChoose04);
						dialog.dismiss();
						break;

					case R.id.exper05:
						et_experience.setText(R.string.experChoose05);
						dialog.dismiss();
						break;

					case R.id.exper06:
						et_experience.setText(R.string.experChoose06);
						dialog.dismiss();
						break;

					case R.id.exper07:
						et_experience.setText(R.string.experChoose07);
						dialog.dismiss();
						break;
					}
				}
			};

			ExperienceDialog dialog = new ExperienceDialog(getActivity(),
					R.style.exDialogStyle, osClickListener);

			dialog.setContentView(R.layout.experience_dialog);
			// 点击dialog外不会关闭dialog
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
	}
	
	private class CommitClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity().getApplicationContext(), "提交成功！", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	/** 监听对话框里面的退出键点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				break;

			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				getActivity().finish();
				
				break;

			default:
				break;

			}
		}
	};
	
	//点击退出会弹出alertDialog
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("确认是否退出？");
			builder.setPositiveButton("取消", listener);
			builder.setNegativeButton("确认", listener);
			AlertDialog alert = builder.create();
			alert.show();
		}
		return false;
	}
}