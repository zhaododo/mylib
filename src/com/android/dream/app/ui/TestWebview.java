package com.android.dream.app.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;

public class TestWebview extends BaseActivity{

	private AppContext appContext;
	private WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testwebview);
		appContext = (AppContext) getApplication();
		initview();
		 
	}
	private void initview(){
		mWebView = (WebView)findViewById(R.id.wv_detail_webview);
    	mWebView.getSettings().setJavaScriptEnabled(false);
    	mWebView.getSettings().setSupportZoom(true);
    	mWebView.getSettings().setBuiltInZoomControls(true);
    	mWebView.getSettings().setDefaultFontSize(15);
    	mWebView.loadUrl("http://www.baidu.com/");// 百度链接
    	mWebView.setWebViewClient(new MyWebViewClient ());  
	}
	
	//Web视图  
	   private class MyWebViewClient extends WebViewClient {  
	        @Override 
	       public boolean shouldOverrideUrlLoading(WebView view, String url) {  
	            view.loadUrl(url);  
	            return true;  
	        }  
	    } 
	   
//	   @Override 
	  //设置回退  
	  //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法  
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
//			mWebView.goBack(); // goBack()表示返回WebView的上一页面
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}


}
