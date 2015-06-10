package com.example.hellostranger.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.mina.core.session.IoSession;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.LocationClient;
import com.chat.ImageUtil;
import com.clientUtils.ClientManager;
import com.clientUtils.ClientService;
import com.clientUtils.ClientService.MyBinder;
import com.clientUtils.ClientUtils;
import com.clientUtils.MsgConvertionUtil;
import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.GameActivity;
import com.example.hellostranger.R;
import com.example.hellostranger.app.MyApplication;
import com.example.hellostranger.bean.ChatInfoEntity;
import com.example.hellostranger.bean.InvitationInfoEntity;
import com.example.hellostranger.fragment_main.MainPart0;
import com.example.hellostranger.fragment_main.MainPart1;
import com.example.hellostranger.fragment_main.MainPart2;
import com.example.hellostranger.util.SharePreferenceUtil;
import com.example.hellostranger.util.myDialog;
import com.freind.FriendBean;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;
import com.strangerlist.StrangerBean;
import com.strangerlist.StrangerListActivity;

public class MainActivity extends Activity {
	// ////////////////////////////λ�ü���ģ����ر���//////////////////////////////////////////
	LocationClient mLocClient;
	// //////////////////////////////////////////////////////////////////////

	SharePreferenceUtil mSpUtil;

	GestureDetector gestureDetector;
	MyOnClickListener myOnClickListener;
	private RelativeLayout topView;
	private RelativeLayout bottonView;
	ImageView ib_part0;
	ImageView ib_part1;
	ImageView ib_part2;
	Fragment[] fragmentarray;
	public static MainPart0 mp0;
	public static MainPart1 mp1;
	public static MainPart2 mp2;
	Button btn_send;

	// //////////////////////////////�����///////////////////////
	RelativeLayout rl_head;
	static ImageView main_iv_head;// ͷ��
	static TextView main_tv_nick;// �ǳ�
	static TextView main_tv_sign;// ����ǩ��
	RelativeLayout rl_loginout;
	RelativeLayout rl_exit;
	Button btn_setting;
	Button btn_reback;
	Button btn_report;

	// ///////////////////////////////////���////////////////////////////////////////
	public boolean isStart = false;
	private boolean canShake = false;

	public static MainActivity context;// �Ķ���������
	public static IoSession ClientSession;// �����ӻỰ
	public static MyBinder myBinder;// service����

	private static NotificationManager notifier;

	private long mExitTime;// ��������˳�

	// public static NotifyUtils notifyUtils;//��ʾ�����𶯹���

	public static SoundPool soundPool = null;// ���Ŷ�С������
	private static int msgcoming;// ��Ϣ���˵���ʾ��
	private static int voicerec;// ¼����ʾ
	private static int voicesend;// ����������ʾ
	
	
	private static myDialog initDialog;//��ʼ���Ի���

