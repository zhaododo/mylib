package com.android.dream.file;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author zhaododo
 *
 */
public class SharedPreferencesUtil {
	
	private SharedPreferences mSharedPref;
	private Context mContext;
	
	public SharedPreferencesUtil(Context context)
	{
		mContext = context;
	}
	
	/**
	 * @param resource_id eg:com.example.myapp.PREFERENCE_FILE_KEY
	 */
	public void init(int resource_id)
	{
		if (mContext == null ) throw new IllegalArgumentException("context is null!");
		
		mSharedPref = mContext.getSharedPreferences(mContext.getString(resource_id),Context.MODE_PRIVATE);
	}
	
	private void check()
	{
		if (mSharedPref == null) throw new IllegalArgumentException("mSharedPref is null!");
		if (mContext == null ) throw new IllegalArgumentException("mContext is null!");
		
	}
	
	public void writeIntValue(int key_id,int i_value)
	{
		check();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putInt(mContext.getString(key_id), i_value);
		editor.commit();
	}
	
	public void writeStringValue(int key_id,String str_value)
	{
		check();
		SharedPreferences.Editor editor = mSharedPref.edit();
		editor.putString(mContext.getString(key_id), str_value);
		editor.commit();
	}
	
	public int getIntValue(int key_id,int default_value)
	{
		check();
		return mSharedPref.getInt(mContext.getString(key_id), default_value);
	}
	
	public String getStringValue(int key_id,String default_value)
	{
		check();
		return mSharedPref.getString(mContext.getString(key_id), default_value);
	}

}
