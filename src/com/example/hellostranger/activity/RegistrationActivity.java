package com.example.hellostranger.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.chat.ImageUtil;
import com.clientUtils.ClientHandler;
import com.example.hellostranger.R;
import com.example.hellostranger.bean.RegActivityData;
import com.example.hellostranger.fragment_reg.ForCheck;
import com.example.hellostranger.fragment_reg.RegStepOne;
import com.example.hellostranger.fragment_reg.RegStepThree;
import com.example.hellostranger.fragment_reg.RegStepTwo;
import com.example.hellostranger.util.PasswordUtil;
import com.example.hellostranger.util.myDialog;
import com.example.hellostranger.fragment_reg.PictureUtil;
import com.imomo_codecfactory.iMoMoCodecFactory;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

public class RegistrationActivity extends Activity {

	FragmentManager manager;
	Fragment[] fragmentarray;
	RegStepOne regStepOne;
	RegStepTwo regStepTwo;
	RegStepThree regStepThree;
	Button btn_left;
	Button btn_right;
	TextView title;
	MyOnClickListener myOnClickListener;
	private int step;
	String username;
	String email;
	String birday;
	String password;
	String sex;
	private Bitmap myBitmap;
	private byte[] myHeadBytes;
	int i = 0;
	
	private IoSession regSession;
	private Context context;
	private Handler handler;
	
	myDialog regDialog;//注册提示框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);
		context = this;
		regDialog = new myDialog(context, "注册中...");
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case iMoMoMsgTypes.REGISTER_SUCCESS:
					regDialog.dismiss();
					Toast.makeText(context, "注册成功", 0).show();
					if(regSession != null && !regSession.isClosing()){
						regSession.close();
					}
					//保存用户图像
					ImageUtil.getInstance().saveImage(context, myHeadBytes, StaticValues.USER_HEADPATH + email+".png");
					finish();
					break;

				case iMoMoMsgTypes.REGISTER_FAILED:
					regDialog.dismiss();
					Toast.makeText(context, "注册失败", 0).show();
					break;
					
