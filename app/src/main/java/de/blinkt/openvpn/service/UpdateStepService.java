package de.blinkt.openvpn.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by Administrator on 2016/10/6.
 */

public class UpdateStepService extends Service implements InterfaceCallback {

	private UartService mService = ICSOpenVPNApplication.uartService;
	//是否第一次请求，如果是，则隔一分钟后请求实时步数
	private boolean isFirstRequestBoolean = true;



	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {

					try {
						if (isFirstRequestBoolean) {
							Thread.sleep(1000 * 10);
							isFirstRequestBoolean = false;
						} else {
							//十分钟上传一次数据
							Thread.sleep(1000 * 60 * 30);
						}
//						sendHandler.sendEmptyMessage(0);
						SendCommandToBluetooth.sendMessageToBlueTooth(Constant.HISTORICAL_STEPS);
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
