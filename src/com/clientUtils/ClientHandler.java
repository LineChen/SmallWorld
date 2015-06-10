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
		Log.i("--", "客户端收到:" + moMoMsg.toString());
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
				msg.obj = moMoMsg;//不包含用户头像
				break;
			case iMoMoMsgTypes.LOGIN_SUPER_FAILED:
				msg.what = iMoMoMsgTypes.LOGIN_SUPER_FAILED;
				break;
			case iMoMoMsgTypes.CHATING_TEXT_MSG:
				// 文本信息:
				msg.what = iMoMoMsgTypes.CHATING_TEXT_MSG;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.STRANGERS_LIST_ONEKM:
				// 一公里内陌生人列表:
				msg.what = iMoMoMsgTypes.STRANGERS_LIST_ONEKM;
				msg.obj = moMoMsg;//陌生人列表
				break;
			case iMoMoMsgTypes.STRANGERS_LIST_MORE:
				// 大于一公里内陌生人列表:
				msg.what = iMoMoMsgTypes.STRANGERS_LIST_MORE;
				msg.obj = moMoMsg;//陌生人列表
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND_SUCCESS:
				// 添加好友成功:
				msg.what = iMoMoMsgTypes.ADD_FRIEND_SUCCESS;
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND_FAILED:
				// 添加失败:
				msg.what = iMoMoMsgTypes.ADD_FRIEND_FAILED;
				break;
				
			case iMoMoMsgTypes.FRIEND_ID_LIST:
				//好友Id列表
				msg.what = iMoMoMsgTypes.FRIEND_ID_LIST;
				msg.obj = moMoMsg.msgJson;//好友Id列表
				break;	
				
			case iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD:
				//好友Id列表
				msg.what = iMoMoMsgTypes.GETA_FRIEND_INFO_HEAD;
				msg.obj = moMoMsg;//好友信息，不包含头像
				break;	
				
			case iMoMoMsgTypes.CREATE_GROUP_FAILED:
				// 添加好友信息（别人添加我）:
				msg.what = iMoMoMsgTypes.CREATE_GROUP_FAILED;
				msg.obj = moMoMsg; 
				break;
			}
		} else if (moMoMsg.symbol == '-') {
			switch (msgJson.getInteger(MsgKeys.msgType)) {
			case iMoMoMsgTypes.LOGIN_SUPER_NOHEAD:
				msg.what = iMoMoMsgTypes.LOGIN_SUPER_NOHEAD;
				msg.obj = moMoMsg;//包含用户头像
				break;
			case iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD:
				msg.what = iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD;
				msg.obj = moMoMsg;//好友的个人信息,包含好友图像
				break;
				
			case iMoMoMsgTypes.GET_STRANGER_INFO:
				msg.what = iMoMoMsgTypes.GET_STRANGER_INFO;
				msg.obj = moMoMsg;//陌生人的个人信息
				break;
				
				
			case iMoMoMsgTypes.CHATING_IMAGE_MSG:
				// 图片消息:
				msg.what = iMoMoMsgTypes.CHATING_IMAGE_MSG;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.ADD_FRIEND:
				// 添加好友信息（别人添加我）:
				msg.what = iMoMoMsgTypes.ADD_FRIEND;
				msg.obj = moMoMsg; 
				break;
				
			case iMoMoMsgTypes.CHATING_VOICE_MSG:
				// 语音消息:
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
				// 添加好友信息（别人添加我）:
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
			ClientManager.isOnline = false;//当用户在线时，和服务器会话中断时
			Message msg = new Message();
			msg.what = iMoMoMsgTypes.CONNECT_DOWN;
			handler.sendMessage(msg);
//			MainActivity.mp0.updateHeader();
//			Log.i("--","添加header提示");
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

}
