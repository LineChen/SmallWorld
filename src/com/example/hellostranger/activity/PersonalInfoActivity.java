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
	ImageView iv_photohead;// 需要从服务器上获取头像
	ImageView iv_bg;
	
	TextView hlmsg;

	static TextView tv_nickname;
	static TextView tv_sex;
	static TextView tv_birth;
	static TextView tv_signatrue;// 个人签名

	Bitmap myBitmap;
	Bitmap db_bitmap = null; // 从数据中获取头像
	String db_nickname = null; // 从数据库中获取昵称
	String db_sex = null; // 从数据库中获取性别
	String db_birth = null; // 从数据库中获取生日
	Bitmap bitmap; // 从ImageView获取头像，用于传值
	String nickname; // 获取昵称传值
	String sex; // 获取性别传值
	String birth; // 获取生日传值

	/**
	 * 刷新个人资料
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
	 * 初始化个人信息
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
		 * 获取头像
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
		 * 修改昵称
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
		 * 修改性别
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
		 * 修改生日
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

		/** 修改个性签名 */
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
				// System.out.println("返回值打印"
				// + regStepTwo.getClass().toString());
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
//					Log.i("--", "删除之前的头像");
//				}
				// 保存头像到本地
				ImageUtil.getInstance().saveImage(PersonalInfoActivity.this,
						headBytes, headPath);
				// 在主界面左侧栏也要修改头像
				MainActivity.refreshPinfo(iMoMoMsgTypes.RESET_HEAD, headPath);
				// System.out.println("--set succeed---");
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
