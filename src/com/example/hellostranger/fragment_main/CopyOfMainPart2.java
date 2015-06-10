package com.example.hellostranger.fragment_main;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
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
import com.example.hellostranger.GameActivity;
import com.example.hellostranger.R;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.app.MyApplication;
import com.example.hellostranger.util.ShakeListener;
import com.example.hellostranger.util.ShakeListener.OnShakeListener;
import com.strangerlist.StrangerBean;
import com.strangerlist.StrangerListActivity;

@SuppressLint("ResourceAsColor")
public class CopyOfMainPart2 extends Fragment implements OnShakeListener {

	View view;
	MapView mMapView = null;
	BaiduMap mBaiduMap;
	boolean isFirstLoc;
	BitmapDescriptor mCurrentMarker;
	private LocationMode mCurrentMode;
	private Marker mMarkerA;
	BitmapDescriptor bdA;
	BitmapDescriptor userX;
	MyLocationData locData;
	MyOnMarkerClickListener myOnMarkerClickListener;
	private List<BitmapDescriptor> coll;
	TextView tv;
	int SHOWUSERMSG = 200;
	int splashDelay = 2000;

	Button bt_str_list;//
	ViewGroup anim;
	ShakeListener shakeListener;
	
	ImageView star0;
	ImageView star1;
	ImageView createGroup;
	boolean isRy = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("开始MainPart2的初始化");
		// SDKInitializer.initialize(getActivity().getApplicationContext());
		view = inflater.inflate(R.layout.fragment_part2, container, false);
		System.out.println("开始初始化MapView");
		mMapView = (MapView) view.findViewById(R.id.fra_p2_bmapView);
		shakeListener = new ShakeListener(getActivity());
		shakeListener.setOnShakeListener(this);
		anim = (ViewGroup) view.findViewById(R.id.fra_p2_layout_anim);
		System.out.println("完成初始化MapView");
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);// 设置地图放大比例
		mBaiduMap.setMapStatus(msu);
		isFirstLoc = true;
		mCurrentMarker = null;
		mCurrentMode = LocationMode.NORMAL;
		bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
		System.out.println("mLocClient启动");
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		myOnMarkerClickListener = new MyOnMarkerClickListener();
		mBaiduMap.setOnMarkerClickListener(myOnMarkerClickListener);

		// 列表查看陌生人
		bt_str_list = (Button) view.findViewById(R.id.bt_str_list);
		bt_str_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						StrangerListActivity.class);
				startActivity(intent);
			}
		});

		coll = new ArrayList<BitmapDescriptor>();
		star0 = (ImageView) view.findViewById(R.id.fra_p2_layout_start0);
		star1 = (ImageView) view.findViewById(R.id.fra_p2_layout_start1);
		new Thread(new Runnable() {

			MyHandler myHandler = new MyHandler();

			@Override
			public void run() {
				while (isRy) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.arg1 = 0;
					myHandler.sendMessage(msg);
				}
			}
		}).start();
		
		new Thread(new Runnable() {

			MyHandler myHandler = new MyHandler();

			@Override
			public void run() {
				while (isRy) {
					Message msg = new Message();
					msg.arg1 = 1;
					myHandler.sendMessage(msg);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		createGroup = (ImageView) view.findViewById(R.id.fra_p2_createGroup);
		createGroup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(getActivity(),CreateGroupActivity.class);
//				startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		int n = coll.size();
		for (int i = 0; i < n; i++) {
			coll.get(i).recycle();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		shakeListener.start();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		shakeListener.stop();
		mMapView.onPause();
	}

	public void setMyLoc(BDLocation location) {
		if (location == null) {
			return;
		}
		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius()).direction(100)
				.latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		LatLng stranger = new LatLng(location.getLatitude() + 0.002,
				location.getLongitude() + 0.02);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		System.out.println("执行位置更改动画");
		mBaiduMap.animateMapStatus(u);
	}

	/**
	 * 在地图上设置陌生人
	 * 
	 */
	@SuppressLint("ResourceAsColor")
	public void setStrangerLoc(List<StrangerBean> list) {
		for (StrangerBean bean : list) {
			Log.i("--", "新增陌生人");
			LatLng ll = new LatLng(bean.Latitude, bean.Longitude);// 经纬度？
			TextView tv;
			tv = new TextView(getActivity());
			tv.setBackgroundResource(R.drawable.nickname_box);
			tv.setTextSize(20);
			tv.setIncludeFontPadding(false);
			tv.setText(bean.strangerName);
			tv.setTextColor(R.color.white);
			tv.setGravity(Gravity.CENTER);
			userX = BitmapDescriptorFactory.fromView(tv);
			tv.destroyDrawingCache();
			tv = null;
			coll.add(userX);
			OverlayOptions oo = new MarkerOptions().position(ll).icon(userX)
					.zIndex(9).draggable(true);
			mMarkerA = (Marker) (mBaiduMap.addOverlay(oo));
			Bundle b = new Bundle();
			b.putString("strangerName", bean.strangerName);
			b.putString("strangerId", bean.strangerId);
			mMarkerA.setExtraInfo(b);
		}
	}

	private class MyOnMarkerClickListener implements OnMarkerClickListener {

		@Override
		public boolean onMarkerClick(Marker marker) {
			Bundle b = marker.getExtraInfo();
//			final String strangerName = b.getString("strangerName");
			final String strangerId = b.getString("strangerId");
			new AlertDialog.Builder(getActivity())
					.setTitle("消息")
					.setMessage("完成游戏查看更多信息")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(getActivity(),
											GameActivity.class);
									intent.putExtra("strangerId", strangerId);
									startActivity(intent);
//									getActivity().startActivityForResult(
//											intent, SHOWUSERMSG);

								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}

							}).show();
			return true;
		}
	}

	@Override
	public void onShake() {
		if (!isHidden()) {
			isRy = false;
			anim.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);
			// ((MyApplication) getActivity().getApplication()).getMyLocation();
			setMyLoc(((MyApplication) getActivity().getApplication())
					.getMyLocation());

			/** 发送我的位置，请求得到陌生人列表 */
			MainActivity.myBinder.sendLocation();
			MainActivity.myBinder.getStrangerListOneKm();
			/** 测试--- **/
		}
	}
	
	class MyHandler extends Handler {

		Animation tanslateAnimation0;
		Animation tanslateAnimation1;

		public MyHandler() {
			tanslateAnimation0 = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF,
					-1f, Animation.RELATIVE_TO_SELF, -1f,
					Animation.RELATIVE_TO_SELF, 2f);
			tanslateAnimation0.setInterpolator(new AccelerateInterpolator());
			tanslateAnimation0.setDuration(splashDelay);
			tanslateAnimation0.setFillAfter(true);
			MyAnimationListener myAnimationListener0 = new MyAnimationListener();
			myAnimationListener0.setView(star0);
			tanslateAnimation0.setAnimationListener(myAnimationListener0);

			tanslateAnimation1 = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF,
					-1f, Animation.RELATIVE_TO_SELF, -1f,
					Animation.RELATIVE_TO_SELF, 3f);
			tanslateAnimation1.setInterpolator(new AccelerateInterpolator());
			tanslateAnimation1.setDuration(splashDelay);
			tanslateAnimation1.setFillAfter(true);
			MyAnimationListener myAnimationListener1 = new MyAnimationListener();
			myAnimationListener1.setView(star1);
			tanslateAnimation1.setAnimationListener(myAnimationListener1);

		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 0) {
				star0.startAnimation(tanslateAnimation0);
			} else {
				star1.startAnimation(tanslateAnimation1);
			}
		}

	}
	
	private class MyAnimationListener implements AnimationListener {

		ImageView view;

		public void setView(ImageView view) {
			this.view = view;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {

			Animation alphaAnimation = new AlphaAnimation(1f, 0f);
			alphaAnimation.setInterpolator(new AccelerateInterpolator());
			alphaAnimation.setDuration(splashDelay);
			alphaAnimation.setFillAfter(true);
			view.startAnimation(alphaAnimation);

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}
}
