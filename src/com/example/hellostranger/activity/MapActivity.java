package com.example.hellostranger.activity;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.hellostranger.R;

public class MapActivity extends Activity {

	MapView mMapView = null;
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	public MyLocationListener myListener = new MyLocationListener();
	boolean isFirstLoc;
	BitmapDescriptor mCurrentMarker;
	private LocationMode mCurrentMode;
	
	private Marker mMarkerA;
	BitmapDescriptor bdA;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_map);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		isFirstLoc = true;
		mCurrentMarker = null;
		mCurrentMode = LocationMode.NORMAL;
		bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);		// 打开GPS
		option.setCoorType("bd09ll"); 	// 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		System.out.println("mLocClient启动");
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		mLocClient.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
//		unbindService(serviceConnection);
		mMapView = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			System.out.println("进入监听器");
			if (location == null || mMapView == null){
				System.out.println("进入监听器，但条件为空");
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius()).direction(100)
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			System.out.println("设置位置");
			mBaiduMap.setMyLocationData(locData);
//			if(isFirstLoc){
//				isFirstLoc = false;
				LatLng ll= new LatLng(location.getLatitude(),location.getLongitude());
				LatLng stranger = new LatLng(location.getLatitude()+0.002,location.getLongitude()+0.02);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				System.out.println("执行位置更改动画");
				mBaiduMap.animateMapStatus(u);
				OverlayOptions ooA = new MarkerOptions().position(stranger).icon(bdA)
						.zIndex(9).draggable(true);
				mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
//			}
		}

	}
}
