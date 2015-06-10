package com.msg_relative;

/**
 * JSON数据的键
 * 
 * @author Administrator
 * 
 */
public interface MsgKeys {
	String msgType = "msgType";// 消息类型
	String userId = "userId";// 发送者Id
	String friendId = "friendId";// 接收者Id
	String friendName = "friendName";// 好友名

	String sendTime = "sendTime";// 发送时间
	String msgCotent = "msgCotent";// 聊天信息-文本信息
	String voiceTime = "voiceTime";// 聊天信息-语音信息长度
	String voicePath = "voicePath";// 聊天信息-语音文件路径
	String imagePath = "imagePath";// 聊天信息-图片路径

	String userEmail = "userEmail";// 用户注册邮箱，登录时用邮箱登录
	String userName = "userName";// 用户名
	String userSex = "userSex";// 用户性别
	String userBirthday = "userBirthday";// 用户生日
	String userPasswd = "userPasswd";// 登录密码
	String personSignature = "personSignature";// 个性签名
	String vitalityValue = "vitalityValue";// 活力值

	String userHeadPath = "userHeadPath";// 用户头像路径

	String loc_province = "loc_province";// 所处省份
	String loc_Longitude = "loc_Longitude";// 经度
	String loc_Latitude = "loc_Latitude";// 纬度

	String distRange = "distRange";// 多少公里之内的

	String strangerList = "strangerList";// 陌生人列表

	String friendIdList = "friendIdList";// 好友Id列表

	String groupId = "groupId";// 群组Id
	String groupName = "groupName";// 群组名
	String groupTopic = "groupTopic";// 群组主题
	String groupCreator = "groupCreator";//创建者

	String isGroupMsg = "isGroupMsg";

}
