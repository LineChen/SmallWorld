package com.chat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.static_configs.StaticValues;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * @desc:���������࣬����¼��������¼����
 */
public class SoundUtil {
//	private static final double EMA_FILTER = 0.6;
	private static SoundUtil INSTANCE;
	private static MediaRecorder mMediaRecorder;
//	private double mEMA = 0.0;
	private MediaPlayer mMediaPlayer;
	/** ¼���洢·�� */
	

	private String tempVoicePath;// ��ʱ�����ļ�·��

	public SoundUtil() {
	}

	public static SoundUtil getInstance() {
		if (INSTANCE == null) {
			synchronized (SoundUtil.class) {
				if (INSTANCE == null) {
					INSTANCE = new SoundUtil();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * ��ʼ��
	 */
	private static void initMedia() throws Exception {
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		}
	}

	/**
	 * ��ʼ¼��
	 * 
	 * @param name
	 *            �����洢��·��
	 */
	public void startRecord(Context context, String voiceName) {
		Log.i("--", "��ʼ¼��...");
		if (FileUtils.hasSdcard()) {
			try {
				initMedia();
			} catch (Exception e1) {
				e1.printStackTrace();
				Toast.makeText(context, "��˷粻����", 0).show();
			}
			//"/sdcard/iMoMo/voiceRecord/"
			File dir = new File(StaticValues.VOICEPATH);
			if(!dir.exists()){
				dir.mkdirs();
			}
			tempVoicePath = StaticValues.VOICEPATH + voiceName + ".amr";
			mMediaRecorder.setOutputFile(tempVoicePath);
			Log.i("--", "¼��·��:" + StaticValues.VOICEPATH + voiceName);
			try {
				mMediaRecorder.prepare();
				mMediaRecorder.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context, "���ڴ濨,�ù�����ʱ�޷�ʹ��", 0).show();
		}
	}
	
	/**
	 * ֹͣ¼��
	 */
	public void stopRecord() throws IllegalStateException {
		Log.i("--", "ֹͣ¼��");
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	/**��ȡ¼���ļ�·��*/
	public String getVoicePath() {
		return tempVoicePath;
	}

	/**
	 * ��ȡ¼���ļ���(��Ϊ���������ݿ��е�ֻ�������ļ������ƻ�Id,����Ҫ�����������ȡ�����ļ�)
	 * @return
	 */
	public byte[] getVoicebytes(Context context, String voiceName) {
		File file = new File(StaticValues.VOICEPATH + voiceName + ".amr");
		ByteBuffer bytebuffer = null;
		if (!file.exists()) { 
			Toast.makeText(context, "���ļ�������", 0).show();
		} else {
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				FileChannel channel = fileInputStream.getChannel();
				bytebuffer = ByteBuffer.allocate((int) channel.size());
				bytebuffer.clear();
				channel.read(bytebuffer);
				channel.close();
				fileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bytebuffer.array();
	}

	

	/** ʱ��̫�̣�ɾ��¼���ļ� **/
	public void deleteVoiceFile() {
		File file = new File(tempVoicePath);
		if (file.exists())
			file.delete();
		Log.i("--", "ʱ��̫��,ɾ��");

	}


	/**
	 * @Description���������ļ�
	 * @param name
	 *            �����ļ���·��(��MsgCotentAdapter��������Ȼ�󴫽���)
	 */
	public void playRecorder(Context context, String voicePath) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			Log.i("--", "����·��:" + voicePath);
			File file = new File(voicePath);
			if (file.exists()) {
				mMediaPlayer.setDataSource(voicePath);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mMediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							public void onCompletion(MediaPlayer mp) {
								Log.i("--", "�������");
								mMediaPlayer.release();
								mMediaPlayer = null;// ��ʱ�ͷ���Դ
							}
						});
			} else {
				// �����������ļ�
				Toast.makeText(context, "�����ļ�������", 0).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**������յ�������**/
	public void saveGettedVoice(Context context, byte[] voiceBytes, String voicePath){
		if(FileUtils.hasSdcard()){
			try {
				File file = new File(voicePath);
				FileOutputStream fileout = new FileOutputStream(file);
				FileChannel fc = fileout.getChannel();
				fileout.write(voiceBytes);
				fc.close();
				fileout.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}








