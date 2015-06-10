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
	
	myDialog regDialog;//ע����ʾ��

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration);
		context = this;
		regDialog = new myDialog(context, "ע����...");
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case iMoMoMsgTypes.REGISTER_SUCCESS:
					regDialog.dismiss();
					Toast.makeText(context, "ע��ɹ�", 0).show();
					if(regSession != null && !regSession.isClosing()){
						regSession.close();
					}
					//�����û�ͼ��
					ImageUtil.getInstance().saveImage(context, myHeadBytes, StaticValues.USER_HEADPATH + email+".png");
					finish();
					break;

				case iMoMoMsgTypes.REGISTER_FAILED:
					regDialog.dismiss();
					Toast.makeText(context, "ע��ʧ��", 0).show();
					break;
					
				case StaticValues.SESSION_OPENED:
					try {
						iMoMoMsg regMoMsg = new iMoMoMsg();
						regMoMsg.symbol = '-';
						JSONObject regJson = new JSONObject();
						regJson.put(MsgKeys.msgType, iMoMoMsgTypes.REGISTER);
						regJson.put(MsgKeys.userEmail, email);//ע��id ������
						regJson.put(MsgKeys.userName, username);//�û���
						regJson.put(MsgKeys.userPasswd, password);//���� 
						regJson.put(MsgKeys.userBirthday, birday);//����
						regJson.put(MsgKeys.userSex, sex);//�Ա�
						regMoMsg.msgJson = regJson.toJSONString();
						myHeadBytes = ImageUtil.getInstance().getBytesFromBitMap(myBitmap);
						regMoMsg.msgBytes = myHeadBytes;
						Log.i("--","ע����Ϣ:"+regMoMsg.msgJson);
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
			System.out.println("���fragmentarray[0]");
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
			System.out.println("����manager�����");
		}
		if (step == 0){
			btn_left.setText("����");
			btn_right.setText("��һ��");
			title.setText("�û���");
		}
		if (step == 1){
			btn_left.setText("��һ��");
			btn_right.setText("��һ��");
			title.setText("��������");
		}
		if (step == 2){
			btn_left.setText("��һ��");
			btn_right.setText("���");
			title.setText("��������");
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
//				System.out.println("����ʲô��");
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
			System.out.println("����Ҽ�");

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
				System.out.println("ֱ����ʾfragment");
			} else{
				ft.add(R.id.reg_layout_mid, fragmentarray[step + 1]);
				System.out.println("���fragment");
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
				Toast.makeText(getBaseContext(), "�û���Ϊ�ջ��û�����ʽ����ȷ�����������룡",
						Toast.LENGTH_SHORT).show();
				regStepOne.resetUsername();
				step--;
				return;
			} else if((email == null)|| !ForCheck.EmailFormat(email)) {
				Toast.makeText(getBaseContext(), "����Ϊ�ջ������ʽ����ȷ�����������룡", Toast.LENGTH_LONG)
				.show();
				regStepOne.resetEmail();
				step--;
				return;
			}
			System.out.println("�û���" + username);
			btn_left.setText("��һ��");
			title.setText("��������");
			nextPage();
		}

		private void stepTwoLeft() {
			btn_left.setText("����");
			title.setText("�û���");
			backPage();
		}

		private void stepTwoRight() {
			birday = regStepTwo.getBirday();
			System.out.println("---birday---" + birday + "---anything---");
			sex = regStepTwo.getSex();
			if (birday.equals("")) {
				Toast.makeText(getBaseContext(), "���ղ���Ϊ�գ�", Toast.LENGTH_SHORT)
						.show();
				step--;
				return;
			} else if (sex == null) {
				Toast.makeText(getBaseContext(), "��ѡ���Ա�", Toast.LENGTH_SHORT)
						.show();
				step--;
				return;
			}
			btn_right.setText("���");
			title.setText("��������");
			nextPage();
		}

		private void stepThreeLeft() {
			btn_right.setText("��һ��");
			title.setText("��������");
			backPage();
		}

		private void stepThreeRight() {
			password = regStepThree.getPassword();
			if (!regStepThree.getPassword().equals(
					regStepThree.getPasswordAgain())) {
				Toast.makeText(getBaseContext(), "������������벻һ�£�",
						Toast.LENGTH_SHORT).show();
				regStepThree.resetPassword();
				step--;
				return;
			} else if ((password == null) || !ForCheck.PasswdFormat(password)) {
				Toast.makeText(getBaseContext(), "����Ϊ�ջ������ʽ����ȷ������������!",
						Toast.LENGTH_LONG).show();
				regStepThree.resetPassword();
				step--;
				return;
			} else {
				password = PasswordUtil.toMD5(password);
				System.out.println("regedit succeed");
				//����ע����Ϣ
				new ConnectServerThread().start();
				regDialog.show();
			}
		}
	}
	
	/**
	 * ���ӷ�����
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
				// System.out.println("����ֵ��ӡ"
				// + regStepTwo.getClass().toString());
				if (regStepTwo == null)
					System.out.println("regStepTwoΪ��");
				this.regStepTwo.setImageViewUri(data2);
				intent.setDataAndType(data2, "image/*");
				intent.putExtra("crop", true);
				// ���òü��ߴ�
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
				// System.out.println("����ֵ��ӡ"
				// + regStepTwo.getClass().toString());
				if (regStepTwo == null)
					System.out.println("regStepTwoΪ��");
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
		ByteArrayOutputStream baos = null;// ����һ���ֽ����������
		try {
			baos = new ByteArrayOutputStream();
			// ����ϵͳ����ѹ��
			bitImage.compress(Bitmap.CompressFormat.PNG, 50, baos);
			byte[] bitmapData = baos.toByteArray();
			// �����ļ��������������ļ�
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bitmapData);// ���ֽ���д���ļ�
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ��ǰϵͳʱ�䣬��ת�����ַ���������ͼƬ������
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
	 * ������ȡͼƬ����
	 * 
	 * @param myBitmap2
	 * @return
	 */
	private Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);
		if (baos.toByteArray().length / 1024 > 200) {// �ж����ͼƬ����200KB,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.PNG, 50, baos);// ����ѹ��50%����ѹ��������ݴ�ŵ�baos��
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		// ָ��ͼƬ��ʲô��ʽ���ص��ڴ�
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
		newOpts.inJustDecodeBounds = true;
		// ���ͼƬ
		Bitmap bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
		float hh = 160f;// �������ø߶�Ϊ800f
		float ww = 130f;// �������ÿ��Ϊ480f
		// ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
		int be = 1;// be=1��ʾ������
		if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// �������ű���
		// ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
		bais = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(bais, null, newOpts);
		return compressImage(bitmap);// ѹ���ñ�����С���ٽ�������ѹ��
	}

	// ����ѹ��
	private Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
		int options = 100;
		while (baos.toByteArray().length / 1024 > 200) { // ѭ���ж����ѹ����ͼƬ�Ƿ����200kb,���ڼ���ѹ��
			baos.reset();// ����baos�����baos
			image.compress(Bitmap.CompressFormat.PNG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��
			options -= 10;// ÿ�ζ�����10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ
		return bitmap;
	}
}
