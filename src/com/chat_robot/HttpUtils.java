package com.chat_robot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chat_robot.ChatMessage.Type;
import com.clientUtils.ClientUtils;

public class HttpUtils {
	private static final String URL = "http://www.tuling123.com/openapi/api";
	private static final String API_KEY = "3104aece69d16096c8711c06003054f9";

	/**
	 * 发送一个消息，得到返回的消息
	 * @param msg
	 * @return
	 */
	public static ChatMessage sendMessage(String msg) {
		ChatMessage chatMessage = new ChatMessage();

		String jsonRes = doGet(msg);
		
//		Log.i("--", "图灵机器人：" + jsonRes);
		
		JSONObject json = JSON.parseObject(jsonRes);
		try {
			chatMessage.setMsg(json.getString("text"));
		} catch (Exception e) {
			chatMessage.setMsg("服务器繁忙，请稍后再试");
		}
		chatMessage.setDate(ClientUtils.getNowTime());
		chatMessage.setType(Type.INCOMING);
		return chatMessage;
	}

	public static String doGet(String msg) {
		String result = "";
		String url = setParams(msg);
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			java.net.URL urlNet = new java.net.URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlNet
					.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");

			is = conn.getInputStream();
			int len = -1;
			byte[] buf = new byte[1024];
			baos = new ByteArrayOutputStream();
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			baos.flush();
			result = new String(baos.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private static String setParams(String INFO) {
		String url = "";
		try {
			url = "http://www.tuling123.com/openapi/api?key=" + API_KEY
					+ "&info=" + URLEncoder.encode(INFO, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
}
