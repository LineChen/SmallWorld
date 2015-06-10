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

	String friendId;// ������ѵ�Id
	String friendName;// ���������
	public static boolean isgroupMsg = false;//�Ƿ���Ⱥ������

	TextView tv_friendName;// ���������

	private EditText Et_msgEdit;
	private ImageButton IbSend_faceicon;// ���ͱ���
	private ImageButton IbSend_moreselect;// ���͸���ѡ���е�..
	private ImageButton IbSend_voice;// ��������
	private Context context;

	// ����ѡ���
	private LinearLayout ll_popwindow;// ���ش���
	private boolean isMoreSelectWindowInited = false;
	private boolean isMoreSelectWindowOpened = false;
	private GridView moreselect_gridView;// ����ѡ��(ͼƬ ����...)
	private BaiduASRDigitalDialog mBaiduASRDigitalDialog;// �ٶ�����ʶ��

	// ����ͼƬ���
	private static final int PHOTO_REQUEST_CAMERA = 1;// ����
	private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
	private String PHOTO_FILE_NAME = "";
	private File tempFile;// ��ʱ�ļ�,���������Եõ�ԭͼ���������ڵõ��ǳ�С������ͼ
	/** ͼƬ�洢·�� */
	private static final String IMAGEPATH = "/sdcard/iMoMo/imageRecord/";

	// ��������
	private boolean isVoiceWindowInited = false;
	private boolean isVoiceWindowOpened = false;
	private TextView Tv_voiceRecding;
	private ImageView Ib_voiceRecding;
	private LinearLayout Ll_voiceRecding;
	private boolean isTosendTextMag = false;// �ж��Ƿ���Ҫ�����ı���Ϣ,������������¼�����ڣ����򵯳�
	private boolean closeRec = false;// ֹͣ¼��
	private int voiceLong = 0;// ¼��ʱ��
	private String tempVoicePath = null;// �����ļ�·��

	/** ������������ */
	private MediaPlayer mPlayer = null;
	/** �������¼�� */
	private MediaRecorder mRecorder = null;
	/** ¼���洢·�� */
	private static final String VOICEPATH = "/sdcard/iMoMo/voiceRecord/";

	// ����������
	private GridView face_pagegridView;// ������gridview
	ViewPager FaceviewPager;// ������Viewpager
	private ArrayList<Fragment> facefragmentsList;
	private boolean isFaceWindowInited = false;
	private boolean isFaceWindowOpened = false;
	private LinearLayout ll_face_dot;// ��ҳ�ڵ�
	private RelativeLayout rl_face;// ������
	int page_indext = 0;// ��ǰ����ҳ
	ImageView face_dot0;// ��ҳ������±��
	ImageView face_dot1;
	ImageView face_dot2;

	public Handler handler;
	private MyListView Lv_msgCotent;// ��Ϣ�б�
	private static List<iMoMoMsgClient> msgList;// ��Ϣ����
	private static MsgCotentAdapter cotentAdapter;// ��Ϣ������

	iMoMoDataBase db;// ��Ϣ���ݿ�

	MsgConvertionUtil convertionUtil;// ��Ϣת������
	
	/**
	 * ���˷�����Ϣ����ʾ�ڽ�����
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
		initViews();// ��ʼ���ؼ�
		initData();// ��ʼ������
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
					Tv_voiceRecding.setText(msg.arg1 + "��");
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

	// �ر�����������
	public void closeHintinpit() {
		if (isFaceWindowOpened)
			closeFaceWindow();
		if (isVoiceWindowOpened)
			closeVoiceWindow();
		if (isMoreSelectWindowOpened)
			closeMoreSelectWindow();
	}

	// ��ʼ����Ϣ�б�
	private void initData() {
		Intent intent = getIntent();
		friendId = intent.getStringExtra("friendId");// �õ�Id
//		Log.i("--", "=============================�������ĵ���Id�� " + friendId);
//		Toast.makeText(context, "�������ĵ���Id��" + friendId, 0).show();
		friendName = intent.getStringExtra("friendName");
		isgroupMsg = intent.getBooleanExtra("isgroupMsg", false);
		tv_friendName.setText(friendName);

		MainActivity.mp0.setChatingId(friendId);// ������������ĺ���Id

		Log.i("--", "��ʼ��Lv_msgCotent...");
		FaceConversionUtil.getInstace().getFileText(context);// Ҫ��ʼ������Ȼ����������ʾ�����֣�����ȵ���һ�α�������ʾ����icon
		Lv_msgCotent = (MyListView) findViewById(R.id.Lv_msgCotent);
		msgList = new ArrayList<iMoMoMsgClient>();
		cotentAdapter = new MsgCotentAdapter(context, msgList);
		Lv_msgCotent.setAdapter(cotentAdapter);

		/** �����ݿ�õ��͸ú��ѵ������¼ **/
		getMsgFromDb();

		Lv_msgCotent
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("�༭");
						menu.add(0, 0, 0, "ɾ��");
						menu.add(0, 1, 0, "ȡ��");
						menu.setQwertyMode(true);
						Log.i("--", "chatactivity-��Ӳ˵�");
					}
				});
		
		/**
		 * �����Ϣ�б��ر����뷨
		 */
		Lv_msgCotent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				closeInputMethod();
				return false;
			}
		});
		
		/**
		 * ������ʾ��ʷ����10����Ϣ
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

	// �����Ĳ˵�����
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int position = menuInfo.position - 1;// ��Ҫ��һ �� menuInfo.position��1��ʼ����
//		Toast.makeText(context, "�ڼ���:" + position, 0).show();
		iMoMoMsgClient msgClient = msgList.get(position);
		JSONObject json = JSON.parseObject(msgClient.getMsgJson());
		String time = json.getString(MsgKeys.sendTime);
//		Log.i("--", "ɾ��==========time = " + time);
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
//			Log.i("--", "cotentAdapter ��� : cotentAdapter.size = " + cotentAdapter.getCount());
			// �����������ͼƬ�ļ���Ҫɾ���ļ�,֮����ɾ�����ݿ�洢����
			if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
				FileUtils.deleteFile(json.getString(MsgKeys.voicePath));
			} else if (type == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
				FileUtils.deleteFile(json.getString(MsgKeys.imagePath));
			}
			db.delete(friendId, json.getString(MsgKeys.sendTime));
			break;
		case 1:
			String msg = msgList.get(position).getMsgJson();
//			Log.i("--", "������Item=================msg :" + msg);
			break;
		}
		return super.onContextItemSelected(item);
	}

	// ��ʼ����Ϣ�����
	private void initViews() {
		ll_popwindow = (LinearLayout) findViewById(R.id.ll_popwindow);// ���ؿ�

		IbSend_faceicon = (ImageButton) findViewById(R.id.IbSend_faceicon);
		IbSend_moreselect = (ImageButton) findViewById(R.id.IbSend_moreselect);
		IbSend_voice = (ImageButton) findViewById(R.id.IbSend_voice);
		Et_msgEdit = (EditText) findViewById(R.id.Et_msgEdit);
		Et_msgEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(context, "���Ҫ����", 0).show();
				if (isMoreSelectWindowOpened) {
					closeMoreSelectWindow();// �رո���ѡ���
				} else if (isVoiceWindowOpened) {
					closeVoiceWindow();// �ر�¼������
				} else if (isFaceWindowOpened) {
					closeFaceWindow();// �رձ�����
				}
			}
		});
		Et_msgEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					isTosendTextMag = true;
					// ת���ɷ���ͼ��
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
	 * �����ݿ�õ��͸ú��ѵ������¼,�ж���û�иñ�
	 */
	public void getMsgFromDb() {
		iMoMoDataBase db = new iMoMoDataBase(context);
		if (db.isTableExists("msg" + ClientManager.clientId + "_" + friendId)) {
			List<iMoMoMsgDb> list = db.getMsgLatice(friendId, 0, 15);
			if (list.size() > 0) {
				// �����ݿ�洢����Ϣת��Ϊ������ʾ����Ϣ
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
			db.createMsgTable(friendId);// ����
		}
	}
	
	/**
	 * ������ʾ��ʷ����10����Ϣ
	 */
	public void getHistoryMsg(){
		iMoMoDataBase db = new iMoMoDataBase(context);
		if (db.isTableExists("msg" + ClientManager.clientId + "_" + friendId)) {
			List<iMoMoMsgDb> list = db.getMsgLatice(friendId,  0, msgList.size() + 5);
			int selection =list.size() -  msgList.size();
			Log.i("--", "============= selection = " + selection);
			Log.i("--","=====������ʾ������Ϣ==================list.size  = " + list.size());
			if (list.size() > 0) {
				// �����ݿ�洢����Ϣת��Ϊ������ʾ����Ϣ
				MsgConvertionUtil convertionUtil = MsgConvertionUtil
						.getInstance();
				List<iMoMoMsgClient> tempList = new ArrayList<iMoMoMsgClient>();
				for (int i = list.size()-1; i>=0; i--) {
					iMoMoMsgDb msgDb = list.get(i);
					tempList.add(convertionUtil.Convert_Db2Client(msgDb));
				}
				Log.i("--","=====������ʾ������Ϣ==================tempList.size  = " + tempList.size());
				msgList.clear();
				msgList.addAll(tempList);
				cotentAdapter = new MsgCotentAdapter(context, msgList);
				cotentAdapter.notifyDataSetChanged();
				Lv_msgCotent.setAdapter(cotentAdapter);
				Lv_msgCotent.setSelection(selection);
			}
		} else {
			db.createMsgTable(friendId);// ����
		}
	}
	
	// �ر����뷨
	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//�л�ʱ�������뷨
		imm.hideSoftInputFromWindow(Et_msgEdit.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS); // ǿ�����ؼ���
	}

	// ��ʼ��������
	@SuppressLint("NewApi")
	private void initFaceWindow() {
		// ������������
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

	// �򿪱��鴰��
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

	// �رձ��鴰��
	private void closeFaceWindow() {
		// FaceviewPager.setVisibility(View.GONE);
		ll_popwindow.setVisibility(View.GONE);
		rl_face.setVisibility(View.GONE);
		IbSend_faceicon
				.setBackgroundResource(R.drawable.chatview_face_button_selector);
		isFaceWindowOpened = false;
	}

	// �л����鴰��
	private void toggleFaceWindow() {
		if (isFaceWindowOpened)
			closeFaceWindow();
		else
			openFaceWindow();
	}

	/** viewpager�������� **/
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

	// ��ʼ��VoiceWindow
	private void initVoiceWindow() {
		Ll_voiceRecding = (LinearLayout) findViewById(R.id.Ll_voiceRecding);
		Tv_voiceRecding = (TextView) findViewById(R.id.Tv_voiceRecding);
		Ib_voiceRecding = (ImageView) findViewById(R.id.Ib_voiceRecding);
		Ib_voiceRecding.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				voiceRecTimeTh timeTh = new voiceRecTimeTh();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					MainActivity.playSound(3);//¼����ʾ
					Ib_voiceRecding.setScaleX(0.7f);
					Ib_voiceRecding.setScaleY(0.7f);
					Ib_voiceRecding.setPivotX(Ib_voiceRecding.getWidth() >> 1);
					Ib_voiceRecding.setPivotY(Ib_voiceRecding.getHeight() >> 1);
					// ��ʼ¼��
					String voiceName = System.currentTimeMillis() + "";// ¼���ļ���
					tempVoicePath = voiceName;
					Log.i("--", "����voiceName = " + voiceName);
//					Toast.makeText(context, "��ʼ¼��", 0).show();
					SoundUtil.getInstance().startRecord(context, voiceName);
					timeTh.start();// ��ʱ��ʼ
					break;
				case MotionEvent.ACTION_UP:
//					MainActivity.playSound(3);//����������ʾ
					Ib_voiceRecding.setScaleX(1.0f);
					Ib_voiceRecding.setScaleY(1.0f);
					Ib_voiceRecding.setPivotX(Ib_voiceRecding.getWidth() >> 1);
					Ib_voiceRecding.setPivotY(Ib_voiceRecding.getHeight() >> 1);
					
					// ֹͣ¼��
//					Toast.makeText(context, "ֹͣ¼��", 0).show();
					timeTh.stopRec();
					SoundUtil.getInstance().stopRecord();
					Log.i("--", "¼��ʱ��=" + voiceLong + "��");
					if (voiceLong < 1) {
						SoundUtil.getInstance().deleteVoiceFile();
					} else {

						/********************* ����������Ϣ *******************************************/
						// �������øղ�¼����byte[]��׼������,����ʾ����Ϣ�б�
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

						AddMsgClientitem(msgClient);// ��ʾ��
						MainActivity.myBinder.sendCharMsg(voiceMsg);// ����

						/*** ���浽���ݿ� **/
						db.insertMsg(
								convertionUtil.ConvertClient2Db(msgClient),
								friendId);

					}
					voiceLong = 0;
					Tv_voiceRecding.setText("�����ʼ¼��");
					break;
				}
				return false;
			}
		});
		isVoiceWindowInited = true;
	}

	/** ¼����ʱ�� */
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

	// ��¼������
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

	// �ر�¼������
	private void closeVoiceWindow() {
		Ll_voiceRecding.setVisibility(View.GONE);
		ll_popwindow.setVisibility(View.GONE);
		isVoiceWindowOpened = false;
	}

	// �л��򿪹ر�¼������
	private void toggleVoiceWindow() {
		if (isVoiceWindowOpened)
			closeVoiceWindow();
		else
			openVoiceWindow();
	}

	// ��ʼ��MoreSelectWindow
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
					// ͼƬ
					getFromGalery();
					break;
				case 1:
					// ����
					takePhoto();
					break;
				
				case 2:
					// �����ҵľ���λ��
					iMoMoMsg locMsg = new iMoMoMsg();
					locMsg.symbol = '+';
					JSONObject locJson = new JSONObject();
					locJson.put(MsgKeys.msgType,
							iMoMoMsgTypes.CHATING_TEXT_MSG);
					locJson.put(MsgKeys.userId,
							ClientManager.clientId);
					locJson.put(MsgKeys.friendId, friendId);
					locJson.put(MsgKeys.msgCotent, "�ҵ�λ��:" + ClientManager.myLocation);
					locJson.put(MsgKeys.sendTime, ClientUtils.getNowTime());
					if(isgroupMsg){
						locJson.put(MsgKeys.isGroupMsg, iMoMoMsgTypes.GROUP_MSG);
					}
					locMsg.msgJson = locJson.toJSONString();
					// ��ʾ��
					iMoMoMsgClient msgClient = convertionUtil
							.Convert_Net2Client(context,
									locMsg, false);
					AddMsgClientitem(msgClient);
					MainActivity.myBinder.sendCharMsg(locMsg);// ����

					/*** ���浽���ݿ� **/
					iMoMoMsgDb msgDb = convertionUtil
							.Convert_Net2Db(context, locMsg,
									0, 1);
					db.insertMsg(msgDb, friendId);
					
					break;	
				
				case 4:
					// ����ʶ��
					Bundle params = new Bundle();
					// !!!!!!!!!!!!!!!!!!!!!!!!!!!
					// ע�⣺
					// �������Ϊ�Լ�Ӧ�ö�Ӧ��key����ʾ�õ�key��������
					// !!!!!!!!!!!!!!!!!!!!!!!!!!!
					params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
							"VGc2Ih0kOEzi7Gmf1rW0pU1M");
					params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
							"xxPXsb2GVT4nhWDwljwiSNyHoM6F6NEn");
					params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
							BaiduASRDigitalDialog.THEME_GREEN_DEEPBG);// ���öԻ�����
					mBaiduASRDigitalDialog = new BaiduASRDigitalDialog(context,
							params);
					mBaiduASRDigitalDialog
							.setDialogRecognitionListener(new DialogRecognitionListener() {
								/** �ص�����, */
								@Override
								public void onResults(Bundle results) {
									ArrayList<String> list = results
											.getStringArrayList(RESULTS_RECOGNITION);
									String str = Arrays
											.toString(null == list ? null
													: list.toArray());
									/******************************* ��������ʶ��������Ϣ����Ϣ ******************************************/
									String content = str.substring(1,
											str.length() - 2);// ʶ����������Ϣ
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
									// ��ʾ��
									iMoMoMsgClient msgClient = convertionUtil
											.Convert_Net2Client(context,
													textMsg, false);
									AddMsgClientitem(msgClient);
									MainActivity.myBinder.sendCharMsg(textMsg);// ����

									/*** ���浽���ݿ� **/
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

	/** ������ȡ */
	public void getFromGalery() {
		Intent intent = new Intent();
		// ����ϵͳͼ�⣬ѡ��һ��ͼƬ//�����ַ�ʽѡ����ṩ�����߸��࣬ǰ��ֻ��ͼ�� ���ļ�����
		// Intent intent = new Intent(Intent.ACTION_PICK);
		/* ����Pictures����Type�趨Ϊimage */
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	// ����
	public void takePhoto() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// �жϴ洢���Ƿ�����ã����ý��д洢
		if (FileUtils.hasSdcard()) {
			PHOTO_FILE_NAME = System.currentTimeMillis() + ".jpg";
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		} else {
			Toast.makeText(context, "δ�����ڴ濨", 0).show();
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
					// �õ�ͼƬ��ȫ·��
					Uri uri = data.getData();
					openPreviewDialog(uri);
				}
			}
		}
	}

	// ͼƬԤ���Ի���
	private void openPreviewDialog(Uri uri) {
		// ���ԣ�����һ������ͼƬ�ĶԻ���(ȫ����ʾ)
		final Dialog previewDialog = new Dialog(context,
				R.style.Dialog_Fullscreen);
		previewDialog.setCanceledOnTouchOutside(true); // ���������λ����ʧ
		Window window = previewDialog.getWindow();
		// ������ʾ����
		window.setWindowAnimations(R.style.dialog_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		previewDialog.onWindowAttributesChanged(wl);// ���öԿ�򶯻�����
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
				// ����ͼƬ��Ϣ
				// ...
				Bitmap bitmap = ((BitmapDrawable) Iv_takenPic.getDrawable())
						.getBitmap();
				bitmap = Bitmap.createScaledBitmap(bitmap, 480, 800, true);
				int size = bitmap.getWidth() * bitmap.getHeight();
				ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
				// ����λͼ��ѹ����ʽ������Ϊ100%���������ֽ������������
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
				// ����ʱҪ�������,�ͻ��˴���ֻ�ñ���ͼƬ��path
				// msg.setMsgBytes(baos.toByteArray());
				// ���浽�����ļ�
				String imagePath = IMAGEPATH + ClientManager.clientId + "_"
						+ friendId + "_" + System.currentTimeMillis() + ".png";
				ImageUtil.getInstance().saveImage(context, baos.toByteArray(),
						imagePath);
				// PictureUtil.getSmallBitmap(imagePath);
				/******************************* ����ͼƬ��Ϣ ******************************************/
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

				AddMsgClientitem(msgClient);// ��ʾ��
				MainActivity.myBinder.sendCharMsg(imageMsg);// ����
				/*** ���浽���ݿ� **/
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

	// �򿪸���ѡ���
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

	// �رո���ѡ���
	private void closeMoreSelectWindow() {
		// if(ll_popwindow != null )
		ll_popwindow.setVisibility(View.GONE);
		moreselect_gridView.setVisibility(View.GONE);
		isMoreSelectWindowOpened = false;
		IbSend_moreselect
				.setBackgroundResource(R.drawable.more_select_button_selector);
	}

	// �л�MoreSelectwindow
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
					/**************************** ����������Ϣ ******************************************/
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

					// ��ʾ��
					iMoMoMsgClient msgClient = convertionUtil
							.Convert_Net2Client(context, textMsg, false);
					AddMsgClientitem(msgClient);
					MainActivity.myBinder.sendCharMsg(textMsg);// ����
					Et_msgEdit.setText("");

					/*** ���浽���ݿ� */
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
	 * ������Ϣ��������ʾ����
	 * 
	 * @param msgClient
	 */
	public void AddMsgClientitem(iMoMoMsgClient msgClient) {
		msgList.add(msgClient);
		cotentAdapter.notifyDataSetChanged();
		Lv_msgCotent.setSelection(cotentAdapter.getCount() - 1);
	}

	/**
	 * ��ոú��ѵ������¼
	 */
	public static void clearMsg(){
		msgList.clear();
		cotentAdapter.notifyDataSetChanged();
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBaiduASRDigitalDialog != null) {
			mBaiduASRDigitalDialog.dismiss();// �ͷ���Դ
		}
		MainActivity.mp0.setChatingId("");// û����������ĺ���
		updateLaticeChat();

	}

	/**
	 * ���������ϵ�����ݿ�
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
				content = "[ͼƬ]";
			} else if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
				content = "[����]";
			} else if (type == iMoMoMsgTypes.CHATING_TEXT_MSG) {
				content = json.getString(MsgKeys.msgCotent);
			}
			
			entity.setFriendId(friendId);
			entity.setFriendName(friendName);
			entity.setChatContent(content);
			entity.setChatCreatTime(json.getString(MsgKeys.sendTime));
			entity.setMsg_num(0);
			entity.setMsgtype(0);
			
			// ���ݿ���ˣ���ʱ�����ϵ�˽���Ҳ�ø���, �Ѿ����ڣ��������
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
