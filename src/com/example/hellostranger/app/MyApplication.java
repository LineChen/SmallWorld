package com.example.hellostranger.app;

import android.app.Application;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.clientUtils.ClientManager;
import com.example.hellostranger.util.SharePreferenceUtil;

public class MyApplication extends Application {

	public final static String SP_NAME = "fiWrR2Ki8NkR6r5GHdM2lY7j";
	private static MyApplication mApplication;
	SharePreferenceUtil mSpUtil;

	// ///////////////��λ���
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	BDLocation mlocation;

	public synchronized static MyApplication getInstance() {
		return mApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		initMap();
		SDKInitializer.initialize(this);
		initData();
	}

	private void initMap() {
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��GPS
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(5 * 60 * 1000);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	private void initData() {
		mSpUtil = new SharePreferenceUtil(this);
	}

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null)
			mSpUtil = new SharePreferenceUtil(this);
		return mSpUtil;
	}

	public synchronized BDLocation getMyLocation() {
		return mlocation;
	}

	/**
	 * ʵ��ʵλ�ص�����,�õ�λ��5���Ӹ���һ��λ��
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			mlocation = location;
			ClientManager.province = mlocation.getProvince();
			ClientManager.Longitude = mlocation.getLongitude();
			ClientManager.Latitude = mlocation.getLatitude();
			ClientManager.myLocation = mlocation.getAddrStr();
		}
	}

}
