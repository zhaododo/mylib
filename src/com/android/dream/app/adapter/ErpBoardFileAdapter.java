package com.android.dream.app.adapter;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dream.app.AppContext;
import com.android.dream.app.R;
import com.android.dream.app.bean.AdErpBoard;
import com.android.dream.app.bean.AdErpBoardFile;
import com.android.dream.app.ui.UIHelper;
import com.android.dream.app.widget.LoadingDialog;
import com.android.dream.dao.DBErpBoardMainDao;
import com.android.dream.dao.DownLoadFileDao;
import com.android.dream.download.DownloadProgressListener;
import com.android.dream.download.FileDownloader;

/**
 * @author Wang Cheng
 * 
 * @date 2014-9-15下午3:24:01
 */
public class ErpBoardFileAdapter extends BaseAdapter {

	private Context context;
	private List<AdErpBoardFile> data;
	private AdErpBoard erpBoard;
	private int resource;
	private LayoutInflater listContainer;// 视图容器
	private DBErpBoardMainDao dbErpBoardMainDao;
	private DownLoadFileDao downLoadFileDao;

	private String TAG = "ErpBoardFileAdapter";
	
	private AppContext appContext;
	
	private String mUserno = "UNDEFINE";
	
	private LoadingDialog loading;
	
	// 初始化标记
    private static final int INIT = 0x01;
    
	// 正在下载实时数据传输Message标志
	private static final int PROCESSING = 0x02; 
	
	// 下载结束
	private static final int FINISHED = 0x03; 
	
	// 获得文件大小
	private static final int GET_FILE_SIZE = 0x04; 
	
	// 下载失败时的Message标志
	private static final int FAILURE = -1;
	
	//响应码为200，即访问成功
	private static final int RESPONSEOK = 200;
	
	// 消息处理
	private UIHander handler;

	// 文件开始下载
	public final static String FILE_START_DOWLOAD = "1";
	// 文件下载中
	public final static String FILE_DOWLOADING = "2";
	// 文件下载完成
	public final static String FILE_DOWLOAD_FINISHED = "3";

	// 存放activty创建的下载线程
	private ErpBoardFileAdapter.DownloadTask[] parentTasks = null;

	// 每个DownloadTask创建的子线程数
	private final static int THREAD_NUMS = 1;

	// 自定义控件
	static class ListItemView {
		public TextView filename; // 下载文件名称
		public TextView resultView; // 现在进度显示百分比文本框
		public ImageView downloadView; // 下载按钮，可以触发下载事件
		public ProgressBar progressBar; // 下载进度条，实时图形化的显示进度信息
		public ImageView imgfiletype;  //文件类型图标
		//public TextView fileSizeView;   //文件大小
	}
	

