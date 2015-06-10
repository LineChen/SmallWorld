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
//		Log.i("--", "����service");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.i("--", "�յ�����");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		CloseSession();
//		Log.i("--", "�˳�service");
	}

	/**
	 * ���ӷ�����
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
		Log.i("--", "��service");
		return mBinder;
	}

	public class MyBinder extends Binder {
		/**
		 * ���ӷ�����
		 */
		public void ConnectServer(){
			new ConnectServerThread().start();
		}
		
		/**
		 * ������¼,�õ�������
		 */
		public void SuperLogin() {
			try {
				iMoMoMsg moMoMsg = new iMoMoMsg();
				JSONObject Json = new JSONObject();
				Json.put(MsgKeys.userEmail, ClientManager.clientEmail);
				// �������û�ͷ�񣬲���Ҫ�ӷ�������ȡ
				if (FileUtils.isFileExists(StaticValues.USER_HEADPATH
						+ ClientManager.clientEmail + ".png")) {
					Json.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN_SUPER_HEAD);
				}
				// �����ޣ����ȡ
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
		 * ��������ӷ�������ȡ��Χһ������İ��������
		 * 
		 * @author Administrator
		 * 
		 */
		public void getStrangerListOneKm() {// ��ǰ�Ѿ����ص���������
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
		 * ����һ�����İ����
		 * 
		 * @param nowNumber
		 */
		public void getStrangerListMore(int disRange) {// ��ǰ�Ѿ����ص���������
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
		 * ����λ����Ϣ
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
			Log.i("--", "����ǰ �� " + loc_json.toJSONString());
		}

		/**
		 * �����ȡһ��İ���˵ĸ�����Ϣ
		 * 
		 * @param friendId
		 */
		public void getStrangerInfo(String friendId) {
			Log.i("--", "getFriendInfo İ����id =   ��" + friendId);
			iMoMoMsg msg = new iMoMoMsg();
			msg.symbol = '+';
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.GET_STRANGER_INFO);// ��Ҫ��ȡͷ��
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.friendId, friendId);
			msg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(msg);
		}

		/**
		 * ��Ӻ���
		 * 
		 * @param friendId
		 */
		public void addFriend(String friendId) {
			iMoMoMsg msg = new iMoMoMsg();
			msg.symbol = '+';
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.ADD_FRIEND);// ��Ӻ���
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.friendId, friendId);
			msg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(msg);
		}
		
		/**
		 * ɾ������,ֻ������Ϣ
		 * 
		 * @param friendId
		 */
		public void deleteFriend(String friendId) {
			iMoMoMsg msg = new iMoMoMsg();
			msg.symbol = '+';
			JSONObject json = new JSONObject();
			json.put(MsgKeys.msgType, iMoMoMsgTypes.DELETE_FRIEND);// ��Ӻ���
			json.put(MsgKeys.userId, ClientManager.clientId);
			json.put(MsgKeys.friendId, friendId);
			msg.msgJson = json.toJSONString();
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(msg);
		}
		
		

		/**
		 * ���������ȡ����Id�б�(�ȵõ�Id��Ȼ��鿴�����Ƿ��иú��ѵ�ͷ��û�о�Ҫ�����������)
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
		 * ��ú����б�
		 * 
		 * @param IdStr
		 */
		public void getFriendList(String IdStr) {
			Log.i("--", "����Id�б� ��" + IdStr);
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
								iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD);// ����Ҫ��ȡͷ��
					} else {
						json.put(MsgKeys.msgType,
								iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD);// ��Ҫ��ȡͷ��
					}
					msg.msgJson = json.toJSONString();
					if (MainActivity.ClientSession != null
							&& !MainActivity.ClientSession.isClosing())
						MainActivity.ClientSession.write(msg);
					Log.i("--", "��ȡ������Ϣ�� " + msg.msgJson);
				}
			}
		}

		/**
		 * �޸ĸ�����Ϣ,��������
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
		 * �޸�ͷ��
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
	 * ���ͷ�����Ϣ
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
		 * ����������Ϣ
		 * @param moMsg
		 */
		public void sendCharMsg(iMoMoMsg moMsg){
			if (MainActivity.ClientSession != null
					&& !MainActivity.ClientSession.isClosing())
				MainActivity.ClientSession.write(moMsg);
		}
		
		/**
		 * ����ֵ��ش���
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









