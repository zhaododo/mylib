package com.android.dream.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.dream.app.R;

public class SalaryDialog extends Dialog{

	Context context;
	public OnSureClickListener myClickListener;
	private Button cancelBtn;
	private TextView salaryChoose01,salaryChoose02,salaryChoose03,salaryChoose04,salaryChoose05,salaryChoose06,salaryChoose07;

	public SalaryDialog(Context context, int theme) {
		super(context,theme);
	}

	public SalaryDialog(Context context, int theme, OnSureClickListener onSureClickListener) {
		super(context,theme);
		this.myClickListener = onSureClickListener;
	}

	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.salary_dialog);
		
		initViews(this);

	}

	private void initViews(final Dialog dialog){
		cancelBtn = (Button) findViewById(R.id.dialogBtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myClickListener.cancelClick(v,dialog);
			}
		});
		
		
		salaryChoose01 = (TextView) findViewById(R.id.salaryChoose01);
		salaryChoose01.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		salaryChoose02 = (TextView) findViewById(R.id.salaryChoose02);
		salaryChoose02.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		salaryChoose03 = (TextView) findViewById(R.id.salaryChoose03);
		salaryChoose03.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		salaryChoose04 = (TextView) findViewById(R.id.salaryChoose04);
		salaryChoose04.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		salaryChoose05 = (TextView) findViewById(R.id.salaryChoose05);
		salaryChoose05.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		salaryChoose06 = (TextView) findViewById(R.id.salaryChoose06);
		salaryChoose06.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		salaryChoose07 = (TextView) findViewById(R.id.salaryChoose07);
		salaryChoose07.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
	}
	
	public interface OnSureClickListener{
		public void cancelClick(View v,Dialog dialog);
	}
	
}
