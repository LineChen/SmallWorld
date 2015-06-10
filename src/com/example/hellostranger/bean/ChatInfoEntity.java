package com.example.hellostranger.bean;

import android.net.Uri;

public class ChatInfoEntity {
	String friendId;//����������ȺId
	String friendName;//�û��� ��Ⱥ�M��
	String chatCreatTime;//���һ��������Ϣʱ��
	String chatContent;//���һ��������Ϣ������
	int msg_num;//δ����Ϣ��
	/**0 = ��ͨ������Ϣ  1 = ϵͳ��Ϣ��Ⱥ������ ; 2 = Ⱥ��������Ϣ  **/
	int Msgtype;//��Ϣ����
	
	public ChatInfoEntity() {

	}

	public ChatInfoEntity(String friendId, String friendName,
			String chatCreatTime, String chatContent, int msg_num,
			int Msgtype) {
		this.friendId = friendId;
		this.friendName = friendName;
		this.chatCreatTime = chatCreatTime;
		this.chatContent = chatContent;
		this.msg_num = msg_num;
		this.Msgtype = Msgtype;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getChatCreatTime() {
		return chatCreatTime;
	}

	public void setChatCreatTime(String chatCreatTime) {
		this.chatCreatTime = chatCreatTime;
	}

	public String getChatContent() {
		return chatContent;
	}

	public void setChatContent(String chatContent) {
		this.chatContent = chatContent;
	}

	public int getMsg_num() {
		return msg_num;
	}

	public void setMsg_num(int msg_num) {
		this.msg_num = msg_num;
	}

	public int getMsgtype() {
		return  Msgtype;
	}
	
	/**0 = ��ͨ������Ϣ  1 = ϵͳ��Ϣ��Ⱥ������*/
	public void setMsgtype(int Msgtype) {
		this.Msgtype =  Msgtype;
	}

}







