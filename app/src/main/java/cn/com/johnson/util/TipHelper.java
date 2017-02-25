package cn.com.johnson.util;

import java.io.IOException;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

public class TipHelper {

	private static MediaPlayer mMediaPlayer;
	private static Vibrator vibrator;//震动

	// 播放默认铃声
	// 返回Notification id
	public static int PlaySound(final Context context) {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int soundId = new Random(System.currentTimeMillis())
				.nextInt(Integer.MAX_VALUE);
		return soundId;
	}

	public static void stopSound() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
	}

	public static void PlayShock(final Context context) {
		  /*
		 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (audio.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
			vibrator.vibrate(pattern, 2);
		}
	}

	public static void stopShock() {
		if (vibrator != null) {
			vibrator.cancel();
		}
	}


}