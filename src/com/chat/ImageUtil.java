package com.chat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
import com.static_configs.StaticValues;

/**
 * �����ͻ����ͼƬ
 * 
 * @author Administrator
 * 
 */
public class ImageUtil {
	private static ImageUtil INSTANCE;

	/** ͼƬ�洢·�� */

	public ImageUtil() {
	}

	public static ImageUtil getInstance() {
		if (INSTANCE == null) {
			synchronized (SoundUtil.class) {
				if (INSTANCE == null) {
					INSTANCE = new ImageUtil();
				}
			}
		}
		return INSTANCE;
	}

	/** ����ͼƬ **/
	public void saveImage(Context context, byte[] imageBytes, String imagePath) {
		try {
			if (FileUtils.hasSdcard()) {
				File dir1 = new File(StaticValues.IMAGEPATH);
				File dir2 = new File(StaticValues.USER_HEADPATH);
				if (!dir1.exists())
					dir1.mkdirs();
				if (!dir2.exists())
					dir2.mkdirs();
				File file = new File(imagePath);
				FileOutputStream fileout;
				fileout = new FileOutputStream(file);
				FileChannel fc = fileout.getChannel();
				fileout.write(imageBytes);
				fc.close();
				fileout.close();
				Log.i("--", "����ͼƬ���");
			} else{
				Toast.makeText(context, "���ڴ濨,�޷�����ͼƬ", 0).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��BitMapת��Ϊbyte����
	 * 
	 * @param bitmap
	 * @return
	 */
	public byte[] getBytesFromBitMap(Bitmap bitmap) {
		int size = bitmap.getWidth() * bitmap.getHeight();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
		// ����λͼ��ѹ����ʽ������Ϊ100%���������ֽ������������
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	
	/**
	 * �Ѷ���������ת��ΪBitMap
	 * @param bytes
	 * @return
	 */
	public Bitmap getBitMapFromByte(byte[] bytes){
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

}



