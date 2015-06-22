package com.android.dream.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.android.dream.app.R;


public class WelcomeActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.welcome);
		View view =(View) findViewById(R.id.welcome_layout_id);
        AlphaAnimation aa = new AlphaAnimation(0.01f,1.0f);//透明度变化
        aa.setDuration(1500);//设置渐变时间
        
        
        view.startAnimation(aa);//设置渐变的view
        aa.setAnimationListener(new AnimationListener(){
       	 //动画结束后自动执行
           @Override
           public void onAnimationEnd(Animation arg0) {
               redirectTo();
           }
           @Override
           public void onAnimationRepeat(Animation animation) {
           	
           }
           @Override
           public void onAnimationStart(Animation animation) {
           	
           }
                                                                         
        });
    }
    
    /**
     * 跳转到登陆界面
     */
    private void redirectTo(){       
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}