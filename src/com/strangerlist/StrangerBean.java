package com.strangerlist;

/**
 * İ������
 * @author Administrator
 *
 */
public class StrangerBean {
	public String strangerId;//İ����Id
	public String strangerName;//İ��������
	public String strangerLoc;//λ��(ֱ����ʾ���ٹ���֮�ڵ�)
	//��γ��
	public double Longitude;
	public double Latitude;
	@Override
	public String toString() {
		return "StrangerBean [strangerId=" + strangerId + ", strangerName="
				+ strangerName + ", strangerLoc=" + strangerLoc
				+ ", Longitude=" + Longitude + ", Latitude=" + Latitude + "]";
	}
	
}



