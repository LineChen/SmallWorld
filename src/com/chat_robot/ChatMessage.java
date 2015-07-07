package com.chat_robot;

import java.util.Date;

public class ChatMessage {
	// private String name;
	private String msg;
	private Type type;
	private String date;

	public enum Type {
		INCOMING, OUTCOMING// Ω” ’°¢∑¢ÀÕ
	}

	public ChatMessage() {
	}

	public ChatMessage(String msg, Type type, String date) {
		this.msg = msg;
		this.type = type;
		this.date = date;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
