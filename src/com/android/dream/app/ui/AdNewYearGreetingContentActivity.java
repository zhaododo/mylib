package com.android.dream.app.ui;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.dream.app.AppContext;
import com.android.dream.app.AppException;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdFriendCommentVo;
import com.android.dream.app.bean.AdFriendStatusVo;
import com.android.dream.app.util.BitmapManagerNewYear;
import com.android.dream.app.util.StringUtils;
import com.android.dream.app.widget.Shimmer;
import com.android.dream.app.widget.ShimmerTextView;
import com.android.dream.app.widget.SmileyParser;

public class AdNewYearGreetingContentActivity extends BaseActivity{

	private AdFriendStatusVo adFriendStatusVo;
	private ImageView carheaderimg;
	private ShimmerTextView statuscontent;
	private static BitmapManagerNewYear bmpManagerNewYear;
	private TextView title;  //ad_newyeargreetingcard_header_tv;
	private ImageView back;  //ad_newyeargreetingcard_header_back
	private AppContext appContext;
	private String picrul;
	private Shimmer shimmer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_newyeargreetingcard_content);
		adFriendStatusVo = (AdFriendStatusVo) getIntent().getSerializableExtra("friendStatusVo");
		appContext = (AppContext) getApplication();
		bmpManagerNewYear = new BitmapManagerNewYear();
		//初始化页面控件
		initview();
	}

	/**
	 * 初始化页面控件
	 */
	private void initview(){
		title = (TextView) findViewById(R.id.ad_newyeargreetingcard_header_tv);
		title.setText("贺卡详细内容");
		back = (ImageView) findViewById(R.id.ad_newyeargreetingcard_header_back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AdNewYearGreetingContentActivity.this.finish();
			}
		});
		carheaderimg = (ImageView) findViewById(R.id.ad_newyeargreetingcard_contentbg);
		statuscontent = (ShimmerTextView) findViewById(R.id.ad_newyeargreetingcard_contenttext);
		AssetManager mgr = getAssets();//得到AssetManager
		Typeface tf = Typeface.createFromAsset(mgr, "fonts/liuxingjianti.ttf");//根据路径得到Typeface
		statuscontent.setTypeface(tf);//设置字体    
		statuscontent.setText(StringUtils.content2Space(adFriendStatusVo.getStrContent()));
		shimmer = new Shimmer();
		shimmer.start(statuscontent);
		carheaderimg.setBackgroundResource(R.drawable.ic_playlist_recommend_icon_default);
		
		if(adFriendStatusVo.getPiclis()!=null&&adFriendStatusVo.getPiclis().size()>0){
			try {
				picrul = appContext.getFCLoadPic(adFriendStatusVo.getUserno(), adFriendStatusVo.getPiclis().get(0));
				if (bmpManagerNewYear != null)
				{
					bmpManagerNewYear.loadBitmap(picrul, carheaderimg,1000,400);
				}
			} catch (AppException e) {
				// TODO Auto-generated catch block
				carheaderimg.setBackgroundResource(R.drawable.ic_playlist_recommend_icon_default);
			}
		}
	}
	
	public LinearLayout createReplyDetail(AdFriendCommentVo friendCommentVo) {
		// 创建一个新的布局
		LinearLayout layout1 = new LinearLayout(getApplicationContext());

		// 定义LinearLayout布局的属性
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		layout1.setLayoutParams(llp);
		layout1.setOrientation(LinearLayout.HORIZONTAL);

		LayoutParams namelp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		Resources res = getResources();
		int nameTextColor = res.getColor(R.color.linktextcontentcolor);
		TextView name = new TextView(this);
		name.setText(friendCommentVo.getName()+":");
		name.setTextColor(nameTextColor);
		name.setTextSize(13);
		name.setLayoutParams(namelp);
		name.setTag(friendCommentVo.getUserno());
		LayoutParams replyContentlp = new LayoutParams(0, 
				LayoutParams.WRAP_CONTENT, 
				1);
		TextView replyContent = new TextView(this);
		replyContent.setLayoutParams(replyContentlp);
		SmileyParser.init(this);		
		SmileyParser parser = SmileyParser.getInstance();
		replyContent.setText(parser.addSmileySpans(friendCommentVo.getComment()));

		layout1.addView(name);
		layout1.addView(replyContent);
		return layout1;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finished();
			return true;
		}
		return true;
	}
	
	private void finished()
	{
		super.onDestroy();
		if (shimmer != null)
		{
			shimmer.cancel();
		}
		if (bmpManagerNewYear != null)
		{
			bmpManagerNewYear.clear();
		}
	}
	
	@Override
	protected void onDestroy() {
		finished();
	}
	
}
