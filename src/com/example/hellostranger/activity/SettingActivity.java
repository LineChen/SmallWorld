package com.example.hellostranger.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.clientUtils.ClientManager;
import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.R;
import com.example.hellostranger.app.MyApplication;
import com.example.hellostranger.util.SharePreferenceUtil;
import com.example.hellostranger.util.myDialog;
import com.example.hellostranger.view.SwitchButton;
import com.personalinfo.activity.ChangePasswdActivity;

public class SettingActivity extends Activity {

	ImageButton ib_back;
	RelativeLayout rl_NameManage;
	SwitchButton sb_voice;
	RelativeLayout rl_music;
	SwitchButton sb_shake;
	SwitchButton sb_AddressShow;
	RelativeLayout rl_changepasswd;
	SharePreferenceUtil mSpUtil;
	
	boolean isRing;
	boolean isVibration;
	boolean isShareLoc;
	
	Context context;
	
	/**
	 * 初始化设置
	 */
	public void initSettins(){
		isRing = ClientManager.isRing;
		isVibration =  ClientManager.isVibration;
		isShareLoc = ClientManager.isShareLoc;
		
		sb_voice.setChecked(!isRing);
		sb_AddressShow.setChecked(!isShareLoc);
		sb_shake.setChecked(!isVibration);
	}
	
	public void initView(){
		/**
		 * 是否开启声音
		 */
		sb_voice = (SwitchButton) findViewById(R.id.setting_sb_voice);
		/**
		 * 是否开启震动
		 */
		sb_shake = (SwitchButton) findViewById(R.id.setting_sb_shake);
		/**
		 * 是否开启位置信息
		 */
		sb_AddressShow = (SwitchButton) findViewById(R.id.setting_sb_addressshow);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		context = this;
		mSpUtil = new SharePreferenceUtil(SettingActivity.this);
		initView();
		initSettins();
	
		sb_voice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mSpUtil.setIsRing(false);
					ClientManager.isRing = false;
					Log.i("--" ,"isChecked");
				} else {
					mSpUtil.setIsRing(true);
					ClientManager.isRing = true;
					Log.i("--" ,"isChecked  not");
				}
			}
		});
		
		
		
		sb_shake.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mSpUtil.setIsVibration(false);
					ClientManager.isVibration = false;
					System.out.println("close");
				} else {
					mSpUtil.setIsVibration(true);
					ClientManager.isVibration = true;
					System.out.println("open");
				}
			}
		});
		
		sb_AddressShow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					mSpUtil.setIsShareLoc(false);
					ClientManager.isShareLoc = false;
					System.out.println("close");
				} else {
					mSpUtil.setIsShareLoc(true);
					ClientManager.isShareLoc = true;
					System.out.println("open");
				}
			}
		});
		
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.setting_ib_back:
			finish();
			break;
		case R.id.setting_rl_changepasswd:
			Intent intent = new Intent(SettingActivity.this,
					ChangePasswdActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_clearAllmsg:
			ClearAllMsg();
			break;
		case R.id.rl_clearLatice:
			ClearLatice();
			break;
		}
	}

	/**
	 * 清空所有聊天记录
	 */
	private void ClearAllMsg() {
		myDialog dialog = new myDialog(context, "清空中...");
		dialog.show();
		// 清空该好友的所有聊天信息
		iMoMoDataBase db = new iMoMoDataBase(context);
		List<String> friendIds = MainActivity.mp1.getFriendsIds();
		if(friendIds.size() > 0){
			for(String Id : friendIds){
				String tName = "msg" + ClientManager.clientId + "_" + Id;
				if (db.isTableExists(tName)) {
					db.clearMsg(Id);
				}
			}
		}
		dialog.dismiss();
		Toast.makeText(context, "清空完成", 0).show();
	}
	
	/**
	 * 清空最近联系人
	 */
	public void ClearLatice(){
		myDialog dialog = new myDialog(context, "清空中...");
		dialog.show();
		MainActivity.mp0.ClearLatice();
		dialog.dismiss();
		Toast.makeText(context, "清空完成", 0).show();
	}
	
}




