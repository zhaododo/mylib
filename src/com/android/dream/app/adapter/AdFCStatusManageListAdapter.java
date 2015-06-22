package com.android.dream.app.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.api.AD_NISCO_NORMAL_URL;
import com.android.dream.app.bean.AdAjaxOpt;
import com.android.dream.app.bean.AdFriendCommentVo;
import com.android.dream.app.bean.AdFriendStatusVo;
import com.android.dream.app.bean.AdFriendZanVo;
import com.android.dream.app.ui.UIHelper;
import com.android.dream.app.util.BitmapManager;
import com.android.dream.app.widget.AdNewYearGreedingOpenAnimDialog;
import com.android.dream.app.widget.CommentsInputDialog;
import com.android.dream.app.widget.PersonalHeaderView;
import com.android.dream.app.widget.SmileyParser;

/**
 * @author zhaoj 朋友圈适配器
 * 
 */
public class AdFCStatusManageListAdapter extends BaseAdapter {

	private Context context;// 运行上下文
	private final List<AdFriendStatusVo> listItems;// 数据集合
	private LayoutInflater listContainer;// 视图容器
	private BitmapManager bmpManager;
	private SmileyParser simileyParser;
	
	//动画效果
	private AdNewYearGreedingOpenAnimDialog animDialog;

	// 用户头像下载路径
	private static final String FACE_PATH = AD_NISCO_NORMAL_URL.XT_USERHEADERPIC_DOWNLOAD;

	// 获取朋友动态（纯文本，图文，贺卡）对应的图像
	public final static String FRIEND_ALBUM_PIC_PATH = AD_NISCO_NORMAL_URL.GET_FRIEND_ALBUM_PIC;

	// 获取朋友动态(朋友分享、超链接）对应的图像
	private static final String FRIEND_SHARE_PIC_PATH = "";

	final private Handler handler;
	
	final private AppContext appContext;
	final private AdFCStatusManageListAdapter adapter;
	private CommentsInputDialog ciDialog;
	private SpannableStringBuilder style;
	private DialogListener dialogListener;
	
	// 自定义控件集合
	static class ListItemView {

		// 图文内容（或超链接对应的layout)
		public LinearLayout content_layout;
		// 赞和评论的layout
		public LinearLayout zan_comment_layout;
		// 用户头像
		public PersonalHeaderView userheaderpic;
		// 用户姓名
		public TextView username;
		// 用户工号
		public TextView userno;
		// 状态屏蔽/删除
		public TextView ismask;
		// 发布时间
		public TextView publishdate;
		// 标题
		public TextView title;
		// 内容
		public TextView content;
		// 内容图片（用于贺卡）
		public ImageView image;
		// 相册集（图文的相册集）
		public GridView imageGridView;

		// 超链接图
		public ImageView linkImage;

		// 超链接内容
		public TextView linkUrl;

		// 赞
		public TextView zan;

		// 赞分割线
		public View zan_line;

		// 赞图标
		public ImageView zan_image;
		// 赞按钮
		public TextView zan_tx_view;
		// 评论图标
		public ImageView comment_image;
		// 评论按钮
		public TextView comment_tx_view;

