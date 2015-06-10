package com.strangerlist;

/**
 * 陌生人类
 * @author Administrator
 *
 */
public class StrangerBean {
	public String strangerId;//陌生人Id
	public String strangerName;//陌生人姓名
	public String strangerLoc;//位置(直接显示多少公里之内的)
	//经纬度
	public double Longitude;
	public double Latitude;
	@Override
	public String toString() {
		return "StrangerBean [strangerId=" + strangerId + ", strangerName="
				+ strangerName + ", strangerLoc=" + strangerLoc
				+ ", Longitude=" + Longitude + ", Latitude=" + Latitude + "]";
	}
	
}



