package com.aixiaoqi.socket;

import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.PercentEntity;
import de.blinkt.openvpn.util.CommonTools;

import static com.aixiaoqi.socket.EventBusUtil.registerFail;

/**
 * Created by Administrator on 2017/1/5 0005.
 */
public class SdkAndBluetoothDataInchange {
	public static final String TAG = "Blue_Chanl";
	UartService mService;
	//	ArrayList<String> messages;
	ReceiveDataframSocketService mReceiveDataframSocketService;

	public void initReceiveDataframSocketService(ReceiveDataframSocketService receiveDataframSocketService, UartService mService) {
		receiveDataframSocketService.setListener(new ReceiveDataframSocketService.MessageOutLisener() {
													 @Override
													 public void sendToBluetoothMsg(final String msg) {
														 //SDK接收到消息发送给蓝牙消息的方法
														 //TODO
														 Log.e(TAG, "&&& server temp:" + msg);

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
	private String mStrSimPowerOnPacket = "";
	Timer timerMessage ;
	TimerTask timerTaskMessage ;

	private void notifyRegisterFail() {
		registerFail(Constant.REGIST_CALLBACK_TYPE,SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA);
	}

	long getSendBlueToothTime;
	private int countMessage = 0;
	private int notCanReceiveBluetoothDataCount = 0;
	private String finalTemp;//保存上一次发给蓝牙的数据，以免出错，需要重发
	private boolean isReceiveBluetoothData = true;//判断5s内是否接收完成，没有完成则重新发送
	public void sendToSDKAboutBluetoothInfo(ArrayList<String> messages) {

		synchronized (this){
			if (countMessage ==0) {
				Log.e("timer", "开启定时器");
				countMessage++;
				if(timerMessage==null){
					timerMessage= new Timer();
				}
				if(timerTaskMessage==null){
					timerTaskMessage= new TimerTask() {
						@Override
						public void run() {

							if (SocketConstant.REGISTER_STATUE_CODE != 3) {
								if (System.currentTimeMillis() - getSendBlueToothTime > 5000 && !isReceiveBluetoothData&&notCanReceiveBluetoothDataCount<3) {
									Log.e("timer", "接收不到蓝牙数据");
									sendToBluetoothAboutCardInfo(finalTemp);
									notCanReceiveBluetoothDataCount++;
								}else if(notCanReceiveBluetoothDataCount>=3){
									Log.e("timer", "注册失败");
									notifyRegisterFail();
									clearTimer();
									notCanReceiveBluetoothDataCount=0;
								}
							}
						}
					};
				}
				timerMessage.schedule(timerTaskMessage, 5000, 5000);

			}


			if (percentEntity == null) {
				percentEntity = new PercentEntity();
			}
			int percent = Integer.parseInt(TextUtils.isEmpty(mReceiveDataframSocketService.getSorcketTag()) ? "-1" : mReceiveDataframSocketService.getSorcketTag().substring(mReceiveDataframSocketService.getSorcketTag().length() - 4, mReceiveDataframSocketService.getSorcketTag().length() - 1));
			percentEntity.setPercent(percent);
			EventBus.getDefault().post(percentEntity);
			isReceiveBluetoothData = true;
			notCanReceiveBluetoothDataCount=0;
			mStrSimPowerOnPacket = PacketeUtil.Combination(messages);
			socketTag = mReceiveDataframSocketService.getSorcketTag();
			Log.e(TAG, "从蓝牙发出的完整数据 socketTag:" + socketTag + "; \n"
					+ mStrSimPowerOnPacket);
			sendToSDKAboutBluetoothInfo(socketTag + mStrSimPowerOnPacket);
			messages.clear();

		}
	}


	PercentEntity percentEntity;

	private void sendToSDKAboutBluetoothInfo(final String finalMessage) {
		if (mReceiveDataframSocketService != null) {
			mReceiveDataframSocketService.sendToSdkMessage(finalMessage);
		}
	}



	private void sendToBluetoothAboutCardInfo(String msg) {
		if(TextUtils.isEmpty(msg)){
			registerFail(Constant.REGIST_CALLBACK_TYPE,SocketConstant.REGISTER_FAIL);
			return;
		}
		Log.e(TAG, "SDK进入: sendToBluetoothAboutCardInfo:" + msg);
		isReceiveBluetoothData = false;
		getSendBlueToothTime = System.currentTimeMillis();
		String temp;
		if (msg.length() > 7) {
			temp = msg.substring(7);
		} else {
			temp = msg;
		}
		finalTemp = msg;
		if (temp.contains("0x0000")) {
			Log.e(TAG, "&&& server temp:" + temp);
			sendMessage(temp);
		} else {
			Log.e(TAG, "SDK进入: sendToBluetoothAboutCardInfo:" + temp);
			String[] messages = PacketeUtil.Separate(temp,Constant.READED_SIM_DATA);
			for (int i = 0; i < messages.length; i++) {
				Log.e(TAG, "&&& server  message: " + messages[i]);
				sendMessage(messages[i]);
			}
		}
	}

	private void sendMessage(String temp) {
		if (temp.contains("0x0000")) {
			CommonTools.delayTime(2000);
			SendCommandToBluetooth.sendMessageToBlueTooth(Constant.UP_TO_POWER_DETAIL);
			Log.e(TAG, "SIM发送上电数据（只有详细卡信息）");
		} else {
			if (mService != null) {
				byte[] value = HexStringExchangeBytesUtil.hexStringToBytes(temp);
				mService.writeRXCharacteristic(value);
			}
		}
	}

	public  void closeReceviceBlueData(){
		clearTimer();
	}

	private void clearTimer() {
		if(timerMessage!=null){
			timerMessage.cancel();
			timerMessage=null;
		}
		if(timerTaskMessage!=null){
			timerTaskMessage.cancel();
			timerTaskMessage=null;
		}
		countMessage=0;
	}
}
