package com.clientUtils;

import android.content.SharedPreferences;

public class ClientManager {

	/** 本机登录的用户的基本信息 */
	public static String clientId = "";// 本机登录Id
	public static String clientEmail = "";// 本机登录userEmail
	public static String clientName = "";// 本机登录用户名
	public static String clientSex = "";//
	public static String clientBirthday = "";
	public static String personSignature = "";// 个性签名
	
	public static int vitalityValue ;//活力值

	public static String province = "";// 用户所在省
	public static double Longitude;// 用户经纬度
	public static double Latitude;
	
	public static String myLocation;//具体地理位置

	public static boolean isOnline;// 记录在线状态
	
	
	public static boolean isRing;//是否提示响铃
	public static boolean isVibration;//是否开启震动
	public static boolean isShareLoc;//是否发送自己的位置

}
