package com.msg_relative;

/**
 * JSON���ݵļ�
 * 
 * @author Administrator
 * 
 */
public interface MsgKeys {
	String msgType = "msgType";// ��Ϣ����
	String userId = "userId";// ������Id
	String friendId = "friendId";// ������Id
	String friendName = "friendName";// ������

	String sendTime = "sendTime";// ����ʱ��
	String msgCotent = "msgCotent";// ������Ϣ-�ı���Ϣ
	String voiceTime = "voiceTime";// ������Ϣ-������Ϣ����
	String voicePath = "voicePath";// ������Ϣ-�����ļ�·��
	String imagePath = "imagePath";// ������Ϣ-ͼƬ·��

	String userEmail = "userEmail";// �û�ע�����䣬��¼ʱ�������¼
	String userName = "userName";// �û���
	String userSex = "userSex";// �û��Ա�
	String userBirthday = "userBirthday";// �û�����
	String userPasswd = "userPasswd";// ��¼����
	String personSignature = "personSignature";// ����ǩ��
	String vitalityValue = "vitalityValue";// ����ֵ

	String userHeadPath = "userHeadPath";// �û�ͷ��·��

	String loc_province = "loc_province";// ����ʡ��
	String loc_Longitude = "loc_Longitude";// ����
	String loc_Latitude = "loc_Latitude";// γ��

	String distRange = "distRange";// ���ٹ���֮�ڵ�

	String strangerList = "strangerList";// İ�����б�

	String friendIdList = "friendIdList";// ����Id�б�

	String groupId = "groupId";// Ⱥ��Id
	String groupName = "groupName";// Ⱥ����
	String groupTopic = "groupTopic";// Ⱥ������
	String groupCreator = "groupCreator";//������

	String isGroupMsg = "isGroupMsg";

}
