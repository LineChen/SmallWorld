package com.clientUtils;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hellostranger.activity.MainActivity;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

public class ClientHandler extends IoHandlerAdapter {

	private Handler handler;

	public ClientHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		Message msg = new Message();
		msg.what = StaticValues.SESSION_OPENED;
		handler.sendMessage(msg);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		super.inputClosed(session);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		super.messageReceived(session, message);
		iMoMoMsg moMoMsg = (iMoMoMsg) message;
		JSONObject msgJson = JSON.parseObject(moMoMsg.msgJson);
		Message msg = new Message();
		Log.i("--", "�ͻ����յ�:" + moMoMsg.toString());
		if (moMoMsg.symbol == '+') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.REGISTER_SUCCESS:
				msg.what = iMoMoMsgTypes.REGISTER_SUCCESS;
				break;
			case iMoMoMsgTypes.REGISTER_FAILED:
				msg.what = iMoMoMsgTypes.REGISTER_FAILED;
				break;
			case iMoMoMsgTypes.FIND_PASSWD_SUCCESS:
				msg.what = iMoMoMsgTypes.FIND_PASSWD_SUCCESS;
				break;
			case iMoMoMsgTypes.FIND_PASSWD_FAILED:
				msg.what = iMoMoMsgTypes.FIND_PASSWD_FAILED;
				break;
			case iMoMoMsgTypes.LOGIN_SUCCESS:
				msg.what = iMoMoMsgTypes.LOGIN_SUCCESS;
				break;
			case iMoMoMsgTypes.LOGIN_FAILED:
				msg.what = iMoMoMsgTypes.LOGIN_FAILED;
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_HEAD:
				msg.what = iMoMoMsgTypes.LOGIN_SUPER_HEAD;
				msg.obj = moMoMsg;//�������û�ͷ��
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_FAILED:
				msg.what = iMoMoMsgTypes.LOGIN_SUPER_FAILED;
				break;
			case iMoMoMsgTypes.CHATING_TEXT_MSG:
				// �ı���Ϣ:
				msg.what = iMoMoMsgTypes.CHATING_TEXT_MSG;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.STRANGERS_LIST_ONEKM:
				// һ������İ�����б�:
				msg.what = iMoMoMsgTypes.STRANGERS_LIST_ONEKM;
				msg.obj = moMoMsg;//İ�����б�
				break;
			case iMoMoMsgTypes.STRANGERS_LIST_MORE:
				// ����һ������İ�����б�:
				msg.what = iMoMoMsgTypes.STRANGERS_LIST_MORE;
				msg.obj = moMoMsg;//İ�����б�
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND_SUCCESS:
				// ��Ӻ��ѳɹ�:
				msg.what = iMoMoMsgTypes.ADD_FRIEND_SUCCESS;
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND_FAILED:
				// ���ʧ��:
				msg.what = iMoMoMsgTypes.ADD_FRIEND_FAILED;
				break;
				
			case iMoMoMsgTypes.FRIEND_ID_LIST:
				//����Id�б�
				msg.what = iMoMoMsgTypes.FRIEND_ID_LIST;
				msg.obj = moMoMsg.msgJson;//����Id�б�
				break;	
				
			case iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD:
				//����Id�б�
				msg.what = iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD;
				msg.obj = moMoMsg;//������Ϣ��������ͷ��
				break;	
				
			case iMoMoMsgTypes.CREATE_GROUP_FAILED:
				// ��Ӻ�����Ϣ����������ң�:
				msg.what = iMoMoMsgTypes.CREATE_GROUP_FAILED;
				msg.obj = moMoMsg; 
				break;
			}
		} else if (moMoMsg.symbol == '-') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.LOGIN_SUPER_NOHEAD:
				msg.what = iMoMoMsgTypes.LOGIN_SUPER_NOHEAD;
				msg.obj = moMoMsg;//�����û�ͷ��
				break;
			case iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD:
				msg.what = iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD;
				msg.obj = moMoMsg;//���ѵĸ�����Ϣ,��������ͼ��
				break;
				
			case iMoMoMsgTypes.GET_STRANGER_INFO:
				msg.what = iMoMoMsgTypes.GET_STRANGER_INFO;
				msg.obj = moMoMsg;//İ���˵ĸ�����Ϣ
				break;
				
				
			case iMoMoMsgTypes.CHATING_IMAGE_MSG:
				// ͼƬ��Ϣ:
				msg.what = iMoMoMsgTypes.CHATING_IMAGE_MSG;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND:
				// ��Ӻ�����Ϣ����������ң�:
				msg.what = iMoMoMsgTypes.ADD_FRIEND;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.CHATING_VOICE_MSG:
				// ������Ϣ:
				msg.what = iMoMoMsgTypes.CHATING_VOICE_MSG;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.INVITE_TO_GROUP:
				msg.what = iMoMoMsgTypes.INVITE_TO_GROUP;
				msg.obj = moMoMsg; 
				break;
				
				
			case iMoMoMsgTypes.RESET_HEAD:
				msg.what = iMoMoMsgTypes.INVITE_TO_GROUP;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.CREATE_GROUP_SUCCESS:
				// ��Ӻ�����Ϣ����������ң�:
				msg.what = iMoMoMsgTypes.CREATE_GROUP_SUCCESS;
				msg.obj = moMoMsg; 
				break;
			}
		}
		handler.sendMessage(msg);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		if(ClientManager.isOnline){
			ClientManager.isOnline = false;//���û�����ʱ���ͷ������Ự�ж�ʱ
			Message msg = new Message();
			msg.what = iMoMoMsgTypes.CONNECT_DOWN;
			handler.sendMessage(msg);
//			MainActivity.mp0.updateHeader();
//			Log.i("--","���header��ʾ");
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

}
