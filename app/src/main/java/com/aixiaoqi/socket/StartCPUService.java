package com.aixiaoqi.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2017/3/4 0004.
 */

public class StartCPUService extends Service {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PowerManager t_power = (PowerManager) getSystemService(POWER_SERVICE);
		PowerManager.WakeLock t_wakelock = t_power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SleepMonitor");
		t_wakelock.acquire();
		CommonTools.delayTime(1000);
		t_wakelock.release();
		return super.onStartCommand(intent, flags, startId);
	}
}
