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

public class SignatureEditActivity extends Activity {

	ImageButton ib_back;
	Button btn_submit;
	EditText et_signature;//nick_et_signature
	String signature = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signature_edit);
		/**
		 * 返回
		 */
		ib_back = (ImageButton) findViewById(R.id.signature_ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		/**
		 * 获得昵称
		 */
		et_signature = (EditText) findViewById(R.id.nick_et_signature);
		
		/**
		 * 向数据库提交
		 */
		btn_submit = (Button) findViewById(R.id.signature_submit);
		btn_submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				signature = et_signature.getText().toString();
				if(!signature.equals("")){
					MainActivity.myBinder.ResetUserInfo(iMoMoMsgTypes.RESET_SIGNATUE, signature);
					ClientManager.personSignature = signature;
					PersonalInfoActivity.refreshClientInfo(iMoMoMsgTypes.RESET_SIGNATUE, signature);
					MainActivity.refreshPinfo(iMoMoMsgTypes.RESET_SIGNATUE, signature);
					finish();
				}else {
					Toast.makeText(SignatureEditActivity.this, "请输入个性签名", 0).show();
				}
			}
		});
	}

}
