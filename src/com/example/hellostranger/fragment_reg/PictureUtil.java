package com.example.hellostranger.fragment_reg;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.chat.ImageUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

public class PictureUtil {
	/**
	 * 设置几个常量
	 */
	/** 用来请求照相功能的常量 */
	public static final int Camera_Data = 168;
	/** 用来图片选择器的常量 */
	public static final int Photo_Data = Camera_Data + 1;
	/** 用于图片裁剪的常量 */
	public static final int Photo_Crop = Photo_Data + 1;
	/**
	 * 图片存储位置常量 getExternalStorageDirectory():获取sd卡根目录
	 */
	public static final File Photo_Dir = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
	/** 拍照获得的图片 */
	public static File PhotoForCamera;

	public File file; // 截图后的图片
	public static Uri imageUri; // 拍照后获得的图片路径
	public static Uri imageCropUri; // 裁剪后的图片路径

	/**
	 * 获取当前图片路径
	 */
	public static File getCurrentPhotoFile() {
		if (PhotoForCamera == null) {
			if (!Photo_Dir.exists()) {
				Photo_Dir.mkdirs();
			}
			PhotoForCamera = new File(Photo_Dir, getCharacterAndNumber()
					+ ".png"/** 后缀可以自己改 */
			);
			if (!PhotoForCamera.exists())
				try {
					PhotoForCamera.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return PhotoForCamera;
	}

	/**
	 * 创建头像选择框
	 * 
	 * @param context
	 */
	public static void doPickAction(final Activity context) {
		/** 创建提示框 */
		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = "从照相机获取";
		choices[1] = "从相册中选择";
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);
		// 弹框
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("请选择图片上传方式");
		// 设置简单的列表选择
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						switch (which) {
						case 0: {
							String statue = Environment
									.getExternalStorageState();
							if (statue.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								doTakePhoto(context);// 点击了从照相机获取
							} else {
								Toast.makeText(dialogContext, "没有找到SD卡",
										Toast.LENGTH_SHORT).show();
							}
							break;
						}
						case 1: {
							doPickPhotoFromGallery(context);
							break;
						}
						}
					}

				});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * 调用照相功能
	 * 
	 * @param context
	 */
	private static void doPickPhotoFromGallery(Activity context) {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			System.out.println("Start photo pick");
			System.out.println("---intent---" + intent);
			System.out.println("---Uri1----" + intent.getData());
			// context.getFragmentManager()
			context.startActivityForResult(intent, Photo_Data);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有找到相册", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 调用相册程序
	 * 
	 * @param context
	 */
	private static void doTakePhoto(Activity context) {
		// TODO Auto-generated method stub
		try {
			if (!Photo_Dir.exists()) {
				Photo_Dir.mkdirs(); // 创建照片的存储目录
			}
			imageUri = Uri.fromFile(getCurrentPhotoFile());
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			context.startActivityForResult(intent, Camera_Data);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "没有找到相机", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取图片路径
	 * 
	 * @return
	 */
	public static Uri getImageUri(Context context) {
		int degree = getBitmapDegree(PhotoForCamera.getPath());
		if (degree != 0) {

			imageUri = Uri.fromFile(PhotoForCamera);
			System.out.println("获取Bitmap");
			Bitmap bitmap = getBitmapFromUri(imageUri, context);
			System.out.println("旋转Bitmap");
			bitmap = rotateBitmapByDegree(bitmap, degree);
			System.out.println("存储Bitmap");
			saveMyBitmap(bitmap, PhotoForCamera);
		}
		System.out.println("读取Uri");
		imageUri = Uri.fromFile(PhotoForCamera);
		System.out.println("PhotoForCamera.toString()"
				+ PhotoForCamera.toString());
		System.out.println("imageUri.toString()" + imageUri.toString());
		return imageUri;
	}

	/**
	 * 获取截图后的图片路径
	 * 
	 * @return
	 */

	public static Uri getImageCropUri() {
		File tempfile = new File(Photo_Dir, getCharacterAndNumber() + ".png");
		imageCropUri = Uri.fromFile(tempfile);
		return imageCropUri;
	}

	// 获取当前时间并转化为字符串
	private static String getCharacterAndNumber() {
		String rel = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
		Date curData = new Date(System.currentTimeMillis());
		rel = format.format(curData);
		return rel;
	}

	private static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	public static void saveMyBitmap(Bitmap bitImage, File file) {
		ByteArrayOutputStream baos = null;// 定义一个字节数组输出流
		try {
			System.out.println("打开输出流");
			baos = new ByteArrayOutputStream();
			// 调用系统函数压缩
			System.out.println("向输出流输出");
			byte[] bitmapData = ImageUtil.getInstance().getBytesFromBitMap(
					bitImage);
			// 定义文件输出流用于输出文件
			System.out.println("打开文件输出流");
			FileOutputStream fos = new FileOutputStream(file);
			System.out.println("文件写");
			fos.write(bitmapData);// 把字节流写入文件
			fos.flush();
			System.out.println("文件写完成");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Bitmap getBitmapFromUri(Uri uri, Context context) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
