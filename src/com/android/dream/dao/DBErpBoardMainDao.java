package com.android.dream.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.dream.db.DBErpBoardMainHelper;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-12上午11:13:13
 */
public class DBErpBoardMainDao {

	private static final String TAG = "DBErpBoardMainDao";
	private DBErpBoardMainHelper openHelper;	//声明数据库管理器
	
	public DBErpBoardMainDao(Context context){
		openHelper = new DBErpBoardMainHelper(context);
	}
	/**
	 * 添加公告信息
	 * @param messageid
	 */
	public void adddata(String messageid){
		SQLiteDatabase db = null;
		try{
			if(!isexit(messageid)){
				db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
				db.execSQL("insert into erp_news(messageid,status) values(?,?)",
						new Object[]{messageid,"Y"});	//插入特定下载路径特定线程ID已经下载的数据
				//db.setTransactionSuccessful();	//设置事务执行的标志为成功
			}
			
		}finally{	//此部分的代码肯定是被执行的，如果不杀死虚拟机的话
			//db.endTransaction();	//结束一个事务，如果事务设立了成功标志，则提交事务，否则会滚事务
		}
		if(db!=null){
			db.close();	//关闭数据库，释放相关资源
		}
		
	}
	
	/**
	 * 判断信息是否存在
	 * @param messageid
	 * @return
	 */
	public Boolean isexit(String messageid){
		Boolean rslt=false;
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		Cursor cursor = db.rawQuery("select messageid from erp_news where messageid = ?", new String[]{messageid});
		while(cursor.moveToNext()){	//从第一条记录开始开始遍历Cursor对象
			rslt=true;
		}
		cursor.close();	//关闭cursor，释放资源
		db.close();	//关闭数据库
		return rslt;
	}
	
	/**
	 * 添加文件公告文件信息
	 * @param messageid
	 * @param filehandle
	 * @param filename
	 */
	public void addmsgfile(String messageid,String filehandle,String filename){
		SQLiteDatabase db = null;
		try{
			if(!msgfileisexit(messageid,filehandle)){
				db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
				db.execSQL("insert into erp_news_item(messageid,filehandle,filename,status) values(?,?,?,?)",
						new Object[]{messageid,filehandle,filename,"1"});	//插入特定下载路径特定线程ID已经下载的数据
				//db.setTransactionSuccessful();	//设置事务执行的标志为成功
			}
			
		}finally{	//此部分的代码肯定是被执行的，如果不杀死虚拟机的话
			//db.endTransaction();	//结束一个事务，如果事务设立了成功标志，则提交事务，否则会滚事务
		}
		if(db!=null){
			db.close();	//关闭数据库，释放相关资源
		}
	}
	/**
	 * 判断信息文件是否存在
	 * @param messageid
	 * @param filehandle
	 * @return
	 */
	public Boolean msgfileisexit(String messageid,String filehandle){
		Boolean rslt=false;
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		Cursor cursor = db.rawQuery("select messageid from erp_news_item where messageid = ? and filehandle = ?", new String[]{messageid,filehandle});
		while(cursor.moveToNext()){	//从第一条记录开始开始遍历Cursor对象
			rslt=true;
		}
		cursor.close();	//关闭cursor，释放资源
		db.close();	//关闭数据库
		return rslt;
	}
	
	public String getfilestatus(String filehandle){
		String filestatus = "";
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		Cursor cursor = db.rawQuery("select status from erp_news_item where filehandle = ?", new String[]{filehandle});
		while(cursor.moveToNext()){	//从第一条记录开始开始遍历Cursor对象
			filestatus=cursor.getString(cursor.getColumnIndex("status"));
		}
		cursor.close();	//关闭cursor，释放资源
		db.close();	//关闭数据库
		
		return filestatus;
	}
	/**
	 * 更新文件状态
	 * @param filehandle
	 * @param status
	 */
	public void updatefilestatus(String filehandle,String status){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		db.execSQL("update erp_news_item set status=? where filehandle=?",
				new Object[]{status, filehandle});	//更新特定下载路径下特定线程已经下载的文件长度
		db.close();	//关闭数据库，释放相关的资源
		
	}
	/**
	 * 更新文件状态和实绩名称
	 * @param filehandle
	 * @param status
	 */
	public void updatefilestatusandrealname(String filehandle,String realname,String status){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		db.execSQL("update erp_news_item set status=?,filerename=? where filehandle=?",
				new Object[]{status,realname,filehandle});	//更新特定下载路径下特定线程已经下载的文件长度
		db.close();	//关闭数据库，释放相关的资源
		
	}
	/**
	 * 得到文件的真实名称
	 * @param filehandle
	 * @return
	 */
	public String getfilerealname(String filehandle){
		String filerename = "";
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		Cursor cursor = db.rawQuery("select filerename from erp_news_item where filehandle = ?", new String[]{filehandle});
		while(cursor.moveToNext()){	//从第一条记录开始开始遍历Cursor对象
			filerename=cursor.getString(cursor.getColumnIndex("filerename"));
		}
		cursor.close();	//关闭cursor，释放资源
		db.close();	//关闭数据库
		
		return filerename;
	}
}
