package com.freind;

import java.io.Serializable;

/**
 * 好友列表item
 * @author Administrator
 *
 */
public class FriendBean implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String friendId;//好友Id
	private String friendName;//好友名
	private String friendHeadPath;//好友头像地址
	private String friendSex;//好友性别
	private String friendBirthday;//好友生日
	private String friendSignature;//好友签名
	private String sortLetters;////显示数据拼音的首字母
	
	public FriendBean(){}

	public FriendBean(String friendId, String friendName,
			String friendHeadPath, String friendSex, String friendBirthday,
			String friendSignature, String sortLetters) {
		this.friendId = friendId;
		this.friendName = friendName;
		this.friendHeadPath = friendHeadPath;
		this.friendSex = friendSex;
		this.friendBirthday = friendBirthday;
		this.friendSignature = friendSignature;
		this.sortLetters = sortLetters;
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

	public String getFriendHeadPath() {
		return friendHeadPath;
	}

	public void setFriendHeadPath(String friendHeadPath) {
		this.friendHeadPath = friendHeadPath;
	}

	public String getFriendSex() {
		return friendSex;
	}

	public void setFriendSex(String friendSex) {
		this.friendSex = friendSex;
	}

	public String getFriendBirthday() {
		return friendBirthday;
	}

	public void setFriendBirthday(String friendBirthday) {
		this.friendBirthday = friendBirthday;
	}

	public String getFriendSignature() {
		return friendSignature;
	}

	public void setFriendSignature(String friendSignature) {
		this.friendSignature = friendSignature;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	@Override
	public String toString() {
		return "FriendBean [friendId=" + friendId + ", friendName="
				+ friendName + ", friendHeadPath=" + friendHeadPath
				+ ", friendSex=" + friendSex + ", friendBirthday="
				+ friendBirthday + ", friendSignature=" + friendSignature
				+ ", sortLetters=" + sortLetters + "]";
	}
}
