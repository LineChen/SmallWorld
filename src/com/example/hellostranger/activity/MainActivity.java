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
	// ////////////////////////////位置监听模块相关变量//////////////////////////////////////////
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

	// //////////////////////////////侧边栏///////////////////////
	RelativeLayout rl_head;
	static ImageView main_iv_head;// 头像
	static TextView main_tv_nick;// 昵称
	static TextView main_tv_sign;// 个性签名
	RelativeLayout rl_loginout;
	RelativeLayout rl_exit;
	Button btn_setting;
	Button btn_reback;
	Button btn_report;

	// ///////////////////////////////////标记////////////////////////////////////////
	public boolean isStart = false;
	private boolean canShake = false;

	public static MainActivity context;// 改动。。。。
	public static IoSession ClientSession;// 长连接会话
	public static MyBinder myBinder;// service绑定器

	private static NotificationManager notifier;

	private long mExitTime;// 点击两次退出

	// public static NotifyUtils notifyUtils;//提示音或震动工具

	public static SoundPool soundPool = null;// 播放短小的音乐
	private static int msgcoming;// 消息来了的提示音
	private static int voicerec;// 录音提示
	private static int voicesend;// 发送语音提示
	
	
	private static myDialog initDialog;//初始化对话框

	// service连接
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
				myBinder.SuperLogin();// 超级登录
				break;

			case iMoMoMsgTypes.LOGIN_SUPER_HEAD:
				// 登录成功，得到长连接，获取该用户相关信息
				Toast.makeText(context, "连接成功", 0).show();
				ClientUtils.getInstance().saveClientInfo(context, msg);
				initPinfo();
				myBinder.getFriendIdList();// 发送请求获得好友列表,,,登录成功后再请求
				// mp0.updateHeader();// 判断是否连接服务器了，
				initDialog.dismiss();
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_NOHEAD:
				// 登录成功，得到长连接，获取该用户相关信息
				Toast.makeText(context, "连接成功", 0).show();
				ClientUtils.getInstance().saveClientInfo(context, msg);
				initPinfo();
				myBinder.getFriendIdList();// 发送请求获得好友列表,,,登录成功后再请求
				// mp0.updateHeader();// 判断是否连接服务器了，
				initDialog.dismiss();
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_FAILED:
				// 登录成功，得到长连接，获取该用户相关信息
				Toast.makeText(context, "和服务器连接失败", 0).show();
				initDialog.dismiss();
				break;

			case iMoMoMsgTypes.NO_STRANGERS:
				// 周围暂无陌生人
				Toast.makeText(context, "周围暂无陌生人", 0).show();
				break;

			case iMoMoMsgTypes.STRANGERS_LIST_ONEKM:
				// 周围陌生人列表
				iMoMoMsg moMsg = (iMoMoMsg) msg.obj;
				List<StrangerBean> strangerList = MsgConvertionUtil
						.getInstance().getStrangerList(moMsg);
				mp2.setStrangerLoc(strangerList);
				// for (StrangerBean strangerBean : strangerList) {
				// Log.i("--", "陌生人: " + strangerBean.toString());
				// }
				break;
			case iMoMoMsgTypes.STRANGERS_LIST_MORE:
				// 周围陌生人列表
				iMoMoMsg moMsg2 = (iMoMoMsg) msg.obj;
				List<StrangerBean> strangerListmore = MsgConvertionUtil
						.getInstance().getStrangerList(moMsg2);
				Message listMore = new Message();
				listMore.what = iMoMoMsgTypes.STRANGERS_LIST_MORE;
				listMore.obj = strangerListmore;
				StrangerListActivity.handler.sendMessage(listMore);
				break;

			case iMoMoMsgTypes.GET_STRANGER_INFO:
				// 陌生人个人信息
				Message info = new Message();
				info.what = iMoMoMsgTypes.GET_STRANGER_INFO;
				info.obj = msg.obj;
				FriendAddActivity.handler.sendMessage(info);
				break;

			case iMoMoMsgTypes.ADD_FRIEND_SUCCESS:
				// 添加好友成功:
				Message notify1 = new Message();
				notify1.what = iMoMoMsgTypes.ADD_FRIEND_SUCCESS;
				FriendAddActivity.handler.sendMessage(notify1);
				break;

			case iMoMoMsgTypes.ADD_FRIEND_FAILED:
				// 添加失败:
				Message notify2 = new Message();
				notify2.what = iMoMoMsgTypes.ADD_FRIEND_FAILED;
				FriendAddActivity.handler.sendMessage(notify2);
				break;

			case iMoMoMsgTypes.FRIEND_ID_LIST:
				// 好友Id列表:
				JSONObject json = JSON.parseObject(msg.obj.toString());
				String IdStr = json.getString(MsgKeys.friendIdList);
				myBinder.getFriendList(IdStr);// 获得好友列表GETA_FRIEND_INFO_HEAD
				break;

			case iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD:
				FriendBean bean = MsgConvertionUtil.getInstance()
						.getFriendBean(context, (iMoMoMsg) msg.obj);
				mp1.AddAFriend(bean);
				Log.i("--", "好友 ：" + bean.toString());
				break;

			case iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD:
				FriendBean bean2 = MsgConvertionUtil.getInstance()
						.getFriendBean(context, (iMoMoMsg) msg.obj);
				mp1.AddAFriend(bean2);
				Log.i("--", "好友 ：" + bean2.toString());
				break;

			case iMoMoMsgTypes.ADD_FRIEND:
				iMoMoMsg addF = (iMoMoMsg) msg.obj;
				JSONObject addFjson = JSON.parseObject(addF.msgJson);
				Notification notification = new Notification(R.drawable.imomo2,
						"添加好友", System.currentTimeMillis());
				Intent notificationIntent = new Intent(context,
						MainActivity.class);
				notification
						.setLatestEventInfo(context, "添加好友",
								addFjson.getString(MsgKeys.friendName)
										+ "添加你为好友", null);
				notifier.notify(0, notification);
				FriendBean addbean = MsgConvertionUtil.getInstance()
						.getFriendBean(context, addF);
				mp1.AddAFriend(addbean);
				// Longvibrate();// 震动
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.CHATING_IMAGE_MSG:
				// 图片消息:
				HandleChatMsg(msg);
				// 判断，震动 或提示音
				// Longvibrate();// 震动
				// playSound(1);// 提示音
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.CHATING_VOICE_MSG:
				// 语音消息:
				HandleChatMsg(msg);
				// 判断，震动 或提示音
				// Longvibrate();// 震动
				// playSound(1);// 提示音
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.CHATING_TEXT_MSG:
				// 文字消息:
				HandleChatMsg(msg);
				// 判断，震动 或提示音
				// Longvibrate();// 震动
				// playSound(1);// 提示音
				MsgComeNotify();
				break;

			case iMoMoMsgTypes.RESET_PASSWD_SUCCESS:
				Toast.makeText(context, "修改密码成功", 0).show();
				break;

			case iMoMoMsgTypes.CREATE_GROUP_SUCCESS:
				Notification noti = new Notification(R.drawable.imomo2, "创建群组",
						System.currentTimeMillis());
				noti.setLatestEventInfo(context, "创建群组", "群组创建成功", null);
				notifier.notify(0, noti);
				createGroupSuccess(msg);
				break;

			case iMoMoMsgTypes.CREATE_GROUP_FAILED:
				Notification noti2 = new Notification(R.drawable.imomo2,
						"创建群组", System.currentTimeMillis());
				// Intent noti2Intent = new Intent(context, MainActivity.class);
				noti2.setLatestEventInfo(context, "创建群组", "群组创建失败", null);
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
				// 和服务器连接断开
				ClientManager.isOnline = false;
				Notification down = new Notification(R.drawable.imomo2, "通知",
						System.currentTimeMillis());
				// Intent downIntent = new Intent(context, MainActivity.class);
				down.setLatestEventInfo(context, "服务器断开连接", "服务器断开连接,请检查网络",
						null);
				notifier.notify(0, down);
				break;
			}
		};
	};

	/**
	 * 新消息提醒
	 */
	public static void MsgComeNotify() {
		if (ClientManager.isRing) {
			playSound(1);// 提示音
			Log.i("--", "提示音");
		}

		if (ClientManager.isVibration) {
			Longvibrate();
			Log.i("--", "震动");
		}
	}

	/**
	 * 创建群组成功,添加一个群组聊天到最近联系人中
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
	 * 处理系统消息
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
					"有好友邀请您加入群组聊天", false);
		} else {
			// 添加到最近联系人中
			ChatInfoEntity entity = new ChatInfoEntity();
			entity.setChatContent("有好友邀请您加入群组聊天");
			entity.setFriendId(systemId);// 系统消息Id
			entity.setFriendName("系统消息");
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
	 * 处理接收的聊天消息
	 * 
	 * @param msg
	 */
	public static void HandleChatMsg(Message msg) {
		iMoMoMsg moMsg = (iMoMoMsg) msg.obj;
		JSONObject json = JSONObject.parseObject(moMsg.msgJson);
		String friendId = json.getString(MsgKeys.userId);// 接收时friendId应该是userId
		iMoMoDataBase db = new iMoMoDataBase(context);
		int type = json.getIntValue(MsgKeys.msgType);
		/** 判断该好友是否在最近联系人中 */
		if (mp0.isExistsInLatice(friendId)) {
			/** 判断是否正在聊天 */
			if (mp0.getChatingId().equals(friendId)) {
				// 正在聊天
				ChatActivity.AddMsgItem(MsgConvertionUtil.getInstance()
						.Convert_Net2Client(context, moMsg, true));
				if (!db.isTableExists("msg" + ClientManager.clientId + "_"
						+ friendId)) {
					// 创建表
					db.createMsgTable(friendId);
				}
				db.insertMsg(
						MsgConvertionUtil.getInstance().Convert_Net2Db(context,
								moMsg, 1, 1), friendId);

			} else {
				// 不在聊天
				if (!db.isTableExists("msg" + ClientManager.clientId + "_"
						+ friendId)) {
					// 创建表
					db.createMsgTable(friendId);
				}
				db.insertMsg(
						MsgConvertionUtil.getInstance().Convert_Net2Db(context,
								moMsg, 1, 0), friendId);
				// 更新最近联系人列表
				String content = "";
				if (type == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
					content = "[图片]";
				} else if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
					content = "[语音]";
				} else if (type == iMoMoMsgTypes.CHATING_TEXT_MSG) {
					content = json.getString(MsgKeys.msgCotent);
				}
				mp0.updateLaticeItem(friendId,
						json.getString(MsgKeys.sendTime), content, false);
			}
		} else {
			// 不在最近联系人中,消息保存到数据库
			if (!db.isTableExists("msg" + ClientManager.clientId + "_"
					+ friendId)) {
				// 创建表
				db.createMsgTable(friendId);
			}
			db.insertMsg(
					MsgConvertionUtil.getInstance().Convert_Net2Db(context,
							moMsg, 1, 0), friendId);

			// 添加到最近联系人中
			ChatInfoEntity entity = new ChatInfoEntity();
			if (type == iMoMoMsgTypes.CHATING_IMAGE_MSG) {
				entity.setChatContent("[图片]");
			} else if (type == iMoMoMsgTypes.CHATING_VOICE_MSG) {
				entity.setChatContent("[语音]");
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
				// 单人聊天消息
				entity.setMsgtype(0);
			} else if (isGroupMsg == iMoMoMsgTypes.GROUP_MSG) {
				// 多人聊天消息
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
		ClientUtils.getInstance().getClientuserEmail(context);// 初始化用户userEmail
		// 启动服务，连接服务器
		Intent serviceIntent = new Intent(this, ClientService.class);
		Intent bindIntent = new Intent(this, ClientService.class);
		bindService(bindIntent, connection, BIND_AUTO_CREATE);
//		Log.i("--", "主界面bindService");
		initView();
		initFragment();
		initMap();

		// 初始化音乐池
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		// 向音乐池中添加音乐
		msgcoming = soundPool.load(getApplicationContext(), R.raw.msgcoming, 0);
		voicerec = soundPool.load(getApplicationContext(), R.raw.voicerec, 0);
		voicesend = soundPool.load(getApplicationContext(), R.raw.voicesend, 0);

		
		initDialog = new myDialog(context, "加载中...");
	}

	/**
	 * 更新个人信息(侧边栏)
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
			Log.i("--", "侧边栏头像修改成功");
			break;
		}
	}

	/**
	 * 初始化个人信息，侧边栏
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
		 * 跳转到个人信息
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
		 * 注销
		 */
		rl_loginout = (RelativeLayout) findViewById(R.id.main_rl_loginout);
		rl_loginout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("注销").setMessage("确认注销?");
				builder.setPositiveButton("确定",
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
				builder.setNegativeButton("取消",
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
		 * 退出
		 */
		rl_exit = (RelativeLayout) findViewById(R.id.main_rl_exit);
		rl_exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Dialog dialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("退出").setMessage("确认退出?");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 断开服务
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						});
				builder.setNegativeButton("取消",
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
		 * 设置
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
		 * 反馈
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
		// 初始化个人设置
		initSettings();
		boolean isReported = mSpUtil.getIsReport();
		String ReportDate = mSpUtil.getReportDate();
		if(ReportDate.equals(new Date()) && isReported){
			isReported = true;
		}
		if (!isReported) {
//			System.out.println("没签到");
			btn_report.setBackgroundResource(R.drawable.bg_btn_mini);
			btn_report.setTextColor(Color.WHITE);
			btn_report.setText("签到");
		} else {
//			System.out.println("已签到");
			btn_report.setBackgroundResource(R.drawable.bg_btn_mini_nor);
			btn_report.setTextColor(Color.rgb(128, 128, 128));
			btn_report.setText("已签到");
		}

		btn_report.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ClientManager.isOnline) {
					if (mSpUtil.getIsReport()) {
						Toast.makeText(context, "已签到", 0)
								.show();
					} else {
						mSpUtil.setIsReport(true);
						mSpUtil.setReportDate();
						btn_report
								.setBackgroundResource(R.drawable.bg_btn_mini_nor);
						btn_report.setTextColor(Color.rgb(128, 128, 128));
						btn_report.setText("已签到");
						myBinder.handleVitality(iMoMoMsgTypes.SIGN);
						Toast.makeText(context, "活力+15", 0)
								.show();
						ClientManager.vitalityValue += 15;
					}
				} else {
					Toast.makeText(context, "您处于离线状态，无法签到", 0).show();
				}

			}
		});
	}

	/**
	 * 初始化个人设置
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
		// 断开服务
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
				System.out.println("点击模块零");
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
				System.out.println("点击模块一");
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
				System.out.println("点击模块二");
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
	 * 跳到最近联系人列表界面 , (not ok)
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
				Toast.makeText(getApplicationContext(), "再按一次退出",
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
	 * 播放消息提示音
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
	 * 震动(长震动)
	 */
	public static void Longvibrate() {
		// 震动
		Vibrator vibe = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(500);
	}

}
