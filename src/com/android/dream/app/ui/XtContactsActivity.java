package com.android.dream.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.adapter.XtDeptContactsAdapter;
import com.android.dream.app.adapter.XtPersonalContactsPaginationAdapter;
import com.android.dream.app.bean.AdXTDepartment;
import com.android.dream.app.bean.AdXTUser;
import com.android.dream.app.bean.AdXTUserPageVo;
import com.android.dream.app.util.StringUtils;
import com.android.dream.app.widget.ContactsSerachDialog;
import com.android.dream.app.widget.ContactsSerachDialog.TextChangedSearchListener;
import com.android.dream.app.widget.LoadingDialog;
import com.android.dream.app.widget.PullToRefreshListView;
import com.android.dream.app.widget.PullToRefreshListView.OnRefreshListener;
import com.android.dream.app.widget.TabPageIndicator;

/**
 * @author zhaoj
 * 
 *  协同办公单位号码和个人号码
 *
 */
public class XtContactsActivity extends BaseActivity{

	/**
	 * appContext对象，用于获取用户信息
	 */
	private AppContext appContext;
	
	/**
	 *  主界面的Tab折页
	 */
	private ViewPager pager;
	
	/**
	 * 主界面的Tab折页指示器
	 */
	private TabPageIndicator indicator;
	
	/**
	 *  通讯录退出按钮
	 */
	private ImageView contactHeaderBackBtn;
	
	
	/*******************************************************************************
	 * 
	 * 单位号码相关
	 * 
	 *******************************************************************************/
	
	/**
	 * 单位号码可折叠组件
	 */
	private ExpandableListView expandListViewDept;
	
	/**
	 * 单位号码可折叠组件
	 */
	private  LinearLayout  ll_top; 
	
	private LoadingDialog loading;
	
	
	/**
	 *  按部门编号查询个人号码适配器
	 */
	private XtDeptContactsAdapter xtDeptContactsAdapter;
	private List<AdXTDepartment> erpdep;
	private AdXTUserPageVo xtdepuser;
	private Handler handler;
	
	private int   the_group_expand_position  = -1;  
	private int   indicatorGroupHeight;  
	private boolean isExpanding  = false;
	private TextView tv_deplis_depuser;
	private String TAG = "contact";
	
	
	/**
	 *  第一次加载按部门编号查询对应的消息
	 */
	private final static int SEARCH_DEPT_INIT = 0;
	
	/**
	 *  点击部门获得部门下的所有号码对应的消息
	 */
	private final static int SEARCH_DEPT_USER = 1;
	
	//默认展开列表
	private final static int DEFAULT_EXPAND_ITEM = -1;
	
	
	/*******************************************************************************
	 * 
	 * 个人号码相关
	 * 
	 *******************************************************************************/
	
	
	/**
	 *  按姓名、拼音查询对话框
	 */
	private ContactsSerachDialog csDialog;
	
	private PullToRefreshListView contactsSearchResultList;
	private ImageView searchBtn;
	private String keyword="INIT";
	private View pull_footer_contacts_search_result;
	private TextView pull_foot_more_contacts_search_result;
	private ProgressBar pull_foot_progress_contacts_search_result;
	private List<AdXTUser> lvContactsSearchResultData = new ArrayList<AdXTUser>();
	private XtPersonalContactsPaginationAdapter pull_adapter_contacts_search_result;
	private int pull_search_result_sum = 0;
	private Handler pull_handler_contacts_search_result = null;
	
	private String mUserno;
	
