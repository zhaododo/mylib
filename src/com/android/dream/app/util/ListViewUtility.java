package com.android.dream.app.util;

import android.R.integer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtility {   
    public static ViewGroup.LayoutParams setListViewHeightBasedOnChildren(ListView listView) {   
        ListAdapter listAdapter = listView.getAdapter();     
        if (listAdapter == null) {   
            // pre-condition   
            return null;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0; i < listAdapter.getCount(); i++) {   
            View listItem = listAdapter.getView(i, null, listView);   
            listItem.measure(0, 0);   
            totalHeight += listItem.getMeasuredHeight();   
        }   
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        listView.setLayoutParams(params);   
        
        return params;
    }   
}
