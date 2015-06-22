package com.android.dream.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.dream.app.R;


public class RightFragment extends Fragment {

	private ListView  lisfun;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView=inflater.inflate(R.layout.slide_right, container, false);
		lisfun=(ListView) rootView.findViewById(R.id.right_lis_item);
		String[] lisitems = getResources().getStringArray(R.array.fun_lis);
		ArrayAdapter<String> arrayadapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,lisitems);
		lisfun.setAdapter(arrayadapter);
		lisfun.setOnItemClickListener(new onitemclicklistener());
		return rootView;
	}
	
	public class onitemclicklistener implements OnItemClickListener{
        Fragment newfragment;
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			switch(arg2){
			
			case 0:
				newfragment=new AdContentFragment();
				break;				
			}
			if (newfragment != null)
				switchFragment(newfragment);

		}
	}
	
	// the meat of switching the above fragment
		private void switchFragment(Fragment fragment) {
			if (getActivity() == null)
				return;
			
			if (getActivity() instanceof AdMainActivity) {
				AdMainActivity fmainac = (AdMainActivity) getActivity();
				fmainac.switchContent(fragment);
			} 
//				else if (getActivity() instanceof ResponsiveUIActivity) {
//				ResponsiveUIActivity ra = (ResponsiveUIActivity) getActivity();
//				ra.switchContent(fragment);
//			}
		}

	

}