	// service����
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("--", "MainActivity-->onServiceConnected");
			myBinder = (ClientService.MyBinder) service;
			myBinder.ConnectServer();
			initDialog.show();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// Log.i("--", "MainActivity-->onServiceDisconnected");
		}
	};

	public static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StaticValues.SESSION_OPENED:
				myBinder.SuperLogin();// ������¼
				break;

			case iMoMoMsgTypes.LOGIN_SUPER_HEAD:
				// ��¼�ɹ����õ������ӣ���ȡ���û������Ϣ
				Toast.makeText(context, "���ӳɹ�", 0).show();
				ClientUtils.getInstance().saveClientInfo(context, msg);
				initPinfo();
				myBinder.getFriendIdList();// ���������ú����б�,,,��¼�ɹ���������
				// mp0.updateHeader();// �ж��Ƿ����ӷ������ˣ�
				initDialog.dismiss();
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_NOHEAD:
				// ��¼�ɹ����õ������ӣ���ȡ���û������Ϣ
				Toast.makeText(context, "���ӳɹ�", 0).show();
				ClientUtils.getInstance().saveClientInfo(context, msg);
				initPinfo();
				myBinder.getFriendIdList();// ���������ú����б�,,,��¼�ɹ���������
				// mp0.updateHeader();// �ж��Ƿ����ӷ������ˣ�
				initDialog.dismiss();
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_FAILED:
				// ��¼�ɹ����õ������ӣ���ȡ���û������Ϣ
				Toast.makeText(context, "�ͷ���������ʧ��", 0).show();
				initDialog.dismiss();
				break;

			case iMoMoMsgTypes.NO_STRANGERS:
				// ��Χ����İ����
				Toast.makeText(context, "��Χ����İ����", 0).show();
				break;

			case iMoMoMsgTypes.STRANGERS_LIST_ONEKM:
				// ��Χİ�����б�
				iMoMoMsg moMsg = (iMoMoMsg) msg.obj;
				List<StrangerBean> strangerList = MsgConvertionUtil
						.getInstance().getStrangerList(moMsg);
				mp2.setStrangerLoc(strangerList);
				// for (StrangerBean strangerBean : strangerList) {
				// Log.i("--", "İ����: " + strangerBean.toString());
				// }
				break;
			case iMoMoMsgTypes.STRANGERS_LIST_MORE:
				// ��Χİ�����б�
				iMoMoMsg moMsg2 = (iMoMoMsg) msg.obj;
				List<StrangerBean> strangerListmore = MsgConvertionUtil
						.getInstance().getStrangerList(moMsg2);
				Message listMore = new Message();
				listMore.what = iMoMoMsgTypes.STRANGERS_LIST_MORE;
				listMore.obj = strangerListmore;
				StrangerListActivity.handler.sendMessage(listMore);
				break;

			case iMoMoMsgTypes.GET_STRANGER_INFO:
				// İ���˸�����Ϣ
				Message info = new Message();
				info.what = iMoMoMsgTypes.GET_STRANGER_INFO;
				info.obj = msg.obj;
				FriendAddActivity.handler.sendMessage(info);
				break;

			case iMoMoMsgTypes.ADD_FRIEND_SUCCESS:
				// ��Ӻ��ѳɹ�:
				Message notify1 = new Message();
				notify1.what = iMoMoMsgTypes.ADD_FRIEND_SUCCESS;
				FriendAddActivity.handler.sendMessage(notify1);
				break;

			case iMoMoMsgTypes.ADD_FRIEND_FAILED:
				// ���ʧ��:
				Message notify2 = new Message();
				notify2.what = iMoMoMsgTypes.ADD_FRIEND_FAILED;
				FriendAddActivity.handler.sendMessage(notify2);
				break;

			case iMoMoMsgTypes.FRIEND_ID_LIST:
				// ����Id�б�:
				JSONObject json = JSON.parseObject(msg.obj.toString());
				String IdStr = json.getString(MsgKeys.friendIdList);
				myBinder.getFriendList(IdStr);// ��ú����б�GETA_FRIEND_INFO_HEAD
				break;

			case iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD:
				FriendBean bean = MsgConvertionUtil.getInstance()
						.getFriendBean(context, (iMoMoMsg) msg.obj);
				mp1.AddAFriend(bean);
				Log.i("--", "���� ��" + bean.toString());
				break;

			case iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD:
				FriendBean bean2 = MsgConvertionUtil.getInstance()
						.getFriendBean(context, (iMoMoMsg) msg.obj);
				mp1.AddAFriend(bean2);
				Log.i("--", "���� ��" + bean2.toString());
				break;

			case iMoMoMsgTypes.ADD_FRIEND:
				iMoMoMsg addF = (iMoMoMsg) msg.obj;
				JSONObject addFjson = JSON.parseObject(addF.msgJson);
				Notification notification = new Notification(R.drawable.imomo2,
						"��Ӻ���", System.currentTimeMillis());
				Intent notificationIntent = new Intent(context,
						MainActivity.class);
				notification
						.setLatestEventInfo(context, "��Ӻ���",
								addFjson.getString(MsgKeys.friendName)
										+ "�����Ϊ����", null);
				notifier.notify(0, notification);
				FriendBean addbean = MsgConvertionUtil.getInstance()
						.getFriendBean(context, addF);
				mp1.AddAFriend(addbean);
				// Longvibrate();// ��
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.CHATING_IMAGE_MSG:
				// ͼƬ��Ϣ:
				HandleChatMsg(msg);
				// �жϣ��� ����ʾ��
				// Longvibrate();// ��
				// playSound(1);// ��ʾ��
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.CHATING_VOICE_MSG:
				// ������Ϣ:
				HandleChatMsg(msg);
				// �жϣ��� ����ʾ��
				// Longvibrate();// ��
				// playSound(1);// ��ʾ��
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.CHATING_TEXT_MSG:
				// ������Ϣ:
				HandleChatMsg(msg);
				// �жϣ��� ����ʾ��
				// Longvibrate();// ��
				// playSound(1);// ��ʾ��
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.RESET_PASSWD_SUCCESS:
				Toast.makeText(context, "�޸�����ɹ�", 0).show();
				break;

			case iMoMoMsgTypes.CREATE_GROUP_SUCCESS:
				Notification noti = new Notification(R.drawable.imomo2, "����Ⱥ��",
						System.currentTimeMillis());
				noti.setLatestEventInfo(context, "����Ⱥ��", "Ⱥ�鴴���ɹ�", null);
				notifier.notify(0, noti);
				createGroupSuccess(msg);
				break;

			case iMoMoMsgTypes.CREATE_GROUP_FAILED:
				Notification noti2 = new Notification(R.drawable.imomo2,
						"����Ⱥ��", System.currentTimeMillis());
				// Intent noti2Intent = new Intent(context, MainActivity.class);
				noti2.setLatestEventInfo(context, "����Ⱥ��", "Ⱥ�鴴��ʧ��", null);
				notifier.notify(0, noti2);
				break;

			case iMoMoMsgTypes.INVITE_TO_GROUP:
				iMoMoMsg invite = (iMoMoMsg) msg.obj;
				handleSystemMsg(msg);
				break;

			case iMoMoMsgTypes.RESET_HEAD:
				iMoMoMsg resetHead = (iMoMoMsg) msg.obj;
				JSONObject resetHeadJson = JSON.parseObject(resetHead.msgJson);
				String freiandId = resetHeadJson.getString(MsgKeys.friendId);
				ImageUtil.getInstance().saveImage(context, resetHead.msgBytes,
						StaticValues.USER_HEADPATH + freiandId + ".png");
				break;

			case iMoMoMsgTypes.CONNECT_DOWN:
				// �ͷ��������ӶϿ�
				ClientManager.isOnline = false;
				Notification down = new Notification(R.drawable.imomo2, "֪ͨ",
						System.currentTimeMillis());
				// Intent downIntent = new Intent(context, MainActivity.class);
				down.setLatestEventInfo(context, "�������Ͽ�����", "�������Ͽ�����,��������",
						null);
				notifier.notify(0, down);
				break;
			}
		};
	};

	/**
	 * ����Ϣ����
	 */
	public static void MsgComeNotify() {
		if (ClientManager.isRing) {
			playSound(1);// ��ʾ��
			Log.i("--", "��ʾ��");
		}

		if (ClientManager.isVibration) {
			Longvibrate();
			Log.i("--", "��");
		}
	}

	/**
	 * ����Ⱥ��ɹ�,���һ��Ⱥ�����쵽�����ϵ����
	 */
	public static void createGroupSuccess(Message msg) {
		iMoMoMsg moMsg = (iMoMoMsg) msg.obj;
		JSONObject json = JSONObject.parseObject(moMsg.msgJson);
		String groupId = json.getString(MsgKeys.groupId);
		String groupName = json.getString(MsgKeys.groupName);
		String groupTopic = json.getString(MsgKeys.groupTopic);
		String groupIconPath = StaticValues.USER_HEADPATH + groupId + ".png";
		ImageUtil.getInstance().saveImage(context, moMsg.msgBytes,
				groupIconPath);

		ChatInfoEntity groupChat = new ChatInfoEntity();
		groupChat.setFriendId(groupId);
		groupChat.setFriendName(groupName);
		groupChat.setChatContent("");
		groupChat.setChatCreatTime("");
		groupChat.setMsg_num(0);
		groupChat.setMsgtype(2);
		MainActivity.mp0.AddLaticeChatItem(groupChat);
	}

	/**
	 * ����ϵͳ��Ϣ
	 * 
	 * @param msg
	 */
	public static void handleSystemMsg(Message msg) {
		iMoMoMsg moMsg = (iMoMoMsg) msg.obj;
		JSONObject json = JSONObject.parseObject(moMsg.msgJson);
		iMoMoDataBase db = new iMoMoDataBase(context);
		String systemId = "100000";
		if (mp0.isExistsInLatice(systemId)) {
			mp0.updateLaticeItem(systemId, ClientUtils.getNowTime(),
					"�к�������������Ⱥ������", false);
		} else {
			// ��ӵ������ϵ����
			ChatInfoEntity entity = new ChatInfoEntity();
			entity.setChatContent("�к�������������Ⱥ������");
			entity.setFriendId(systemId);// ϵͳ��ϢId
			entity.setFriendName("ϵͳ��Ϣ");
			entity.setChatCreatTime(ClientUtils.getNowTime());
			entity.setMsg_num(1);
			entity.setMsgtype(1);
			mp0.AddLaticeChatItem(entity);
		}

		InvitationInfoEntity invitation = MsgConvertionUtil.getInstance()
				.getInvitFromNet(context, moMsg);
		if (!db.isTableExists("invitations")) {
			db.createInviteTable();
		}
		db.insertInvite(invitation);
	}

	/**
	 * ������յ�������Ϣ
	 * 
	 * @param msg
	 */
	public static void HandleChatMsg(Message msg) {
		iMoMoMsg moMsg = (iMoMoMsg) msg.obj;
		JSONObject json = JSONObject.parseObject(moMsg.msgJson);
		String friendId = json.getString(MsgKeys.userId);// ����ʱfriendIdӦ����userId
		iMoMoDataBase db = new iMoMoDataBase(context);
		int type = json.getIntValue(MsgKeys.msgType);
		/** �жϸú����Ƿ��������ϵ���� */
		if (mp0.isExistsInLatice(friendId)) {
			/** �ж��Ƿ��������� */
			if (mp0.getChatingId().equals(friendId)) {
				// ��������
				ChatActivity.AddMsgItem(MsgConvertionUtil.getInstance()
						.Convert_Net2Client(context, moMsg, true));
				if (!db.isTableExists("msg" + ClientManager.clientId + "_"
						+ friendId)) {
					// ������
					db.createMsgTable(friendId);
				}
				db.insertMsg(
						MsgConvertionUtil.getInstance().Convert_Net2Db(context,
								moMsg, 1, 1), friendId);

			} else {
				// ��������
				if (!db.isTableExists("msg" + ClientManager.clientId + "_"
						+ friendId)) {
					// ������
					db.createMsgTable(friendId);
				}
				db.insertMsg(
						MsgConvertionUtil.getInstance().Convert_Net2Db(context,
								moMsg, 1, 0), friendId);
				// ���������ϵ���б�
				String content = "";
				if (type == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
					content = "[ͼƬ]";
				} else if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
					content = "[����]";
				} else if (type == iMoMoMsgTypes.CHATING_TEXT_MSG) {
					content = json.getString(MsgKeys.msgCotent);
				}
				mp0.updateLaticeItem(friendId,
						json.getString(MsgKeys.sendTime), content, false);
			}
		} else {
			// ���������ϵ����,��Ϣ���浽���ݿ�
			if (!db.isTableExists("msg" + ClientManager.clientId + "_"
					+ friendId)) {
				// ������
				db.createMsgTable(friendId);
			}
			db.insertMsg(
					MsgConvertionUtil.getInstance().Convert_Net2Db(context,
							moMsg, 1, 0), friendId);

			// ��ӵ������ϵ����
			ChatInfoEntity entity = new ChatInfoEntity();
			if (type == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
				entity.setChatContent("[ͼƬ]");
			} else if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
				entity.setChatContent("[����]");
			} else if (type == iMoMoMsgTypes.CHATING_TEXT_MSG) {
				entity.setChatContent(json.getString(MsgKeys.msgCotent));
			}
			entity.setFriendId(friendId);
			entity.setFriendName(mp1.getFriendName(friendId));
			entity.setChatCreatTime(json.getString(MsgKeys.sendTime));
			entity.setMsg_num(1);
			int isGroupMsg = 0;
			if (json.containsKey(MsgKeys.isGroupMsg)) {
				isGroupMsg = json.getInteger(MsgKeys.isGroupMsg);
			}
			if (isGroupMsg == 0) {
				// ����������Ϣ
				entity.setMsgtype(0);
			} else if (isGroupMsg == iMoMoMsgTypes.GROUP_MSG) {
				// ����������Ϣ
				entity.setMsgtype(2);
			}
			mp0.AddLaticeChatItem(entity);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		context = this;
		ClientUtils.getInstance().getClientuserEmail(context);// ��ʼ���û�userEmail
		// �����������ӷ�����
		Intent serviceIntent = new Intent(this, ClientService.class);
		Intent bindIntent = new Intent(this, ClientService.class);
		bindService(bindIntent, connection, BIND_AUTO_CREATE);
