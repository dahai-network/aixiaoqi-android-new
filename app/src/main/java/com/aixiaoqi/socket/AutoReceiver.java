package com.aixiaoqi.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.NetworkUtils;

import static android.content.Context.POWER_SERVICE;
import static com.aixiaoqi.socket.SocketConstant.HEARTBEAT_PACKET_TIMER;

/**
 * Created by Administrator on 2017/1/20 0020.
 * 系统定时发送心跳包
 */

public class AutoReceiver extends BroadcastReceiver {

	private String TAG = "AutoReceiver";

	public static PowerManager.WakeLock t_wakelock;
	@Override
	public void onReceive(final Context context, Intent intent) {

		if (intent.getAction().equals(HEARTBEAT_PACKET_TIMER)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (TestProvider.sendYiZhengService != null) {
//						Log.e("AutoReceiver","AutoReceiver111"+System.currentTimeMillis());
//						if (t_wakelock == null) {
//							PowerManager	t_power = (PowerManager) context
//									.getSystemService(POWER_SERVICE);
//							Log.e("AutoReceiver","AutoReceiver222"+System.currentTimeMillis());
//						 t_wakelock = t_power.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SleepMonitor");
//							t_wakelock.acquire();
//							Log.e("AutoReceiver","AutoReceiver333"+System.currentTimeMillis());
//						}
						Log.i(TAG, DateUtils.getCurrentDateForFileDetail() + " 发送心跳包，是否联网：" + NetworkUtils.isNetworkAvailable(context));
						Log.e("AutoReceiver","AutoReceiver444"+System.currentTimeMillis());
						TestProvider.sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
						Log.e("AutoReceiver","AutoReceiver555"+System.currentTimeMillis());
					} else {
						Log.e(TAG, "AutoReceiver 异常！");
					}

				}
			}).start();
		}
	}
}
