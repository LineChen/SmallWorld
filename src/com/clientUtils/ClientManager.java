package com.clientUtils;

import android.content.SharedPreferences;

public class ClientManager {

	/** ������¼���û��Ļ�����Ϣ */
	public static String clientId = "";// ������¼Id
	public static String clientEmail = "";// ������¼userEmail
	public static String clientName = "";// ������¼�û���
	public static String clientSex = "";//
	public static String clientBirthday = "";
	public static String personSignature = "";// ����ǩ��
	
	public static int vitalityValue ;//����ֵ

	public static String province = "";// �û�����ʡ
	public static double Longitude;// �û���γ��
	public static double Latitude;
	
	public static String myLocation;//�������λ��

	public static boolean isOnline;// ��¼����״̬
	
	
	public static boolean isRing;//�Ƿ���ʾ����
	public static boolean isVibration;//�Ƿ�����
	public static boolean isShareLoc;//�Ƿ����Լ���λ��

}
