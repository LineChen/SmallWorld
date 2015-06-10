package com.static_configs;

/**
 * 一些静态常量
 * 
 * @author Administrator
 * 
 */
public interface StaticValues {
	int FACE_GETTED = 100; // 点击表情
	int VOICE_REC_TIME = 101;// 录音时，实时更新录音时间

	int SESSION_OPENED = 102;// 和服务器连接成功

	// String SERVER_IP = "192.168.137.190";
	String SERVER_IP = "192.168.23.1";
//	String SERVER_IP = "10.50.44.130";
	int SERVER_PORT = 9090;

	String VOICEPATH = "/sdcard/iMoMo/voiceRecord/";// 存放语音消息的文件夹
	String IMAGEPATH = "/sdcard/iMoMo/imageRecord/";// 存放图片消息的文件夹
	String USER_HEADPATH = "/sdcard/iMoMo/userHead/";// 用户头像文件夹
	String MSG_TEXT = "/sdcard/iMoMo/msgText/";//导出的text消息文件

	String sharePreName = "com.imomo";// SharePreferance保存名
	String userEmail = "userEmail";// SharePreferance保存键
	String userPasswd = "userPasswd";// SharePreferance保存键

	int DEL_EDIT_TEXT = 3333;//删除
	
	int NET_DELAY = 3500;//网络延迟
	
	
	
}



