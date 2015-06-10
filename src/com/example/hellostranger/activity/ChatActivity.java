package com.example.hellostranger.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.chat.FaceConversionUtil;
import com.chat.FacePageFragment;
import com.chat.FacePageFragmentAdapter;
import com.chat.FileUtils;
import com.chat.ImageUtil;
import com.chat.MoreSelectAdapter;
import com.chat.MsgCotentAdapter;
import com.chat.MyListView;
import com.chat.SoundUtil;
import com.clientUtils.ClientManager;
import com.clientUtils.ClientUtils;
import com.clientUtils.MsgConvertionUtil;
import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.R;
import com.example.hellostranger.bean.ChatInfoEntity;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgClient;
import com.msg_relative.iMoMoMsgDb;
import com.msg_relative.iMoMoMsgTypes;
import com.chat.MyListView.OnRefreshListener;
import com.static_configs.StaticValues;

public class ChatActivity extends FragmentActivity {

	String friendId;// 聊天好友的Id
	String friendName;// 聊天好友名
	public static boolean isgroupMsg = false;//是否是群组聊天

	TextView tv_friendName;// 聊天好友名

	private EditText Et_msgEdit;
	private ImageButton IbSend_faceicon;// 发送表情
	private ImageButton IbSend_moreselect;// 发送更多选项中的..
	private ImageButton IbSend_voice;// 发送语音
	private Context context;

	// 更多选项窗口
	private LinearLayout ll_popwindow;// 隐藏窗口
	private boolean isMoreSelectWindowInited = false;
	private boolean isMoreSelectWindowOpened = false;
	private GridView moreselect_gridView;// 更多选项(图片 拍照...)
	private BaiduASRDigitalDialog mBaiduASRDigitalDialog;// 百度语音识别

	// 分享图片相关
	private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private String PHOTO_FILE_NAME = "";
	private File tempFile;// 临时文件,这样做可以得到原图，而不至于得到非常小的缩略图
	/** 图片存储路径 */
	private static final String IMAGEPATH = "/sdcard/iMoMo/imageRecord/";

	// 语音窗口
	private boolean isVoiceWindowInited = false;
	private boolean isVoiceWindowOpened = false;
	private TextView Tv_voiceRecding;
	private ImageView Ib_voiceRecding;
	private LinearLayout Ll_voiceRecding;
	private boolean isTosendTextMag = false;// 判断是否是要发送文本信息,是则点击不弹出录音窗口，否则弹出
	private boolean closeRec = false;// 停止录音
	private int voiceLong = 0;// 录音时间
	private String tempVoicePath = null;// 语音文件路径

	/** 用于语音播放 */
	private MediaPlayer mPlayer = null;
	/** 用于完成录音 */
	private MediaRecorder mRecorder = null;
	/** 录音存储路径 */
	private static final String VOICEPATH = "/sdcard/iMoMo/voiceRecord/";

	// 表情栏窗口
	private GridView face_pagegridView;// 表情栏gridview
	ViewPager FaceviewPager;// 表情栏Viewpager
	private ArrayList<Fragment> facefragmentsList;
	private boolean isFaceWindowInited = false;
	private boolean isFaceWindowOpened = false;
	private LinearLayout ll_face_dot;// 分页节点
	private RelativeLayout rl_face;// 表情栏
	int page_indext = 0;// 当前表情页
	ImageView face_dot0;// 三页表情的下标点
	ImageView face_dot1;
	ImageView face_dot2;

	public Handler handler;
	private MyListView Lv_msgCotent;// 消息列表
	private static List<iMoMoMsgClient> msgList;// 消息集合
	private static MsgCotentAdapter cotentAdapter;// 消息适配器

	iMoMoDataBase db;// 消息数据库

	MsgConvertionUtil convertionUtil;// 消息转换工具
	
