package de.blinkt.openvpn.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class GrayService extends Service {

	private final static int GRAY_SERVICE_ID = 1001;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent innerIntent = new Intent(this, GrayInnerService.class);
		startService(innerIntent);
		startForeground(GRAY_SERVICE_ID, new Notification());
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 给 API >= 18 的平台上用的灰色保活手段
	 */
	public static class GrayInnerService extends Service {

		@Nullable
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			startForeground(GRAY_SERVICE_ID, new Notification());
			stopForeground(true);
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}

	}
}