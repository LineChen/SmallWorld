package com.msg_relative;

public interface iMoMoMsgTypes {
	int REGISTER = 0;// 注册
	int REGISTER_SUCCESS = 1;// 注册成功
	int REGISTER_FAILED = 2;// 注册失败

	int LOGIN = 3;// 登录
	int LOGIN_SUCCESS = 4;// 登录成功
	int LOGIN_FAILED = 5;// 登录失败

	int LOGIN_SUPER_HEAD = 6;// 超级登录,用户手机有用户图像,无需获取头像
	int LOGIN_SUPER_NOHEAD = 7;//超级登录,用户手机无用户图像,需要从服务器获取头像等信息
	int LOGIN_SUPER_SUCCESS = 8;// 成功
	int LOGIN_SUPER_FAILED = 9;// 失败

	int LOGOUT = 10;// 用户下线

	int FIND_PASSWD = 11;// 找回密码
	int FIND_PASSWD_SUCCESS = 12;// 已发送找回密码邮件
	int FIND_PASSWD_FAILED = 13;// 找回密码失败

	int RESET_PASSWD = 14;// 重置密码
	int RESET_PASSWD_SUCCESS = 15;// 重置密码成功
	int RESET_PASSWD_FAILED = 16;// 重置密码失败

	int RESET_USERNAME = 17;// 修改用户名
//	int RESET_USERNAME_SUCCESS = 18;// 修改用户名成功
//	int RESET_USERNAME_FAILED = 19;// 修改用户名失败
	
	int RESET_SEX = 200;//修改性别
	int RESET_BIRTHDAY = 201;//修改生日
	int RESET_SIGNATUE = 202;//修改个人签名

	int RESET_HEAD = 20;// 修改头像
	int RESET_HEAD_SUCCESS = 21;// 修改头像成功
	int RESET_HEAD_FAILED = 22;// 修改头像失败

	int CHATING_TEXT_MSG = 23;// 聊天信息-文本信息
	int CHATING_VOICE_MSG = 24;// 聊天信息-语音信息
	int CHATING_IMAGE_MSG = 25;// 聊天信息-图片信息
	
	int CONNECT_DOWN = 26;//和服务器的连接断开
	
	int LOCATION = 27;//用户的地理位置
	
	
	int GET_STRANGERS_LOC_ONEKM = 28;//请求得到周围一公里陌生人地理位置
	int GET_STRANGERS_LOC_MORE =40; //大于一公里的人
	int STRANGERS_LIST_ONEKM = 31;//一公里内陌生人列表
	int STRANGERS_LIST_MORE = 38;//更多附近的人
	int NO_STRANGERS = 32;//周围无陌生人
	
	int GET_STRANGER_INFO = 50;//获取陌生人个人信息
	
	int ADD_FRIEND = 33;//添加好友
	int ADD_FRIEND_SUCCESS = 36;//添加成功
	int ADD_FRIEND_FAILED = 37;//添加成功
	int DELETE_FRIEND = 35;//删除好友
	
	int GET_FRIEND_ID_LIST = 34;//获得好友Id列表
	int FRIEND_ID_LIST = 42;//好友Id列表
	int GETA_FRIEND_INFO_HEAD = 29;//得到一个陌生人的具体信息(本地有图像,服务器不用发送头像)
	int GETA_FRIEND_INFO_NOHEAD = 30;//得到一个陌生人的具体信息(本地无图像,服务器要发头像)
	
	
	int REBACK = 51;//用户反馈
	
	
	int GROUP_INVITE = 52;//群聊天邀请
	
	int SIGN = 55;//每日签到
	
	int CREATE_GROUP = 56;//创建群组
	int CREATE_GROUP_SUCCESS = 57;
	int CREATE_GROUP_FAILED = 58;
	
	int INVITE_TO_GROUP = 59;//邀请加入多人聊天
	
	int AGREEE_TO_GROUP = 60;//同意加入群组
	int GROUP_MSG = 61;
	
	
	int PASS_GAME = 62;//活力值通关
}