	/**
	 * 有人发来消息，显示在界面上
	 * 
	 * @param msgClient
	 */
	public static void AddMsgItem(iMoMoMsgClient msgClient) {
		msgList.add(msgClient);
		cotentAdapter.notifyDataSetChanged();
//		Lv_msgCotent.setSelection(cotentAdapter.getCount() - 1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_view);
		context = this;
		db = new iMoMoDataBase(context);
		convertionUtil = MsgConvertionUtil.getInstance();
		initViews();// 初始化控件
		initData();// 初始化内容
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case StaticValues.FACE_GETTED:
					int index = Et_msgEdit.getSelectionStart();
					Et_msgEdit.getText().insert(index,
							(SpannableString) msg.obj);
					Et_msgEdit.setSelection(index
							+ ((SpannableString) msg.obj).length());
					break;
				case StaticValues.DEL_EDIT_TEXT:
					int selection = Et_msgEdit.getSelectionStart();
                    String text = Et_msgEdit.getText().toString();
                    if (selection > 0) {
                        String text2 = text.substring(selection - 1,selection);
                        if ("]".equals(text2)) {
                        	int l = selection - 2;
                        	while(l>=0){
                        		String text3 = text.substring(l, l+1);
                        		if ("[".equals(text3)){
                        			Et_msgEdit.getText().delete(l, selection);
                        			break;
                        		}
                        		l--;
                        	}
                        }else
                        	Et_msgEdit.getText().delete(selection - 1, selection);
                    }
					break;
				case StaticValues.VOICE_REC_TIME:
					voiceLong = msg.arg1;
					Tv_voiceRecding.setText(msg.arg1 + "″");
					break;
				}
			};
		};
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		closeHintinpit();
	}

	// 关闭输入隐藏栏
	public void closeHintinpit() {
		if (isFaceWindowOpened)
			closeFaceWindow();
		if (isVoiceWindowOpened)
			closeVoiceWindow();
		if (isMoreSelectWindowOpened)
			closeMoreSelectWindow();
	}

	// 初始化消息列表
	private void initData() {
		Intent intent = getIntent();
		friendId = intent.getStringExtra("friendId");// 得到Id
//		Log.i("--", "=============================聊天界面的到的Id是 " + friendId);
//		Toast.makeText(context, "聊天界面的到的Id是" + friendId, 0).show();
		friendName = intent.getStringExtra("friendName");
		isgroupMsg = intent.getBooleanExtra("isgroupMsg", false);
		tv_friendName.setText(friendName);

		MainActivity.mp0.setChatingId(friendId);// 设置正在聊天的好友Id

		Log.i("--", "初始化Lv_msgCotent...");
		FaceConversionUtil.getInstace().getFileText(context);// 要初始化，不然发语音是显示的是字，必须等到发一次表情后才显示语音icon
		Lv_msgCotent = (MyListView) findViewById(R.id.Lv_msgCotent);
		msgList = new ArrayList<iMoMoMsgClient>();
		cotentAdapter = new MsgCotentAdapter(context, msgList);
		Lv_msgCotent.setAdapter(cotentAdapter);

		/** 从数据库得到和该好友的聊天记录 **/
		getMsgFromDb();

		Lv_msgCotent
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("编辑");
						menu.add(0, 0, 0, "删除");
						menu.add(0, 1, 0, "取消");
						menu.setQwertyMode(true);
						Log.i("--", "chatactivity-添加菜单");
					}
				});
		
		/**
		 * 点击消息列表，关闭输入法
		 */
		Lv_msgCotent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				closeInputMethod();
				return false;
			}
		});
		
		/**
		 * 下拉显示历史更多10条消息
		 */
		Lv_msgCotent.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(1500);
							
							getHistoryMsg();
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						Lv_msgCotent.onRefreshComplete();
					}
				}.execute(null, null, null);
			}
		});
		
	}

	// 上下文菜单监听
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = menuInfo.position - 1;// 需要减一 ， menuInfo.position从1开始计数
//		Toast.makeText(context, "第几个:" + position, 0).show();
		iMoMoMsgClient msgClient = msgList.get(position);
		JSONObject json = JSON.parseObject(msgClient.getMsgJson());
		String time = json.getString(MsgKeys.sendTime);
//		Log.i("--", "删除==========time = " + time);
		int type = json.getInteger(MsgKeys.msgType);
		switch (item.getItemId()) {
		case 0:
			msgList.remove(position);
			cotentAdapter = new MsgCotentAdapter(context, msgList);
			cotentAdapter.notifyDataSetChanged();
			Lv_msgCotent.setAdapter(cotentAdapter);
			if(position > 0){
				Lv_msgCotent.setSelection(position - 1);
			}else {
				Lv_msgCotent.setSelection(0);
			}
//			Log.i("--", "cotentAdapter 情况 : cotentAdapter.size = " + cotentAdapter.getCount());
			// 如果是语音或图片文件，要删除文件,之后还有删除数据库存储数据
			if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
				FileUtils.deleteFile(json.getString(MsgKeys.voicePath));
			} else if (type == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
				FileUtils.deleteFile(json.getString(MsgKeys.imagePath));
			}
			db.delete(friendId, json.getString(MsgKeys.sendTime));
			break;
		case 1:
			String msg = msgList.get(position).getMsgJson();
