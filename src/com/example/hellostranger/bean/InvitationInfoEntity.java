package com.example.hellostranger.bean;

public class InvitationInfoEntity {

	String invitorName;
	String groupId;
	String groupName;
	String topic;
	String groupIconPath;

	// public int isLooked;// ��Ϣ�Ƿ񱻲鿴 (0 = false , 1 = true) ������ɾ�������Բ���Ҫ�����Ƿ�鿴

	public InvitationInfoEntity() {
	}

	public InvitationInfoEntity(String invitorName, String groupId,
			String groupName, String topic, String groupIconPath) {
		this.invitorName = invitorName;
		this.groupId = groupId;
		this.groupName = groupName;
		this.topic = topic;
		this.groupIconPath = groupIconPath;
	}

	public String getInvitorName() {
		return invitorName;
	}

	public void setInvitorName(String invitorName) {
		this.invitorName = invitorName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getGroupIconPath() {
		return groupIconPath;
	}

	public void setGroupIconPath(String groupIconPath) {
		this.groupIconPath = groupIconPath;
	}

	@Override
	public String toString() {
		return "InvitationInfoEntity [invitorName=" + invitorName
				+ ", groupId=" + groupId + ", groupName=" + groupName
				+ ", topic=" + topic + ", groupIconPath=" + groupIconPath + "]";
	}

}