		// 评论
		public LinearLayout replycontent;
		// 增加评论
		public TextView add_replycontent;
	}

			
	/**
	 * 实例化Adapter
	 * 
	 * @param context
	 * @param data
	 * @param resource
	 */
	public AdFCStatusManageListAdapter(Context context, AppContext appContext, List<AdFriendStatusVo> data) {
		this.context = context;
		this.appContext = appContext;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = data;
		this.handler = createHandler();
		bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.default_face));

		SmileyParser.init(context);
		simileyParser = SmileyParser.getInstance();
		
		animDialog = new AdNewYearGreedingOpenAnimDialog(context, R.style.AdNewYearCardOpenAnimDialog);
		animDialog.setContentView(R.layout.ad_newyeargreetingcard_openanim_dialog);
		Window dialogWindow = animDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.gravity = Gravity.CENTER;
		dialogWindow.setAttributes(lp);
		animDialog.setCanceledOnTouchOutside(true);
		animDialog.hide();
		adapter = this;
		
		ciDialog = new CommentsInputDialog(context, R.style.CommentsInputDialog);
		ciDialog.setContentView(R.layout.ad_commentsinput_dialog);
		Window dialogWindowCi = ciDialog.getWindow();
		WindowManager.LayoutParams lpCi = dialogWindowCi.getAttributes();
		lpCi.width = WindowManager.LayoutParams.MATCH_PARENT;
		lpCi.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lpCi.gravity = Gravity.BOTTOM;
		dialogWindowCi.setAttributes(lpCi);
		ciDialog.setCanceledOnTouchOutside(true);
		dialogListener = new DialogListener();
		ciDialog.setListener(dialogListener);
		ciDialog.hide();
	}
	
	
	/**
	 * @author zhaoj
	 * 对话框Listener
	 *
	 */
	class DialogListener implements CommentsInputDialog.PriorityListener
	{
		private String friendsMainId;
		private int position;
		
		public String getFriendsMainId() {
			return friendsMainId;
		}

		public void setFriendsMainId(String friendsMainId) {
			this.friendsMainId = friendsMainId;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void refreshPriorityUI(String comment) {
			
			if (appContext != null)
			{
				adapter.addFriendComment(appContext.getCurrentUser(), appContext.getCurrentUserName(), friendsMainId, comment, position);
			}
		}
		
	}
	
	private Handler createHandler()
	{
		return new Handler() {
			
			public void handleMessage(Message msg) {
				
				if(msg.what == UIHelper.MSG_SUCCESS){
					switch (msg.arg1) {
					case UIHelper.MSG_ZAN:
						
						if(msg.obj != null)
						{
							String userno = msg.getData().getString("userno");
							String username = msg.getData().getString("username");
							int position = msg.getData().getInt("position");
							
							AdFriendStatusVo item = listItems.get(position);
							AdFriendZanVo zanVo = new AdFriendZanVo();
							zanVo.setUserno(userno);
							zanVo.setUsername(username);
							item.addZan(zanVo);
							//Toast.makeText(context, "赞成功", Toast.LENGTH_SHORT).show();
							adapter.notifyDataSetChanged();
						}
						
						break;
					case UIHelper.MSG_COMMENT:
						
						if(msg.obj != null)
						{
							AdAjaxOpt opt = (AdAjaxOpt)msg.obj;
							if(opt.getStatus().equals(AdAjaxOpt.OK_STATUS)){
								String userno = msg.getData().getString("userno");
								String username = msg.getData().getString("username");
								String comment = msg.getData().getString("comment");
								int position = msg.getData().getInt("position");
								
								AdFriendStatusVo item = listItems.get(position);
								AdFriendCommentVo commentVo = new AdFriendCommentVo();
								commentVo.setUserno(userno);
								commentVo.setName(username);
								commentVo.setComment(comment);
								item.addComment(commentVo);
								
								//Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
								adapter.notifyDataSetChanged();
							}else{
								Toast.makeText(context, opt.getMsg(), Toast.LENGTH_SHORT).show();
							}
						}
						
						break;
					
						case UIHelper.MSG_PINGBI:
						
							if(msg.obj != null)
							{
								AdAjaxOpt opt = (AdAjaxOpt)msg.obj;
								if(opt.getStatus().equals(AdAjaxOpt.OK_STATUS)){
									int position = msg.getData().getInt("position");
									int intIsActive = msg.getData().getInt("intIsActive");
									listItems.get(position).setIntIsActive(intIsActive);
									adapter.notifyDataSetChanged();
									Toast.makeText(context, opt.getMsg(), Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(context, opt.getMsg(), Toast.LENGTH_SHORT).show();
								}
							}
						break;
						
						case UIHelper.MSG_DELFC:
							
							if(msg.obj != null)
							{
								AdAjaxOpt opt = (AdAjaxOpt)msg.obj;
								if(opt.getStatus().equals(AdAjaxOpt.OK_STATUS)){
									int position = msg.getData().getInt("position");
									listItems.get(position).setIntCardActive(0);
									adapter.notifyDataSetChanged();
									Toast.makeText(context, opt.getMsg(), Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(context, opt.getMsg(), Toast.LENGTH_SHORT).show();
								}
							}
						break;
					}
				}
				
				else if (msg.what == UIHelper.MSG_ERROR) {
					
					switch (msg.arg1) {
					case UIHelper.MSG_ZAN:
						
						if(msg.obj != null)
						{
							Toast.makeText(context, "赞失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
					case UIHelper.MSG_COMMENT:
						
						if(msg.obj != null)
						{
							Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
					case UIHelper.MSG_PINGBI:
						
						if(msg.obj != null)
						{
							Toast.makeText(context, "屏蔽失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
					case UIHelper.MSG_DELFC:
						
						if(msg.obj != null)
						{
							Toast.makeText(context, "删帖失败", Toast.LENGTH_SHORT).show();
						}
						
						break;
						
					}
				}
			};
		};
		
	}
	

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 自定义视图
		ListItemView listItemView = null;
		AdFriendStatusVo item = listItems.get(position);

		// 贺卡
		if (item.getType() == UIHelper.FRINDS_CICLE_TYPE_CARD) {

			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(R.layout.linkcontent_admin, null);

				listItemView = new ListItemView();

				// 内容的layout
				listItemView.content_layout = (LinearLayout) convertView
						.findViewById(R.id.content_layout_admin);

				// 赞和评论的layout
				listItemView.zan_comment_layout = (LinearLayout) convertView
						.findViewById(R.id.layout_zan_comment_admin);
				listItemView.zan_comment_layout.setVisibility(View.GONE);

				// 头像
				listItemView.userheaderpic = (PersonalHeaderView) convertView
						.findViewById(R.id.iv_fc_status_header_admin);
				bmpManager.loadBitmap(FACE_PATH + item.getUserno(),
						listItemView.userheaderpic);

				// 用户名
				listItemView.username = (TextView) convertView
						.findViewById(R.id.tv_fc_username_admin);
				listItemView.username.setText(item.getUsername());
				
				///用户工号
				listItemView.userno = (TextView) convertView
						.findViewById(R.id.tv_fc_userid_admin);
//				是否屏蔽
				listItemView.ismask= (TextView) convertView
						.findViewById(R.id.tv_fc_del_admin);
				// 发布时间
				listItemView.publishdate = (TextView) convertView
						.findViewById(R.id.tv_fc_status_time_admin);
				listItemView.publishdate.setText(item.getDisplayTime());

				// 标题
				listItemView.title = (TextView) convertView
						.findViewById(R.id.linktextcontent_admin);
				listItemView.title.setText(item.getStrTitle());

				// 点赞人数
				listItemView.zan = (TextView) convertView
						.findViewById(R.id.zan_admin);

				// 启用
				listItemView.zan_image = (ImageView) convertView
						.findViewById(R.id.zan_image_admin);
				listItemView.zan_tx_view = (TextView) convertView
						.findViewById(R.id.zan_tx_view_admin);
				// 删帖
				listItemView.comment_image = (ImageView) convertView
						.findViewById(R.id.comment_image_admin);
				listItemView.comment_tx_view = (TextView) convertView
						.findViewById(R.id.comment_tx_view_admin);

				// 点赞分割线
				listItemView.zan_line = (View) convertView
						.findViewById(R.id.zan_line_admin);
				// 回复列表
				listItemView.replycontent = (LinearLayout) convertView
						.findViewById(R.id.replycontent_admin);
				// 有回复时显示
				listItemView.add_replycontent = (TextView) convertView
						.findViewById(R.id.add_replycontent_admin);

				// 相册集
				// listItemView.imageGridView = (GridView)
				// convertView.findViewById(R.id.grd_items);

				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
				listItemView.zan_comment_layout.setVisibility(View.GONE);
			}
			
			// 默认评论和赞都不显示（有数据才显示）
			listItemView.zan_comment_layout.setVisibility(View.GONE);
			listItemView.zan.setVisibility(View.GONE);
			listItemView.zan_line.setVisibility(View.GONE);
			listItemView.replycontent.setVisibility(View.GONE);
			listItemView.add_replycontent.setVisibility(View.GONE);
			listItemView.replycontent.removeAllViews();

			if (item.getType() == UIHelper.FRINDS_CICLE_TYPE_CARD) {
				listItemView.content_layout.setOnClickListener(new ContentOnClick(position));
			}
			
//			启用
			listItemView.zan_image.setOnClickListener(new HeaderOnClick(item
					.getMainStatuId(),position,1));
			listItemView.zan_tx_view.setOnClickListener(new HeaderOnClick(item
					.getMainStatuId(),position,1));
			
//			删帖
			listItemView.comment_image.setOnClickListener(new CommentOnClick(item
					.getMainStatuId(),position));
			listItemView.comment_tx_view.setOnClickListener(new CommentOnClick(item
					.getMainStatuId(),position));
			listItemView.add_replycontent.setOnClickListener(new CommentOnClick(item
					.getMainStatuId(),position));

			// 头像
			bmpManager.loadBitmap(FACE_PATH + item.getUserno(),
					listItemView.userheaderpic);

//			头像点击屏蔽
			listItemView.userheaderpic.setOnClickListener(new HeaderOnClick(item
					.getMainStatuId(),position,0));
 
			// 姓名
			listItemView.username.setText(item.getUsername());
			//工号
			int isactive = item.getIntIsActive();
			if(isactive == 0){
				listItemView.userno.setText(item.getUserno()+"已屏蔽");
			}else{
				listItemView.userno.setText(item.getUserno());
			}
			
			int iscardActive = item.getIntCardActive();
			if(iscardActive == 0){
				listItemView.ismask.setText("(已删除)");
			}else{
				listItemView.ismask.setText("(显示)");
			}
			// 标题
			
			String usermsg = item.getCompanyname()+item.getDepname()+" "+item.getUsername();
			style = new SpannableStringBuilder("新收到  "+usermsg+" 发来的新年贺卡，点击查看。"); 
			style.setSpan(new ForegroundColorSpan(Color.RED), 5, 5+usermsg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			listItemView.title.setTypeface(null, Typeface.BOLD);//设置字体
			listItemView.title.setText(style);
			
			//日期
			listItemView.publishdate.setText(item.getDisplayTime());

			// 赞
			if (item.getZanlis() != null && item.getZanlis().size() > 0) {
				StringBuffer sbuser = new StringBuffer("[fac赞] ");
				int size = item.getZanlis().size();
				int end = (size - 1) > 0 ? size - 1 : 0;
				for (int i = 0; i < size; i++) {
					if (i == end) {
						sbuser.append(item.getZanlis().get(i).getUsername());
					} else {
						sbuser.append(item.getZanlis().get(i).getUsername() + "，");
					}
				}
				String goodmanStr = sbuser.toString();
				listItemView.zan_comment_layout.setVisibility(View.VISIBLE);
				listItemView.zan.setVisibility(View.VISIBLE);
				listItemView.zan.setText(simileyParser.addSmileySpans(goodmanStr));

				if (item.getCommentlis() != null && item.getCommentlis().size() > 0) {
					listItemView.zan_line.setVisibility(View.VISIBLE);
				}
			}

			// 评论数据
			if (item.getCommentlis() != null && item.getCommentlis().size() > 0) {

				listItemView.zan_comment_layout.setVisibility(View.VISIBLE);
				listItemView.replycontent.setVisibility(View.VISIBLE);
				listItemView.add_replycontent.setVisibility(View.VISIBLE);
				for (int i = 0; i < item.getCommentlis().size(); i++) {
					LinearLayout replycontentChild = createReplyDetail(item
							.getCommentlis().get(i));
					listItemView.replycontent.addView(replycontentChild);
				}

			}

		}

		return convertView;
	}

	public LinearLayout createReplyDetail(AdFriendCommentVo friendCommentVo) {
		// 创建一个新的布局
		LinearLayout layout1 = new LinearLayout(context);

		// 定义LinearLayout布局的属性
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		layout1.setLayoutParams(llp);
		layout1.setOrientation(LinearLayout.HORIZONTAL);

		LayoutParams namelp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		Resources res = context.getResources();
		int nameTextColor = res.getColor(R.color.linktextcontentcolor);
		TextView name = new TextView(context);
		name.setText(friendCommentVo.getName() + ":");
		name.setTextColor(nameTextColor);
		name.setTextSize(13);
		name.setLayoutParams(namelp);
		name.setTag(friendCommentVo.getUserno());
		LayoutParams replyContentlp = new LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1);
		TextView replyContent = new TextView(context);
		replyContent.setLayoutParams(replyContentlp);
		SmileyParser.init(context);
		SmileyParser parser = SmileyParser.getInstance();
		replyContent
				.setText(parser.addSmileySpans(friendCommentVo.getComment()));

		layout1.addView(name);
		layout1.addView(replyContent);
		return layout1;
	}

	/**
	 * 删帖点击对象
	 * 
	 */
	class CommentOnClick implements View.OnClickListener {
		private String friendsMainId;
		private int position;

		public CommentOnClick(String friendsMainId,int position) {
			this.friendsMainId = friendsMainId;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
//			调用删帖的方法
//			Toast.makeText(context, "要删帖"+friendsMainId, Toast.LENGTH_SHORT).show();
			delFCStatus(appContext.getCurrentUser(),listItems.get(position).getMainStatuId(),position);
		}
	}

	/**
	 * 内容点击
	 * 
	 */
	class ContentOnClick implements View.OnClickListener {
		private int position;
		public ContentOnClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			animDialog.show();
			animDialog.startAnim(listItems.get(position));
		}
	}
	
	/**
	 * 头像点击
	 * 
	 */
	class HeaderOnClick implements View.OnClickListener {
		private String friendsMainId;
		private int position;
		private int intIsActive;

		public HeaderOnClick(String friendsMainId,int position,int intIsActive) {
			this.friendsMainId = friendsMainId;
			this.position = position;
			this.intIsActive = intIsActive;
		}

		@Override
		public void onClick(View v) {
//			 调用屏蔽的方法
//			Toast.makeText(context, "要屏蔽该条信息"+friendsMainId, Toast.LENGTH_SHORT).show();
			maskFCStatus(appContext.getCurrentUser(),listItems.get(position).getUserno(),position,intIsActive);
		}
	}
	
	/**
	 * 调用屏蔽的方法
	 * @param userno 操作工号
	 * @param userno 屏蔽工号
	 * @param id     id  
	 * @param position
	 */
	private void maskFCStatus(final String operauserno,final String userno,final int position,final int intIsActive) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				try {
					// 屏蔽
					AdAjaxOpt opt = appContext.maskFCStatus(operauserno,userno,intIsActive);
					if (opt != null
							&& opt.getStatus().equals(AdAjaxOpt.OK_STATUS)) {
						msg.what = UIHelper.MSG_SUCCESS;
						msg.arg1 = UIHelper.MSG_PINGBI;
						msg.obj = opt;
						bundle.putInt("position", position);
						bundle.putInt("intIsActive", intIsActive);
						msg.setData(bundle);
					}
				} catch (Exception e) {
					msg.what = UIHelper.MSG_ERROR;
					msg.arg1 = UIHelper.MSG_PINGBI;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	
	/**
	 * 调用删帖的方法
	 * @param userno 操作工号
	 * @param id     id  
	 * @param position
	 */
	private void delFCStatus(final String operauserno,final String id,final int position) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				try {
					// 删帖
					AdAjaxOpt opt = appContext.delFCStatus(operauserno,id);
					if (opt != null
							&& opt.getStatus().equals(AdAjaxOpt.OK_STATUS)) {
						msg.what = UIHelper.MSG_SUCCESS;
						msg.arg1 = UIHelper.MSG_DELFC;
						msg.obj = opt;
						bundle.putInt("position", position);
						msg.setData(bundle);
					}
				} catch (Exception e) {
					msg.what = UIHelper.MSG_ERROR;
					msg.arg1 = UIHelper.MSG_DELFC;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 朋友圈点赞
	 * @param userno 工号
	 * @param username 姓名
	 * @param id     id  
	 * @param position
	 */
	private void addFriendZan(final String userno, final String username,final String id,final int position) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				try {
					// 点赞
					AdAjaxOpt opt = appContext.addFriendZan(userno, id);
					if (opt != null
							&& opt.getStatus().equals(AdAjaxOpt.OK_STATUS)) {
						msg.what = UIHelper.MSG_SUCCESS;
						msg.arg1 = UIHelper.MSG_ZAN;
						msg.obj = opt;
						bundle.putString("userno", userno);
						bundle.putString("username", username);
						bundle.putString("id", id);
						bundle.putInt("position", position);
						msg.setData(bundle);
					}
				} catch (Exception e) {
					msg.what = UIHelper.MSG_ERROR;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}
	
	
	/**
	 * @param userno 工号
	 * @param username 姓名
	 * @param id     id
	 * @param comment 评论内容
	 * @param position
	 */
	private void addFriendComment(final String userno, final String username,final String id,
			final String comment,final int position) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				try {
					// 发评论
					AdAjaxOpt opt = appContext.addFriendComment(userno, id,comment);
					if (opt != null) {
						msg.what = UIHelper.MSG_SUCCESS;
						msg.arg1 = UIHelper.MSG_COMMENT;
						msg.obj = opt;
						bundle.putString("userno", userno);
						bundle.putString("username", username);
						bundle.putString("id", id);
						bundle.putString("comment", comment);
						bundle.putInt("position", position);
						msg.setData(bundle);
					}
				} catch (Exception e) {
					msg.what = UIHelper.MSG_ERROR;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}
	/**
	 * 点赞动画
	 * @param v
	 */
	private void scaleAnimation(View v) {
		Animation scaleAnimation;
		scaleAnimation = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		
		scaleAnimation.setDuration(1000);
		scaleAnimation.setFillAfter(true);
		// 动画重复次数(-1 表示一直重复)
//		scaleAnimation.setRepeatCount(-1);
		scaleAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
		// 图片配置动画
		v.setAnimation(scaleAnimation);
		v.startAnimation(scaleAnimation);
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finished();
			return true;
		}
		return true;
	}
	
	public void finished()
	{
		if (ciDialog != null)
		{
			ciDialog.dismiss();
		}
		if (animDialog != null)
		{
			animDialog.dismiss();
		}
		bmpManager = null;
		simileyParser = null;
	}

}
