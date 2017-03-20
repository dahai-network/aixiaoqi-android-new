package com.aixiaoqi.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.NetworkUtils;

import static com.aixiaoqi.socket.SocketConstant.HEARTBEAT_PACKET_TIMER;

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
					if (ProMainActivity.sendYiZhengService != null) {
						Log.i(TAG, DateUtils.getCurrentDateForFileDetail() + " 发送心跳包，是否联网：" + NetworkUtils.isNetworkAvailable(context));
						ProMainActivity.sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
					} else {
						Log.e(TAG, "AutoReceiver 异常！");
					}

				}
			}).start();
		}
	}
}
