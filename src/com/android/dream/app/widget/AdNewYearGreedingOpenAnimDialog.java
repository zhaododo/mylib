package com.android.dream.app.widget;



import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdFriendStatusVo;
import com.android.dream.app.ui.AdNewYearGreetingContentActivity;

/**
 * @author zhaoj
 * 搜索对话框
 * 
 */
public class AdNewYearGreedingOpenAnimDialog extends Dialog{

	Context context;
	
	/**
	 * 打开贺卡动画View
	 */
	private ImageView openAnimationImage;
	
	
	/**
	 * 保存Dialog对象
	 */
	private AdNewYearGreedingOpenAnimDialog mDialog;
	
	/**
	 * 构造函数
	 * @param context
	 * @param theme      layout
	 * @param csListener 查询接口的实现
	 */
	public AdNewYearGreedingOpenAnimDialog(Context context, int theme) {
		super(context,theme);
		this.context = context;
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.ad_newyeargreetingcard_openanim_dialog);
		mDialog = this;
		initView();
	}
	/**
	 *  隐藏对话框
	 */
	private void hideDialog()
	{
		if (mDialog != null)
		{
			mDialog.hide();
		}
	}
	
	private void initView()
	{
		openAnimationImage = (ImageView) findViewById(R.id.animationImage);
		
	}
	
	@SuppressWarnings("deprecation")
	public void startAnim(final AdFriendStatusVo vo){
		
		MyAnimationDrawable frameAnimation = new MyAnimationDrawable((AnimationDrawable) context.getResources().getDrawable(R.anim.ad_newyeargreetingcard_open_animation)) {
			
			//动画结束调用改方法
			@Override
			void onAnimationEnd() {
				hideDialog();
				Intent intent = new Intent();
				intent.putExtra("friendStatusVo", vo);
				intent.setClass(context, AdNewYearGreetingContentActivity.class);
				context.startActivity(intent);
			}
		};
		
		openAnimationImage.setBackgroundDrawable(frameAnimation);
		frameAnimation.start();
	}
 
}
