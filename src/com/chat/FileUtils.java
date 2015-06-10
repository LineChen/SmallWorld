package com.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.msg_relative.MsgKeys;
import com.msg_relative.iMoMoMsgDb;
import com.msg_relative.iMoMoMsgTypes;
import com.static_configs.StaticValues;

import android.content.Context;
import android.os.Environment;

/**
 * 文件工具类
 * @author Administrator
 *
 */
public class FileUtils {
	/**
	 * 读取表情文件
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("faces.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"gbk"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**删除文件**/
	public static void deleteFile(String path){
		File file = new File(path);
		if(file.exists())
			file.delete();
	}
	
	/**判断文件是否存在**/
	public static boolean isFileExists(String path){
		File file = new File(path);
			return file.exists();
	}
	
	
	/** 是否有内存卡 */
	public static boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 保存好友消息到本地文件
	 * @param list
	 */
	public static void saveMsgToText(List<iMoMoMsgDb> list, String friendName) throws Exception{
		String path = null;
		BufferedWriter writer = null;
		File msgDir = new File(StaticValues.MSG_TEXT);
		if (!msgDir.exists()) {
			msgDir.mkdirs();
		}
		path = StaticValues.MSG_TEXT + friendName + "_"
				+ System.currentTimeMillis() + ".txt";
		File newFile = new File(path);
		newFile.createNewFile();
		writer = writer = new BufferedWriter(new FileWriter(newFile));
		for (int i = list.size() - 1; i >= 0; i--) {
			iMoMoMsgDb msgDb = list.get(i);
			if (msgDb.msgType == iMoMoMsgTypes.CHATING_TEXT_MSG) {
				JSONObject json = JSON.parseObject(msgDb.msgJson);
				String content = json.getString(MsgKeys.msgCotent);
				if(msgDb.isGetted == 1){
					writer.write(friendName + "		" + msgDb.sendTime);
				} else {
					writer.write("我		" + msgDb.sendTime);
				}
				writer.newLine();
				writer.write(content);
				writer.newLine();
			}
		}
		writer.close();
	}
	
	
}




