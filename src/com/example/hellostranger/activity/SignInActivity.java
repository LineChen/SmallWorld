package com.example.hellostranger.activity;

import java.io.File;
import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.chat.FileUtils;
import com.clientUtils.ClientHandler;
import com.example.hellostranger.R;
import com.example.hellostranger.fragment_reg.ForCheck;
import com.example.hellostranger.util.PasswordUtil;
import com.example.hellostranger.util.SharePreferenceUtil;
import com.example.hellostranger.util.myDialog;
import com.example.hellostranger.view.RoundImageView;
import com.imomo_codecfactory.iMoMoCodecFactory;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

public class SignInActivity extends Activity {

	Intent intent_server;
	private long mExitTime;
	Button btn_submit;
	Button btn_forget;
	RoundImageView userHead;

	EditText et_username;
	EditText et_pwd;
	private String userEmail;
	private String pwd;
	TextView tv_forgetpassword;
	TextView tv_registration;
	MyOnClickListener myOnClickListener;
	MyHandler myHandler = new MyHandler();
	private Messenger rMessenger = null;
	private Messenger mMessenger = null;
	private boolean mIsBind;
	private ServiceConnection serConn = null;

	private IoSession session;
	private Context context;
	
	private myDialog loginLog;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case iMoMoMsgTypes.LOGIN_SUCCESS:
				// 登录成功,跳到主界面,由于该activity destroy了，所以该session已经close了
				// 保存相关信息到
				SharePreferenceUtil util = new SharePreferenceUtil(context);
				util.SaveValue(StaticValues.userEmail, userEmail);
				util.SaveValue(StaticValues.userPasswd, pwd);

				Intent intent = new Intent(context, MainActivity.class);
				startActivity(intent);
				
				loginLog.dismiss();
				
				CloseSession();
				finish();
				break;
			case iMoMoMsgTypes.LOGIN_FAILED:
				Toast.makeText(context, "登录失败", 0).show();
				loginLog.dismiss();
				break;
				
			case StaticValues.SESSION_OPENED:
				//会话打开
				try {
					iMoMoMsg moMoMsg = new iMoMoMsg();
					JSONObject Json = new JSONObject();
					Json.put(MsgKeys.msgType, iMoMoMsgTypes.LOGIN);
					Json.put(MsgKeys.userEmail, userEmail);
					Json.put(MsgKeys.userPasswd, PasswordUtil.toMD5(pwd));
					moMoMsg.msgJson = Json.toJSONString();
					moMoMsg.symbol = '+';
					session.write(moMoMsg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		context = this;
		setContentView(R.layout.activity_signin);
		initView();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出",
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		userHead = (RoundImageView) findViewById(R.id.signin_iv_headicon);
		et_username = (EditText) findViewById(R.id.signin_et_username);
		et_pwd = (EditText) findViewById(R.id.signin_et_pwd);
		btn_submit = (Button) findViewById(R.id.signin_btn_submit);
		btn_submit.setTag("btn_submit");
		tv_forgetpassword = (TextView) findViewById(R.id.signin_tv_forgetpassword);
		tv_forgetpassword.setTag("tv_forgetpassword");
		tv_registration = (TextView) findViewById(R.id.signin_tv_registration);
		tv_registration.setTag("tv_registration");
		myOnClickListener = new MyOnClickListener();

		btn_submit.setOnClickListener(myOnClickListener);
		tv_forgetpassword.setOnClickListener(myOnClickListener);
		tv_registration.setOnClickListener(myOnClickListener);

		et_username.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String headPath = StaticValues.USER_HEADPATH + s + ".png";
				if (FileUtils.isFileExists(headPath)) {
					userHead.setImageURI(Uri.fromFile(new File(headPath)));
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.signin_btn_submit) {
				userEmail = et_username.getText().toString().trim();
				pwd = et_pwd.getText().toString().trim();
				if (userEmail.equals("")) {
					Toast.makeText(context, "账号不能为空", 0).show();
					return;
				}
				if (!ForCheck.EmailFormat(userEmail)) {
					Toast.makeText(context, "请输入注册邮箱", 0).show();
					et_username.setText("");
					return;
				}
				if (pwd.equals("")) {
					Toast.makeText(context, "密码不能为空", 0).show();
					return;
				}
				// 发送消息
				new ConnectServerThread().start();
				
				loginLog = new myDialog(context, "登录中...");
				loginLog.show();
			} else if (v.getId() == R.id.signin_tv_forgetpassword) {
				System.out.println("点击了忘记密码");
				Intent intent = new Intent(SignInActivity.this,
						ForgetPasswdActivity.class);
				startActivity(intent);
			} else if (v.getId() == R.id.signin_tv_registration) {
				Intent intent = new Intent(SignInActivity.this,
						RegistrationActivity.class);
				startActivity(intent);
			} else {
				return;
			}
		}
	}

	/**
	 * 连接服务器
	 */
	private class ConnectServerThread extends Thread {
		public void run() {
			NioSocketConnector connector = new NioSocketConnector();
			connector.setHandler(new ClientHandler(handler));
			connector.getFilterChain().addLast("code",
					new ProtocolCodecFilter(new iMoMoCodecFactory()));
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(
						StaticValues.SERVER_IP, StaticValues.SERVER_PORT));
				future.awaitUninterruptibly();
				session = future.getSession();
				System.out.println("session is ok" + session);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendMessage() {
		Message msg = Message.obtain(null, 1, "");
		msg.replyTo = mMessenger;
		try {
			rMessenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			et_username.setText((String) msg.obj);
		}
	}

	private void CloseSession() {
		if (session != null && !session.isClosing()) {
			session.close();
		}
	}
}
