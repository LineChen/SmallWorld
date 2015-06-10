package com.clientUtils;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.chat.FileUtils;
import com.chat.ImageUtil;
import com.example.hellostranger.R;
import com.example.hellostranger.activity.MainActivity;
import com.imomo_codecfactory.iMoMoCodecFactory;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;
import com.strangerlist.StrangerBean;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class ClientService extends Service {

	private MyBinder mBinder;

	@Override
	public void onCreate() {
		super.onCreate();
		mBinder = new MyBinder();
//		Log.i("--", "创建service");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.i("--", "收到任务");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		CloseSession();
//		Log.i("--", "退出service");
	}

	/**
	 * 连接服务器
	 */
	private class ConnectServerThread extends Thread {
		public void run() {
			NioSocketConnector connector = new NioSocketConnector();
			connector.setHandler(new ClientHandler(MainActivity.handler));
			connector.getFilterChain().addLast("code",
					new ProtocolCodecFilter(new iMoMoCodecFactory()));
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(
						StaticValues.SERVER_IP, StaticValues.SERVER_PORT));
				future.awaitUninterruptibly();
				MainActivity.ClientSession = future.getSession();
//				System.out
//						.println("session is ok" + MainActivity.ClientSession);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void CloseSession() {
		if (MainActivity.ClientSession != null
				&& !MainActivity.ClientSession.isClosing()) {
			MainActivity.ClientSession.close();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i("--", "绑定service");
		return mBinder;
	}

	public class MyBinder extends Binder {
		/**
		 * 连接服务器
		 */
		public void ConnectServer(){
			new ConnectServerThread().start();
		}
		
		/**
		 * 超级登录,得到长连接
		 */
		public void SuperLogin() {
			try {
				iMoMoMsg moMoMsg = new iMoMoMsg();
				JSONObject Json = new JSONObject();
				Json.put(MsgKeys.userEmail, ClientManager.clientEmail);
				// 本地有用户头像，不需要从服务器获取
				if (FileUtils.isFileExists(StaticValues.USER_HEADPATH
						+ ClientManager.clientEmail + ".png")) {
					Json.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUPER_HEAD);
				}
				// 本地无，需获取
				else {
					Json.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUPER_NOHEAD);
				}
				moMoMsg.msgJson = Json.toJSONString();
				moMoMsg.symbol = '+';
				MainActivity.ClientSession.write(moMoMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * 发送请求从服务器获取周围一公里内陌生人数据
		 * 
		 * @author Administrator
		 * 
		 */
		public void getStrangerListOneKm() {// 当前已经加载的数据条数
			iMoMoMsg moMsg = new iMoMoMsg();
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.GET_STRANGERS_LOC_ONEKM);
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.loc_province, ClientManager.province);
			json.put(MsgKeys.loc_Longitude, ClientManager.Longitude);
			json.put(MsgKeys.loc_Latitude, ClientManager.Latitude);
			moMsg.symbol = '+';
			moMsg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing()) {
				MainActivity.ClientSession.write(moMsg);
			}
		}

		/**
		 * 大于一公里的陌生人
		 * 
		 * @param nowNumber
		 */
		public void getStrangerListMore(int disRange) {// 当前已经加载的数据条数
			iMoMoMsg moMsg = new iMoMoMsg();
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.GET_STRANGERS_LOC_MORE);
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.distRange, disRange);
			json.put(MsgKeys.loc_province, ClientManager.province);
			json.put(MsgKeys.loc_Longitude, ClientManager.Longitude);
			json.put(MsgKeys.loc_Latitude, ClientManager.Latitude);
			moMsg.symbol = '+';
			moMsg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing()) {
				MainActivity.ClientSession.write(moMsg);
			}
		}

		/**
		 * 发送位置消息
		 */
		public void sendLocation() {
			iMoMoMsg loc_msg = new iMoMoMsg();
			loc_msg.symbol = '+';
			JSONObject loc_json = new JSONObject();
			loc_json.put(MsgKeys.msgType, iMoMoMsgTypes.LOCATION);
			loc_json.put(MsgKeys.userId, ClientManager.clientId);
			loc_json.put(MsgKeys.loc_province, ClientManager.province);
			loc_json.put(MsgKeys.loc_Longitude, ClientManager.Longitude);
			loc_json.put(MsgKeys.loc_Latitude, ClientManager.Latitude);
			loc_msg.msgJson = loc_json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(loc_msg);
			Log.i("--", "发送前 ： " + loc_json.toJSONString());
		}

		/**
		 * 请求获取一个陌生人的个人信息
		 * 
		 * @param friendId
		 */
		public void getStrangerInfo(String friendId) {
			Log.i("--", "getFriendInfo 陌生人id =   ：" + friendId);
			iMoMoMsg msg = new iMoMoMsg();
			msg.symbol = '+';
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.GET_STRANGER_INFO);// 需要获取头像
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.friendId, friendId);
			msg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(msg);
		}

		/**
		 * 添加好友
		 * 
		 * @param friendId
		 */
		public void addFriend(String friendId) {
			iMoMoMsg msg = new iMoMoMsg();
			msg.symbol = '+';
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND);// 添加好友
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.friendId, friendId);
			msg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(msg);
		}
		
		/**
		 * 删除好友,只发送消息
		 * 
		 * @param friendId
		 */
		public void deleteFriend(String friendId) {
			iMoMoMsg msg = new iMoMoMsg();
			msg.symbol = '+';
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.DELETE_FRIEND);// 添加好友
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.friendId, friendId);
			msg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(msg);
		}
		
		

		/**
		 * 想服务器获取好友Id列表(先得到Id，然后查看本地是否有该好友的头像，没有就要像服务器请求)
		 */
		public void getFriendIdList() {
			iMoMoMsg msg = new iMoMoMsg();
			msg.symbol = '+';
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.GET_FRIEND_ID_LIST);
			json.put(MsgKeys.userId, ClientManager.clientId);
			msg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(msg);
		}

		/**
		 * 获得好友列表
		 * 
		 * @param IdStr
		 */
		public void getFriendList(String IdStr) {
			Log.i("--", "好友Id列表 ：" + IdStr);
			if (!IdStr.equals("none")) {
				String[] Ids = IdStr.split(",");
				for (String friendId : Ids) {
					iMoMoMsg msg = new iMoMoMsg();
					msg.symbol = '+';
					JSONObject json = new JSONObject();
					json.put(MsgKeys.userId, ClientManager.clientId);
					json.put(MsgKeys.friendId, friendId);

					if (FileUtils.isFileExists(StaticValues.USER_HEADPATH
							+ friendId + ".png")) {
						json.put(MsgKeys.msgType,
								iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD);// 不需要获取头像
					} else {
						json.put(MsgKeys.msgType,
								iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD);// 需要获取头像
					}
					msg.msgJson = json.toJSONString();
					if (MainActivity.ClientSession != null
							&& !MainActivity.ClientSession.isClosing())
						MainActivity.ClientSession.write(msg);
					Log.i("--", "获取好友信息： " + msg.msgJson);
				}
			}
		}

		/**
		 * 修改个人信息,包括密码
		 * 
		 * @param type
		 * @param value
		 */
		public void ResetUserInfo(int type, String value) {
			Log.i("--", "type = " + type + "value = " + value);
			iMoMoMsg moMoMsg = new iMoMoMsg();
			moMoMsg.symbol = '+';
			JSONObject Json = new JSONObject();
			Json.put(MsgKeys.userId, ClientManager.clientId);
			switch (type) {
			case iMoMoMsgTypes.RESET_USERNAME:
				Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_USERNAME);
				Json.put(MsgKeys.userName, value);
				break;
			case iMoMoMsgTypes.RESET_SEX:
				Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_SEX);
				Json.put(MsgKeys.userSex, value);
				break;
			case iMoMoMsgTypes.RESET_BIRTHDAY:
				Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_BIRTHDAY);
				Json.put(MsgKeys.userBirthday, value);
				break;
			case iMoMoMsgTypes.RESET_SIGNATUE:
				Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_SIGNATUE);
				Json.put(MsgKeys.personSignature, value);
				break;
			case iMoMoMsgTypes.RESET_PASSWD:
				Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_PASSWD);
				Json.put(MsgKeys.userPasswd, value);
				break;
			}
			moMoMsg.msgJson = Json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(moMoMsg);
		}

		/**
		 * 修改头像
		 * @param headBytes
		 */
		public void ResetUserHead(byte[] headBytes) {
			iMoMoMsg moMoMsg = new iMoMoMsg();
			moMoMsg.symbol = '-';
			JSONObject Json = new JSONObject();
			Json.put(MsgKeys.msgType, iMoMoMsgTypes.RESET_HEAD);
			Json.put(MsgKeys.userId, ClientManager.clientId);
			moMoMsg.msgJson = Json.toJSONString();
			moMoMsg.msgBytes = headBytes;
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(moMoMsg);
		}
	
	
	/**
	 * 发送反馈信息
	 * @param value
	 */
		public void SendReback(String value){
			iMoMoMsg moMoMsg = new iMoMoMsg();
			moMoMsg.symbol = '+';
			JSONObject Json = new JSONObject();
			Json.put(MsgKeys.msgType, iMoMoMsgTypes.REBACK);
			Json.put(MsgKeys.userId, ClientManager.clientId);
			Json.put(MsgKeys.msgCotent, value);
			moMoMsg.msgJson = Json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(moMoMsg);
		}
		
		/**
		 * 发送聊天消息
		 * @param moMsg
		 */
		public void sendCharMsg(iMoMoMsg moMsg){
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(moMsg);
		}
		
		/**
		 * 活力值相关处理
		 * @param type
		 */
		public void handleVitality(int type){
			iMoMoMsg moMsg = new iMoMoMsg();
			moMsg.symbol = '+';
			JSONObject Json = new JSONObject();
			Json.put(MsgKeys.userId, ClientManager.clientId);
			Json.put(MsgKeys.msgType, type);
			moMsg.msgJson = Json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(moMsg);
		}
	}

}









