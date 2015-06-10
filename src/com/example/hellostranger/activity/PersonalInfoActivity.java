package com.example.hellostranger.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.FileUtils;
import com.chat.ImageUtil;
import com.clientUtils.ClientManager;
import com.example.hellostranger.R;
import com.example.hellostranger.fragment_reg.PictureUtil;
import com.msg_relative.iMoMoMsgTypes;
import com.personalinfo.activity.BirthEditActivity;
import com.personalinfo.activity.NicknameEditActivity;
import com.personalinfo.activity.SexEditActivity;
import com.personalinfo.activity.SignatureEditActivity;
import com.static_configs.StaticValues;

public class PersonalInfoActivity extends Activity {

	RelativeLayout rl_nick;
	RelativeLayout rl_sex;
	RelativeLayout rl_birth;
	RelativeLayout rl_signatrue;
	ImageButton ib_back;
	ImageView iv_photohead;// ��Ҫ�ӷ������ϻ�ȡͷ��
	ImageView iv_bg;
	
	TextView hlmsg;

	static TextView tv_nickname;
	static TextView tv_sex;
	static TextView tv_birth;
	static TextView tv_signatrue;// ����ǩ��

	Bitmap myBitmap;
	Bitmap db_bitmap = null; // �������л�ȡͷ��
	String db_nickname = null; // �����ݿ��л�ȡ�ǳ�
	String db_sex = null; // �����ݿ��л�ȡ�Ա�
	String db_birth = null; // �����ݿ��л�ȡ����
	Bitmap bitmap; // ��ImageView��ȡͷ�����ڴ�ֵ
	String nickname; // ��ȡ�ǳƴ�ֵ
	String sex; // ��ȡ�Ա�ֵ
	String birth; // ��ȡ���մ�ֵ

	/**
	 * ˢ�¸�������
	 * 
	 * @param type
	 * @param value
	 */
	public static void refreshClientInfo(int type, String value) {
		switch (type) {
		case iMoMoMsgTypes.RESET_USERNAME:
			tv_nickname.setText(value);
			break;
		case iMoMoMsgTypes.RESET_SEX:
			tv_sex.setText(value);
			break;
		case iMoMoMsgTypes.RESET_BIRTHDAY:
			tv_birth.setText(value);
			break;
		case iMoMoMsgTypes.RESET_SIGNATUE:
			tv_signatrue.setText(value);
			break;
		}
	}

	/**
	 * ��ʼ��������Ϣ
	 */
	public void initData() {
		tv_nickname.setText(ClientManager.clientName);
		tv_sex.setText(ClientManager.clientSex);
		tv_birth.setText(ClientManager.clientBirthday);
		iv_photohead.setImageURI(Uri
				.fromFile(new File(StaticValues.USER_HEADPATH
						+ ClientManager.clientEmail + ".png")));
		tv_signatrue.setText(ClientManager.personSignature);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_info);
		System.out.println("get here");
		ib_back = (ImageButton) findViewById(R.id.personal_ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		/**
		 * ��ȡͷ��
		 */
		iv_photohead = (ImageView) findViewById(R.id.personal_iv_headlogo);
		iv_photohead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PictureUtil.doPickAction(PersonalInfoActivity.this);
			}
		});

		tv_nickname = (TextView) findViewById(R.id.personal_tv_nickname);
		tv_sex = (TextView) findViewById(R.id.personal_tv_sexvalue);
		tv_birth = (TextView) findViewById(R.id.personal_tv_birday);
		tv_signatrue = (TextView) findViewById(R.id.personal_tv_signvalue);
		initData();

		/**
		 * �޸��ǳ�
		 */
		rl_nick = (RelativeLayout) findViewById(R.id.personal_rl_nick);
		rl_nick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PersonalInfoActivity.this,
						NicknameEditActivity.class);
				startActivity(intent);
			}
		});
		/**
		 * �޸��Ա�
		 */
		rl_sex = (RelativeLayout) findViewById(R.id.personal_rl_sex);
		rl_sex.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PersonalInfoActivity.this,
						SexEditActivity.class);
				startActivity(intent);
			}
		});
		/**
		 * �޸�����
		 */
		rl_birth = (RelativeLayout) findViewById(R.id.personal_rl_birth);
		rl_birth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PersonalInfoActivity.this,
						BirthEditActivity.class);
				startActivity(intent);
			}
		});//

		/** �޸ĸ���ǩ�� */
		rl_signatrue = (RelativeLayout) findViewById(R.id.personal_rl_signatrue);
		rl_signatrue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PersonalInfoActivity.this,
						SignatureEditActivity.class);
				startActivity(intent);
			}
		});
		
		hlmsg = (TextView) findViewById(R.id.personal_tv_hlmsg);
		hlmsg.setText(ClientManager.vitalityValue+"");

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			Uri data2 = null;
			if (data == null) {
				System.out.println("---data === null        :" + resultCode);
				data2 = PictureUtil.getImageUri(PersonalInfoActivity.this);
			} else {
				System.out.println("---data not null");
				data2 = data.getData();
			}
			switch (requestCode) {
			case PictureUtil.Photo_Data:
				System.out.println("---start from gallery---");
				// System.out.println("����ֵ��ӡ"
				// + regStepTwo.getClass().toString());
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
				Bundle bundle = data.getExtras();
				myBitmap = (Bitmap) bundle.get("data");
				String fileName = getCharacterAndNumber();
				File file = new File(PictureUtil.Photo_Dir, fileName + ".png");
				Bitmap bitImage = comp(myBitmap);
				if (bitImage == null)
					System.out.println("bitImage == null");
				saveMyBitmap(bitImage, file);
				// System.out.println("---save succeed---");
				iv_photohead.setImageBitmap(bitImage);
				byte[] headBytes = ImageUtil.getInstance().getBytesFromBitMap(
						bitImage);
				MainActivity.myBinder.ResetUserHead(headBytes);
				String headPath = StaticValues.USER_HEADPATH
						+ ClientManager.clientEmail + ".png";
				File headFile = new File(headPath);
//				if(FileUtils.isFileExists(headPath)){
//					FileUtils.deleteFile(headPath);
//					Log.i("--", "ɾ��֮ǰ��ͷ��");
//				}
				// ����ͷ�񵽱���
				ImageUtil.getInstance().saveImage(PersonalInfoActivity.this,
						headBytes, headPath);
				// �������������ҲҪ�޸�ͷ��
				MainActivity.refreshPinfo(iMoMoMsgTypes.RESET_HEAD, headPath);
				// System.out.println("--set succeed---");
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
