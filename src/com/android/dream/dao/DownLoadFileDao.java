package com.android.dream.dao;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.dream.db.DBDownLoadFileHelper;

/**
 * @author Wang Cheng
 *
 * @date 2014-9-3上午10:53:56
 */
public class DownLoadFileDao {

	private static final String TAG = "DownLoadFileDao";
	private DBDownLoadFileHelper openHelper;	//声明数据库管理器
	
	public DownLoadFileDao(Context context) {
		openHelper = new DBDownLoadFileHelper(context);	//根据上下文对象实例化数据库管理器
	}
	
	/**
	 * 获取特定URI的每条线程已经下载的文件长度
	 * @param path
	 * @return
	 */
	public Map<Integer, Integer> getData(String path){
		SQLiteDatabase db = openHelper.getReadableDatabase();	//获取可读的数据库句柄，一般情况下在该操作的内部实现中其返回的其实是可写的数据库句柄
		Cursor cursor = db.rawQuery("select threadid, downlength from filedownlog where downpath=?", new String[]{path});	//根据下载路径查询所有线程下载数据，返回的Cursor指向第一条记录之前
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();	//建立一个哈希表用于存放每条线程的已经下载的文件长度
		while(cursor.moveToNext()){	//从第一条记录开始开始遍历Cursor对象
			data.put(cursor.getInt(0), cursor.getInt(1));	//把线程id和该线程已下载的长度设置进data哈希表中
			data.put(cursor.getInt(cursor.getColumnIndexOrThrow("threadid")), cursor.getInt(cursor.getColumnIndexOrThrow("downlength")));
		}
		cursor.close();	//关闭cursor，释放资源
		db.close();	//关闭数据库
		return data;	//返回获得的每条线程和每条线程的下载长度
	}
	/**
	 * 保存每条线程已经下载的文件长度
	 * @param filehandle	文件的id编号
	 * @param path	下载的路径
	 * @param map 现在的id和已经下载的长度的集合
	 * @param filelength 文件长度
	 */
	public void save(String filehandle,String path,  Map<Integer, Integer> map,Integer filelength){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		db.beginTransaction();	//开始事务，因为此处要插入多批数据
		try{
			for(Map.Entry<Integer, Integer> entry : map.entrySet()){	//采用For-Each的方式遍历数据集合
				db.execSQL("insert into filedownlog(filehandle,downpath, threadid, downlength,filelength) values(?,?,?,?,?)",
						new Object[]{filehandle,path, entry.getKey(), entry.getValue(),filelength});	//插入特定下载路径特定线程ID已经下载的数据
			}
			db.setTransactionSuccessful();	//设置事务执行的标志为成功
		}finally{	//此部分的代码肯定是被执行的，如果不杀死虚拟机的话
			db.endTransaction();	//结束一个事务，如果事务设立了成功标志，则提交事务，否则会滚事务
		}
		db.close();	//关闭数据库，释放相关资源
	}
	/**
	 * 实时更新每条线程已经下载的文件长度
	 * @param path
	 * @param map
	 */
	public void update(String path, int threadId, int pos){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		db.execSQL("update filedownlog set downlength=? where downpath=? and threadid=?",
				new Object[]{pos, path, threadId});	//更新特定下载路径下特定线程已经下载的文件长度
		db.close();	//关闭数据库，释放相关的资源
	}
	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * @param path
	 */
	public void delete(String path){
		SQLiteDatabase db = openHelper.getWritableDatabase();	//获取可写的数据库句柄
		db.execSQL("delete from filedownlog where downpath=?", new Object[]{path});	//删除特定下载路径的所有线程记录
		db.close();	//关闭数据库，释放资源
	}
	

	/**
	 * 按fileHandle查询已下载的数据和文件总长度
	 * @param fileHandle
	 * @return
	 */
	public Map<String,Integer> getFileDownInfo(String fileHandle){
		Map<String,Integer> maprslt =new HashMap<String,Integer>();
		SQLiteDatabase db = openHelper.getReadableDatabase();	//获取可读的数据库句柄，一般情况下在该操作的内部实现中其返回的其实是可写的数据库句柄
		Cursor cursor = db.rawQuery("select sum(downlength) downlength,max(filelength) filelength from filedownlog where filehandle=?", new String[]{fileHandle});	//根据下载路径查询所有线程下载数据，返回的Cursor指向第一条记录之前
		while(cursor.moveToNext()){	//从第一条记录开始开始遍历Cursor对象
			maprslt.put("downlength", cursor.getInt(cursor.getColumnIndex("downlength")));	//把线程id和该线程已下载的长度设置进data哈希表中
			maprslt.put("filelength", cursor.getInt(cursor.getColumnIndex("filelength")));
		}
		cursor.close();	//关闭cursor，释放资源
		db.close();	//关闭数据库
		printMap(maprslt);
		return maprslt;
	}
	
	/**
	 * 打印fileHandle对应的已下载的数据和文件总长度
	 * @param maprslt
	 */
	public static void printMap(Map<String,Integer> maprslt){
		for(Map.Entry<String, Integer> entry : maprslt.entrySet()){	//使用For-Each循环的方式遍历获取的头字段的值，此时遍历的循序和输入的顺序相同
			String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";	//当有键的时候这获取键，如果没有则为空字符串
			print(key+ entry.getValue());	//答应键和值的组合
		}
	}
	
	private static void print(String msg){
		Log.i(TAG, msg);	//使用LogCat的Information方式打印信息
	}
	
}
