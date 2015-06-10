package com.example.hellostranger.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chat.FileUtils;
import com.clientUtils.ClientManager;
import com.clientUtils.ClientUtils;
import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.R;
import com.example.hellostranger.util.myDialog;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsgDb;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

/**
 * �����������ת�ĺ�����Ϣ����
 * 
 * @author Administrator
 * 
 */
public class FriendMsgSettingActivity extends Activity {

	String friendId;
	String friendName;

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_msg_setting);
		context = this;
		Intent intent = getIntent();
		friendId = intent.getStringExtra("friendId");
		friendName = intent.getStringExtra("friendName");
	}

	public void onClick(View v) {
		if (v.getId() == R.id.setting_ib_back) {
			finish();
		} else if (v.getId() == R.id.rl_clearMsg) {
			clearMsg();
		} else if (v.getId() == R.id.rl_toTxt) {
			MsgToText();
		}
	}

	/**
	 * �����Ϣ
	 */
	public void clearMsg() {
		myDialog dialog = new myDialog(context, "�����...");
		dialog.show();
		// ��ոú��ѵ�����������Ϣ
		iMoMoDataBase db = new iMoMoDataBase(context);
		String tName = "msg" + ClientManager.clientId + "_" + friendId;
		if (db.isTableExists(tName)) {
			db.clearMsg(friendId);
			ChatActivity.clearMsg();
		}
		dialog.dismiss();
		Toast.makeText(context, "������", 0).show();
				
	}

	/**
	 * �����������ļ�
	 */
	public void MsgToText() {
		myDialog dialog = new myDialog(context, "������...");
		dialog.show();
		iMoMoDataBase db = new iMoMoDataBase(context);
		// ������Ϣ�������ļ�
		List<iMoMoMsgDb> list = db.getMsgLatice(friendId, 0,
				db.getMsgCount(friendId));
		try {
			if (list.size() > 0) {
				FileUtils.saveMsgToText(list, friendName);
				Toast.makeText(context,
						"�ѱ��浽" + StaticValues.MSG_TEXT + "�ļ�����", 0).show();
			}
		} catch (Exception e) {
			Toast.makeText(context, "����ʧ��", 0).show();
			e.printStackTrace();
		}
		dialog.dismiss();
	}
}
