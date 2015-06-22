package com.android.dream.app.util;

public class TestDES {
	
	public static void main(String[] args) {
		
		String key = "12345678";
		String text = "12345678";
		
		
		String result1="";
		String result2="";
		try {
			result1 = DES.encryptDES(text,key);
			result2 = DES.decryptDES(result1, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(result1);
		System.out.println(result2);

	}

}