//		Log.i("--", "������bindService");
		initView();
		initFragment();
		initMap();

		// ��ʼ�����ֳ�
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		// �����ֳ����������
		msgcoming = soundPool.load(getApplicationContext(), R.raw.msgcoming, 0);
		voicerec = soundPool.load(getApplicationContext(), R.raw.voicerec, 0);
		voicesend = soundPool.load(getApplicationContext(), R.raw.voicesend, 0);

		
		initDialog = new myDialog(context, "������...");
	}

	/**
	 * ���¸�����Ϣ(�����)
	 */
	public static void refreshPinfo(int type, String value) {
		switch (type) {
		case iMoMoMsgTypes.RESET_USERNAME:
			main_tv_nick.setText(value);
			break;
		case iMoMoMsgTypes.RESET_SIGNATUE:
			main_tv_sign.setText(value);
			break;
		case iMoMoMsgTypes.RESET_HEAD:
			main_iv_head.setImageURI(Uri.fromFile(new File(value)));
			Log.i("--", "�����ͷ���޸ĳɹ�");
			break;
		}
	}

	/**
	 * ��ʼ��������Ϣ�������
	 */
	public static void initPinfo() {
		main_tv_nick.setText(ClientManager.clientName);
		main_tv_sign.setText(ClientManager.personSignature);
		main_iv_head.setImageURI(Uri
				.fromFile(new File(StaticValues.USER_HEADPATH
						+ ClientManager.clientEmail + ".png")));
		
		
	}

	private void initView() {
		myOnClickListener = new MyOnClickListener();
		// topView = (RelativeLayout) findViewById(R.id.main_layout_main);
		// bottonView = (RelativeLayout) findViewById(R.id.main_layout_edge);
		ib_part0 = (ImageView) findViewById(R.id.main_ib_part0);
		ib_part1 = (ImageView) findViewById(R.id.main_ib_part1);
		ib_part2 = (ImageView) findViewById(R.id.main_ib_part2);

		ib_part0.setOnClickListener(myOnClickListener);
		ib_part1.setOnClickListener(myOnClickListener);
		ib_part2.setOnClickListener(myOnClickListener);

		main_tv_nick = (TextView) findViewById(R.id.main_tv_nick);
		main_tv_sign = (TextView) findViewById(R.id.main_tv_sign);
		/**
		 * ��ת��������Ϣ
		 */
		main_iv_head = (ImageView) findViewById(R.id.main_iv_head);
		main_iv_head.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						PersonalInfoActivity.class);
				startActivity(intent);
			}
		});
		/**
		 * ע��
		 */
		rl_loginout = (RelativeLayout) findViewById(R.id.main_rl_loginout);
		rl_loginout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("ע��").setMessage("ȷ��ע��?");
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(context,
										SignInActivity.class);
								startActivity(intent);
								finish();
							}
						});
				builder.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				dialog = builder.create();
				dialog.show();
			}
		});
		/**
		 * �˳�
		 */
		rl_exit = (RelativeLayout) findViewById(R.id.main_rl_exit);
		rl_exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("�˳�").setMessage("ȷ���˳�?");
				builder.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// �Ͽ�����
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						});
				builder.setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				dialog = builder.create();
				dialog.show();
			}
		});
		/**
		 * ����
		 */
		btn_setting = (Button) findViewById(R.id.main_btn_setting);
		btn_setting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						SettingActivity.class);
				startActivity(intent);
			}
		});
		/***
		 * ����
		 */
		btn_reback = (Button) findViewById(R.id.main_btn_reback);
		btn_reback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						RebackActivity.class);
				startActivity(intent);
			}
		});

		btn_report = (Button) findViewById(R.id.main_btn_report);

		mSpUtil = new SharePreferenceUtil(context);
		// ��ʼ����������
		initSettings();
		boolean isReported = mSpUtil.getIsReport();
		String ReportDate = mSpUtil.getReportDate();
		if(ReportDate.equals(new Date()) && isReported){
			isReported = true;
		}
		if (!isReported) {
//			System.out.println("ûǩ��");
			btn_report.setBackgroundResource(R.drawable.bg_btn_mini);
			btn_report.setTextColor(Color.WHITE);
			btn_report.setText("ǩ��");
		} else {
//			System.out.println("��ǩ��");
			btn_report.setBackgroundResource(R.drawable.bg_btn_mini_nor);
			btn_report.setTextColor(Color.rgb(128, 128, 128));
			btn_report.setText("��ǩ��");
		}

		btn_report.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ClientManager.isOnline) {
					if (mSpUtil.getIsReport()) {
						Toast.makeText(context, "��ǩ��", 0)
								.show();
					} else {
						mSpUtil.setIsReport(true);
						mSpUtil.setReportDate();
						btn_report
								.setBackgroundResource(R.drawable.bg_btn_mini_nor);
						btn_report.setTextColor(Color.rgb(128, 128, 128));
						btn_report.setText("��ǩ��");
						myBinder.handleVitality(iMoMoMsgTypes.SIGN);
						Toast.makeText(context, "����+15", 0)
								.show();
						ClientManager.vitalityValue += 15;
					}
				} else {
					Toast.makeText(context, "����������״̬���޷�ǩ��", 0).show();
				}

			}
		});
	}

	/**
	 * ��ʼ����������
	 */
	public void initSettings() {
		ClientManager.isRing = mSpUtil.getIsRing();
		ClientManager.isVibration = mSpUtil.getIsVibration();
		ClientManager.isShareLoc = mSpUtil.getIsShareLoc();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocClient != null)
			mLocClient.stop();
		if (mLocClient != null)
			mLocClient.stop();
		// �Ͽ�����
		if (connection != null)
			unbindService(connection);
	}

	private void initFragment() {
		fragmentarray = new Fragment[3];
		mp0 = new MainPart0();
		mp1 = new MainPart1();
		mp2 = new MainPart2();
		fragmentarray[0] = mp0;
		fragmentarray[1] = mp1;
		fragmentarray[2] = mp2;
		fragmentarray[0].setRetainInstance(true);
		fragmentarray[1].setRetainInstance(true);
		fragmentarray[2].setRetainInstance(true);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.main_layout_mid, fragmentarray[0]);
		ft.add(R.id.main_layout_mid, fragmentarray[1]);
		ft.add(R.id.main_layout_mid, fragmentarray[2]);
		ft.hide(fragmentarray[1]);
		ft.hide(fragmentarray[2]);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
	}

	private void initMap() {
		mLocClient = ((MyApplication) getApplication()).mLocationClient;
		mLocClient.start();
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			canShake = false;
			ImageView ib = (ImageView) v;
			ib_part0.setImageResource(R.drawable.icon_chat_home);
			ib_part1.setImageResource(R.drawable.icon_myfriend_nor);
			ib_part2.setImageResource(R.drawable.icon_find_nor);
			if (ib.equals(ib_part0)) {
				System.out.println("���ģ����");
				ib_part0.setImageResource(R.drawable.icon_chat_home_press);
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.hide(fragmentarray[1]);
				ft.hide(fragmentarray[2]);
				ft.show(fragmentarray[0]);
				ft.setTransition(FragmentTransaction.TRANSIT_NONE);
				ft.commit();
			}
			if (ib.equals(ib_part1)) {
				System.out.println("���ģ��һ");
				ib_part1.setImageResource(R.drawable.icon_myfriend_press);
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.hide(fragmentarray[0]);
				ft.hide(fragmentarray[2]);
				ft.show(fragmentarray[1]);
				ft.setTransition(FragmentTransaction.TRANSIT_NONE);
				ft.commit();

			}
			if (ib.equals(ib_part2)) {
				System.out.println("���ģ���");
				ib_part2.setImageResource(R.drawable.icon_find_press);
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.hide(fragmentarray[0]);
				ft.hide(fragmentarray[1]);
				ft.show(fragmentarray[2]);
				ft.setTransition(FragmentTransaction.TRANSIT_NONE);
				ft.commit();
				isStart = true;
				// if (mlocation!=null)
				// mp2.setMyLoc(mlocation);
			}
		}

	}

	/**
	 * ���������ϵ���б���� , (not ok)
	 */
	public void LocToMp0() {
		ib_part0.setImageResource(R.drawable.icon_chat_home_press);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.hide(fragmentarray[1]);
		ft.hide(fragmentarray[2]);
		ft.show(fragmentarray[0]);
		ft.setTransition(FragmentTransaction.TRANSIT_NONE);
		ft.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�",
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ������Ϣ��ʾ��
	 */
	public static void playSound(int id) {
		switch (id) {
		case 1:
			soundPool.play(msgcoming, 1, 1, 0, 0, 1);
			break;
		case 2:
			soundPool.play(voicerec, 1, 1, 0, 0, 1);
			break;
		case 3:
			soundPool.play(voicesend, 1, 1, 0, 0, 1);
			break;
		}
	}

	/**
	 * ��(����)
	 */
	public static void Longvibrate() {
		// ��
		Vibrator vibe = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(500);
	}

}
