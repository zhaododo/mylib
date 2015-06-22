package com.android.dream.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-12上午10:49:17
 */
public class DBErpBoardMainHelper extends SQLiteOpenHelper{

	private static final String DBNAME = "erpboard.db";	//设置数据库的名称
	private static final int VERSION = 1;	//设置数据库的版本
	
	public DBErpBoardMainHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS erp_news (id integer primary key autoincrement,messageid  varchar(100), status varchar(2))");
		db.execSQL("CREATE TABLE IF NOT EXISTS erp_news_item (id integer primary key autoincrement," +
				"messageid  varchar(100), filehandle varchar(100)," +
				"filename varchar(100), filerename varchar(100)," +
				"status varchar(2), path varchar(100))");
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS erp_news");	//此处是删除数据表，在实际的业务中一般是需要数据备份的
		onCreate(db);	//调用onCreate方法重新创建数据表，也可以自己根据业务需要创建新的的数据表
	}

}
