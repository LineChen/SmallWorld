package com.msg_relative;

/**
 * �ͻ��˴������Ϣ��
 * @author Administrator
 * 
 */
public class iMoMoMsgClient {
	
	private String msgJson;// �������ı���Ϣ����Ľ���
//	private byte[] msgBytes;// ���ı���Ϣ
	private boolean isGetted;// �Ƿ��Ǳ��˷��͵���Ϣ,��������ж���Ϣ��ʾ����Ļ�������Ҳ�
	public String getMsgJson() {
		return msgJson;
	}

	public void setMsgJson(String msgJson) {
		this.msgJson = msgJson;
	}

	public boolean isGetted() {
		return isGetted;
	}

	public void setGetted(boolean isGetted) {
		this.isGetted = isGetted;
	}

}
