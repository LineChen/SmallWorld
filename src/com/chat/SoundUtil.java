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
 * @desc:声音工具类，包括录音，播放录音等
 */
public class SoundUtil {
//	private static final double EMA_FILTER = 0.6;
	private static SoundUtil INSTANCE;
	private static MediaRecorder mMediaRecorder;
//	private double mEMA = 0.0;
	private MediaPlayer mMediaPlayer;
	/** 录音存储路径 */
	

	private String tempVoicePath;// 临时语音文件路径

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
	 * 初始化
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
	 * 开始录音
	 * 
	 * @param name
	 *            声音存储的路径
	 */
	public void startRecord(Context context, String voiceName) {
		Log.i("--", "开始录音...");
		if (FileUtils.hasSdcard()) {
			try {
				initMedia();
			} catch (Exception e1) {
				e1.printStackTrace();
				Toast.makeText(context, "麦克风不可用", 0).show();
			}
			//"/sdcard/iMoMo/voiceRecord/"
			File dir = new File(StaticValues.VOICEPATH);
			if(!dir.exists()){
				dir.mkdirs();
			}
			tempVoicePath = StaticValues.VOICEPATH + voiceName + ".amr";
			mMediaRecorder.setOutputFile(tempVoicePath);
			Log.i("--", "录音路径:" + StaticValues.VOICEPATH + voiceName);
			try {
				mMediaRecorder.prepare();
				mMediaRecorder.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context, "无内存卡,该功能暂时无法使用", 0).show();
		}
	}
	
	/**
	 * 停止录音
	 */
	public void stopRecord() throws IllegalStateException {
		Log.i("--", "停止录音");
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
	}

	/**获取录音文件路径*/
	public String getVoicePath() {
		return tempVoicePath;
	}

	/**
	 * 获取录音文件流(因为保存在数据库中的只是声音文件的名称或Id,所以要根据这个来获取声音文件)
	 * @return
	 */
	public byte[] getVoicebytes(Context context, String voiceName) {
		File file = new File(StaticValues.VOICEPATH + voiceName + ".amr");
		ByteBuffer bytebuffer = null;
		if (!file.exists()) { 
			Toast.makeText(context, "该文件不存在", 0).show();
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

	

	/** 时间太短，删除录音文件 **/
	public void deleteVoiceFile() {
		File file = new File(tempVoicePath);
		if (file.exists())
			file.delete();
		Log.i("--", "时间太短,删除");

	}


	/**
	 * @Description播放声音文件
	 * @param name
	 *            声音文件的路径(从MsgCotentAdapter里面点击，然后传进来)
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
			Log.i("--", "播放路径:" + voicePath);
			File file = new File(voicePath);
			if (file.exists()) {
				mMediaPlayer.setDataSource(voicePath);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mMediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							public void onCompletion(MediaPlayer mp) {
								Log.i("--", "播放完成");
								mMediaPlayer.release();
								mMediaPlayer = null;// 即时释放资源
							}
						});
			} else {
				// 不存在语音文件
				Toast.makeText(context, "声音文件不存在", 0).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**保存接收到的语音**/
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








