package com.aixiaoqi.socket;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.util.CommonTools;

import static com.aixiaoqi.socket.SocketConstant.HEARTBEAT_PACKET_TIMER;
import static com.aixiaoqi.socket.SocketConstant.TRAN_DATA_TO_SDK;
import static com.aixiaoqi.socket.TlvAnalyticalUtils.sendToSdkLisener;

/**
 * Created by Administrator on 2016/12/30 0030.
 */
public class ReceiveSocketService extends Service {
	private final IBinder mBinder = new LocalBinder();
	private int contactFailCount = 1;
	PendingIntent sender;
	AlarmManager am;
	private static String TAG="ReceiveSocketService";
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public ReceiveSocketService getService() {
			return ReceiveSocketService.this;
		}
	}

	public void initSocket() {
		tcpClient.connect();
	}

	TcpClient tcpClient = new TcpClient() {
		@Override
		public void onConnect(SocketTransceiver transceiver) {
			Log.i("Blue_Chanl", "正在注册GOIP");
			SocketConstant.SESSION_ID=SocketConstant.SESSION_ID_TEMP;
			createSocketLisener.create();
		}

		@Override
		public void onConnectFailed() {
			connectFailReconnect();
		}




		@Override
		public void onReceive(SocketTransceiver transceiver, byte[] s, int length) {
			TlvAnalyticalUtils.builderMessagePackageList(HexStringExchangeBytesUtil.bytesToHexString(s, length));
			createHeartBeatPackage();
		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			Log.e("Blue_Chanl", "断开连接 - onDisconnect");

			disConnectReconnect();
		}


	};

	private void connectFailReconnect() {
		CommonTools.delayTime(2000);
		if(!tcpClient.isConnected()){
			if (contactFailCount <= 3) {
				reConnect();
			}
			contactFailCount++;
		}
	}
	private void disConnectReconnect() {
		CommonTools.delayTime(2000);
		if(!tcpClient.isConnected()) {
			sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
			reConnect();
		}
	}
	private void createHeartBeatPackage() {
		if (!SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID) && count == 0) {
//                timer.schedule(task,60000,60000);
            count = count + 1;
            Log.e("onReceive", "开启定时器");
            Intent intent = new Intent(ReceiveSocketService.this, AutoReceiver.class);
            intent.setAction(HEARTBEAT_PACKET_TIMER);
            sender = PendingIntent.getBroadcast(ReceiveSocketService.this, 0, intent, 0);
            am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60 * 1000, sender);
        }
	}


	private void reConnect() {
		tcpClient.disconnect();
		initSocket();

	}

	public void sendMessage(String s) {
		Log.e("sendMessage", "发送到GOIPtcpClient" + (tcpClient != null) + "\n发送到GOIPtcpClient" + (tcpClient.getTransceiver() != null));
		if (tcpClient != null && tcpClient.getTransceiver() != null) {
			tcpClient.getTransceiver().send(s);
		}
	}

	@Override
	public void onDestroy() {
		Log.e(TAG,"onDestroy()");
		tcpClient.disconnect();
		tcpClient=null;
		Log.e(TAG,"tcpClient=null"+(tcpClient==null));
		if (am != null){
			am.cancel(sender);
			am=null;
		}
		TlvAnalyticalUtils.clearData();
		TestProvider.clearData();
		if (SocketConstant.REGISTER_STATUE_CODE != 0) {
			SocketConstant.REGISTER_STATUE_CODE = 1;
		}
		super.onDestroy();
	}

	CreateSocketLisener createSocketLisener;

	public void setListener(CreateSocketLisener listener) {
		this.createSocketLisener = listener;
	}

	public interface CreateSocketLisener {
		void create();
	}

	int count = 0;
}
