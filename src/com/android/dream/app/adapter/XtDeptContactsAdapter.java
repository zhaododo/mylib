package com.android.dream.app.adapter;

import java.util.List;

import com.android.dream.app.R;
import com.android.dream.app.bean.AdXTDepartment;
import com.android.dream.app.bean.AdXTUser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author zhaoj
 * 
 * 按部门编号查询个人号码适配器
 *  
 */
public class XtDeptContactsAdapter extends BaseExpandableListAdapter {

	private Context mContext;
    private LayoutInflater mInflater = null;
    private List<AdXTDepartment>   mData = null;
    private View pull_footer_contacts_search_result;

    public XtDeptContactsAdapter(Context ctx, List<AdXTDepartment> list) {
        mContext = ctx;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = list;
    }
    
	@Override
	public AdXTUser getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if(mData.get(groupPosition).getDepuser()!=null){
			return mData.get(groupPosition).getDepuser().get(childPosition);
		}
		else{
			return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ChildViewHolder holder;
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.child_item_layout, null);
            holder = new ChildViewHolder();
            holder.mphone = (ImageView) convertView.findViewById(R.id.ad_erp_concacts_downarrow);
            holder.mUserName = (TextView) convertView.findViewById(R.id.ad_erp_concacts_detail);
            holder.lina = (LinearLayout) convertView.findViewById(R.id.lin_contact_child);
            convertView.setTag(holder);
        }else{
        	holder = (ChildViewHolder) convertView.getTag();
        }
//        holder.mIcon.setBackgroundResource(getChild(groupPosition,
//                childPosition).getImageId());
		if(getChild(groupPosition, childPosition).getMobilephone() !=""){
			 holder.mUserName.setText(getChild(groupPosition, childPosition).getUsername()+"("+
		        		getChild(groupPosition, childPosition).getMobilephone()+")");
		}else{
			holder.mUserName.setText(getChild(groupPosition, childPosition).getUsername());
		}
       
        holder.mphone.setTag(getChild(groupPosition, childPosition).getMobilephone());
        holder.lina.removeView(pull_footer_contacts_search_result);
        holder.mphone.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String mobilePhone = getChild(groupPosition, childPosition).getMobilephone();
				
				if (mobilePhone != null && mobilePhone.length() >0 && !mobilePhone.equals("未公开"))
				{
					Intent intent = new Intent(
							"android.intent.action.CALL", Uri.parse("tel:"+ mobilePhone));
					mContext.startActivity(intent);
				}
				
				
			}
		});
        //当在最后时添加加载更多功能按钮,没有加载完时显示加载更多
//        if(childPosition == mData.get(groupPosition).getDepuser().size()-1 && mData.get(groupPosition).getIsfull() =="N"){
//        	pull_footer_contacts_search_result = mInflater.inflate(
//    				R.layout.listview_footer, null);
//
//    		pull_foot_more_contacts_search_result = (TextView) pull_footer_contacts_search_result
//    				.findViewById(R.id.listview_foot_more);
//    		pull_foot_progress_contacts_search_result = (ProgressBar) pull_footer_contacts_search_result
//    				.findViewById(R.id.listview_foot_progress);
//    		holder.lina.addView(pull_footer_contacts_search_result);
//        }
        
        return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if(mData.get(groupPosition).getDepuser()!=null){
			return  mData.get(groupPosition).getDepuser().size();
		}
		return  0;
	}

	@Override
	public AdXTDepartment getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		if(mData == null){
			return null;
		}
		return mData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if(mData == null){
			return 0;
		}
		return mData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupViewHolder holder;
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_item_layout, null);
            holder = new GroupViewHolder();
            holder.mGroupName = (TextView) convertView.findViewById(R.id.group_name);
            holder.mGroupCount = (TextView) convertView.findViewById(R.id.group_count);
            convertView.setTag(holder);
		}else{
			holder = (GroupViewHolder) convertView.getTag();
        }
        holder.mGroupName.setText(mData.get(groupPosition).getDepname());
        int usernum = 0;
//        if(mData.get(groupPosition).getDepuser()!=null){
//        	usernum = mData.get(groupPosition).getDepuser().size();
//        }
        usernum = mData.get(groupPosition).getDepusernum();
        holder.mGroupCount.setText("("+String.valueOf(usernum)+")");
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	/**
	 * 部门
	 * @author Wang Cheng
	 *
	 */
	private class GroupViewHolder {
//		部门名称
        TextView mGroupName;
//      部门人数统计
        TextView mGroupCount;
    }
	/**
	 * 用户
	 * @author Wang Cheng
	 *
	 */
    private class ChildViewHolder {
    	LinearLayout lina;
//    	员工头像
        ImageView mIcon;
//      员工名称
        TextView mUserName;
//      号码
        ImageView mphone;
    }
}
