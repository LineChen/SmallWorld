//package com.example.hellostranger.util;
//
//import com.example.hellostranger.R;
//
//import android.app.NotificationManager;
//import android.content.Context;
//import android.media.AudioManager;
//import android.media.SoundPool;
//import android.os.Vibrator;
//
//public class NotifyUtils {
//
//	private SoundPool soundPool = null;// 播放短小的音乐
//	private int msgcoming;// 消息来了的提示音
//	private int voicerec;// 录音提示
//	private int voicesend;// 发送语音提示
//	
//	private Context context;
//
//	public NotifyUtils(Context context) {
//		this.context = context;
//	}
//
//	/**
//	 * 初始化提示音播放
//	 */
//	public void initPlayer(){
//		// 初始化音乐池
//		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//		// 向音乐池中添加音乐
//		msgcoming = soundPool.load(context, R.raw.msgcoming, 0);
//		voicerec = soundPool.load(context, R.raw.voicerec, 0);
//		voicesend = soundPool.load(context, R.raw.voicesend, 0);
//	}
//
//	/**
//	 * 播放消息提示音
//	 */
//	public void playSound(int id) {
//		initPlayer();
//		switch (id) {
//		case 1:
//			soundPool.play(msgcoming, 1, 1, 0, 0, 1);
//			break;
//		case 3:
//			soundPool.play(voicerec, 1, 1, 0, 0, 1);
//			break;
//		case 4:
//			soundPool.play(voicesend, 1, 1, 0, 0, 1);
//			break;
//		}
//	}
//	
//	/**
//	 * 震动(长震动)
//	 */
//	public void Longvibrate(){
//		// 震动
//		Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//		vibe.vibrate(800);
//	}
//	
//	
//}
//
//
//
