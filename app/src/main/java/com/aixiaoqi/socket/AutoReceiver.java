package com.aixiaoqi.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.aixiaoqi.socket.SocketConstant.HEARTBEAT_PACKET_TIMER;
import static de.blinkt.openvpn.activities.Device.ModelImpl.HasPreDataRegisterImpl.sendYiZhengService;

/**
 * Created by Administrator on 2017/1/20 0020.
 * 系统定时发送心跳包
 */

public class AutoReceiver extends BroadcastReceiver {

	private String TAG = "AutoReceiver";
	@Override
	public void onReceive(final Context context, Intent intent) {

		if (intent.getAction().equals(HEARTBEAT_PACKET_TIMER)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (sendYiZhengService != null) {
						sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
					} else {
						Log.e(TAG, "AutoReceiver 异常！");
					}
				}
			}).start();
		}
	}
}
