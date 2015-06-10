package com.clientUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chat.ImageUtil;
import com.chat.SoundUtil;
import com.example.hellostranger.bean.InvitationInfoEntity;
import com.freind.FriendBean;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgClient;
import com.msg_relative.iMoMoMsgDb;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;
import com.strangerlist.StrangerBean;

/**
 * 网络消息包、数据库存数消息包、显示的消息包之间的转换
 * 
 * @author Administrator
 * 
 */
public class MsgConvertionUtil {

	/**
	 * 为了实现并发性，不要返回静态的
	 * 
	 * @return
	 */
	public static MsgConvertionUtil getInstance() {
		return new MsgConvertionUtil();
	}

	/**
	 * 网络传输消息，转化为数据库存储消息
	 * 这里是从外界收到的消息,
	 * @param context
	 * @param moMsg
	 * @param isGetted 0 = false, 1 = true
	 * @param isLooked 0 = false, 1 = true
	 * @return
	 */
	public iMoMoMsgDb Convert_Net2Db(Context context, iMoMoMsg moMsg, int isGetted, int isLooked) {
		iMoMoMsgDb msgDb = new iMoMoMsgDb();
		JSONObject json = JSON.parseObject(moMsg.msgJson);
		int type = json.getInteger(MsgKeys.msgType);
		String sendTime = json.getString(MsgKeys.sendTime);
		String senderId = json.getString(MsgKeys.userId);
		String getterId = json.getString(MsgKeys.friendId);
		msgDb.isGetted = isGetted;
		msgDb.isLooked = isLooked;//默认未查看
		msgDb.msgType = type;
		msgDb.sendTime = sendTime;
		JSONObject jsonDb = new JSONObject();
		//是收到消息，Id互换
		if(isGetted == 1){
			jsonDb.put(MsgKeys.friendId, senderId);
			jsonDb.put(MsgKeys.userId, getterId);
		} else {
			jsonDb.put(MsgKeys.friendId, getterId);
			jsonDb.put(MsgKeys.userId, senderId);
		}
		switch (type) {
		case iMoMoMsgTypes.CHATING_TEXT_MSG:
			jsonDb.put(MsgKeys.msgCotent, json.getString(MsgKeys.msgCotent));
			break;
		case iMoMoMsgTypes.CHATING_IMAGE_MSG:
			String imagePath = StaticValues.IMAGEPATH + senderId+"_"+getterId + System.currentTimeMillis() + ".png";
			ImageUtil.getInstance().saveImage(context, moMsg.msgBytes, imagePath);
			jsonDb.put(MsgKeys.imagePath, imagePath);
			break;
		case iMoMoMsgTypes.CHATING_VOICE_MSG:
			String voicePath = StaticValues.VOICEPATH + senderId+"_"+getterId + System.currentTimeMillis() + ".amr";
			SoundUtil.getInstance().saveGettedVoice(context, moMsg.msgBytes, voicePath);
			jsonDb.put(MsgKeys.voicePath, voicePath);
			jsonDb.put(MsgKeys.voiceTime, json.getIntValue(MsgKeys.voiceTime));
			break;
		}
		
		msgDb.msgJson = jsonDb.toJSONString();
		return msgDb;
	}

	/**
	 * 网络传输消息，转化为手机显示的消息
	 * 图片或语音文件名格式：<发送用户Id_接收用户Id+接收时间>
	 * 
	 * 发送和接收都用
	 * @param moMsg
	 * @return
	 */
	public iMoMoMsgClient Convert_Net2Client(Context context, iMoMoMsg moMsg, boolean isGetted) {
		iMoMoMsgClient msgClient = new iMoMoMsgClient();
		JSONObject json = JSON.parseObject(moMsg.msgJson);
		int type = json.getInteger(MsgKeys.msgType);
		switch (type) {
		case iMoMoMsgTypes.CHATING_TEXT_MSG:
			break;
		case iMoMoMsgTypes.CHATING_IMAGE_MSG:
			String imagePath = StaticValues.IMAGEPATH + json.getString(MsgKeys.userId)+"_"+json.getString(MsgKeys.friendId) + "_" + System.currentTimeMillis() + ".png";
			ImageUtil.getInstance().saveImage(context, moMsg.msgBytes, imagePath);
			json.put(MsgKeys.imagePath, imagePath);
			break;
		case iMoMoMsgTypes.CHATING_VOICE_MSG:
			String voicePath = StaticValues.VOICEPATH + json.getString(MsgKeys.userId)+"_"+json.getString(MsgKeys.friendId) + "_" + System.currentTimeMillis() + ".amr";
			SoundUtil.getInstance().saveGettedVoice(context, moMsg.msgBytes, voicePath);
			json.put(MsgKeys.voicePath, voicePath);
			break;
		}
		//如果是接收到的消息，好友Id是发送方Id==userId
		if(isGetted){
			json.put(MsgKeys.friendId,  json.getString(MsgKeys.userId));
		}
		msgClient.setGetted(isGetted);//
		msgClient.setMsgJson(json.toJSONString());
		return msgClient;
	}