	public ErpBoardFileAdapter(Context context, AdErpBoard erpBoard,List<AdErpBoardFile> data, int resource,
			ErpBoardFileAdapter.DownloadTask[] parentTasks) {
		this.context = context;
		this.data = data;
		this.erpBoard = erpBoard;
		this.resource = resource;
		this.listContainer = LayoutInflater.from(context);
		dbErpBoardMainDao = new DBErpBoardMainDao(context);
		downLoadFileDao = new DownLoadFileDao(context);
		handler = new UIHander();
		this.parentTasks = parentTasks;
		
		appContext = (AppContext) context.getApplicationContext();
		
		if (appContext!= null)
		{
			mUserno = appContext.getCurrentUser();
		}
		fileLengthTaskStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int loc) {
		return data.get(loc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ListItemView listItemView = null;
		
		//获得View对象的数据
		final AdErpBoardFile boardFileInfo = data.get(position);

		if (convertView == null) {
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(this.resource, null);
			listItemView = new ListItemView();
			listItemView.filename = (TextView) convertView.findViewById(R.id.txt_filename);
			listItemView.resultView = (TextView) convertView.findViewById(R.id.txt_percent);
			listItemView.downloadView = (ImageView) convertView.findViewById(R.id.btn_down);
			listItemView.progressBar = (ProgressBar) convertView.findViewById(R.id.pb_downlength);
			listItemView.imgfiletype = (ImageView) convertView.findViewById(R.id.img_filetype);
			//listItemView.fileSizeView = (TextView) convertView.findViewById(R.id.txt_filesize);
			
			// 设置进度条内容,处理初始化状态
			updateprocess(erpBoard,boardFileInfo, position,listItemView.downloadView);
			//根据文件类型设定图片
			setfilepic(boardFileInfo.getFileName(),listItemView.imgfiletype);
			
			Log.i(TAG, "invoke updateprocess position:"+position);
			
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		
		if (boardFileInfo != null)
		{
			// 设定文件名
			listItemView.filename.setText(boardFileInfo.getFileName());
			listItemView.resultView.setText(boardFileInfo.getPercent());
			listItemView.progressBar.setProgress(boardFileInfo.getDownlength());
			listItemView.progressBar.setMax(boardFileInfo.getFilelength());
			
			if (boardFileInfo.getFilelength() >0)
			{
				listItemView.filename.setText(boardFileInfo.getFileName()+ "  "+boardFileInfo.getFileLengthDesc());
			}
			//设置按钮图片
			if(boardFileInfo.getDownloadStatus().equals(FILE_DOWLOAD_FINISHED)){
				listItemView.downloadView.setImageResource(R.drawable.ad_board_open);
				listItemView.progressBar.setVisibility(View.GONE);
				listItemView.resultView.setVisibility(View.GONE);
				
			}
			else
			{
				listItemView.progressBar.setVisibility(View.VISIBLE);
				listItemView.resultView.setVisibility(View.VISIBLE);
			}
			
			if (convertView != null)
			{
				convertView.setOnTouchListener(new ListViewOnTouchListener(boardFileInfo,position,listItemView.downloadView));
			}
			
			listItemView.downloadView.setOnClickListener(new DownFileOnClickListener(boardFileInfo,position,listItemView.downloadView));
		}
		return convertView;
	}
	
	
	/**
	 * 统一事件处理
	 * @author zhaoj
	 *
	 */
	class ListViewProcess
	{
		
		private AdErpBoardFile boardFileInfo;
		private int position;
		private ImageView imgbtn;
		
		public ListViewProcess(AdErpBoardFile boardFileInfo,int position,ImageView imgbtn)
		{
			this.boardFileInfo = boardFileInfo;
			this.position = position;
			this.imgbtn = imgbtn;
		}
		
		public void onProcess()
		{
			//如果状态为开始下载或者为下载中，图片改为下载中
			if (boardFileInfo.getDownloadStatus().equals(FILE_START_DOWLOAD))
			{
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					String saveDirsbsolute = Environment.getExternalStorageDirectory().getAbsolutePath();
					File saveDir = new File(saveDirsbsolute+ "/Dream/Files/TempFile");
					
					String url = "";
					
					if (appContext!= null)
					{
						url = appContext.getAdErpBoardFileUrl(mUserno, boardFileInfo.getFileHandle());
					}
					boardFileInfo.setFileLocation(url);
					download(url, saveDir, position);
				}
				//改变图片,改变状态。
				boardFileInfo.setDownloadStatus(FILE_DOWLOADING);
				imgbtn.setImageResource(R.drawable.ad_board_downing);
				setBoardFile(position, boardFileInfo);
				
			}
			//如果状态在下载中则暂停
			else if (boardFileInfo.getDownloadStatus().equals(FILE_DOWLOADING))
			{
				if (parentTasks != null && parentTasks[position] != null)
				{
					parentTasks[position].exit();
				}
				boardFileInfo.setDownloadStatus(FILE_START_DOWLOAD);
				imgbtn.setImageResource(R.drawable.ad_board_downstart);
				setBoardFile(position, boardFileInfo);
			}
			//如果已完成
			else if (boardFileInfo.getDownloadStatus().equals(FILE_DOWLOAD_FINISHED))
			{
				String saveDirsbsolute = Environment.getExternalStorageDirectory().getAbsolutePath(); // 获取SDCard根目录文件
				//找到文件的真实名称
				String filename = dbErpBoardMainDao.getfilerealname(boardFileInfo.getFileHandle());
				File realfile = new File(saveDirsbsolute + "/Dream/Files/BoardFile/" + filename);// 正式文件保存目录
				if(realfile.exists()){
					UIHelper.openFile(context, realfile);
				}else{
//					下载完被删除要重新下载
					// 更新文件下载中状态，此处可以在加载的时候检查
					dbErpBoardMainDao.updatefilestatus(boardFileInfo.getFileHandle(), FILE_START_DOWLOAD);
					boardFileInfo.setDownloadStatus(FILE_START_DOWLOAD);
					boardFileInfo.setPercent("0%");
					boardFileInfo.setDownlength(0);
					imgbtn.setImageResource(R.drawable.ad_board_downstart);
					setBoardFile(position, boardFileInfo);
					Toast.makeText(context,"文件不存在请重新下载",Toast.LENGTH_LONG).show(); // 使用Toast技术，提示用户下载完成
				}
			}
			
		}
	}
	
	class ListViewOnTouchListener  implements View.OnTouchListener
	{
		private ListViewProcess process = null;
		public ListViewOnTouchListener(AdErpBoardFile boardFileInfo,int position,ImageView imgbtn){
			process = new ListViewProcess(boardFileInfo, position,imgbtn);
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				process.onProcess();
			}
			return true;
		}
		
	}