				case StaticValues.SESSION_OPENED:
					try {
						iMoMoMsg regMoMsg = new iMoMoMsg();
						regMoMsg.symbol = '-';
						JSONObject regJson = new JSONObject();
						regJson.put(MsgKeys.msgType, iMoMoMsgTypes.REGISTER);
						regJson.put(MsgKeys.userEmail, email);//注册id 即邮箱
						regJson.put(MsgKeys.userName, username);//用户名
						regJson.put(MsgKeys.userPasswd, password);//密码 
						regJson.put(MsgKeys.userBirthday, birday);//生日
						regJson.put(MsgKeys.userSex, sex);//性别
						regMoMsg.msgJson = regJson.toJSONString();
						myHeadBytes = ImageUtil.getInstance().getBytesFromBitMap(myBitmap);
						regMoMsg.msgBytes = myHeadBytes;
						Log.i("--","注册信息:"+regMoMsg.msgJson);
						regSession.write(regMoMsg);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				}
			};
		};
		
		System.out.println("RegistrationActivity:onCreate");
		myOnClickListener = new MyOnClickListener();
		btn_left = (Button) findViewById(R.id.reg_btn_leftbtn);
		btn_left.setOnClickListener(myOnClickListener);
		btn_right = (Button) findViewById(R.id.reg_btn_rightbtn);
		btn_right.setOnClickListener(myOnClickListener);
		title = (TextView) findViewById(R.id.reg_tv_title);
		if (savedInstanceState == null) {
			step = 0;
			initFragment();
			FragmentTransaction ft = manager.beginTransaction();
			System.out.println("添加fragmentarray[0]");
			ft.add(R.id.reg_layout_mid, fragmentarray[0]);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		} else {
			final RegActivityData data = (RegActivityData) getLastNonConfigurationInstance();
			loadMyData(data);
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (step>0){
				myOnClickListener.leftBtnDown();
			}
			else
				this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public Object onRetainNonConfigurationInstance() {

		final RegActivityData data = collectMyLoadedData();
		System.out
				.println("RegistrationActivity:onRetainNonConfigurationInstance");
		return data;

	}


	private RegActivityData collectMyLoadedData() {
		// TODO Auto-generated method stub
		RegActivityData data = new RegActivityData();
		data.fragmentarray = this.fragmentarray;
		data.regStepOne = this.regStepOne;
		data.regStepTwo = this.regStepTwo;
		data.regStepThree = this.regStepThree;
		data.btn_left = this.btn_left;
		data.btn_right = this.btn_right;
		data.step = this.step;
		data.username = this.username;
		data.birday = this.birday;
		data.password = this.password;
		data.sex = this.sex;
		data.myBitmap = this.myBitmap;
		data.manager = this.manager;
		return data;
	}

	private void loadMyData(RegActivityData data) {
		this.fragmentarray = data.fragmentarray;
		this.regStepOne = data.regStepOne;
		this.regStepTwo = data.regStepTwo;
		this.regStepThree = data.regStepThree;
		this.step = data.step;
		this.username = data.username;
		this.birday = data.birday;
		this.password = data.password;
		this.sex = data.sex;
		this.myBitmap = data.myBitmap;
		this.manager = getFragmentManager();
		if (!this.manager.equals(getFragmentManager())) {
			System.out.println("两个manager不相等");
		}
		if (step == 0){
			btn_left.setText("返回");
			btn_right.setText("下一步");
			title.setText("用户名");
		}
		if (step == 1){
			btn_left.setText("上一步");
			btn_right.setText("下一步");
			title.setText("个人资料");
		}
		if (step == 2){
			btn_left.setText("上一步");
			btn_right.setText("完成");
			title.setText("设置密码");
		}

	}

	private void initFragment() {
		manager = getFragmentManager();
		fragmentarray = new Fragment[3];
		regStepOne = new RegStepOne();
		regStepOne.setRetainInstance(true);
		regStepTwo = new RegStepTwo();
		regStepTwo.setRetainInstance(true);
		regStepThree = new RegStepThree();
		regStepThree.setRetainInstance(true);
		fragmentarray[0] = regStepOne;
		fragmentarray[1] = regStepTwo;
		fragmentarray[2] = regStepThree;
	}

	class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Button btn = (Button) v;
			if (btn.equals(btn_left)) {
				leftBtnDown();
			} else if (btn.equals(btn_right)) {
				rightBtnDown();
			} else {
//				System.out.println("这是什么鬼？");
			}
		}
		
		private void leftBtnDown(){
			if (step == 0) {
				stepOneLeft();
			} else if (step == 1) {
				stepTwoLeft();
			} else {
				stepThreeLeft();
			}

			step--;
		}
		
		private void rightBtnDown(){
			System.out.println("点击右键");

			if (step == 0) {
				System.err.println("00000000000000000000");
				stepOneRight();
			} else if (step == 1) {
				stepTwoRight();
			} else {
				stepThreeRight();
			}
			if (step<2)
				step++;
		}

		private void backPage() {
			FragmentTransaction ft = manager.beginTransaction();
			ft.hide(fragmentarray[step]);
			ft.show(fragmentarray[step - 1]);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}

		private void nextPage() {
			FragmentTransaction ft = manager.beginTransaction();
			ft.hide(fragmentarray[step]);
			if (fragmentarray[step + 1].isAdded()) {
				ft.show(fragmentarray[step + 1]);
				System.out.println("直接显示fragment");
			} else{
				ft.add(R.id.reg_layout_mid, fragmentarray[step + 1]);
				System.out.println("添加fragment");
			}
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}

		private void stepOneLeft() {
			finish();
		}

		private void stepOneRight() {
			username = regStepOne.getUsername();
			email = regStepOne.getEmail();
			if ((username == null) || !ForCheck.UserFormat(username)) {
				Toast.makeText(getBaseContext(), "用户名为空或用户名格式不正确，请重新输入！",
						Toast.LENGTH_SHORT).show();
				regStepOne.resetUsername();
				step--;
				return;
			} else if((email == null)|| !ForCheck.EmailFormat(email)) {
				Toast.makeText(getBaseContext(), "邮箱为空或邮箱格式不正确，请重新输入！", Toast.LENGTH_LONG)
				.show();
				regStepOne.resetEmail();
				step--;
				return;
			}
			System.out.println("用户名" + username);
			btn_left.setText("上一步");
			title.setText("个人资料");
			nextPage();
		}

		private void stepTwoLeft() {
			btn_left.setText("返回");
			title.setText("用户名");
			backPage();
		}

		private void stepTwoRight() {
			birday = regStepTwo.getBirday();
			System.out.println("---birday---" + birday + "---anything---");
			sex = regStepTwo.getSex();
			if (birday.equals("")) {
				Toast.makeText(getBaseContext(), "生日不能为空！", Toast.LENGTH_SHORT)
						.show();
				step--;
				return;
			} else if (sex == null) {
				Toast.makeText(getBaseContext(), "请选择性别！", Toast.LENGTH_SHORT)
						.show();
				step--;
				return;
			}
			btn_right.setText("完成");
			title.setText("设置密码");
			nextPage();
		}

		private void stepThreeLeft() {
			btn_right.setText("下一步");
			title.setText("个人资料");
			backPage();
		}

		private void stepThreeRight() {
			password = regStepThree.getPassword();
			if (!regStepThree.getPassword().equals(
					regStepThree.getPasswordAgain())) {
				Toast.makeText(getBaseContext(), "两次输入的密码不一致！",
						Toast.LENGTH_SHORT).show();
				regStepThree.resetPassword();
				step--;
				return;
			} else if ((password == null) || !ForCheck.PasswdFormat(password)) {
				Toast.makeText(getBaseContext(), "密码为空或密码格式不正确，请重新输入!",
						Toast.LENGTH_LONG).show();
				regStepThree.resetPassword();
				step--;
				return;
			} else {
				password = PasswordUtil.toMD5(password);
				System.out.println("regedit succeed");
				//发送注册信息
				new ConnectServerThread().start();
				regDialog.show();
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
			connector.getFilterChain().addLast("code",new ProtocolCodecFilter(new iMoMoCodecFactory()));
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(
						StaticValues.SERVER_IP, StaticValues.SERVER_PORT));
				future.awaitUninterruptibly();
				regSession = future.getSession();
				System.out.println("session is ok" + regSession);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			Uri data2 = null;
			if (data == null) {
				System.out.println("---data === null        :" + resultCode);
				data2 = PictureUtil.getImageUri(RegistrationActivity.this);
			} else {
				System.out.println("---data not null");
				data2 = data.getData();
			}
			System.out.println("----data:" + data);
			System.out.println("----Uri2---" + data2);
			System.out.println("enter choose");
			switch (requestCode) {
			case PictureUtil.Photo_Data:
				System.out.println("---start from gallery---");
				// System.out.println("返回值打印"
				// + regStepTwo.getClass().toString());
				if (regStepTwo == null)
					System.out.println("regStepTwo为空");
				this.regStepTwo.setImageViewUri(data2);
				intent.setDataAndType(data2, "image/*");
				intent.putExtra("crop", true);
				// 设置裁剪尺寸
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 130);
				intent.putExtra("outputY", 130);
				intent.putExtra("return-data", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						PictureUtil.getImageCropUri());
				System.out.println("---going to photocrop---");
				startActivityForResult(intent, PictureUtil.Photo_Crop);
				break;
			case PictureUtil.Camera_Data:
				System.out.println("---start from Camera---");
				// System.out.println("返回值打印"
				// + regStepTwo.getClass().toString());
				if (regStepTwo == null)
					System.out.println("regStepTwo为空");
				// this.regStepTwo.setImageViewUri(data2);
				intent.setDataAndType(data2, "image/*");
				intent.putExtra("crop", true);
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 130);
				intent.putExtra("outputY", 130);
				intent.putExtra("return-data", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,
						PictureUtil.getImageCropUri());
				startActivityForResult(intent, PictureUtil.Photo_Crop);
				break;
			case PictureUtil.Photo_Crop:
				System.out.println("enter Crop");
				Bundle bundle = data.getExtras();
				myBitmap = (Bitmap) bundle.get("data");
				String fileName = getCharacterAndNumber();
				File file = new File(PictureUtil.Photo_Dir, fileName + ".png");
				Bitmap bitImage = comp(myBitmap);
				if (bitImage == null)
					System.out.println("bitImage == null");
				saveMyBitmap(bitImage, file);
				System.out.println("---save succeed---");
				this.regStepTwo.setImageView(bitImage);
				System.out.println("--set succeed---");
				break;
			default:
				break;
			}
		}
	}

	public static void saveMyBitmap(Bitmap bitImage, File file) {
		ByteArrayOutputStream baos = null;// 定义一个字节数组输出流
		try {
			baos = new ByteArrayOutputStream();
			// 调用系统函数压缩
			bitImage.compress(Bitmap.CompressFormat.PNG, 50, baos);
			byte[] bitmapData = baos.toByteArray();
			// 定义文件输出流用于输出文件
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bitmapData);// 把字节流写入文件
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前系统时间，并转化成字符串，用做图片的名称
	 * 
	 * @return
	 */
	private String getCharacterAndNumber() {
		String rel = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		rel = formatter.format(curDate);
		return rel;
	}

	/**
	 * 创建截取图片函数
	 * 
	 * @param myBitmap2
	 * @return
	 */
	private Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);
		if (baos.toByteArray().length / 1024 > 200) {// 判断如果图片大于200KB,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		// 指定图片以什么方式加载到内存
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		// 输出图片
		Bitmap bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 160f;// 这里设置高度为800f
		float ww = 130f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bais = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	// 二次压缩
	private Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
}
