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
	public static PowerManager t_power;

	@Override
	public void onReceive(final Context context, Intent intent) {

		if (intent.getAction().equals(HEARTBEAT_PACKET_TIMER)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (TestProvider.sendYiZhengService != null) {
						if (t_power == null) {
							t_power = (PowerManager) context
									.getSystemService(POWER_SERVICE);
							PowerManager.WakeLock t_wakelock = t_power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SleepMonitor");
							t_wakelock.acquire();
						}
						Log.i(TAG, DateUtils.getCurrentDateForFileDetail() + " 发送心跳包，是否联网：" + NetworkUtils.isNetworkAvailable(context));
						TestProvider.sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
					} else {
						Log.e(TAG, "AutoReceiver 异常！");
					}

				}
			}).start();
		}
	}
}
