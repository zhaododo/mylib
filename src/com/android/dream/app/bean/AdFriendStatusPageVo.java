package com.android.dream.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.dream.app.util.StringUtils;

/**
 * @author zhaoj
 * 朋友圈分页主档
 *
 */
public class AdFriendStatusPageVo implements Serializable{
	
	private static final long serialVersionUID = 7110741528608368663L;
	private List<AdFriendStatusVo> friendStatuslis;
	private int pageSize;

	public List<AdFriendStatusVo> getFriendStatuslis() {
		return friendStatuslis;
	}

	public void setFriendStatuslis(List<AdFriendStatusVo> friendStatuslis) {
		this.friendStatuslis = friendStatuslis;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

    //解析朋友圈动态
	public static AdFriendStatusPageVo parse(String result) {
		JSONArray friendStatusArray = null; // 朋友状态数组
		JSONArray commentArray = null; // 评论数组
		JSONArray zanArray = null; // 赞数组
		JSONArray picArray = null; // 图片数组

		JSONObject jsonReportPage = null;
		JSONObject jsonfriendStatus = null;
		JSONObject jsoncomment = null;
		JSONObject jsonzan = null;

		List<AdFriendStatusVo> friendStatusList = new ArrayList<AdFriendStatusVo>();
		List<AdFriendZanVo> adZanList = null;
		List<AdFriendCommentVo> adFriendCommentList = null;
		List<String> adPicList = null;

		AdFriendStatusPageVo friendStatusPage = new AdFriendStatusPageVo();
		AdFriendStatusVo adFriendStatusVo = null;
		AdFriendZanVo adFriendZanVo = null;
		AdFriendCommentVo adFriendCommentVo = null;
		
		try {
			jsonReportPage = new JSONObject(result);

			if (!jsonReportPage.isNull("pageSize")) {
				friendStatusPage.setPageSize(jsonReportPage.getInt("pageSize"));
			}

			if (!jsonReportPage.isNull("friendStatuslis")) {
				friendStatusArray = jsonReportPage.getJSONArray("friendStatuslis");
			}

			if (friendStatusArray != null && friendStatusArray.length() > 0) {
				for (int i = 0; i < friendStatusArray.length(); ++i) {
					jsonfriendStatus = friendStatusArray.getJSONObject(i);
					adFriendStatusVo = new AdFriendStatusVo();
					adFriendStatusVo.setMainStatuId(jsonfriendStatus.getString("mainStatuId"));
					adFriendStatusVo.setUserno(jsonfriendStatus.getString("userno"));
					adFriendStatusVo.setCreateDate(jsonfriendStatus.getString("createDate"));
					adFriendStatusVo.setDisplayTime(jsonfriendStatus.getString("displayTime"));
					
					if (!jsonfriendStatus.isNull("username")) {
						adFriendStatusVo.setUsername(jsonfriendStatus.getString("username"));
					}
					
					if (!jsonfriendStatus.isNull("companyname")) {
						adFriendStatusVo.setCompanyname(jsonfriendStatus.getString("companyname"));
					}
					
					if (!jsonfriendStatus.isNull("depname")) {
						adFriendStatusVo.setDepname(jsonfriendStatus.getString("depname"));
					}
					
					if (!jsonfriendStatus.isNull("strTitle")) {
						adFriendStatusVo.setStrTitle(jsonfriendStatus.getString("strTitle"));
					}
					
					if (!jsonfriendStatus.isNull("strContent")) {
						adFriendStatusVo.setStrContent(jsonfriendStatus.getString("strContent"));
					}
					
					if (!jsonfriendStatus.isNull("type")) {
						adFriendStatusVo.setType(jsonfriendStatus.getInt("type"));
					}
					if (!jsonfriendStatus.isNull("intIsActive")) {
						adFriendStatusVo.setIntIsActive(jsonfriendStatus.getInt("intIsActive"));
					}else{
						adFriendStatusVo.setIntIsActive(0);
					}
					if (!jsonfriendStatus.isNull("intCardActive")) {
						adFriendStatusVo.setIntCardActive(jsonfriendStatus.getInt("intCardActive"));
					}else{
						adFriendStatusVo.setIntCardActive(0);
					}
					
					adFriendStatusVo.setIntHasZan(jsonfriendStatus.getInt("intHasZan"));
					adFriendStatusVo.setIntHasComment(jsonfriendStatus.getInt("intHasComment"));
					adFriendStatusVo.setIntHaspic(jsonfriendStatus.getInt("intHaspic"));
					
					// 分享
					if (!jsonfriendStatus.isNull("strImageUrl")) {
						adFriendStatusVo.setStrImageUrl(jsonfriendStatus.getString("strImageUrl"));
					}
					if (!jsonfriendStatus.isNull("strShareContent")) {
						adFriendStatusVo.setStrShareContent(jsonfriendStatus.getString("strShareContent"));
					}
					if (!jsonfriendStatus.isNull("strShareUrl")) {
						adFriendStatusVo.setStrShareUrl(jsonfriendStatus.getString("strShareUrl"));
					}
					
					// 解析评论列表
					if (!jsonfriendStatus.isNull("commentlis")) {
						commentArray = jsonfriendStatus.getJSONArray("commentlis");
						adFriendCommentList = new ArrayList<AdFriendCommentVo>();

						// 获得行记录对象
						for (int j = 0; j < commentArray.length(); ++j) {
							jsoncomment = commentArray.getJSONObject(j);
							if (!jsoncomment.isNull("comment")) {
								adFriendCommentVo = new AdFriendCommentVo();
								
								if (!jsoncomment.isNull("userno"))
								{
									adFriendCommentVo.setUserno(jsoncomment.getString("userno"));
								}
								
								if (!jsoncomment.isNull("name"))
								{
									adFriendCommentVo.setName(jsoncomment.getString("name"));
								}
								
								if (!jsoncomment.isNull("comment"))
								{
									adFriendCommentVo.setComment(jsoncomment.getString("comment"));
								}

								adFriendCommentList.add(adFriendCommentVo);
							}
						}
						adFriendStatusVo.setCommentlis(adFriendCommentList);
					}
					// 解析赞列表
					if (!jsonfriendStatus.isNull("zanlis")) {
						
						zanArray = jsonfriendStatus.getJSONArray("zanlis");
						adZanList = new ArrayList<AdFriendZanVo>();
						// 获得行记录对象
						for (int j = 0; j < zanArray.length(); ++j) {
							jsonzan = zanArray.getJSONObject(j);
							if (jsonzan != null)
							{
								adFriendZanVo = new AdFriendZanVo();
								if (!jsonzan.isNull("userno")) {
									adFriendZanVo.setUserno(jsonzan.getString("userno"));
								}
								
								if (!jsonzan.isNull("username")) {
									adFriendZanVo.setUsername(jsonzan.getString("username"));
								}
								adZanList.add(adFriendZanVo);
							}
						}
						adFriendStatusVo.setZanlis(adZanList);
					}
					// 解析图片列表
					if (!jsonfriendStatus.isNull("piclis")) {
						picArray = jsonfriendStatus.getJSONArray("piclis");
						
						adPicList = new ArrayList<String>();

						// 获得行记录对象
						for (int j = 0; j < picArray.length(); ++j) {
							
							adPicList.add(picArray.getString(j));
						}
						adFriendStatusVo.setPiclis(adPicList);

					}
					friendStatusList.add(adFriendStatusVo);
				}
				friendStatusPage.setFriendStatuslis(friendStatusList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return friendStatusPage;
	}
}