	/**
	 * 每页的大小，必须与后台的PAGE_SIZE一致
	 */
	private final static int PAGE_SIZE = 10;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ad_erp_contacts_main);
		appContext = (AppContext) getApplication();
		handler = this.getDeptHandler();
		mUserno = appContext.getCurrentUser();
		initView();
		firstloadView();
	}
	
	
	/**
	 * 初始化个人通讯录，keyword= INIT
	 */
	private void firstloadView()
	{
		if (lvContactsSearchResultData.isEmpty()){
			  loadLvContactsSearchResultUserData(1,UIHelper.LISTVIEW_ACTION_INIT, keyword);
		}
	}

	private void initView() {
		LayoutInflater lf = getLayoutInflater();
		List<View> views = new ArrayList<View>();

		View view1 = lf.inflate(R.layout.ad_erp_contacts_listview1, null);
		View view2 = lf.inflate(R.layout.ad_contacts_searchresult_listview, null);

		expandListViewDept = (ExpandableListView) view1
				.findViewById(R.id.pull_content_department_listview);
		ll_top = (LinearLayout) view1.findViewById(R.id.ll_deplist_top);  
		ll_top.setVisibility(View.GONE);  
		tv_deplis_depuser = (TextView) view1.findViewById(R.id.tv_deplist_depuser);

		//通讯录退出按钮
		contactHeaderBackBtn = (ImageView) findViewById(R.id.contact_header_back_btn);
		contactHeaderBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		loadLvErpUserDepData(mUserno,"",SEARCH_DEPT_INIT,DEFAULT_EXPAND_ITEM);
		
		views.add(view1);
		views.add(view2);
		List<String> titles = new ArrayList<String>();
		titles.add("本单位");
		titles.add("个人");
		pager = (ViewPager) findViewById(R.id.contacts_pager);
		pager.setAdapter(new MypagerAdapter(views, titles));

		indicator = (TabPageIndicator) findViewById(R.id.contacts_indicator);
		indicator.setViewPager(pager, 0);
		
		expandListViewDept.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				the_group_expand_position = groupPosition;
				ll_top.setVisibility(View.VISIBLE);
				// lineView.setVisibility(View.VISIBLE);
				isExpanding = true;
				if (the_group_expand_position != -1) {
					tv_deplis_depuser.setText(erpdep.get(the_group_expand_position).getDepname());  
				}
			}

		});
		expandListViewDept.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				if (isExpanding) {
					ll_top.setVisibility(View.GONE);
					// lineView.setVisibility(View.GONE);
				}
				the_group_expand_position = -1;
				isExpanding = false;
			}

		});


		ll_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// linear.setVisibility(View.GONE);
				if (isExpanding) {
					ll_top.setVisibility(View.GONE);
					// lineView.setVisibility(View.GONE);
					expandListViewDept.collapseGroup(the_group_expand_position);
					isExpanding = false;
				}
			}

		});

		//点击
		expandListViewDept.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				if (the_group_expand_position == -1) {
					expandListViewDept.expandGroup(groupPosition);
					expandListViewDept.setSelectedGroup(groupPosition);
					the_group_expand_position = groupPosition;
					ll_top.setVisibility(View.VISIBLE);
					// lineView.setVisibility(View.VISIBLE);
					//开辟线程根据部门查询用户
					if(erpdep.get(groupPosition).getDepuser()==null || erpdep.get(groupPosition).getDepuser().size() ==0)
					{
						loadLvErpUserDepData(mUserno,erpdep.get(groupPosition).getDepno(),SEARCH_DEPT_USER,groupPosition);
					}
					isExpanding = true;
				}
				else if (the_group_expand_position == groupPosition) {
			
					ll_top.setVisibility(View.GONE);
					
					expandListViewDept.collapseGroup(groupPosition);
					the_group_expand_position = -1;
					isExpanding = false;
				}
				else {
					expandListViewDept.collapseGroup(the_group_expand_position);
					expandListViewDept.expandGroup(groupPosition);
					//开辟线程根据部门查询用户
					if(erpdep.get(groupPosition).getDepuser()==null || erpdep.get(groupPosition).getDepuser().size() ==0)
					{
						loadLvErpUserDepData(mUserno,erpdep.get(groupPosition).getDepno(),SEARCH_DEPT_USER,groupPosition);
					}
					expandListViewDept.setSelectedGroup(groupPosition);
					the_group_expand_position = groupPosition;
				}
				return true;
			}
		});

		
		expandListViewDept.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int npos = view.pointToPosition(0, 0); 
				if (npos != AdapterView.INVALID_POSITION) {  
                    long pos = expandListViewDept.getExpandableListPosition(npos);  
                    int childPos = ExpandableListView.getPackedPositionChild(pos);  
                    int groupPos = ExpandableListView.getPackedPositionGroup(pos); 
                   // Log.i("expandlist", "pos"+String.valueOf(pos)+"childPos"+String.valueOf(childPos)+"groupPos"+String.valueOf(groupPos));
                   
                    if (childPos == AdapterView.INVALID_POSITION) {  
                        View groupView = expandListViewDept.getChildAt(npos  
                                - expandListViewDept.getFirstVisiblePosition());  
                        indicatorGroupHeight = groupView.getHeight(); 
                        Log.i("expandlist", "indicatorGroupHeight"+String.valueOf(indicatorGroupHeight));
                    }  
                    if (indicatorGroupHeight == 0) {  
                        return;  
                    }  
                    if (isExpanding) {  
                        if (the_group_expand_position != -1) {  
                        	tv_deplis_depuser.setText(erpdep.get(the_group_expand_position).getDepname());  
                        	Log.i("erpdep", "erpdep"+erpdep.get(the_group_expand_position).getDepname());
                        }  
                        if (the_group_expand_position != groupPos) {  
                           ll_top.setVisibility(View.GONE);  
                        }  
                        else {  
                            ll_top.setVisibility(View.VISIBLE);  
                            Log.i("erpdep", "可以看见了");
                        }  
                    }  
                }
                if (the_group_expand_position == -1) {  
                    return;  
                }
                int showHeight = t();  
                
                MarginLayoutParams layoutParams = (MarginLayoutParams) ll_top.getLayoutParams();
                //layoutParams.topMargin = -(indicatorGroupHeight - showHeight);
                layoutParams.setMargins(0, -(indicatorGroupHeight - showHeight), 0, 0);
                ll_top.setLayoutParams(layoutParams);
                Log.i("showHeight", "showHeight"+String.valueOf(-(indicatorGroupHeight - showHeight)));
			}
		});
		
		pull_footer_contacts_search_result = getLayoutInflater().inflate(
				R.layout.listview_footer, null);

		pull_foot_more_contacts_search_result = (TextView) pull_footer_contacts_search_result
				.findViewById(R.id.listview_foot_more);
		pull_foot_progress_contacts_search_result = (ProgressBar) pull_footer_contacts_search_result
				.findViewById(R.id.listview_foot_progress);
		pull_foot_more_contacts_search_result.setText(R.string.load_empty);
		pull_foot_progress_contacts_search_result.setVisibility(View.GONE);
		
		
		//查询条件改变触发的事件
		TextChangedSearchListener tcListener = new TextChangedSearchListener() {
			@Override
			public void searchAction(EditText et) {
				doQueryByKeyWord(et);
			}
		};
		
		//初始化查询对话框
		csDialog= new ContactsSerachDialog(XtContactsActivity.this, R.style.ContactsSearchDialog, tcListener);
		csDialog.setContentView(R.layout.ad_erp_contacts_search_dialog);
		Window dialogWindow = csDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.gravity = Gravity.TOP;
		dialogWindow.setAttributes(lp);
		csDialog.setCanceledOnTouchOutside(true);
		csDialog.hide();
		
		
		//主画面查询按钮
		searchBtn = (ImageView) findViewById(R.id.contacts_search_btn);
		searchBtn.setOnClickListener(new ContactSearchDialogClicklistener());
		
		
		//关键字查询PullToRefreshListView
		contactsSearchResultList = (PullToRefreshListView) view2
				.findViewById(R.id.pull_content_contacts_searchresult_listview);
		contactsSearchResultList
				.addFooterView(pull_footer_contacts_search_result);

		contactsSearchResultList
				.setOnScrollListener(new AbsListView.OnScrollListener() {

					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						contactsSearchResultList.onScrollStateChanged(view,
								scrollState);

						// 数据为空--不用继续下面代码了
						if (lvContactsSearchResultData.isEmpty()){
							pull_foot_progress_contacts_search_result.setVisibility(View.GONE);
							return;
						}

						// 判断是否滚动到底部
						boolean scrollEnd = false;
						try {
							if (view.getPositionForView(pull_footer_contacts_search_result) == view
									.getLastVisiblePosition())
								scrollEnd = true;
						} catch (Exception e) {
							scrollEnd = false;
						}

						int lvDataState = StringUtils
								.toInt(contactsSearchResultList.getTag());
						if (scrollEnd
								&& lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
							contactsSearchResultList
									.setTag(UIHelper.LISTVIEW_DATA_LOADING);
							pull_foot_more_contacts_search_result
									.setText(R.string.load_ing);
							pull_foot_progress_contacts_search_result
									.setVisibility(View.VISIBLE);
							// 当前pageIndex
							int pageIndex = (int) (pull_search_result_sum / PAGE_SIZE) + 1;
							loadLvContactsSearchResultUserData(pageIndex,
									UIHelper.LISTVIEW_ACTION_SCROLL, keyword);
						}
					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						contactsSearchResultList.onScroll(view,
								firstVisibleItem, visibleItemCount,
								totalItemCount);

					}
				});

		contactsSearchResultList.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadLvContactsSearchResultUserData(1,
						UIHelper.LISTVIEW_ACTION_REFRESH, keyword);
			}
		});
		

		pull_adapter_contacts_search_result = new XtPersonalContactsPaginationAdapter(
				this, lvContactsSearchResultData,
				R.layout.ad_contacts_searchresult_item);
		contactsSearchResultList
				.setAdapter(pull_adapter_contacts_search_result);
		// 定义Handler,分页为10页
		pull_handler_contacts_search_result = this
				.getContactsSearchResultLvHandler(contactsSearchResultList,
						pull_adapter_contacts_search_result,
						pull_foot_more_contacts_search_result,
						pull_foot_progress_contacts_search_result, PAGE_SIZE);
	}
	
	private int t() { 
		
       int showHeight = indicatorGroupHeight;  
        int nEndPos = expandListViewDept.pointToPosition(0, indicatorGroupHeight);  
        if (nEndPos != AdapterView.INVALID_POSITION) {  
             long pos = expandListViewDept.getExpandableListPosition(nEndPos);  
            int groupPos = ExpandableListView.getPackedPositionGroup(pos);  
             if (groupPos != the_group_expand_position) {  
                 View viewNext = expandListViewDept.getChildAt(nEndPos - expandListViewDept.getFirstVisiblePosition());  
                 showHeight = viewNext.getTop();  
             }  
         }  
         return showHeight;  
    } 

   
	public static class MypagerAdapter extends PagerAdapter {

		private List<View> view_lists;
		private List<String> title_lists;

		public MypagerAdapter(List<View> views, List<String> titles) {
			view_lists = views;
			title_lists = titles;
		}

		@Override
		public int getCount() {

			return view_lists.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {

			return view == obj;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView(view_lists.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return title_lists.get(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(view_lists.get(position));
			return view_lists.get(position);
		}

	}
	/**
	 * 创建线程加载数据
	 * @param pageIndex
	 * @param action
	 * @param userno
	 */
	private void loadLvErpUserDepData(final String userno,final String depno,final int flg, final int groupid) {
		//pull_foot_progress.setVisibility(ProgressBar.VISIBLE);
//		正在等待dialog
		loading = new LoadingDialog(this);		
		loading.show();
		new Thread() {
			public void run() {

				Message msg = new Message();
				try {
					if(flg == SEARCH_DEPT_INIT){
						//根据用户查询部门及本部门用户
						erpdep = appContext.adGetXTUserbyUserNo(mUserno);
						if (erpdep != null)
						{
							msg.what = erpdep.size();
							msg.obj = erpdep;
							msg.arg1 = SEARCH_DEPT_INIT;
							msg.arg2 = groupid;
						}
					}
					else if (flg == SEARCH_DEPT_USER){
//						根据部门查询部门用户
						xtdepuser = appContext.adGetXTUserbyDepNo(mUserno,depno,1);
						if (xtdepuser != null)
						{
							msg.what = xtdepuser.getPageSize();
							msg.obj = depno;
							msg.arg1 = SEARCH_DEPT_USER;
							msg.arg2 = groupid;
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

	/**
	 * 创建按部门查询号码对应的Handler
	 * @return
	 */
	private Handler getDeptHandler() {
		
		Log.i(TAG, "getDeptHandler");
		
		return new Handler() {
	
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					if(msg.arg1 == SEARCH_DEPT_INIT){
//						加载部门和本部门用户
						xtDeptContactsAdapter = new XtDeptContactsAdapter(XtContactsActivity.this, erpdep);
						expandListViewDept.setAdapter(xtDeptContactsAdapter);
					}else if(msg.arg1 == SEARCH_DEPT_USER){
//						根据部门编号加载部门用户
						for(int i = 0;i<erpdep.size();i++)
						{
							if(erpdep.get(i).getDepno() == msg.obj.toString() ){
								erpdep.get(i).setDepuser(xtdepuser.getErpdeplis());
							}
						}
						xtDeptContactsAdapter.notifyDataSetChanged();
					}
					if(msg.arg2>=0){
						expandListViewDept.setSelectedGroup(msg.arg2);
					}
					
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					Toast.makeText(XtContactsActivity.this, "检查网络", Toast.LENGTH_SHORT).show();
				}
				if(loading != null){
					loading.dismiss();
				}
			}
		};
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (xtDeptContactsAdapter != null)
		{
			xtDeptContactsAdapter.notifyDataSetChanged();
		}
	}
	private void loadLvContactsSearchResultUserData(final int pageIndex,
			final int action, final String keyword) {
		pull_foot_progress_contacts_search_result.setVisibility(ProgressBar.VISIBLE);
		
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {

					AdXTUserPageVo erpuserPage = appContext.adGetXTUserbykey(mUserno,
							keyword, pageIndex);

					if (erpuserPage != null
							&& erpuserPage.getErpdeplis() != null) {
						msg.what = erpuserPage.getPageSize();
						msg.obj = erpuserPage.getErpdeplis();
						Log.i(TAG,
								"loadLvContactsSearchResultUserData#list size:"
										+ erpuserPage.getErpdeplis().size());
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_CONTACTS_SEARCH_RESULT;
				pull_handler_contacts_search_result.sendMessage(msg);
			}
		}.start();

	}

	/**
	 * 创建Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @param more
	 * @param progress
	 * @param pageSize
	 * @return
	 */
	private Handler getContactsSearchResultLvHandler(
			final PullToRefreshListView lv, final BaseAdapter adapter,
			final TextView more, final ProgressBar progress, final int pageSize) {

		Log.i(TAG, "getSearchResultLvHandler");

		return new Handler() {

			public void handleMessage(Message msg) {
				handleSearchResultLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
				if (msg.what >= 0) {
					if (msg.what < pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					lv.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
	}

	/**
	 * @param what
	 *            大小
	 * @param obj
	 *            list对象
	 * @param objtype
	 *            LISTVIEW_ERP_REPORT
	 * @param actiontype
	 *            类别对象
	 */
	@SuppressWarnings("unchecked")
	private void handleSearchResultLvData(int what, Object obj, int objtype,
			int actiontype) {
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_CONTACTS_SEARCH_RESULT:

				List<AdXTUser> nlist = (List<AdXTUser>) obj;
				pull_search_result_sum = what;

				if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvContactsSearchResultData.size() > 0) {
						if (nlist != null) {
							for (AdXTUser remoteXTUser : nlist) {
								boolean b = false;

								for (AdXTUser localXTUser : lvContactsSearchResultData) {
									if (remoteXTUser.getUserno()==localXTUser.getUserno() ||remoteXTUser.getUserno().equals(
											localXTUser.getUserno())) {
										b = true;
										break;
									}
								}
								if (!b)
									newdata++;

							}

						}
					} else {
						newdata = what;
					}
				}
				if (nlist != null) {
					lvContactsSearchResultData.clear();// 先清除原有数据
					lvContactsSearchResultData.addAll(nlist);
				}
				break;
			}
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {

			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			switch (objtype) {
			case UIHelper.LISTVIEW_CONTACTS_SEARCH_RESULT:

				List<AdXTUser> nlist = (List<AdXTUser>) obj;
				pull_search_result_sum += what;

				if (lvContactsSearchResultData.size() > 0) {
					if (nlist != null) {
						for (AdXTUser remoteXTUser : nlist) {
							boolean b = false;
							for (AdXTUser localXTUser : lvContactsSearchResultData) {
								if (remoteXTUser.getUserno().equals(
										localXTUser.getUserno())) {
									b = true;
									break;
								}
							}
							if (!b)
								lvContactsSearchResultData.add(remoteXTUser);
						}

					}
				} else {
					if (nlist != null) {
						lvContactsSearchResultData.addAll(nlist);
					}
				}
			}
			break;
		}
	}

	
	/**
	 * 按条件查询
	 * @param dialogSearch
	 */
	private void doQueryByKeyWord(EditText dialogSearch) {
		// 取得EditText的輸入內容
		keyword = dialogSearch.getText().toString();
		
		if (keyword == null  || keyword.length() == 0) {
			keyword = "INIT";
			Log.i("C", "关键字------------------------>"+keyword);
		}
		
		if (keyword != null  && keyword.length() > 0) {
		  loadLvContactsSearchResultUserData(1,UIHelper.LISTVIEW_ACTION_INIT, keyword);
		}
	}
	
	/**
	 * @author zhaoj
	 * 点击放大镜（查询按钮）时，弹出对话框。
	 */
	class ContactSearchDialogClicklistener implements OnClickListener {

		@Override
		public void onClick(View v) {
			csDialog.show();
			indicator.setViewPager(pager, 1);
		
		}
	}
}