	class DownFileOnClickListener implements View.OnClickListener{
		private ListViewProcess process = null;
		public DownFileOnClickListener(AdErpBoardFile boardFileInfo,int position,ImageView imgbtn){
			process = new ListViewProcess(boardFileInfo, position,imgbtn);
		}
		@Override
		public void onClick(View v) {
			
			process.onProcess();
		}
		
	}
	
	/**
	 * 设定文件图标
	 * @param filename
	 * @param imgfiletype
	 */
	private void setfilepic(String fileName, ImageView imgfiletype) {
		// TODO Auto-generated method stub
		if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingImage))){
			imgfiletype.setImageResource(R.drawable.ico_picture);
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingWebText))){
        	imgfiletype.setImageResource(R.drawable.ico_unknow);
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPackage))){
        	imgfiletype.setImageResource(R.drawable.ico_rar_zip);

       }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingAudio))){
    	   imgfiletype.setImageResource(R.drawable.ico_unknow);
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingVideo))){
        	imgfiletype.setImageResource(R.drawable.ico_unknow);
        	
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingText))){
        	imgfiletype.setImageResource(R.drawable.ico_unknow);
        	
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPdf))){
        	imgfiletype.setImageResource(R.drawable.ico_pdf);
        	
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingWord))){
        	imgfiletype.setImageResource(R.drawable.ico_word);
        	
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingExcel))){
        	imgfiletype.setImageResource(R.drawable.ico_excel);
        	
        }else if(checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPPT))){
        	imgfiletype.setImageResource(R.drawable.ico_ppt);
        }else
        {
        	imgfiletype.setImageResource(R.drawable.ico_unknow);
        }
	}
	//检查文件是否存在
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	/**
	 * 打印信息
	 * @param msg
	 */
	private  void print(String msg){
		Log.i(TAG, msg);	//使用LogCat的Information方式打印信息
	}
	
	
	
	/**
	 * 初始化AdErpBoardFile的信息，设定已下载文件大小，文件长度
	 * @param boardFileInfo
	 * @param status
	 */
	private void initErpBoardFile(AdErpBoardFile boardFileInfo,String status)
	{
		Map<String, Integer> downinfo = downLoadFileDao.getFileDownInfo(boardFileInfo.getFileHandle()); // 查询下载信息
		if (downinfo != null && downinfo.size() > 0) {
			
			boardFileInfo.setFilelength(downinfo.get("filelength"));
			boardFileInfo.setDownlength(downinfo.get("downlength"));
			boardFileInfo.setPercent("0%");
			boardFileInfo.setDownloadStatus(status);
			
			print("filelength:"+downinfo.get("filelength"));
			print("downlength:"+downinfo.get("downlength"));
			if (downinfo.get("filelength") > 0 && downinfo.get("downlength") > 0) {
				// 计算已经下载的百分比，此处需要转换为浮点数计算
				float num = (float) downinfo.get("downlength")/ (float) downinfo.get("filelength"); 
				// 把获取的浮点数计算结构专访为整数
				int result = (int) (num * 100);
				// 把下载的百分比显示在界面显示控件上
				boardFileInfo.setPercent(result + "%");
				print("result:"+result+ "%");
			}
		}
	
	}
	
	/**
	 * 创建适配器的时候，会按数据的大小多次调用getView方法，此函数用于更新每个文件的下载进度。
	 * 
	 * @param adErpBoard
	 *            明细页对象用于获得文件列表
	 * @param position
	 *            第x个文件（x从0开始）
	 */
	public void updateprocess(AdErpBoard adErpBoard, AdErpBoardFile boardFileInfo,int position,ImageView downimg) {
		Boolean fileisexit = false;
		String filestatus = "";
		// 查看是否存在文件信息
		fileisexit = dbErpBoardMainDao.msgfileisexit(adErpBoard.getMsgid(),boardFileInfo.getFileHandle()); 
		if (fileisexit) {
			// 存在时查看文件的状态
			filestatus = dbErpBoardMainDao.getfilestatus(boardFileInfo.getFileHandle());

			// 如果文件是下载中更新进度条
			if (filestatus.equals(FILE_START_DOWLOAD) || filestatus.equals(FILE_DOWLOADING)) {
				initErpBoardFile(boardFileInfo,FILE_START_DOWLOAD);
			}
			// 如果文件已下载 下载按钮变成打开文档
			else if (filestatus.equals(FILE_DOWLOAD_FINISHED)) {
				boardFileInfo.setFilelength(100);
				boardFileInfo.setDownlength(100);
				boardFileInfo.setDownloadStatus(FILE_DOWLOAD_FINISHED);
				boardFileInfo.setPercent("100%");
				downimg.setImageResource(R.drawable.ad_board_open);
			}
		} else {
			// 不存在时数据库中添加信息
			dbErpBoardMainDao.addmsgfile(adErpBoard.getMsgid(),boardFileInfo.getFileHandle(), boardFileInfo.getFileName());
			boardFileInfo.setDownloadStatus(FILE_START_DOWLOAD);
			boardFileInfo.setPercent("0%");
		}

	}

	private void download(String path, File saveDir, int taskId) {
		Log.i(TAG, "download taskId:"+taskId);
		DownloadTask task = new DownloadTask(path, saveDir, taskId);
		parentTasks[taskId] = task;
		new Thread(task).start(); // 开始下载
	}

	/*
	 * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
	 * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
	 */
	public final class DownloadTask implements Runnable {
		private String path; // 下载路径
		private File saveDir; // 下载到保存到的文件
		private FileDownloader loader; // 文件下载器(下载线程的容器)

		// 为DownloadTask编号，确定Handler处理哪个UI
		private int taskId = -1;
		
		//初始化
		private AdErpBoardFile boardFileInfo;
		

		/**
		 * 构造方法，实现变量初始化
		 * 
		 * @param path
		 *            下载路径
		 * @param saveDir
		 *            下载要保存到的文件
		 */
		public DownloadTask(String path, File saveDir, int taskId) {
			Log.i(TAG, "DownloadTask taskId："+taskId);
			this.path = path;
			this.saveDir = saveDir;
			this.taskId = taskId;
			this.boardFileInfo = data.get(taskId);
			// 更新文件下载中状态
			if (boardFileInfo != null)
			{
				dbErpBoardMainDao.updatefilestatus(boardFileInfo.getFileHandle(), FILE_DOWLOADING);
			}
		}

		/**
		 * 退出下载
		 */
		public void exit() {
			if (loader != null)
				loader.exit(); // 如果下载器存在的话则退出下载
		}

		DownloadProgressListener downloadProgressListener = new DownloadProgressListener() { // 开始下载，并设置下载的监听器

			/**
			 * 下载的文件长度会不断的被传入该回调方法
			 */
			public void onDownloadSize(int size) {
				Message msg = handler.obtainMessage(); // 新建立一个Message对象
				msg.what = PROCESSING; // 设置ID为1；
				msg.arg1 = taskId;
				msg.getData().putInt("size", size); // 把文件下载的size设置进Message对象
				handler.sendMessage(msg); // 通过handler发送消息到消息队列
			}

			@Override
			public void onFinished() {
				
				Message msg = handler.obtainMessage(); // 新建立一个Message对象
				msg.what = FINISHED; // 设置ID为1；
				msg.arg1 = taskId;
				handler.sendMessage(msg); // 通过handler发送消息到消息队列
			}
		};

		/**
		 * 下载线程的执行方法，会被系统自动调用
		 */
		public void run() {
			try {
				
				// 初始化下载
				loader = new FileDownloader(context, path, saveDir,boardFileInfo.getFileHandle(), THREAD_NUMS);
				
				int fileLength = loader.getFileSize();
				
				//设置文件状态
				if (fileLength > 0)
				{
					Message msg = handler.obtainMessage();
					msg.what = INIT;
					msg.arg1 = this.taskId;
					msg.getData().putInt("fileLength", fileLength);	
					handler.sendMessage(msg);	
				}
				
				loader.download(downloadProgressListener);

			} catch (Exception e) {
				
				if (appContext!= null)
				{
					appContext.saveErrorLog(e);
				}
				e.printStackTrace();
				
				Message msg =handler.obtainMessage(FAILURE);
				msg.arg1 = this.taskId;
				handler.sendMessage(msg); // 下载失败时向消息队列发送消息
			}
		}
	}
	

	/**
	 * @author zhaoj
	 * 用于包裹数据，未了防止线程修改本地数据
	 *
	 */
	class FileWrapper {
		public String fileHandle;
		public int  fileLength; 
		public int  taskId;
		public String downUrl;
	}
	
	
	/**
	 * 用于获得文件大小
	 * @author zhaoj
	 *
	 */
	public final class FileLengthTask implements Runnable {
		
		private Vector<FileWrapper> fileList;
		
		public FileLengthTask() {
			
			fileList = new Vector<FileWrapper>();
			
			FileWrapper fileWrapper =null;
			
			for (int i=0; i< data.size(); ++i)
			{
				fileWrapper = new FileWrapper();
				fileWrapper.fileHandle = data.get(i).getFileHandle();
				fileWrapper.taskId =i;
				fileWrapper.fileLength =0;
				if (appContext != null)
				{
					fileWrapper.downUrl = appContext.getAdErpBoardFileUrl(mUserno, fileWrapper.fileHandle);
				}
				fileList.add(fileWrapper);
			}
		}
		
		private  Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
			Map<String, String> header = new LinkedHashMap<String, String>();	//使用LinkedHashMap保证写入和遍历的时候的顺序相同，而且允许空值存在
			for (int i = 0;; i++) {	//此处为无限循环，因为不知道头字段的数量
				String fieldValue = http.getHeaderField(i);	//getHeaderField(int n)用于返回 第n个头字段的值。

				if (fieldValue == null) break;	//如果第i个字段没有值了，则表明头字段部分已经循环完毕，此处使用Break退出循环
				header.put(http.getHeaderFieldKey(i), fieldValue);	//getHeaderFieldKey(int n)用于返回 第n个头字段的键。
			}
			return header;
		}
		
		private  void print(String msg){
			Log.i(TAG, msg);	//使用LogCat的Information方式打印信息
		}
		
		private  void printResponseHeader(HttpURLConnection http){
			Map<String, String> header = getHttpResponseHeader(http);	//获取Http响应头字段
			for(Map.Entry<String, String> entry : header.entrySet()){	//使用For-Each循环的方式遍历获取的头字段的值，此时遍历的循序和输入的顺序相同
				String key = entry.getKey()!=null ? entry.getKey()+ ":" : "";	//当有键的时候这获取键，如果没有则为空字符串
				print(key+ entry.getValue());	//答应键和值的组合
			}
		}
		
		private void process(FileWrapper fileWrapper)
		{
			URL url;
			try {
				url = new URL(fileWrapper.downUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();	//建立一个远程连接句柄，此时尚未真正连接
				conn.setConnectTimeout(5*1000);	//设置连接超时时间为5秒
				conn.setRequestMethod("GET");	//设置请求方式为GET
				conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");	//设置客户端可以接受的媒体类型
				conn.setRequestProperty("Accept-Language", "zh-CN");	//设置客户端语言
				conn.setRequestProperty("Charset", "UTF-8");	//设置客户端编码
				conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");	//设置用户代理
				conn.setRequestProperty("Connection", "Keep-Alive");	//设置Connection的方式
				conn.connect();	//和远程资源建立真正的连接，但尚无返回的数据流
				printResponseHeader(conn);	//答应返回的HTTP头字段集合
				if (conn.getResponseCode()==RESPONSEOK) {	//此处的请求会打开返回流并获取返回的状态码，用于检查是否请求成功，当返回码为200时执行下面的代码
					int fileSize = conn.getContentLength();//根据响应获取文件大小
					fileWrapper.fileLength = fileSize;
					print("fileWrapper.fileLength:"+fileWrapper.fileLength);
				}
				else
				{
					print("服务器响应错误:" + conn.getResponseCode() + conn.getResponseMessage());	//打印错误
					throw new RuntimeException("server response error ");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				print(e.toString());
			}
			
		}

		@Override
		public void run() {
			
			for (FileWrapper fileWrapper : fileList) {
				process(fileWrapper);
			}
			
			Message msg = handler.obtainMessage();
			msg.what = GET_FILE_SIZE;
			msg.obj = fileList;
			handler.sendMessage(msg);	
			
		}
		
	}
	
	
	private void setBoardFile(int position,AdErpBoardFile boardFile)
	{
		data.set(position, boardFile);
		this.notifyDataSetChanged();
	}
	
	
	/**
	 * 
	 */
	private void fileLengthTaskStart() {
		loading = new LoadingDialog(context);
		loading.show();
		new Thread(new FileLengthTask()).start();

	}

	public class UIHander extends Handler {
		/**
		 * 系统会自动调用的回调方法，用于处理消息事件 Mesaage一般会包含消息的标志和消息的内容以及消息的处理器Handler
		 */
		public void handleMessage(Message msg) {
			int taskId = msg.arg1;
			AdErpBoardFile boardFile =null;
			
			switch (msg.what) {
			
			case  INIT:
				
				int fileLength = msg.getData().getInt("fileLength"); // 从消息中获取已经下载的数据长度
				boardFile = data.get(taskId);
				boardFile.setFilelength(fileLength);
				setBoardFile(taskId,boardFile);
				
			    break;
			
			case PROCESSING: // 下载时
				int size = msg.getData().getInt("size"); // 从消息中获取已经下载的数据长度
				boardFile = data.get(taskId);
				if (boardFile != null)
				{
					boardFile.setDownlength(size);
					Log.i(TAG, "handleMessage taskId："+taskId+",size:"+size);
					float num = (float) boardFile.getDownlength()/ (float) boardFile.getFilelength(); 
					int result = (int) (num * 100); // 把获取的浮点数计算结构专访为整数
					boardFile.setPercent(result + "%");
					setBoardFile(taskId,boardFile);
				}
				break;
				
				
			case FINISHED: //下载完成
				
				boardFile = data.get(taskId);
				
				if (boardFile != null)
				{
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						
						boardFile.setDownloadStatus(FILE_DOWLOAD_FINISHED);
						
						 // 当下载完成时
						String saveDirsbsolute = Environment.getExternalStorageDirectory().getAbsolutePath();
						
						
						// 正式文件保存目录
						File realDir = new File(saveDirsbsolute + "/Dream/Files/BoardFile/");
						
						if (!realDir.exists()) {
							realDir.mkdirs();
						}
						
						File realfile = new File(saveDirsbsolute+ "/Dream/Files/BoardFile/"+ boardFile.getFileName());
						Boolean flg = true;
						int k = 1;
						while (flg) {
							if (realfile.exists()) {
								realfile = new File(saveDirsbsolute
										+ "/Dream/Files/BoardFile/" + "(" + k
										+ ")" + boardFile.getFileName());
								k++;
							} else {
								flg = false;
							}
						}
						File tempfile = null;
						try {
							if (!realfile.exists()) {
								realfile.createNewFile();
							}
							// 找到已下载的文件
							String tempfilename =boardFile.getFileHandle() + ".tmp";
							tempfile = new File(saveDirsbsolute
									+ "/Dream/Files/TempFile/"
									+ tempfilename);
							if (tempfile.isFile()) {
								tempfile.renameTo(realfile);
								tempfile.delete();
							}

						} catch (Exception e) {
							
							if (appContext!= null)
							{
								appContext.saveErrorLog(e);
							}
							e.printStackTrace();
						}

						// 要更新erp_news_item状态为下载完成,文件的实绩名称
						dbErpBoardMainDao.updatefilestatusandrealname(
								boardFile.getFileHandle(), realfile.getName(),FILE_DOWLOAD_FINISHED);// 更新文件下载中状态
						
						Toast.makeText(context,
								"文件" + boardFile.getFileName() + "下载完成",
								Toast.LENGTH_LONG).show(); // 使用Toast技术，提示用户下载完成
						
					}
					setBoardFile(taskId,boardFile);
					
				}
				
				break;
				
			case GET_FILE_SIZE:
			
				Vector<FileWrapper> fileList = (Vector<FileWrapper>)msg.obj;
				FileWrapper fileWrapper = null;
				
				if (fileList != null)
				{
					for (int i =0; i < fileList.size(); ++i)
					{
						fileWrapper = fileList.get(i);
						boardFile = data.get(fileWrapper.taskId);
						boardFile.setFilelength(fileWrapper.fileLength);
						data.set(fileWrapper.taskId, boardFile);
					}
					notifyDataSetChanged();
					
					if (loading != null)
					{
						loading.dismiss();
					}
				}
			
			    break;
				

			case -1: // 下载失败时
				Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show(); // 提示用户下载失败
				break;
			}
		}
	}
}
