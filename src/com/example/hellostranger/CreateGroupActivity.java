package com.example.hellostranger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.chat.ImageUtil;
import com.clientUtils.ClientManager;
import com.clientUtils.ClientUtils;
import com.example.hellostranger.activity.MainActivity;
import com.example.hellostranger.bean.ChatInfoEntity;
import com.example.hellostranger.fragment_reg.PictureUtil;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsg;
import com.msg_relative.iMoMoMsgTypes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateGroupActivity extends Activity {

	EditText groupName;
	EditText groupTopic;
	ImageView groupIcon;
	Button submit;

	TextView tv_xnum;
	private byte[] bitmapDatas;
	private Bitmap bitImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_create_group);
		initView();
	}

	private void initView() {
		groupName = (EditText) findViewById(R.id.cg_et_group_name);
		groupTopic = (EditText) findViewById(R.id.cg_et_group_topic);
		groupIcon = (ImageView) findViewById(R.id.cg_iv_icon);
		submit = (Button) findViewById(R.id.cg_btn_submit);
		tv_xnum = (TextView) findViewById(R.id.cg_tv_xnum);
		tv_xnum.setText(ClientManager.vitalityValue + "");

		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String name = groupName.getText().toString().trim();
				final String topic = groupTopic.getText().toString().trim();
				if (name.equals("")) {
					Toast.makeText(CreateGroupActivity.this, "�������������", 0)
							.show();
					return;
				}
				if (topic.equals("")) {
					Toast.makeText(CreateGroupActivity.this, "����������", 0).show();
					return;
				}

				if (ClientManager.vitalityValue < 10) {
					Toast.makeText(CreateGroupActivity.this,
							"����ǰ�Ļ���ֵС��10,���ܴ���Ⱥ��", 0).show();
				} else {
					Dialog dialog = null;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CreateGroupActivity.this);
					builder.setTitle("����Ⱥ��").setMessage("������10�Ļ���ֵ,ȷ�ϴ���?");
					builder.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									iMoMoMsg moMsg = new iMoMoMsg();
									moMsg.symbol = '-';
									JSONObject json = new JSONObject();
									json.put(MsgKeys.msgType,
											iMoMoMsgTypes.CREATE_GROUP);
									json.put(MsgKeys.userId,
											ClientManager.clientId);
									json.put(MsgKeys.loc_province,
											ClientManager.province);
									json.put(MsgKeys.groupName, name);
									json.put(MsgKeys.groupTopic, topic);
									moMsg.msgBytes = ImageUtil.getInstance()
											.getBytesFromBitMap(bitImage);
									moMsg.msgJson = json.toJSONString();

									if (MainActivity.ClientSession != null
											&& !MainActivity.ClientSession
													.isClosing())
										MainActivity.ClientSession.write(moMsg);
									ClientManager.vitalityValue -= 10;
									
//									// ��ӵ������ϵ����
//									ChatInfoEntity entity = new ChatInfoEntity();
//									entity.setChatContent("�к�������������Ⱥ������");
//									entity.setFriendId(systemId);// ϵͳ��ϢId
//									entity.setFriendName("ϵͳ��Ϣ");
//									entity.setChatCreatTime(ClientUtils.getNowTime());
//									entity.setMsg_num(1);
//									entity.setMsgtype(1);
//									mp0.AddLaticeChatItem(entity);
									
									
									finish();
								}
							});
					builder.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
					dialog = builder.create();
					dialog.show();
				}
			}
		});

		groupIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PictureUtil.doPickAction(CreateGroupActivity.this);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			Uri data2 = null;
			if (data == null) {
				System.out.println("---data === null        :" + resultCode);
				data2 = PictureUtil.getImageUri(this);
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
				Bitmap myBitmap = (Bitmap) bundle.get("data");
				String fileName = getCharacterAndNumber();
				File file = new File(PictureUtil.Photo_Dir, fileName + ".png");
				bitImage = comp(myBitmap);
				if (bitImage == null)
					System.out.println("bitImage == null");
				saveMyBitmap(bitImage, file);
				System.out.println("---save succeed---");
				groupIcon.setImageBitmap(bitImage);
				System.out.println("--set succeed---");
				break;
			default:
				break;
			}
		}
	}

	public void onBack(View v) {
		finish();
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

	public void saveMyBitmap(Bitmap bitImage, File file) {
		ByteArrayOutputStream baos = null;// ����һ���ֽ����������
		try {
			baos = new ByteArrayOutputStream();
			// ����ϵͳ����ѹ��
			bitImage.compress(Bitmap.CompressFormat.PNG, 50, baos);
			bitmapDatas = baos.toByteArray();
			// �����ļ��������������ļ�
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bitmapDatas);// ���ֽ���д���ļ�
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
