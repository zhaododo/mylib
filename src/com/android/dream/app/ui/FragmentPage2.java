package com.android.dream.app.ui;

import com.android.dream.app.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentPage2 extends Fragment{

	private String TAG="FragmentPage2";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		
		Log.i(TAG, "onCreateView()");
		return inflater.inflate(R.layout.fragment_2, null);		
	}	
}