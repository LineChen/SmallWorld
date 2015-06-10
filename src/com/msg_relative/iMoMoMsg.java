package com.msg_relative;

/**
 * symbol = '+' : 纯本文件
 * symbol = '-' : 非纯本文件
 * @author Administrator
 */
/** 和服务器间通讯的消息类 */
public class iMoMoMsg {
	/** symbol = '+' : 纯本文件 symbol = '-' : 非纯本文件 */
	public char symbol;// 判断是否是纯文本文件
	/** 包含非文本信息详情的解释 */
	public String msgJson;
	/** 非文本信息 */
	public byte[] msgBytes;

	@Override
	public String toString() {
		return "iMoMoMsg [symbol=" + symbol + ", msgJson=" + msgJson + "]";
	}

}
