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
	 * ���ü�������
	 */
	/** �����������๦�ܵĳ��� */
	public static final int Camera_Data = 168;
	/** ����ͼƬѡ�����ĳ��� */
	public static final int Photo_Data = Camera_Data + 1;
	/** ����ͼƬ�ü��ĳ��� */
	public static final int Photo_Crop = Photo_Data + 1;
	/**
	 * ͼƬ�洢λ�ó��� getExternalStorageDirectory():��ȡsd����Ŀ¼
	 */
	public static final File Photo_Dir = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
	/** ���ջ�õ�ͼƬ */
	public static File PhotoForCamera;

	public File file; // ��ͼ���ͼƬ
	public static Uri imageUri; // ���պ��õ�ͼƬ·��
	public static Uri imageCropUri; // �ü����ͼƬ·��

	/**
	 * ��ȡ��ǰͼƬ·��
	 */
	public static File getCurrentPhotoFile() {
		if (PhotoForCamera == null) {
			if (!Photo_Dir.exists()) {
				Photo_Dir.mkdirs();
			}
			PhotoForCamera = new File(Photo_Dir, getCharacterAndNumber()
					+ ".png"/** ��׺�����Լ��� */
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
	 * ����ͷ��ѡ���
	 * 
	 * @param context
	 */
	public static void doPickAction(final Activity context) {
		/** ������ʾ�� */
		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = "���������ȡ";
		choices[1] = "�������ѡ��";
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);
		// ����
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle("��ѡ��ͼƬ�ϴ���ʽ");
		// ���ü򵥵��б�ѡ��
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
							if (statue.equals(Environment.MEDIA_MOUNTED)) {// �ж��Ƿ���SD��
								doTakePhoto(context);// ����˴��������ȡ
							} else {
								Toast.makeText(dialogContext, "û���ҵ�SD��",
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
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/**
	 * �������๦��
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
			Toast.makeText(context, "û���ҵ����", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ����������
	 * 
	 * @param context
	 */
	private static void doTakePhoto(Activity context) {
		// TODO Auto-generated method stub
		try {
			if (!Photo_Dir.exists()) {
				Photo_Dir.mkdirs(); // ������Ƭ�Ĵ洢Ŀ¼
			}
			imageUri = Uri.fromFile(getCurrentPhotoFile());
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			context.startActivityForResult(intent, Camera_Data);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(context, "û���ҵ����", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ��ȡͼƬ·��
	 * 
	 * @return
	 */
	public static Uri getImageUri(Context context) {
		int degree = getBitmapDegree(PhotoForCamera.getPath());
		if (degree != 0) {

			imageUri = Uri.fromFile(PhotoForCamera);
			System.out.println("��ȡBitmap");
			Bitmap bitmap = getBitmapFromUri(imageUri, context);
			System.out.println("��תBitmap");
			bitmap = rotateBitmapByDegree(bitmap, degree);
			System.out.println("�洢Bitmap");
			saveMyBitmap(bitmap, PhotoForCamera);
		}
		System.out.println("��ȡUri");
		imageUri = Uri.fromFile(PhotoForCamera);
		System.out.println("PhotoForCamera.toString()"
				+ PhotoForCamera.toString());
		System.out.println("imageUri.toString()" + imageUri.toString());
		return imageUri;
	}

	/**
	 * ��ȡ��ͼ���ͼƬ·��
	 * 
	 * @return
	 */

	public static Uri getImageCropUri() {
		File tempfile = new File(Photo_Dir, getCharacterAndNumber() + ".png");
		imageCropUri = Uri.fromFile(tempfile);
		return imageCropUri;
	}

	// ��ȡ��ǰʱ�䲢ת��Ϊ�ַ���
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
			// ��ָ��·���¶�ȡͼƬ������ȡ��EXIF��Ϣ
			ExifInterface exifInterface = new ExifInterface(path);
			// ��ȡͼƬ����ת��Ϣ
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

		// ������ת�Ƕȣ�������ת����
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// ��ԭʼͼƬ������ת���������ת�����õ��µ�ͼƬ
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
		ByteArrayOutputStream baos = null;// ����һ���ֽ����������
		try {
			System.out.println("�������");
			baos = new ByteArrayOutputStream();
			// ����ϵͳ����ѹ��
			System.out.println("����������");
			byte[] bitmapData = ImageUtil.getInstance().getBytesFromBitMap(
					bitImage);
			// �����ļ��������������ļ�
			System.out.println("���ļ������");
			FileOutputStream fos = new FileOutputStream(file);
			System.out.println("�ļ�д");
			fos.write(bitmapData);// ���ֽ���д���ļ�
			fos.flush();
			System.out.println("�ļ�д���");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Bitmap getBitmapFromUri(Uri uri, Context context) {
		try {
			// ��ȡuri���ڵ�ͼƬ
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					context.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
