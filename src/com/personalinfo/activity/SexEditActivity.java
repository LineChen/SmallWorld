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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SexEditActivity extends Activity {

	ImageButton ib_back;
	Button btn_submit;
	RelativeLayout rl_boy;
	ImageView iv_boy;
	TextView tv_boy;
	RelativeLayout rl_girl;
	ImageView iv_girl;
	TextView tv_girl;
	String sex = ClientManager.clientSex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sex_edit);
		/**
		 * 返回
		 */
		ib_back = (ImageButton) findViewById(R.id.sex_ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		/**
		 * 选择性别
		 */
		iv_boy = (ImageView) findViewById(R.id.sex_boy_checked);
		iv_girl = (ImageView) findViewById(R.id.sex_girl_checked);
		tv_boy = (TextView) findViewById(R.id.sex_tv_boy);
		tv_girl = (TextView) findViewById(R.id.sex_tv_girl);
		
		rl_boy = (RelativeLayout) findViewById(R.id.sex_rl_boy);
		rl_boy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				iv_boy.setVisibility(View.VISIBLE);
				iv_girl.setVisibility(View.GONE);
				sex = tv_boy.getText().toString();
			}
		});
		
		rl_girl = (RelativeLayout) findViewById(R.id.sex_rl_girl);
		rl_girl.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				iv_boy.setVisibility(View.GONE);
				iv_girl.setVisibility(View.VISIBLE);
				sex = tv_girl.getText().toString();
			}
		});
		
		btn_submit = (Button) findViewById(R.id.sex_btn_submit);
		btn_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.myBinder.ResetUserInfo(iMoMoMsgTypes.RESET_SEX, sex);
				ClientManager.clientSex = sex;
				PersonalInfoActivity.refreshClientInfo(iMoMoMsgTypes.RESET_SEX, sex);
				finish();
			}
		});
	}

}
