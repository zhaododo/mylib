package com.android.dream.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.dream.app.R;

public class ExperienceDialog extends Dialog{

	Context context;
	public OnExperClickListener myClickListener;
	private Button cancelBtn;
	private TextView exChoose01,exChoose02,exChoose03,exChoose04,exChoose05,exChoose06,exChoose07;

	public ExperienceDialog(Context context, int theme) {
		super(context,theme);
	}

	public ExperienceDialog(Context context, int theme, OnExperClickListener onExperClickListener) {
		super(context,theme);
		this.myClickListener = onExperClickListener;
	}

	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.experience_dialog);
		
		initViews(this);

	}

	private void initViews(final Dialog dialog){
		cancelBtn = (Button) findViewById(R.id.exDialogBtn);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myClickListener.cancelClick(v,dialog);
			}
		});
		
		
		exChoose01 = (TextView) findViewById(R.id.exper01);
		exChoose01.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		exChoose02 = (TextView) findViewById(R.id.exper02);
		exChoose02.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		exChoose03 = (TextView) findViewById(R.id.exper03);
		exChoose03.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		exChoose04 = (TextView) findViewById(R.id.exper04);
		exChoose04.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		exChoose05 = (TextView) findViewById(R.id.exper05);
		exChoose05.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		exChoose06 = (TextView) findViewById(R.id.exper06);
		exChoose06.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
		exChoose07 = (TextView) findViewById(R.id.exper07);
		exChoose07.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myClickListener.cancelClick(v,dialog);
				
			}
		});
		
	}
	
	public interface OnExperClickListener{
		public void cancelClick(View v,Dialog dialog);
	}
	
}
