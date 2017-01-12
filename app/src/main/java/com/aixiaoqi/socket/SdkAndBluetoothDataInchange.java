package com.aixiaoqi.socket;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;

/**
 * Created by Administrator on 2017/1/5 0005.
 */
public class SdkAndBluetoothDataInchange {
	public static final String TAG = "Blue_Chanl";
	UartService mService;
	ArrayList<String> messages;
	ReceiveDataframSocketService mReceiveDataframSocketService;

	public void initReceiveDataframSocketService(ReceiveDataframSocketService receiveDataframSocketService, UartService mService) {
		receiveDataframSocketService.setListener(new ReceiveDataframSocketService.MessageOutLisener() {
													 @Override
													 public void sendToBluetoothMsg(final String msg) {
														 //SDK接收到消息发送给蓝牙消息的方法
														 //TODO
														 Log.e(TAG, "&&& server temp:" + msg);
														 if(!TextUtils.isEmpty(msg))
															 sendToBluetoothAboutCardInfo(msg);


													 }

												 }
		);
		//TODO 初始化UDPsocket
		receiveDataframSocketService.initDataframSocketService();
		this.mReceiveDataframSocketService = receiveDataframSocketService;
		this.mService = mService;
	}

	private String socketTag = "0";
	private String sendToOneServerTemp;
	private String mStrSimPowerOnPacket = "";
	byte num = 0;

	public void sendToSDKAboutBluetoothInfo(String temp, byte[] txValue) {
		num++;
		if (num != txValue[4]) {
			try {
				 Thread.sleep(1000);
			}catch (Exception e){

			}
			num = 0;
			Log.e(TAG, "蓝牙数据出错重发=" + finalTemp);
			sendToBluetoothAboutCardInfo(finalTemp);
			return;
		}
		if (messages == null) {
			messages = new ArrayList<>();
		}
		messages.add(temp);
		if (txValue[3] == txValue[4]) {
			mStrSimPowerOnPacket = PacketeUtil.Combination(messages);

			// 接收到一个完整的数据包,发送到SDK
			int length = (txValue[2] & 0xff);
			String sendToOnService = null;
			Log.e(TAG, "从蓝牙发出的完整数据 mStrSimPowerOnPacket:" + mStrSimPowerOnPacket.length() + "; \n"
					+ mStrSimPowerOnPacket + "\nlength=" + length);
			if (mStrSimPowerOnPacket.length() >= length) {
				try {
					sendToOnService = mStrSimPowerOnPacket.substring(0, length * 2);
				} catch (StringIndexOutOfBoundsException e) {
					Log.e(TAG, "catch socketTag:" + socketTag + "; \n"
							+ sendToOneServerTemp);
					sendToBluetoothAboutCardInfo(finalTemp);
					return;
				}
			} else {
				Log.e(TAG, "catch else:" + socketTag + "; \n"
						+ sendToOneServerTemp);
				sendToBluetoothAboutCardInfo(finalTemp);
				return;
			}
			socketTag = mReceiveDataframSocketService.getSorcketTag();
			sendToOneServerTemp = sendToOnService;
			Log.e(TAG, "从蓝牙发出的完整数据 socketTag:" + socketTag + "; \n"
					+ sendToOneServerTemp);
			sendToSDKAboutBluetoothInfo(socketTag + sendToOneServerTemp);
			num = 0;
			Log.e(TAG, "从蓝牙发出的数据" + socketTag + sendToOneServerTemp);

		}
	}


	private void sendToSDKAboutBluetoothInfo(final String finalMessage) {
		if (mReceiveDataframSocketService != null) {
			mReceiveDataframSocketService.sendToSdkMessage(finalMessage);
		}
		messages.clear();
	}

	private String finalTemp;

	private void sendToBluetoothAboutCardInfo(String msg) {
		String temp = "";
		if (msg.length() > 7) {
			temp = msg.substring(7);
		} else {
			temp = msg;
		}
		finalTemp = msg;
		if (temp.contains("0x0000")) {
			sendMessage(temp);
		} else {
			Log.e(TAG, "&&& server temp:" + temp);
			String[] messages = PacketeUtil.Separate(temp);

			for (int i = 0; i < messages.length; i++) {

//				if(i>=1){
//					try {
//						Log.e(TAG, "发送延迟100ms");
//						Thread.sleep(200);
//						Log.e(TAG, "发送延迟200ms");
//					}catch (Exception e){
//						Log.e(TAG, "发送延迟300ms");
//					}
//				}
				Log.e(TAG, "&&& server  message: " + messages[i].toString());
				Log.e(TAG, "发送到蓝牙的数据" + socketTag + sendToOneServerTemp);
				sendMessage(messages[i]);


			}
		}
	}

	private void sendMessage(String temp) {
		if (temp.contains("0x0000")) {
			byte[] value;
			value = HexStringExchangeBytesUtil.hexStringToBytes(Constant.UP_TO_POWER);
			mService.writeRXCharacteristic(value);
			TlvAnalyticalUtils.isOffToPower=false;
			Log.e(TAG, "SIM发送上电数据");
		} else {
			byte[] value = HexStringExchangeBytesUtil.hexStringToBytes(temp);
			mService.writeRXCharacteristic(value);
		}
	}
}
