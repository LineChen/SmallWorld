package com.example.hellostranger.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chat.FileUtils;
import com.chat.ImageUtil;
import com.example.hellostranger.R;
import com.freind.FriendBean;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

public class FriendAddActivity extends Activity {

	ImageButton ib_back;
	TextView tv_setting;
	Button btn_add;

	static ImageView iv_headlogo;
	static TextView tv_nickname;
	static TextView tv_sex;
	static TextView tv_singnatrue;

	private String strangerId;
	static String name;
	static String sex;
	static String birthday;
	static String personSignature;

	static byte[] headByes;
	static Context context;

	public static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == iMoMoMsgTypes.GET_STRANGER_INFO) {
				iMoMoMsg friendInfo = (iMoMoMsg) msg.obj;
				JSONObject json = JSON.parseObject(friendInfo.msgJson);
				name = json.getString(MsgKeys.userName);
				sex = json.getString(MsgKeys.userSex);
				birthday = json.getString(MsgKeys.userBirthday);
				personSignature = json.getString(MsgKeys.personSignature);
				tv_nickname.setText(name);
				tv_sex.setText(sex);
				tv_singnatrue.setText(personSignature);
				headByes = friendInfo.msgBytes;
				iv_headlogo.setImageBitmap(ImageUtil.getInstance()
						.getBitMapFromByte(headByes));
			} else if (msg.what == iMoMoMsgTypes.ADD_FRIEND_SUCCESS) {
				Toast.makeText(context, "添加好友成功", 0).show();
			} else if (msg.what == iMoMoMsgTypes.ADD_FRIEND_FAILED) {
				Toast.makeText(context, "添加好友失败", 0).show();
			}

		};
	};

	public void initView() {
		iv_headlogo = (ImageView) findViewById(R.id.friendadd_iv_headlogo);
		tv_nickname = (TextView) findViewById(R.id.friendadd_tv_nickname);
		tv_sex = (TextView) findViewById(R.id.friendadd_tv_sexvalue);
		tv_singnatrue = (TextView) findViewById(R.id.friendadd_tv_singnatrue);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_friend_add);
		context = this;
		Intent intent = getIntent();
		strangerId = intent.getStringExtra("strangerId");
		Log.i("--", "FriendAddActivity id = " + strangerId);
		initView();
		// 获取陌生人个人信息
		MainActivity.myBinder.getStrangerInfo(strangerId);

		ib_back = (ImageButton) findViewById(R.id.friendadd_ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		btn_add = (Button) findViewById(R.id.friendadd_btn_friendadd);
		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.i("--", "添加好友");
				// 添加好友
				MainActivity.myBinder.addFriend(strangerId);

				// 保存该好友的头像到本地
				// 确认添加好友,刷新好友列表界面
				String headPath = StaticValues.USER_HEADPATH + strangerId
						+ ".png";
				ImageUtil.getInstance().saveImage(FriendAddActivity.this,
						headByes, headPath);
				FriendBean friendBean = new FriendBean();
				friendBean.setFriendId(strangerId);
				friendBean.setFriendName(name);
				friendBean.setFriendSex(sex);
				friendBean.setFriendBirthday(birthday);
				friendBean.setFriendSignature(personSignature);
				friendBean.setFriendHeadPath(headPath);
				//添加好友
				MainActivity.mp1.AddAFriend(friendBean);
			}
		});
	}

}
