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
 * 处理发送或接收图片
 * 
 * @author Administrator
 * 
 */
public class ImageUtil {
	private static ImageUtil INSTANCE;

	/** 图片存储路径 */

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

	/** 保存图片 **/
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
				Log.i("--", "保存图片完成");
			} else{
				Toast.makeText(context, "无内存卡,无法保存图片", 0).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把BitMap转换为byte数组
	 * 
	 * @param bitmap
	 * @return
	 */
	public byte[] getBytesFromBitMap(Bitmap bitmap) {
		int size = bitmap.getWidth() * bitmap.getHeight();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
		// 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	
	/**
	 * 把二进制数组转化为BitMap
	 * @param bytes
	 * @return
	 */
	public Bitmap getBitMapFromByte(byte[] bytes){
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

}



