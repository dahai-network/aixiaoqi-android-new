package com.aixiaoqi.socket;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;

import static com.aixiaoqi.socket.EventBusUtil.registerFail;
import static com.aixiaoqi.socket.SocketConstant.HEARTBEAT_PACKET_TIMER;
import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;
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
	private static String TAG = "ReceiveSocketService";

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
			SocketConstant.SESSION_ID = SocketConstant.SESSION_ID_TEMP;
			createSocketLisener.create();
		}

		@Override
		public void onConnectFailed() {
			connectFailReconnect();
		}


		@Override
		public void onReceive(SocketTransceiver transceiver, byte[] s, int length) {
//			Log.e("Blue_Chanl", "接收数据 - onReceive");
//			if(AutoReceiver.t_wakelock!=null){
//				AutoReceiver.t_wakelock.release();
//				AutoReceiver.t_wakelock=null;
//			}
			TlvAnalyticalUtils.builderMessagePackageList(HexStringExchangeBytesUtil.bytesToHexString(s, length));
			Log.e("Blue_Chanl", "接收数据 - onReceive2");
			createHeartBeatPackage();
			recordStringLog(DateUtils.getCurrentDateForFileDetail() + "read :" + HexStringExchangeBytesUtil.bytesToHexString(s, length));

		}

		@Override
		public void onDisconnect(SocketTransceiver transceiver) {
			Log.e("Blue_Chanl", "断开连接 - onDisconnect");
			disConnectReconnect();
		}


	};

	//首次创建连接失败，重试三次还不成功，则断开连接，并且提示注册失败。
	private void connectFailReconnect() {
		ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "connect fail:\n" );
		if(!isDisconnect){
			CommonTools.delayTime(5000);
			if (tcpClient != null && !tcpClient.isConnected()) {
				if (contactFailCount <= 3) {
					reConnect();
					contactFailCount++;
				} else {
					contactFailCount = 0;
					registerFail(Constant.REGIST_CALLBACK_TYPE, SocketConstant.START_TCP_FAIL);
				}
			}

		}
	}

	public void disconnect() {
		tcpClient.disconnect();
	}


	private boolean isDisconnect = false;

	//断开连接，如果注册成功，需要重新注册，并且改变注册状态
	private void disConnectReconnect() {
		isDisconnect = true;

//		cancelTimer();
		CommonTools.delayTime(5000);
		if (tcpClient != null && !tcpClient.isConnected()) {
			if (REGISTER_STATUE_CODE == 3) {
				REGISTER_STATUE_CODE = 2;
				registerFail(Constant.REGIST_CALLBACK_TYPE, SocketConstant.TCP_DISCONNECT);
			}
			sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
			recordStringLog(DateUtils.getCurrentDateForFileDetail() + "restart connect :\n" );
			reConnect();
		}
	}

	/**
	 * 打开日志文件并写入日志
	 *
	 * @return
	 **/
	public static void recordStringLog(String text) {// 新建或打开日志文件
		String path = Environment.getExternalStorageDirectory().getPath() + "/aixiaoqi/";
		String fileName = "TCP" + DateUtils.getCurrentDateForFile() + ".text";
		File file = new File(path + fileName);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(text);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
			Log.d("行为日志写入成功", text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createHeartBeatPackage() {
		Log.e(TAG, "count=" + count + "\nSocketConstant.SESSION_ID_TEMP" + SocketConstant.SESSION_ID_TEMP + "\nSocketConstant.SESSION_ID=" + SocketConstant.SESSION_ID + (SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID)));
		if (!SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID) && count == 0&&am==null) {
			count = count + 1;
			Log.e("onReceive", "开启定时器");
			Intent intent = new Intent(ReceiveSocketService.this, AutoReceiver.class);
			intent.setAction(HEARTBEAT_PACKET_TIMER);
			sender = PendingIntent.getBroadcast(ReceiveSocketService.this, 0, intent, 0);
			am = (AlarmManager) getSystemService(ALARM_SERVICE);
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, sender);
		}
	}



	private void reConnect() {

		initSocket();

	}

	public void sendMessage(String s) {
		Log.e("sendMessage", s);
		Log.e("sendMessage", "发送到GOIPtcpClient" + (tcpClient != null));
		Log.e("sendMessage", "发送到GOIPtcpClient" + (tcpClient != null) + "\n发送到GOIPtcpClient" + (tcpClient.getTransceiver() != null));

		if (tcpClient != null && tcpClient.getTransceiver() != null) {
			tcpClient.getTransceiver().send(s);
			recordStringLog(DateUtils.getCurrentDateForFileDetail() + "write :\n" + s);
		}
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy()");
		if (SocketConnection.sdkAndBluetoothDataInchange != null)
			SocketConnection.sdkAndBluetoothDataInchange.closeReceviceBlueData();
		if (tcpClient != null) {
			tcpClient.closeTimer();
			tcpClient.disconnect();
//			tcpClient=null;
		}
		Log.e(TAG, "tcpClient=null" + (tcpClient == null));
		count = 0;
		SocketConstant.SESSION_ID = SocketConstant.SESSION_ID_TEMP;
		cancelTimer();
		TlvAnalyticalUtils.clearData();
		TestProvider.clearData();

		if (SocketConstant.REGISTER_STATUE_CODE != 0) {
			SocketConstant.REGISTER_STATUE_CODE = 1;
		}
		super.onDestroy();
	}

	private void cancelTimer() {
		if (am != null) {
			am.cancel(sender);
			am = null;
		}
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
