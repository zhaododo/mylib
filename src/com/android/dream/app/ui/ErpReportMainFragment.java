package com.android.dream.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.dream.app.R;

public class ErpReportMainFragment extends Fragment{

	private String TAG="wchtest_Fra_product_main";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.i(TAG, "onCreateView()");
		
		return inflater.inflate(R.layout.product_main_con, null);	
		
	
	}	
}
