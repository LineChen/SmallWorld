package com.personalinfo.activity;

import com.example.hellostranger.R;
import com.example.hellostranger.R.layout;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.util.PasswordUtil;
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

public class ChangePasswdActivity extends Activity {

	ImageButton ib_back;
	EditText change_et_pwd1;
	String pwd1;
	EditText change_et_pwd2;
	String pwd2;
	Button btn_submit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_change_passwd);
		/**
		 * 返回
		 */
		ib_back = (ImageButton) findViewById(R.id.change_ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		/**
		 * 获取密码
		 */
		change_et_pwd1 = (EditText) findViewById(R.id.change_et_pwd1);
		
		
		change_et_pwd2 = (EditText) findViewById(R.id.change_et_pwd2);
		
		/**
		 * 提交
		 */
		btn_submit = (Button) findViewById(R.id.change_btn_submit);
		btn_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				pwd1 = change_et_pwd1.getText().toString().trim();
				if(pwd1.equals("")){
					Toast.makeText(ChangePasswdActivity.this, "请输入密码", 0).show();
					return;
				}
				if(pwd1.length() < 6){
					Toast.makeText(ChangePasswdActivity.this, "密码小于6位", 0).show();
					return;
				}
				pwd2 = change_et_pwd2.getText().toString().trim();
				if(!pwd2.equals(pwd1)){
					Toast.makeText(ChangePasswdActivity.this, "两次密码输入不一致", 0).show();
					return;
				}
				MainActivity.myBinder.ResetUserInfo(iMoMoMsgTypes.RESET_PASSWD, PasswordUtil.toMD5(pwd2));
				finish();
			}
		});
	}

}
