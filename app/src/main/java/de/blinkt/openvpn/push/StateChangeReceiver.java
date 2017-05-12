package de.blinkt.openvpn.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SocketConstant;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.StateChangeEntity;


/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class StateChangeReceiver extends BroadcastReceiver {

	private final String CONNECT_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	private final String BLUETOOTH_CHANGE = "android.bluetooth.adapter.action.STATE_CHANGED";

	private void connectGoip() {
		if (ProMainActivity.sendYiZhengService != null) {
			//网络状态改变，比如从3G变为2G等网络的改变。
			EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING,SocketConstant.REG_STATUE_CHANGE);
			ProMainActivity.sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
		}
	}

	private void restartConnect() {
		if (SocketConstant.REGISTER_STATUE_CODE == 3 || SocketConstant.REGISTER_STATUE_CODE == 2) {
			SocketConstant.REGISTER_STATUE_CODE = 2;
			if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
				//从预读取数据那里重新注册
				connectGoip();
			} else {
				//如果TCP服务关闭了，则通知主界面重新开启
				EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING,SocketConstant.RESTART_TCP);
			}

		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		switch (intent.getAction()) {
			case CONNECT_CHANGE:
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo ni = cm.getActiveNetworkInfo();
				if (ni != null && ni.isConnectedOrConnecting()) {
					switch (ni.getType()) {
						case ConnectivityManager.TYPE_WIFI:
							restartConnect();
							break;
						case ConnectivityManager.TYPE_MOBILE:
							switch (ni.getSubtype()) {
								case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
								case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
								case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
								case TelephonyManager.NETWORK_TYPE_1xRTT:
								case TelephonyManager.NETWORK_TYPE_IDEN:
									restartConnect();
									break;
								case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
								case TelephonyManager.NETWORK_TYPE_UMTS:
								case TelephonyManager.NETWORK_TYPE_EVDO_0:
								case TelephonyManager.NETWORK_TYPE_HSDPA:
								case TelephonyManager.NETWORK_TYPE_HSUPA:
								case TelephonyManager.NETWORK_TYPE_HSPA:
								case TelephonyManager.NETWORK_TYPE_EVDO_B:
								case TelephonyManager.NETWORK_TYPE_EHRPD:
								case TelephonyManager.NETWORK_TYPE_HSPAP:
									restartConnect();
									break;
								case TelephonyManager.NETWORK_TYPE_LTE:
									restartConnect();
									break;
								default:

							}
							break;
						default:
							break;
					}

					EventBusUtil.simStateChange(StateChangeEntity.NET_STATE,true);
				} else {
					EventBusUtil.simStateChange(StateChangeEntity.NET_STATE,false);
				}
				break;
			case BLUETOOTH_CHANGE:
				if (ICSOpenVPNApplication.uartService != null && ICSOpenVPNApplication.uartService.isOpenBlueTooth()) {
					EventBusUtil.simStateChange(StateChangeEntity.BLUETOOTH_STATE,true);
				} else {
					EventBusUtil.simStateChange(StateChangeEntity.BLUETOOTH_STATE,false);
				}


				break;
		}
	}
}
