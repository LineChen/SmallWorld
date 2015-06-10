package com.example.hellostranger.activity;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.clientUtils.ClientHandler;
import com.example.hellostranger.R;
import com.example.hellostranger.fragment_reg.ForCheck;
import com.imomo_codecfactory.iMoMoCodecFactory;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

public class ForgetPasswdActivity extends Activity {

	private EditText et_email;
	private Button btn_submit;
	String email = null;
	String code = null;

	private IoSession session;
	private Context context;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forget_passwd);
		et_email = (EditText) findViewById(R.id.forgetpass_et_emiladdress);
		context = this;
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case iMoMoMsgTypes.FIND_PASSWD_SUCCESS:
					Toast.makeText(context, "请查看邮箱", 1).show();
					CloseSession();
					finish();
					break;
				case iMoMoMsgTypes.FIND_PASSWD_FAILED:
					Toast.makeText(context, "查找密码失败", 0).show();
					CloseSession();
					break;
				}
			}
		};
	}

	// 提交信息
	public void onSubmit(View v) {
		email = et_email.getText().toString();
		if (email.equals("") || !ForCheck.EmailFormat(email)) {
			Toast.makeText(context, "邮箱为空或邮箱格式不正确，请重新输入", Toast.LENGTH_SHORT)
					.show();
			et_email.setText("");
			return;
		} else {
			// 发送消息
			new ConnectServerThread().start();
			try {
				Thread.sleep(2000);
				iMoMoMsg moMoMsg = new iMoMoMsg();
				JSONObject Json = new JSONObject();
				Json.put(MsgKeys.msgType, iMoMoMsgTypes.FIND_PASSWD);
				Json.put(MsgKeys.userEmail, email);
				moMoMsg.msgJson = Json.toJSONString();
				moMoMsg.symbol = '+';
				session.write(moMoMsg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 返回
	public void onBack(View v) {
		finish();
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

	
	private void CloseSession(){
		if (session != null && !session.isClosing()) {
			session.close();
		}
	}
	
}
