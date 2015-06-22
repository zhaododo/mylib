package com.android.dream.file;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	
	private String LOG_TAG = "FileUtil";
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public File getTempFile(Context context, String url) {
	    File file = null;
	    try {
	        String fileName = Uri.parse(url).getLastPathSegment();
	        file = File.createTempFile(fileName, null, context.getCacheDir());
	    }
	    catch (IOException e) {
	        // Error while creating file
	    }
	    return file;
	}
}
