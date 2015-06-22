package com.android.dream.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.dream.app.util.StringUtils;


/**
 * @author zhaoj
 * 包装AdErpBoardVo，分页
 *
 */
public class AdErpBoardPage {

	public AdErpBoardPage()
	{
		pageSize = 0;
		boards = new ArrayList<AdErpBoard>();
	}
	
	/**
	 *  本次加载的记录数（实际记录数）
	 */
	private int pageSize;
	
	/**
	 *  公告栏主档数据
	 */
	private List<AdErpBoard> boards = null;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public List<AdErpBoard> getBoards() {
		return boards;
	}

	public void setBoards(List<AdErpBoard> boards) {
		this.boards = boards;
	}
	
	
	/**
	 * 解析数据
	 * @param result
	 * @return
	 */
	public static AdErpBoardPage parse(String result)
	{
		JSONObject jsonBoardPage = null;
		JSONArray jsonBoardArray = null;
		JSONObject jsonBoard = null;
		JSONObject jsonBoardItem = null;
		JSONObject jsonBoardFile = null;
		JSONArray jsonBoardFiles = null;
		
		//ERP公告主档
		List<AdErpBoard> boardList = new ArrayList<AdErpBoard>();
		List<AdErpBoardFile> boardFiles = null;
		AdErpBoardPage adBoardPage = new AdErpBoardPage();
		AdErpBoard adBoard = null;
		AdErpBoardItem adBoardItem = null;
		AdErpBoardFile adBoardFile = null;
		try {
			jsonBoardPage = new JSONObject(result);

			if (!jsonBoardPage.isNull("pageSize")) {
				adBoardPage.setPageSize(jsonBoardPage.getInt("pageSize"));
			}

			if (!jsonBoardPage.isNull("boards")) {
				jsonBoardArray = jsonBoardPage.getJSONArray("boards");

				for (int i = 0; i < jsonBoardArray.length(); ++i) {
					jsonBoard = jsonBoardArray.getJSONObject(i);
					adBoard = new AdErpBoard();

					adBoard.setMsgid(jsonBoard.getString("msgid"));
					adBoard.setBoardid(jsonBoard.getString("boardid"));
					adBoard.setBoardName(jsonBoard.getString("boardName"));
					adBoard.setCreateDate(jsonBoard.getString("createDate"));
					adBoard.setTitle(jsonBoard.getString("title"));
					adBoard.setPublichost(jsonBoard.getString("publichost"));
					adBoard.setPublisher(jsonBoard.getString("publisher"));
					adBoard.setPublisherName(jsonBoard.getString("publisherName"));
					adBoard.setFileTag(jsonBoard.getString("fileTag"));
					adBoard.setExpireDate(jsonBoard.getString("expireDate"));
					adBoard.setImportance(jsonBoard.getInt("importance"));

					// 公告明细
					if (!jsonBoard.isNull("boardItem")) {
						jsonBoardItem = jsonBoard.getJSONObject("boardItem");
						adBoardItem = new AdErpBoardItem();
						adBoardItem.setContent(StringUtils.contentFormat(jsonBoardItem.getString("content")));
						adBoardItem.setMsgid(jsonBoardItem.getString("msgid"));
						adBoard.setBoardItem(adBoardItem);
					}

					// 文件列表
					if (!jsonBoard.isNull("boardFiles")) {
						
						jsonBoardFiles = jsonBoard.getJSONArray("boardFiles");
						boardFiles = new ArrayList<AdErpBoardFile>();
						for (int j = 0; j < jsonBoardFiles.length(); ++j) {
							jsonBoardFile = jsonBoardFiles.getJSONObject(j);
							adBoardFile = new AdErpBoardFile();
							adBoardFile.setFileHandle(jsonBoardFile.getString("fileHandle"));
							adBoardFile.setFileName(jsonBoardFile.getString("fileName"));
							adBoardFile.setInfo(jsonBoardFile.getString("info"));
							boardFiles.add(adBoardFile);
						}
						adBoard.setBoardFiles(boardFiles);
					}
					boardList.add(adBoard);
				}
				adBoardPage.setBoards(boardList);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return adBoardPage;
	}

}
