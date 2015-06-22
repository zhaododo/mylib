package com.android.dream.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBPersonHelper extends SQLiteOpenHelper{
	
	private final static  String DATABASE_NAME = "local.db";  
	private final static  int DATABASE_VERSION = 1;
	 
	public DBPersonHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE IF NOT EXISTS person" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, code VARCHAR,  pwd VARCHAR, age INTEGER, info TEXT)");  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");  
	}

}
