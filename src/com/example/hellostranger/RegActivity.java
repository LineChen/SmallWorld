//package com.example.hellostranger;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//
//public class RegActivity extends Activity {
//
//	EditText username;
//	EditText email;
//	EditText birday;
//	RadioButton rbm;
//	RadioButton rbw;
//	EditText pwd;
//	EditText pwda;
//	ImageView iv;
//	Button btn_submit;
//	Bitmap hp;
//	String s_username;
//	String s_email;
//	String s_birday;
//	String s_sex;
//	String s_pwd;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_reg);
//		iv = (ImageView) findViewById(R.id.reg_iv_hp);
//		username = (EditText) findViewById(R.id.reg_et_username);
//		email = (EditText) findViewById(R.id.reg_et_email);
//		birday = (EditText) findViewById(R.id.reg_et_bir);
//		rbm = (RadioButton) findViewById(R.id.regtwo_rb_boy);
//		rbw = (RadioButton) findViewById(R.id.regtwo_rb_girl);
//		pwd = (EditText) findViewById(R.id.reg_et_pwd);
//		pwda = (EditText) findViewById(R.id.reg_et_pwda);
//		btn_submit = (Button) findViewById(R.id.reg_btn_submit);
//		btn_submit.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				getSex();
//				getUsername();
//				getEmail();
//				getBir();
//				getPwd();
//			}
//		});
//	}
//	
//	private void getUsername(){
//		s_username = username.getText().toString();
//	}
//	
//	private void getEmail(){
//		s_birday = birday.getText().toString();
//	}
//	
//	private void getBir(){
//		s_email = email.getText().toString();
//	}
//	
//	private void getPwd(){
//		s_email = email.getText().toString();
//		String p1 = pwd.getText().toString();
//		String p2 = pwda.getText().toString();
//		if (p1.equals(p2))
//			s_pwd = p1;
//	}
//	
//	private void getSex(){
//		if (rbm.isChecked()){
//			s_sex = rbm.getText().toString();
//		} else if(rbw.isChecked()) {
//			s_sex = rbw.getText().toString();
//		} else {
//			s_sex = null;
//		}
//	}
//}
