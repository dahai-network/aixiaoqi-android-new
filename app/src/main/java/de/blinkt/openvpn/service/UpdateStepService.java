package de.blinkt.openvpn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by Administrator on 2016/10/6.
 */

public class UpdateStepService extends Service implements InterfaceCallback {

	private UartService mService = ICSOpenVPNApplication.uartService;

	private void sendMessageToBlueTooth(final String message) {
		byte[] value;
		value = HexStringExchangeBytesUtil.hexStringToBytes(message);
		if (mService != null) {
			if (mService.mConnectionState == UartService.STATE_CONNECTED) {
				mService.writeRXCharacteristic(value);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						//十分钟上传一次数据
						Thread.sleep(1000 * 60 * 10);
						sendMessageToBlueTooth(Constant.HISTORICAL_STEPS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {

	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	@Override
	public void noNet() {

	}
}
