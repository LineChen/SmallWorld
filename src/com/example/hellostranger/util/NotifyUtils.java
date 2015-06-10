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
//	private SoundPool soundPool = null;// ���Ŷ�С������
//	private int msgcoming;// ��Ϣ���˵���ʾ��
//	private int voicerec;// ¼����ʾ
//	private int voicesend;// ����������ʾ
//	
//	private Context context;
//
//	public NotifyUtils(Context context) {
//		this.context = context;
//	}
//
//	/**
//	 * ��ʼ����ʾ������
//	 */
//	public void initPlayer(){
//		// ��ʼ�����ֳ�
//		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//		// �����ֳ����������
//		msgcoming = soundPool.load(context, R.raw.msgcoming, 0);
//		voicerec = soundPool.load(context, R.raw.voicerec, 0);
//		voicesend = soundPool.load(context, R.raw.voicesend, 0);
//	}
//
//	/**
//	 * ������Ϣ��ʾ��
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
//	 * ��(����)
//	 */
//	public void Longvibrate(){
//		// ��
//		Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//		vibe.vibrate(800);
//	}
//	
//	
//}
//
//
//
