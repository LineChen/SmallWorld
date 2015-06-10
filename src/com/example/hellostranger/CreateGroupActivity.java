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
					Toast.makeText(CreateGroupActivity.this, "请输入队伍名称", 0)
							.show();
					return;
				}
				if (topic.equals("")) {
					Toast.makeText(CreateGroupActivity.this, "请输入主题", 0).show();
					return;
				}

				if (ClientManager.vitalityValue < 10) {
					Toast.makeText(CreateGroupActivity.this,
							"您当前的活力值小于10,不能创建群组", 0).show();
				} else {
					Dialog dialog = null;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							CreateGroupActivity.this);
					builder.setTitle("创建群组").setMessage("将减掉10的活力值,确认创建?");
					builder.setPositiveButton("确定",
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
									
//									// 添加到最近联系人中
//									ChatInfoEntity entity = new ChatInfoEntity();
//									entity.setChatContent("有好友邀请您加入群组聊天");
//									entity.setFriendId(systemId);// 系统消息Id
//									entity.setFriendName("系统消息");
//									entity.setChatCreatTime(ClientUtils.getNowTime());
//									entity.setMsg_num(1);
//									entity.setMsgtype(1);
//									mp0.AddLaticeChatItem(entity);
									
									
									finish();
								}
							});
					builder.setNegativeButton("取消",
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

	public void saveMyBitmap(Bitmap bitImage, File file) {
		ByteArrayOutputStream baos = null;// 定义一个字节数组输出流
		try {
			baos = new ByteArrayOutputStream();
			// 调用系统函数压缩
			bitImage.compress(Bitmap.CompressFormat.PNG, 50, baos);
			bitmapDatas = baos.toByteArray();
			// 定义文件输出流用于输出文件
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bitmapDatas);// 把字节流写入文件
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
