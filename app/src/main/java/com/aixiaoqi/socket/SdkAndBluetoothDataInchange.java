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
import de.blinkt.openvpn.model.IsSuccessEntity;
import de.blinkt.openvpn.model.PercentEntity;
import de.blinkt.openvpn.util.CommonTools;

import static com.aixiaoqi.socket.EventBusUtil.registerFail;

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
	//	byte num = 0;
	private long lastTime;
	private int count = 0;

	Timer timerMessage ;
	TimerTask timerTaskMessage = new TimerTask() {
		@Override
		public void run() {

			if (SocketConstant.REGISTER_STATUE_CODE != 3) {
				if (System.currentTimeMillis() - getSendBlueToothTime > 5000 && !isReceiveBluetoothData&&notCanReceiveBluetoothDataCount<3) {
					Log.e("timer", "接收不到蓝牙数据");
					JNIUtil.startSDK(2);
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

	private void notifyRegisterFail() {
		registerFail(Constant.REGIST_CALLBACK_TYPE,SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA);
	}
	private boolean isWholeDataPackage=false;//怕最后一个包搞混了
	long getSendBlueToothTime;
	private int countMessage = 0;
	private int notCanReceiveBluetoothDataCount = 0;
	private String finalTemp;//保存上一次发给蓝牙的数据，以免出错，需要重发
	private boolean isReceiveBluetoothData = true;//判断5s内是否接收完成，没有完成则重新发送
	public void sendToSDKAboutBluetoothInfo(String temp, byte[] txValue) {

		synchronized (this){
//			if (countMessage ==0) {
//				Log.e("timer", "开启定时器");
//				countMessage++;
//				if(timerMessage==null){
//					timerMessage= new Timer();
//				}
//				timerMessage.schedule(timerTaskMessage, 5000, 5000);
//
//			}


			if (percentEntity == null) {
				percentEntity = new PercentEntity();
			}
			int percent = Integer.parseInt(TextUtils.isEmpty(mReceiveDataframSocketService.getSorcketTag()) ? "-1" : mReceiveDataframSocketService.getSorcketTag().substring(mReceiveDataframSocketService.getSorcketTag().length() - 4, mReceiveDataframSocketService.getSorcketTag().length() - 1));
			percentEntity.setPercent(percent);
			EventBus.getDefault().post(percentEntity);
			lastTime = 0;
			count = 0;
			notCanReceiveBluetoothDataCount=0;
			if (messages == null) {
				messages = new ArrayList<>();
			}
			messages.add(temp);
			int lengthData=(txValue[1]&0x7f)+1;
			int dataStatue=txValue[1]&0x80;
			if(messages.size()<lengthData){
				if(dataStatue== 0x80){
					isWholeDataPackage=true;
				}
				return;
			}

			if (isWholeDataPackage||dataStatue==0x80) {

				isReceiveBluetoothData = true;
				isWholeDataPackage=false;
				Log.e(TAG, "messages:" + messages.size() );
				if(messages.size()<lengthData){
					sendToBluetoothAboutCardInfo(finalTemp);
					return;
				}
				sortMessage();
				mStrSimPowerOnPacket = PacketeUtil.Combination(messages);

				// 接收到一个完整的数据包,发送到SDK
//				int length = (txValue[2] & 0xff);
//				if (messages.size() >= 127 && length < 252) {
//					length += 255;
//				}
//				String sendToOnService = null;
//				Log.e(TAG, "从蓝牙发出的完整数据 mStrSimPowerOnPacket:" + mStrSimPowerOnPacket.length() + "; \n"
//						+ mStrSimPowerOnPacket + "\nlength=" + length);
//				if (mStrSimPowerOnPacket.length() >= length) {
//					try {
//						sendToOnService = mStrSimPowerOnPacket.substring(0, length * 2);
//					} catch (StringIndexOutOfBoundsException e) {
//						Log.e(TAG, "catch socketTag:" + socketTag + "; \n"
//								+ sendToOneServerTemp);
//						sendToBluetoothAboutCardInfo(finalTemp);
//						return;
//					}
//				} else {
//					Log.e(TAG, "catch else:" + socketTag + "; \n"
//							+ sendToOneServerTemp);
//					sendToBluetoothAboutCardInfo(finalTemp);
//					return;
//				}
				socketTag = mReceiveDataframSocketService.getSorcketTag();
//				sendToOneServerTemp = sendToOnService;
				Log.e(TAG, "从蓝牙发出的完整数据 socketTag:" + socketTag + "; \n"
						+ mStrSimPowerOnPacket);

				sendToSDKAboutBluetoothInfo(socketTag + mStrSimPowerOnPacket);

			}
		}
	}

	private void sortMessage() {
		if(messages.size()>1){
			ArrayList<String> messagesList=new ArrayList<>();
			int z=0;
			for(int i=0;i<messages.size();i++){
				for(int j=0;j<messages.size();j++){
					Log.e("messages","messages()"+(Integer.parseInt(messages.get(j).substring(2,4),16)&127)+"  i="+i);
					if((Integer.parseInt(messages.get(j).substring(2,4),16)&127)==i){
						z=j;
						Log.e("messages","messagesz"+z);
						break;
					}
				}
				messagesList.add(messages.get(z));
			}
			messages.clear();
			messages=messagesList;
			for(int i=0;i<messages.size();i++){
				Log.e("messages","messages="+messages.get(i));
			}
			Log.e("messages","===========================");
		}
	}

	PercentEntity percentEntity;

	private void sendToSDKAboutBluetoothInfo(final String finalMessage) {
		if (mReceiveDataframSocketService != null) {
			mReceiveDataframSocketService.sendToSdkMessage(finalMessage);
		}
		messages.clear();
	}



	private void sendToBluetoothAboutCardInfo(String msg) {
		if(TextUtils.isEmpty(msg)){
			registerFail(Constant.REGIST_CALLBACK_TYPE,SocketConstant.REGISTER_FAIL);
			return;
		}
		Log.e(TAG, "SDK进入: sendToBluetoothAboutCardInfo:" + msg);
		isReceiveBluetoothData = false;
		getSendBlueToothTime = System.currentTimeMillis();
		String temp = "";
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
				Log.e(TAG, "&&& server  message: " + messages[i].toString());
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
