package com.msg_relative;

/**
 * �ͻ��˱��浽�������ݿ����Ϣ��
 * 
 * @author Administrator
 * 
 */
public class iMoMoMsgDb {
	public int msgType;// ��Ϣ���ͣ��ı���Ϣ��ͼƬ��Ϣ��������Ϣ
	public String msgJson;// �������ı���Ϣ�Ľ���
	public String sendTime;// ��Ϣ���ջ��͵�ʱ��
	public int isGetted;// �Ƿ��Ǳ��˷��͵���Ϣ,��������ж���Ϣ��ʾ����Ļ�������Ҳ�
	public int isLooked;// ��Ϣ�Ƿ񱻲鿴 (0 = false , 1 = true)

	@Override
	public String toString() {
		return "iMoMoMsgDb [msgType=" + msgType + ", msgJson=" + msgJson
				+ ", sendTime=" + sendTime + ", isGetted=" + isGetted
				+ ", isLooked=" + isLooked + "]";
	}

}
