package com.clientUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chat.ImageUtil;
import com.example.hellostranger.util.SharePreferenceUtil;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;


/**
 * �����û������Ϣ�Ĺ�����
 * @author Administrator
 *
 */
public class ClientUtils {
	public static ClientUtils clientUtils;
	
	public static ClientUtils getInstance() {
		if (clientUtils == null) {
			synchronized (ClientUtils.class) {
				if (clientUtils == null) {
					clientUtils = new ClientUtils();
				}
			}
		}
		return clientUtils;
	}
	
	/**
	 * �ӹ������еõ��Ϸ��û�userEmail���ڵ�¼
	 */
	public void getClientuserEmail(Context context) {
		
//		SharedPreferences preferences = context.getSharedPreferences(
//				StaticValues.sharePreName, context.MODE_PRIVATE);
//		ClientManager.clientEmail = preferences.getString(StaticValues.userEmail, null);
	SharePreferenceUtil util = new SharePreferenceUtil(context);
	ClientManager.clientEmail = util.GetValue(StaticValues.userEmail);
	ClientManager.clientId = util.GetValue(ClientManager.clientEmail);
	
	}

	/**
	 * �����û���Ϣ
	 * @param context
	 * @param msg
	 */
	public void saveClientInfo(Context context, Message msg) {
		ClientManager.isOnline = true;
		iMoMoMsg moMsg = (iMoMoMsg) msg.obj;
		JSONObject json = JSON.parseObject(moMsg.msgJson);
		if(msg.what == iMoMoMsgTypes.LOGIN_SUPER_NOHEAD){
			//�����û�ͷ�񵽱���
			ImageUtil.getInstance().saveImage(context, moMsg.msgBytes, StaticValues.USER_HEADPATH + ClientManager.clientEmail+".png");
		}
		ClientManager.clientId = json.getString(MsgKeys.userId);
		ClientManager.clientName = json.getString(MsgKeys.userName);
		ClientManager.clientSex = json.getString(MsgKeys.userSex);
		ClientManager.clientBirthday = json.getString(MsgKeys.userBirthday);
		ClientManager.personSignature = json.getString(MsgKeys.personSignature);
		ClientManager.vitalityValue = json.getIntValue(MsgKeys.vitalityValue);
		
		//����Email - userId
		new SharePreferenceUtil(context).SaveValue(ClientManager.clientEmail, ClientManager.clientId);
	}
	
	// ��ȡ��ǰʱ��
	public static String getNowTime() {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		String date = format.format(new Date());
		return date;
	}
	
	
}