//			Log.i("--", "长按的Item=================msg :" + msg);
			break;
		}
		return super.onContextItemSelected(item);
	}

	// 初始化消息输入框
	private void initViews() {
		ll_popwindow = (LinearLayout) findViewById(R.id.ll_popwindow);// 隐藏框

		IbSend_faceicon = (ImageButton) findViewById(R.id.IbSend_faceicon);
		IbSend_moreselect = (ImageButton) findViewById(R.id.IbSend_moreselect);
		IbSend_voice = (ImageButton) findViewById(R.id.IbSend_voice);
		Et_msgEdit = (EditText) findViewById(R.id.Et_msgEdit);
		Et_msgEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(context, "点击要输入", 0).show();
				if (isMoreSelectWindowOpened) {
					closeMoreSelectWindow();// 关闭更多选项窗口
				} else if (isVoiceWindowOpened) {
					closeVoiceWindow();// 关闭录音窗口
				} else if (isFaceWindowOpened) {
					closeFaceWindow();// 关闭表情栏
				}
			}
		});
		Et_msgEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					isTosendTextMag = true;
					// 转换成发送图标
					IbSend_voice
							.setBackgroundResource(R.drawable.chatview_send_msg_ok);
				} else if (s.length() == 0) {
					isTosendTextMag = false;
					IbSend_voice
							.setBackgroundResource(R.drawable.chatview_voice_rec_selector);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		tv_friendName = (TextView) findViewById(R.id.chating_friendName);
	}

	/**
	 * 从数据库得到和该好友的聊天记录,判断有没有该表
	 */
	public void getMsgFromDb() {
		iMoMoDataBase db = new iMoMoDataBase(context);
		if (db.isTableExists("msg" + ClientManager.clientId + "_" + friendId)) {
			List<iMoMoMsgDb> list = db.getMsgLatice(friendId, 0, 15);
			if (list.size() > 0) {
				// 把数据库存储的消息转化为界面显示的消息
				MsgConvertionUtil convertionUtil = MsgConvertionUtil
						.getInstance();
				List<iMoMoMsgClient> tempList = new ArrayList<iMoMoMsgClient>();
				for (int i = list.size()-1; i>=0; i--) {
					iMoMoMsgDb msgDb = list.get(i);
					tempList.add(convertionUtil.Convert_Db2Client(msgDb));
				}
				msgList.addAll(tempList);
				cotentAdapter.notifyDataSetChanged();
				Lv_msgCotent.setSelection(cotentAdapter.getCount() - 1);
			}
		} else {
			db.createMsgTable(friendId);// 建表
		}
	}
	
	/**
	 * 下拉显示历史更多10条消息
	 */
	public void getHistoryMsg(){
		iMoMoDataBase db = new iMoMoDataBase(context);
		if (db.isTableExists("msg" + ClientManager.clientId + "_" + friendId)) {
			List<iMoMoMsgDb> list = db.getMsgLatice(friendId,  0, msgList.size() + 5);
			int selection =list.size() -  msgList.size();
			Log.i("--", "============= selection = " + selection);
			Log.i("--","=====下拉显示更多消息==================list.size  = " + list.size());
			if (list.size() > 0) {
				// 把数据库存储的消息转化为界面显示的消息
				MsgConvertionUtil convertionUtil = MsgConvertionUtil
						.getInstance();
				List<iMoMoMsgClient> tempList = new ArrayList<iMoMoMsgClient>();
				for (int i = list.size()-1; i>=0; i--) {
					iMoMoMsgDb msgDb = list.get(i);
					tempList.add(convertionUtil.Convert_Db2Client(msgDb));
				}
				Log.i("--","=====下拉显示更多消息==================tempList.size  = " + tempList.size());
				msgList.clear();
				msgList.addAll(tempList);
				cotentAdapter = new MsgCotentAdapter(context, msgList);
				cotentAdapter.notifyDataSetChanged();
				Lv_msgCotent.setAdapter(cotentAdapter);
				Lv_msgCotent.setSelection(selection);
			}
		} else {
			db.createMsgTable(friendId);// 建表
		}
	}
	
	// 关闭输入法
	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//切换时隐藏输入法
		imm.hideSoftInputFromWindow(Et_msgEdit.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS); // 强制隐藏键盘
	}

	// 初始化表情栏
	@SuppressLint("NewApi")
	private void initFaceWindow() {
		// 加载三屏表情
		facefragmentsList = new ArrayList<Fragment>();
		facefragmentsList.add(new FacePageFragment(1, handler));
		facefragmentsList.add(new FacePageFragment(2, handler));
		facefragmentsList.add(new FacePageFragment(3, handler));
		rl_face = (RelativeLayout) findViewById(R.id.rl_face);
		face_dot0 = (ImageView) findViewById(R.id.face_dot0);
		face_dot1 = (ImageView) findViewById(R.id.face_dot1);
		face_dot2 = (ImageView) findViewById(R.id.face_dot2);
		face_dot0.setBackgroundResource(R.drawable.face_page_dot_selected);
		FaceviewPager = (ViewPager) findViewById(R.id.face_vPager);
		FaceviewPager.setCurrentItem(0);
		FaceviewPager.setAdapter(new FacePageFragmentAdapter(
				getSupportFragmentManager(), facefragmentsList));
		FaceviewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		ll_face_dot = (LinearLayout) findViewById(R.id.ll_face_dot);
		isFaceWindowInited = true;
	}

	// 打开表情窗口
	private void openFaceWindow() {
		closeInputMethod();
		if (isMoreSelectWindowOpened)
			closeMoreSelectWindow();
		if (isVoiceWindowOpened)
			closeVoiceWindow();
		if (!isFaceWindowInited)
			initFaceWindow();
		Animation enter = AnimationUtils.loadAnimation(context,
				R.anim.dialog_enter);
		// ll_popwindow.startAnimation(enter);
		FaceviewPager.startAnimation(enter);
		ll_popwindow.setVisibility(View.VISIBLE);
		rl_face.setVisibility(View.VISIBLE);
		IbSend_faceicon
				.setBackgroundResource(R.drawable.chatview_text_button_selector);
		isFaceWindowOpened = true;
	}

	// 关闭表情窗口
	private void closeFaceWindow() {
		// FaceviewPager.setVisibility(View.GONE);
		ll_popwindow.setVisibility(View.GONE);
		rl_face.setVisibility(View.GONE);
		IbSend_faceicon
				.setBackgroundResource(R.drawable.chatview_face_button_selector);
		isFaceWindowOpened = false;
	}

	// 切换表情窗口
	private void toggleFaceWindow() {
		if (isFaceWindowOpened)
			closeFaceWindow();
		else
			openFaceWindow();
	}

	/** viewpager滑动监听 **/
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int select_indext) {
			switch (select_indext) {
			case 0:
				if (page_indext == 1) {
					face_dot1.setBackgroundResource(R.drawable.face_page_dot);
				} else if (page_indext == 2) {
					face_dot2.setBackgroundResource(R.drawable.face_page_dot);
				}
				face_dot0
						.setBackgroundResource(R.drawable.face_page_dot_selected);
				break;
			case 1:
				if (page_indext == 0) {
					face_dot0.setBackgroundResource(R.drawable.face_page_dot);
				} else if (page_indext == 2) {
					face_dot2.setBackgroundResource(R.drawable.face_page_dot);
				}
				face_dot1
						.setBackgroundResource(R.drawable.face_page_dot_selected);
				break;
			case 2:
				if (page_indext == 0) {
					face_dot0.setBackgroundResource(R.drawable.face_page_dot);
				} else if (page_indext == 1) {
					face_dot1.setBackgroundResource(R.drawable.face_page_dot);
				}
				face_dot2
						.setBackgroundResource(R.drawable.face_page_dot_selected);
				break;
			}
			page_indext = select_indext;
		}

		@Override
		public void onPageScrollStateChanged(int select_indext) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

	}

	// 初始化VoiceWindow
	private void initVoiceWindow() {
		Ll_voiceRecding = (LinearLayout) findViewById(R.id.Ll_voiceRecding);
		Tv_voiceRecding = (TextView) findViewById(R.id.Tv_voiceRecding);
		Ib_voiceRecding = (ImageView) findViewById(R.id.Ib_voiceRecding);
		Ib_voiceRecding.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				voiceRecTimeTh timeTh = new voiceRecTimeTh();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					MainActivity.playSound(3);//录音提示
					Ib_voiceRecding.setScaleX(0.7f);
					Ib_voiceRecding.setScaleY(0.7f);
					Ib_voiceRecding.setPivotX(Ib_voiceRecding.getWidth() >> 1);
					Ib_voiceRecding.setPivotY(Ib_voiceRecding.getHeight() >> 1);
					// 开始录音
					String voiceName = System.currentTimeMillis() + "";// 录音文件名
					tempVoicePath = voiceName;
					Log.i("--", "生成voiceName = " + voiceName);
//					Toast.makeText(context, "开始录音", 0).show();
					SoundUtil.getInstance().startRecord(context, voiceName);
					timeTh.start();// 计时开始
					break;
				case MotionEvent.ACTION_UP:
//					MainActivity.playSound(3);//发送语音提示
					Ib_voiceRecding.setScaleX(1.0f);
					Ib_voiceRecding.setScaleY(1.0f);
					Ib_voiceRecding.setPivotX(Ib_voiceRecding.getWidth() >> 1);
					Ib_voiceRecding.setPivotY(Ib_voiceRecding.getHeight() >> 1);
					
					// 停止录音
//					Toast.makeText(context, "停止录音", 0).show();
					timeTh.stopRec();
					SoundUtil.getInstance().stopRecord();
					Log.i("--", "录音时间=" + voiceLong + "秒");
					if (voiceLong < 1) {
						SoundUtil.getInstance().deleteVoiceFile();
					} else {

						/********************* 发送语音消息 *******************************************/
						// 在这里获得刚才录音的byte[]，准备发送,并显示在消息列表
						byte[] voiceBytes = SoundUtil.getInstance()
								.getVoicebytes(context, tempVoicePath);

						iMoMoMsg voiceMsg = new iMoMoMsg();
						voiceMsg.symbol = '-';
						JSONObject moJson = new JSONObject();
						moJson.put(MsgKeys.msgType,
								iMoMoMsgTypes.CHATING_VOICE_MSG);
						moJson.put(MsgKeys.userId, ClientManager.clientId);
						moJson.put(MsgKeys.friendId, friendId);
						moJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
						moJson.put(MsgKeys.voiceTime, voiceLong);
						if(isgroupMsg){
							moJson.put(MsgKeys.isGroupMsg, iMoMoMsgTypes.GROUP_MSG);
						}
						
						voiceMsg.msgJson = moJson.toJSONString();
						voiceMsg.msgBytes = voiceBytes;

						iMoMoMsgClient msgClient = new iMoMoMsgClient();
						JSONObject msgJson = new JSONObject();
						msgJson.put(MsgKeys.msgType,
								iMoMoMsgTypes.CHATING_VOICE_MSG);
						msgJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
						msgJson.put(MsgKeys.voicePath, SoundUtil.getInstance()
								.getVoicePath());
						msgJson.put(MsgKeys.voiceTime, voiceLong);
						msgClient.setMsgJson(msgJson.toJSONString());
						msgClient.setGetted(false);

						AddMsgClientitem(msgClient);// 显示：
						MainActivity.myBinder.sendCharMsg(voiceMsg);// 发送

						/*** 保存到数据库 **/
						db.insertMsg(
								convertionUtil.ConvertClient2Db(msgClient),
								friendId);

					}
					voiceLong = 0;
					Tv_voiceRecding.setText("点击开始录音");
					break;
				}
				return false;
			}
		});
		isVoiceWindowInited = true;
	}

	/** 录音计时器 */
	private class voiceRecTimeTh extends Thread {
		int tempLong = 0;

		public voiceRecTimeTh() {
			closeRec = false;
		}

		public void stopRec() {
			closeRec = true;
		}

		public void run() {
			while (!closeRec) {
				try {
					Message msg = handler.obtainMessage();
					msg.what = StaticValues.VOICE_REC_TIME;
					msg.arg1 = tempLong;
					handler.sendMessage(msg);
					Thread.sleep(1000);
					tempLong++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 打开录音窗口
	private void openVoiceWindow() {
		closeInputMethod();
		if (isMoreSelectWindowOpened)
			closeMoreSelectWindow();
		if (isFaceWindowOpened)
			closeFaceWindow();
		if (!isVoiceWindowInited)
			initVoiceWindow();
		ll_popwindow.setVisibility(View.VISIBLE);
		Ll_voiceRecding.setVisibility(View.VISIBLE);
		isVoiceWindowOpened = true;
	}

	// 关闭录音窗口
	private void closeVoiceWindow() {
		Ll_voiceRecding.setVisibility(View.GONE);
		ll_popwindow.setVisibility(View.GONE);
		isVoiceWindowOpened = false;
	}

	// 切换打开关闭录音窗口
	private void toggleVoiceWindow() {
		if (isVoiceWindowOpened)
			closeVoiceWindow();
		else
			openVoiceWindow();
	}

	// 初始化MoreSelectWindow
	private void initMoreSelectWindow() {
		moreselect_gridView = (GridView) findViewById(R.id.moreselect_gridView);
		MoreSelectAdapter adapter = new MoreSelectAdapter(context);
		moreselect_gridView.setAdapter(adapter);
		isMoreSelectWindowInited = true;
		moreselect_gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageView selectView = (ImageView) view
						.findViewById(R.id.selec_icon);
				Animation anim = AnimationUtils.loadAnimation(context,
						R.anim.moselect_item_amin);
				selectView.startAnimation(anim);
				switch (position) {
				case 0:
					// 图片
					getFromGalery();
					break;
				case 1:
					// 拍照
					takePhoto();
					break;
				
				case 2:
					// 发送我的具体位置
					iMoMoMsg locMsg = new iMoMoMsg();
					locMsg.symbol = '+';
					JSONObject locJson = new JSONObject();
					locJson.put(MsgKeys.msgType,
							iMoMoMsgTypes.CHATING_TEXT_MSG);
					locJson.put(MsgKeys.userId,
							ClientManager.clientId);
					locJson.put(MsgKeys.friendId, friendId);
					locJson.put(MsgKeys.msgCotent, "我的位置:" + ClientManager.myLocation);
					locJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
					if(isgroupMsg){
						locJson.put(MsgKeys.isGroupMsg, iMoMoMsgTypes.GROUP_MSG);
					}
					locMsg.msgJson = locJson.toJSONString();
					// 显示：
					iMoMoMsgClient msgClient = convertionUtil
							.Convert_Net2Client(context,
									locMsg, false);
					AddMsgClientitem(msgClient);
					MainActivity.myBinder.sendCharMsg(locMsg);// 发送

					/*** 保存到数据库 **/
					iMoMoMsgDb msgDb = convertionUtil
							.Convert_Net2Db(context, locMsg,
									0, 1);
					db.insertMsg(msgDb, friendId);
					
					break;	
				
				case 4:
					// 语音识别
					Bundle params = new Bundle();
					// !!!!!!!!!!!!!!!!!!!!!!!!!!!
					// 注意：
					// 务必设置为自己应用对应的key，演示用的key定期清理
					// !!!!!!!!!!!!!!!!!!!!!!!!!!!
					params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
							"VGc2Ih0kOEzi7Gmf1rW0pU1M");
					params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
							"xxPXsb2GVT4nhWDwljwiSNyHoM6F6NEn");
					params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
							BaiduASRDigitalDialog.THEME_GREEN_DEEPBG);// 设置对话框风格
					mBaiduASRDigitalDialog = new BaiduASRDigitalDialog(context,
							params);
					mBaiduASRDigitalDialog
							.setDialogRecognitionListener(new DialogRecognitionListener() {
								/** 回调函数, */
								@Override
								public void onResults(Bundle results) {
									ArrayList<String> list = results
											.getStringArrayList(RESULTS_RECOGNITION);
									String str = Arrays
											.toString(null == list ? null
													: list.toArray());
									/******************************* 发送语音识别（文字消息）消息 ******************************************/
									String content = str.substring(1,
											str.length() - 2);// 识别后的文字消息
									iMoMoMsg textMsg = new iMoMoMsg();
									textMsg.symbol = '+';
									JSONObject moJson = new JSONObject();
									moJson.put(MsgKeys.msgType,
											iMoMoMsgTypes.CHATING_TEXT_MSG);
									moJson.put(MsgKeys.userId,
											ClientManager.clientId);
									moJson.put(MsgKeys.friendId, friendId);
									moJson.put(MsgKeys.msgCotent, content);
									moJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
									if(isgroupMsg){
										moJson.put(MsgKeys.isGroupMsg, iMoMoMsgTypes.GROUP_MSG);
									}
									textMsg.msgJson = moJson.toJSONString();
									// 显示：
									iMoMoMsgClient msgClient = convertionUtil
											.Convert_Net2Client(context,
													textMsg, false);
									AddMsgClientitem(msgClient);
									MainActivity.myBinder.sendCharMsg(textMsg);// 发送

									/*** 保存到数据库 **/
									iMoMoMsgDb msgDb = convertionUtil
											.Convert_Net2Db(context, textMsg,
													0, 1);
									db.insertMsg(msgDb, friendId);
								}
							});
					mBaiduASRDigitalDialog.show();
					break;
				}
			}
		});
	}

	/** 从相册获取 */
	public void getFromGalery() {
		Intent intent = new Intent();
		// 激活系统图库，选择一张图片//这两种方式选择的提供器后者更多，前者只有图库 和文件管理
		// Intent intent = new Intent(Intent.ACTION_PICK);
		/* 开启Pictures画面Type设定为image */
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	// 拍照
	public void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (FileUtils.hasSdcard()) {
			PHOTO_FILE_NAME = System.currentTimeMillis() + ".jpg";
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		} else {
			Toast.makeText(context, "未发现内存卡", 0).show();
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == PHOTO_REQUEST_CAMERA) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				openPreviewDialog(Uri.fromFile(tempFile));
			} else if (requestCode == PHOTO_REQUEST_GALLERY) {
				if (data != null) {
					// 得到图片的全路径
					Uri uri = data.getData();
					openPreviewDialog(uri);
				}
			}
		}
	}

	// 图片预览对话框
	private void openPreviewDialog(Uri uri) {
		// 测试：弹出一个愈看图片的对话框(全屏显示)
		final Dialog previewDialog = new Dialog(context,
				R.style.Dialog_Fullscreen);
		previewDialog.setCanceledOnTouchOutside(true); // 点击其他部位，消失
		Window window = previewDialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.dialog_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		previewDialog.onWindowAttributesChanged(wl);// 设置对框框动画属性
		LinearLayout dialoglayout = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.takephoto_preview_dialog, null);
		previewDialog.setContentView(dialoglayout);
		previewDialog.setCancelable(true);
		final ImageView Iv_takenPic = (ImageView) dialoglayout
				.findViewById(R.id.Iv_takenPic);
		Button Bt_send_takenPic = (Button) dialoglayout
				.findViewById(R.id.Bt_send_takenPic);
		ImageButton btn_preview_back = (ImageButton) dialoglayout
				.findViewById(R.id.btn_preview_back);
		Iv_takenPic.setImageURI(uri);
		if (tempFile != null)
			tempFile.delete();
		previewDialog.show();
		Bt_send_takenPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 发送图片信息
				// ...
				Bitmap bitmap = ((BitmapDrawable) Iv_takenPic.getDrawable())
						.getBitmap();
				bitmap = Bitmap.createScaledBitmap(bitmap, 480, 800, true);
				int size = bitmap.getWidth() * bitmap.getHeight();
				ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
				// 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
				// 发送时要发送这个,客户端处理只用保存图片的path
				// msg.setMsgBytes(baos.toByteArray());
				// 保存到本地文件
				String imagePath = IMAGEPATH + ClientManager.clientId + "_"
						+ friendId + "_" + System.currentTimeMillis() + ".png";
				ImageUtil.getInstance().saveImage(context, baos.toByteArray(),
						imagePath);
				// PictureUtil.getSmallBitmap(imagePath);
				/******************************* 发送图片消息 ******************************************/
				iMoMoMsg imageMsg = new iMoMoMsg();
				imageMsg.symbol = '-';
				JSONObject moJson = new JSONObject();
				moJson.put(MsgKeys.msgType, iMoMoMsgTypes.CHATING_IMAGE_MSG);
				moJson.put(MsgKeys.userId, ClientManager.clientId);
				moJson.put(MsgKeys.friendId, friendId);
				moJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
				if(isgroupMsg){
					moJson.put(MsgKeys.isGroupMsg, iMoMoMsgTypes.GROUP_MSG);
				}
				imageMsg.msgJson = moJson.toJSONString();
				imageMsg.msgBytes = baos.toByteArray();

				iMoMoMsgClient msgClient = new iMoMoMsgClient();
				JSONObject msgJson = new JSONObject();
				msgJson.put(MsgKeys.msgType, iMoMoMsgTypes.CHATING_IMAGE_MSG);
				msgJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
				msgJson.put(MsgKeys.imagePath, imagePath);
				msgClient.setMsgJson(msgJson.toJSONString());
				msgClient.setGetted(false);

				AddMsgClientitem(msgClient);// 显示：
				MainActivity.myBinder.sendCharMsg(imageMsg);// 发送
				/*** 保存到数据库 **/
				db.insertMsg(convertionUtil.ConvertClient2Db(msgClient),
						friendId);

				previewDialog.dismiss();
			}
		});
		btn_preview_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				previewDialog.dismiss();
			}
		});
	}

	// 打开更多选项窗口
	private void openMoreSelectWindow() {
		closeInputMethod();
		if (isFaceWindowOpened)
			closeFaceWindow();
		if (isVoiceWindowOpened)
			closeVoiceWindow();
		if (!isMoreSelectWindowInited)
			initMoreSelectWindow();
		ll_popwindow.setVisibility(View.VISIBLE);
		moreselect_gridView.setVisibility(View.VISIBLE);
		IbSend_moreselect
				.setBackgroundResource(R.drawable.chatview_text_button_selector);
		isMoreSelectWindowOpened = true;
	}

	// 关闭更多选项窗口
	private void closeMoreSelectWindow() {
		// if(ll_popwindow != null )
		ll_popwindow.setVisibility(View.GONE);
		moreselect_gridView.setVisibility(View.GONE);
		isMoreSelectWindowOpened = false;
		IbSend_moreselect
				.setBackgroundResource(R.drawable.more_select_button_selector);
	}

	// 切换MoreSelectwindow
	private void toggleMoreSelectWindow() {
		closeInputMethod();
		if (isMoreSelectWindowOpened)
			closeMoreSelectWindow();
		else
			openMoreSelectWindow();
	}

	public void onButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.IbSend_faceicon:
			toggleFaceWindow();
			break;
		case R.id.IbSend_moreselect:
			toggleMoreSelectWindow();
			break;
		case R.id.IbSend_voice:
			if (isTosendTextMag) {
				String content = Et_msgEdit.getText().toString();
				if (!content.equals("")) {
					/**************************** 发送文字消息 ******************************************/
					iMoMoMsg textMsg = new iMoMoMsg();
					textMsg.symbol = '+';
					JSONObject moJson = new JSONObject();
					moJson.put(MsgKeys.msgType, iMoMoMsgTypes.CHATING_TEXT_MSG);
					moJson.put(MsgKeys.userId, ClientManager.clientId);
					moJson.put(MsgKeys.friendId, friendId);
					moJson.put(MsgKeys.msgCotent, content);
					moJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
					if(isgroupMsg){
						moJson.put(MsgKeys.isGroupMsg, iMoMoMsgTypes.GROUP_MSG);
					}
					textMsg.msgJson = moJson.toJSONString();

					// 显示：
					iMoMoMsgClient msgClient = convertionUtil
							.Convert_Net2Client(context, textMsg, false);
					AddMsgClientitem(msgClient);
					MainActivity.myBinder.sendCharMsg(textMsg);// 发送
					Et_msgEdit.setText("");

					/*** 保存到数据库 */
					iMoMoMsgDb msgDb = convertionUtil.Convert_Net2Db(context,
							textMsg, 0, 1);
					db.insertMsg(msgDb, friendId);
				}
			} else {
				toggleVoiceWindow();
			}
			break;
			
		case R.id.btn_options:
			Intent intent = new Intent(context, FriendMsgSettingActivity.class);
			intent.putExtra("friendId", friendId);
			intent.putExtra("friendName", friendName);
			startActivity(intent);
			break;
		}
	}

	/**
	 * 发送消息，更新显示界面
	 * 
	 * @param msgClient
	 */
	public void AddMsgClientitem(iMoMoMsgClient msgClient) {
		msgList.add(msgClient);
		cotentAdapter.notifyDataSetChanged();
		Lv_msgCotent.setSelection(cotentAdapter.getCount() - 1);
	}

	/**
	 * 清空该好友的聊天记录
	 */
	public static void clearMsg(){
		msgList.clear();
		cotentAdapter.notifyDataSetChanged();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBaiduASRDigitalDialog != null) {
			mBaiduASRDigitalDialog.dismiss();// 释放资源
		}
		MainActivity.mp0.setChatingId("");// 没有正在聊天的好友
		updateLaticeChat();

	}

	/**
	 * 更新最近联系人数据库
	 */
	public void updateLaticeChat() {
		iMoMoDataBase db = new iMoMoDataBase(context);
		ChatInfoEntity entity = new ChatInfoEntity();
		if (msgList.size() > 0) {
			iMoMoMsgClient msgClient = msgList.get(msgList.size() - 1);
			JSONObject json = JSON.parseObject(msgClient.getMsgJson());
			int type = json.getIntValue(MsgKeys.msgType);
			String content = "";
			if (type == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
				content = "[图片]";
			} else if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
				content = "[语音]";
			} else if (type == iMoMoMsgTypes.CHATING_TEXT_MSG) {
				content = json.getString(MsgKeys.msgCotent);
			}
			
			entity.setFriendId(friendId);
			entity.setFriendName(friendName);
			entity.setChatContent(content);
			entity.setChatCreatTime(json.getString(MsgKeys.sendTime));
			entity.setMsg_num(0);
			entity.setMsgtype(0);
			
			// 数据库改了，这时最近联系人界面也得更新, 已经存在，不是添加
			MainActivity.mp0.updateLaticeItem(friendId,
					json.getString(MsgKeys.sendTime), content, true);
		} else {
			entity.setChatContent("");
			entity.setFriendId(friendId);
			entity.setFriendName(friendName);
			entity.setChatCreatTime("");
			entity.setMsg_num(0);
			entity.setMsgtype(0);
//			db.updateLaticeChat(entity);
			MainActivity.mp0.updateLaticeItem(friendId,
					"", "", true);
		}
		if(db.isExistsLatice(friendId)){
			db.updateLaticeChat(entity);
		} else {
			db.insertLaticeChat(entity);
		}
	}
}
