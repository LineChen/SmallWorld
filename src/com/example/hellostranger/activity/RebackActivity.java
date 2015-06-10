package com.example.hellostranger.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hellostranger.R;

public class RebackActivity extends Activity {
	
	EditText et_reback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reback);
		et_reback = (EditText) findViewById(R.id.et_reback);
	}
	/**
	 * 发送反馈信息
	 * @param v
	 */
	public void onSendReback(View v){
		String value = et_reback.getText().toString().trim();
		if(value.equals("")){
			Toast.makeText(RebackActivity.this, "请输入反馈信息", 0).show();
		} else {
			MainActivity.myBinder.SendReback(value);
			Toast.makeText(RebackActivity.this, "发送成功", 0).show();
			finish();
		}
	}
	
	public void onBack(View v){
		finish();
	}
}







