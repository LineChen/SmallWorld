package com.personalinfo.activity;

import com.clientUtils.ClientManager;
import com.example.hellostranger.R;
import com.example.hellostranger.R.layout;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.activity.PersonalInfoActivity;
import com.msg_relative.iMoMoMsgTypes;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class NicknameEditActivity extends Activity {

	ImageButton ib_back;
	Button btn_submit;
	EditText et_nick;
	String nickname = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_nickname_edit);
		/**
		 * 返回
		 */
		ib_back = (ImageButton) findViewById(R.id.nick_ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		/**
		 * 获得昵称
		 */
		et_nick = (EditText) findViewById(R.id.nick_et_nickname);
		
		/**
		 * 向数据库提交
		 */
		btn_submit = (Button) findViewById(R.id.nick_submit);
		btn_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				nickname = et_nick.getText().toString();
				if(!nickname.equals("")){
					MainActivity.myBinder.ResetUserInfo(iMoMoMsgTypes.RESET_USERNAME, nickname);
					ClientManager.clientName = nickname;
					PersonalInfoActivity.refreshClientInfo(iMoMoMsgTypes.RESET_USERNAME, nickname);
					MainActivity.refreshPinfo(iMoMoMsgTypes.RESET_USERNAME, nickname);
					finish();
				}else {
					Toast.makeText(NicknameEditActivity.this, "请输入昵称", 0).show();
				}
			}
		});
	}

}
