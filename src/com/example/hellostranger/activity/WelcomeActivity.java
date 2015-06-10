package com.example.hellostranger.activity;


import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.clientUtils.iMoMoDataBase;
import com.example.hellostranger.R;
import com.example.hellostranger.util.SharePreferenceUtil;
import com.msg_relative.iMoMoMsgDb;
import com.static_configs.StaticValues;

public class WelcomeActivity extends Activity {

	private int splashDelay = 3000;
	private ImageView iv_welcome;
	MyAnimationListener myAnimationListener;
	Intent intent;
	Intent intent_server;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getActionBar().hide();
		setContentView(R.layout.activity_welcome);
		iv_welcome = (ImageView) findViewById(R.id.welcome_iv_welcome);
		myAnimationListener = new MyAnimationListener();
		startanimation();
		checkLogin();
		
		iMoMoDataBase db = new iMoMoDataBase(this);
		//这个表是公用的，
		if(!db.isTableExists("latice_chat_table")){
			db.createLaticeChatTable();
		}
		
		/**测试*/
//		Log.i("--","9091 存在吗? : " + db.isExistsLatice("9091"));
		
		
//		iMoMoDataBase db = new iMoMoDataBase(this);
//		Log.i("--","111isTableExists latice_chat_table = " + db.isTableExists("latice_chat_table"));
//		if(!db.isTableExists("latice_chat_table")){
//			db.createLaticeChatTable();
//			Log.i("--","不存在，创建.");
//		}
//		ChatInfoEntity entity = new ChatInfoEntity();
//		entity.setFriendId("9091");
//		entity.setFriendName("皇子");
//		entity.setChatContent("hello 皇子");
//		entity.setChatCreatTime("2015-9-09");
//		entity.setMsg_num(9);
//		db.insertLaticeChat(entity);
//		db.deleteLaticeItem("9091");
//		List<ChatInfoEntity> list = db.getLaticeChatList();
//		for (ChatInfoEntity enty : list) {
//			Log.i("--", "enty = " + enty.toString());
//		}
		
//		Log.i("--","111isTableExists ttl = " + dataBase.isTableExists("ttl"));
//		if(!dataBase.isTableExists("ttl"))
//			dataBase.createMsgTable("ttl");
//		Log.i("--","111isTableExists ttl = " + dataBase.isTableExists("ttl"));
//		dataBase.clearMsg("ttl");
//		for(int i = 0; i < 5; i++){
//			iMoMoMsgDb msgDb = new iMoMoMsgDb();
//			msgDb.isGetted = 1;
//			msgDb.isLooked = 0;
//			msgDb.msgJson = "////";
//			msgDb.sendTime = i + "";
//			msgDb.msgType = 9;
//		boolean ok = dataBase.insertMsg(msgDb, "ttl");
//		Log.i("--","ok = " + ok+"");
//		}
//		dataBase.updateLooked("ttl");
//		Log.i("--", dataBase.getCount("ttl") + "条数据");
//		List<iMoMoMsgDb> list  = dataBase.getMsgNotLooked("ttl");
//		List<iMoMoMsgDb> list2  = dataBase.getMsgLatice("ttl", 0, 0);
//		for(iMoMoMsgDb moDb : list2){
//			Log.i("--", moDb.toString());
//		}
		
		
		
	}

	
	
	private void checkLogin() {
//		SharedPreferences preferences = this.getSharedPreferences(
//				StaticValues.sharePreName, Activity.MODE_PRIVATE);
//		String userEmail = preferences.getString(StaticValues.userEmail, "");
//		String userPasswd = preferences.getString(StaticValues.userPasswd, "");
		SharePreferenceUtil util = new SharePreferenceUtil(this);
		String userEmail = util.GetValue(StaticValues.userEmail);
		String userPasswd = util.GetValue(StaticValues.userPasswd);
		
		if ("".equals(userEmail) || "".equals(userPasswd)) {
			intent = new Intent(this, SignInActivity.class);
		} else {
			intent = new Intent(this, MainActivity.class);
		}
	}

	private void startanimation() {
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(splashDelay);
		scaleAnimation.setFillAfter(true);
		scaleAnimation.setAnimationListener(myAnimationListener);
		animationSet.addAnimation(scaleAnimation);
		iv_welcome.startAnimation(animationSet);
	}

	private class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);  
			finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
}
