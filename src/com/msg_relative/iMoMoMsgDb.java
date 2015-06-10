package com.msg_relative;

/**
 * 客户端保存到本地数据库的消息类
 * 
 * @author Administrator
 * 
 */
public class iMoMoMsgDb {
	public int msgType;// 消息类型，文本消息，图片消息，语音消息
	public String msgJson;// 包含非文本信息的解释
	public String sendTime;// 消息接收或发送的时间
	public int isGetted;// 是否是别人发送的消息,根据这个判断信息显示在屏幕左侧或是右侧
	public int isLooked;// 消息是否被查看 (0 = false , 1 = true)

	@Override
	public String toString() {
		return "iMoMoMsgDb [msgType=" + msgType + ", msgJson=" + msgJson
				+ ", sendTime=" + sendTime + ", isGetted=" + isGetted
				+ ", isLooked=" + isLooked + "]";
	}

}
