package com.android.dream.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.dream.app.bean.Person;
import com.android.dream.db.DBPersonHelper;

public class PersonDao {
	private DBPersonHelper helper;  
    private SQLiteDatabase db;
    
    public PersonDao(Context context) {  
        helper = new DBPersonHelper(context);  
        db = helper.getWritableDatabase();  
    }  
    
    public void add(List<Person> persons) {  
        db.beginTransaction();  //开始事务   
        try {  
            for (Person person : persons) {  
                db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?,?,?)", new Object[]{person.getName(), person.getCode(),person.getPwd(),person.getAge(), person.getInfo()});  
            }  
            db.setTransactionSuccessful();  //设置事务成功完成   
        } finally {  
            db.endTransaction();    //结束事务   
        }  
    }
    
    public void addone(Person person) {  
        db.beginTransaction();  //开始事务   
        try {  
              
            db.execSQL("INSERT INTO person VALUES(null, ?, ?, ?,?,?)", new Object[]{person.getName(), person.getCode(),person.getPwd(),person.getAge(), person.getInfo()});  
          
            db.setTransactionSuccessful();  //设置事务成功完成   
        } finally {  
            db.endTransaction();    //结束事务   
        }  
    }
    
    public void updateAge(Person person) {  
        ContentValues cv = new ContentValues();  
        cv.put("age", person.getAge());  
        db.update("person", cv, "name = ?", new String[]{person.getCode()});  
    }
    
    public void updatePwd(Person person) {  
        ContentValues cv = new ContentValues();  
        cv.put("pwd", person.getPwd());  
        db.update("person", cv, "code = ?", new String[]{person.getCode()});  
    }
    
    public void deleteOldPerson(Person person) {
        db.delete("person", "age >= ?", new String[]{String.valueOf(person.getAge())});  
    }
    
    public void deleteByPersonCode(Person person) {
        db.delete("person", "code = ?", new String[]{String.valueOf(person.getCode())});  
    }
    
    public List<Person> query() {  
        ArrayList<Person> persons = new ArrayList<Person>();  
        Cursor c = queryTheCursor();  
        while (c.moveToNext()) {
            Person person = new Person();  
            person.set_id(c.getInt(c.getColumnIndex("_id")));  
            person.setName(c.getString(c.getColumnIndex("name")));  
            person.setAge(c.getInt(c.getColumnIndex("age")));  
            person.setInfo(c.getString(c.getColumnIndex("info")));
            person.setPwd(c.getString(c.getColumnIndex("pwd")));
            person.setCode(c.getString(c.getColumnIndex("code")));
            persons.add(person);  
        }  
        c.close();  
        return persons;  
    } 
    
    public int findbycode(Person person){
    	 // 第一个参数String：表名  
        // 第二个参数String[]:要查询的列名  
        // 第三个参数String：查询条件  
        // 第四个参数String[]：查询条件的参数  
        // 第五个参数String:对查询的结果进行分组  
        // 第六个参数String：对分组的结果进行限制  
        // 第七个参数String：对查询的结果进行排序  
        Cursor cursor = db.query("person", new String[] { "name",  
                "code" }, "code=?", new String[] { person.getCode() }, null, null, null);  
        if(cursor.getCount()==0){
        	return 0;
        }
         
    	 return 1;
    }
    
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM person", null);  
        return c;  
    }  

    public void closeDB() {  
        db.close();  
    }  
}