	/**
	 * 数据库存储消息，转化为手机显示的消息 (有 bug)
	 * 
	 * @param moMsg
	 * @return
	 */
	public iMoMoMsgClient Convert_Db2Client(iMoMoMsgDb msgDb) {
		iMoMoMsgClient msgClient = new iMoMoMsgClient();
		JSONObject jsonDb = JSON.parseObject(msgDb.msgJson);
//		Log.i("--", "-------------------Convert_Db2Client :" + jsonDb);
		jsonDb.put(MsgKeys.sendTime, msgDb.sendTime);
		jsonDb.put(MsgKeys.msgType, msgDb.msgType);
		if (msgDb.isGetted == 0) {
			msgClient.setGetted(false);
		} else {
			msgClient.setGetted(true);
		}
		msgClient.setMsgJson(jsonDb.toJSONString());
		return msgClient;
	}

	/**
	 * 客户端显示消息转换为数据库存储消息--用于发送图片和语音消息时存储到数据库时，因为这时图片和语音路径都存在
	 * @param msgClient
	 * @return
	 */
	public iMoMoMsgDb ConvertClient2Db(iMoMoMsgClient msgClient){
		iMoMoMsgDb msgDb = new iMoMoMsgDb();
		JSONObject jsonDb = new JSONObject();
		JSONObject json = JSON.parseObject(msgClient.getMsgJson());
		
		int type = json.getIntValue(MsgKeys.msgType);
		msgDb.isGetted = 0;//自己发送的
		msgDb.isLooked = 1;//因为是直接发送的，所以是已经看了
		msgDb.msgType = type;
		msgDb.sendTime = json.getString(MsgKeys.sendTime);
		jsonDb.put(MsgKeys.sendTime, msgDb.sendTime);
		jsonDb.put(MsgKeys.msgType, msgDb.msgType);
		jsonDb.put(MsgKeys.friendId, json.getString(MsgKeys.friendId));
		if(type == iMoMoMsgTypes.CHATING_IMAGE_MSG){
			jsonDb.put(MsgKeys.imagePath, json.getString(MsgKeys.imagePath));
		} else if(type == iMoMoMsgTypes.CHATING_VOICE_MSG){
			jsonDb.put(MsgKeys.voicePath, json.getString(MsgKeys.voicePath));
			jsonDb.put(MsgKeys.voiceTime, json.getIntValue(MsgKeys.voiceTime));
		}
		msgDb.msgJson = jsonDb.toJSONString();
		
//		Log.i("--", "---------------------ConvertClient2Db : client = " + json + "msgDb = " + msgDb.msgJson);
		return msgDb;
	}
	
	
	/**
	 * 从得到的iMoMoMsg消息中提取陌生人列表
	 * @param moMsg
	 * @return
	 */
	public List<StrangerBean> getStrangerList(iMoMoMsg moMsg){
		JSONObject slistJson = JSON.parseObject(moMsg.msgJson);
		JSONArray jsonArray = JSON.parseArray(slistJson.getString(MsgKeys.strangerList));
		List<StrangerBean> strangerList = new ArrayList<StrangerBean>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			StrangerBean bean = new StrangerBean();
			bean.Latitude = object.getDoubleValue("Latitude");
			bean.Longitude = object.getDoubleValue("Longitude");
			bean.strangerId = object.getString("strangerId");
			bean.strangerName = object.getString("strangerName");
			bean.strangerLoc = object.getString("strangerLoc");
			strangerList.add(bean);
		}
		return strangerList;
	}
	
	
	/**
	 * 从moMsg中获取FriendBean对象 
	 * @param moMsg
	 * @return
	 */
	public FriendBean getFriendBean(Context context, iMoMoMsg moMsg){
		FriendBean friendBean = new FriendBean();
		JSONObject json = JSON.parseObject(moMsg.msgJson);
		int type = json.getIntValue(MsgKeys.msgType);
		String id = json.getString(MsgKeys.userId);
		String headPath = StaticValues.USER_HEADPATH + id + ".png";
		friendBean.setFriendId(id);
		friendBean.setFriendName(json.getString(MsgKeys.userName));
		friendBean.setFriendSex(json.getString(MsgKeys.userSex));
		friendBean.setFriendBirthday(json.getString(MsgKeys.userBirthday));
		friendBean.setFriendSignature(json.getString(MsgKeys.personSignature));
		friendBean.setFriendHeadPath(headPath);
		//包含好友头像：两种包类型都要保存头像
		if(type == iMoMoMsgTypes.GETA_FRIEND_INFO_NOHEAD || type == iMoMoMsgTypes.ADD_FRIEND){
			ImageUtil.getInstance().saveImage(context, moMsg.msgBytes, headPath);
		}
		return friendBean;
	}

	/**
	 * 获得邀请的内容，关于群组的信息
	 * @param invite
	 * @return
	 */
	public InvitationInfoEntity getInvitFromNet(Context context, iMoMoMsg invite) {
		InvitationInfoEntity entity = new InvitationInfoEntity();
		JSONObject json = JSONObject.parseObject(invite.msgJson);
		String groupId = json.getString(MsgKeys.groupId);
		entity.setGroupId(groupId);
		entity.setGroupName(json.getString(MsgKeys.groupName));
		entity.setTopic(json.getString(MsgKeys.groupTopic));
		entity.setInvitorName(json.getString(MsgKeys.groupCreator));
		String groupIconPath = StaticValues.USER_HEADPATH + groupId + ".png";
				ImageUtil.getInstance().saveImage(context, invite.msgBytes, groupIconPath);
		entity.setGroupIconPath(groupIconPath);
		return entity;
	}
	
	
}




