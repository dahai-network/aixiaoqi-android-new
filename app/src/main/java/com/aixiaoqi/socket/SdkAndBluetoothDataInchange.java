package com.aixiaoqi.socket;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.CommonTools;

import static de.blinkt.openvpn.activities.Device.ModelImpl.NoPreDataRegisterModelImpl.isStartSdk;

/**
 * Created by Administrator on 2017/1/5 0005.
 */
public class SdkAndBluetoothDataInchange {
	public static final String TAG = "Blue_Chanl";
	UartService mService;
	ReceiveDataframSocketService mReceiveDataframSocketService;
	private String saveBluetoothData;
	long getSendBlueToothTime;
	private int countMessage = 0;//阻止多次进入定时器
	private int notCanReceiveBluetoothDataCount = 0;
	private String finalTemp;//保存上一次发给蓝牙的数据，以免出错，需要重发
	private boolean isReceiveBluetoothData = true;//判断5s内是否接收完成，没有完成则重新发送
	public int count = 0;
	public static int PERCENT=0;//定义为静态变量的目的是为了下次注册的时候清零
	private String socketTag = "0";
	private String mStrSimPowerOnPacket = "";
	Timer timerMessage ;
	TimerTask timerTaskMessage ;
	private String hasPreResendToBlue="";//保存有预读取数据的信息
	public void initReceiveDataframSocketService(ReceiveDataframSocketService receiveDataframSocketService, UartService mService) {
		receiveDataframSocketService.setListener(
				new ReceiveDataframSocketService.MessageOutLisener() {
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

	private void notifyRegisterFail() {
		//重试三次发出给蓝牙的指令没有收到蓝牙那边的回应。
		EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA);
	}

	/**
	 * sendToSDKAboutBluetoothInfo
	 * @param messages
	 */
	public void sendToSDKAboutBluetoothInfo(ArrayList<String> messages) {

		synchronized (this){
			isReceiveBluetoothData = true;
			notCanReceiveBluetoothDataCount = 0;
			startTimer();
			if(isHasPreData){
				PERCENT=PERCENT+1;
				eventPercent(PERCENT);
				registerGoip(messages);
			}else if(isStartSdk) {
				int percent = Integer.parseInt(TextUtils.isEmpty(mReceiveDataframSocketService.getSorcketTag()) ? "-1" : mReceiveDataframSocketService.getSorcketTag().substring(mReceiveDataframSocketService.getSorcketTag().length() - 4, mReceiveDataframSocketService.getSorcketTag().length() - 1));
				eventPercent(percent);
				mStrSimPowerOnPacket = PacketeUtil.Combination(messages);
				socketTag = mReceiveDataframSocketService.getSorcketTag();
				Log.e(TAG, "从蓝牙发出的完整数据 socketTag:" + socketTag + "; \n"
						+ mStrSimPowerOnPacket);
				sendToSDKAboutBluetoothInfo(socketTag + mStrSimPowerOnPacket);

			}
			messages.clear();
		}
	}

	private void startTimer() {
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
							if (System.currentTimeMillis() - getSendBlueToothTime >=5000 && !isReceiveBluetoothData&&notCanReceiveBluetoothDataCount<3) {
								Log.e("timer", "接收不到蓝牙数据");
								if(isHasPreData){
									SendCommandToBluetooth.sendToBlue(hasPreResendToBlue,Constant.READED_SIM_DATA);
								}else{
									sendToBluetoothAboutCardInfo(finalTemp);
								}
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
	}
	//注册进度变化通知
	private void eventPercent(int percent) {
		EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING,SocketConstant.UPDATE_PERCENT,percent);
	}

	private void registerGoip(ArrayList<String> messages) {

		count=count+1;
		if(count+1==Integer.parseInt(TlvAnalyticalUtils.preData[7])&&TlvAnalyticalUtils.preData[6].startsWith("a088")){
			//判断是否是电信还是联通的卡
			saveBluetoothData= PacketeUtil.Combination(messages);
			sendBluetoothFlag();
			String imsi=	RadixAsciiChange.convertHexToString(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[1]]);
			if (telType(imsi)==CUCC_OR_CMCC){//移动和联通
				SendCommandToBluetooth.sendToBlue("a0c000000c",Constant.READED_SIM_DATA);
				hasPreResendToBlue="a0c000000c";
			}else if( telType(imsi)==TELECOM){//电信
				SendCommandToBluetooth.sendToBlue("a0c0000003",Constant.READED_SIM_DATA);
				hasPreResendToBlue="a0c0000003";
			}
		}else if(count+1==Integer.parseInt(TlvAnalyticalUtils.preData[7])&&!TlvAnalyticalUtils.preData[6].startsWith("a088")){
			SendCommandToBluetooth.sendToBlue(TlvAnalyticalUtils.preData[6],Constant.READED_SIM_DATA);
			hasPreResendToBlue=TlvAnalyticalUtils.preData[6];
			sendBluetoothFlag();
		}else if(count+1<Integer.parseInt(TlvAnalyticalUtils.preData[7])){
			sendBluetoothFlag();
			if(count+2==Integer.parseInt(TlvAnalyticalUtils.preData[7])&&TlvAnalyticalUtils.preData[6].startsWith("a088")){
				SendCommandToBluetooth.sendToBlue(TlvAnalyticalUtils.preData[6],Constant.READED_SIM_DATA);
				hasPreResendToBlue=TlvAnalyticalUtils.preData[6];
				return;
			}
			SendCommandToBluetooth.sendToBlue(TlvAnalyticalUtils.preData[count+2],Constant.READED_SIM_DATA);
			hasPreResendToBlue=TlvAnalyticalUtils.preData[count+2];
		}else{
			// 组数据
			SendCommandToBluetooth.sendMessageToBlueTooth(Constant.OFF_TO_POWER);
			count=0;
			String  toServerMessage="";
			String value=PacketeUtil.Combination(messages);
			if(TlvAnalyticalUtils.preData[6].startsWith("a088")){
				String number=formatByte(Integer.toHexString(value.length()/2+1+saveBluetoothData.length()/2),1);
				String subNumber=formatByte(Integer.toHexString(value.length()/2+1),2);
				String imsi=	RadixAsciiChange.convertHexToString(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[1]]);
				Log.e("TlvAnalyticalUtils","imsi="+imsi);
				if (telType(imsi)==CUCC_OR_CMCC){
					toServerMessage=TlvAnalyticalUtils.preData[0]+number+subNumber+"a0c000000c"+saveBluetoothData+"c0"+value;
				}else if(telType(imsi)==TELECOM){
					toServerMessage=TlvAnalyticalUtils.preData[0]+number+subNumber+"a0c0000003"+saveBluetoothData+"c0"+value;
				}
			}else if(speData(TlvAnalyticalUtils.preData[6])){
				String number=formatByte(Integer.toHexString(value.length()/2+1),1);
				toServerMessage=TlvAnalyticalUtils.preData[0]+number+"000000000000"+TlvAnalyticalUtils.preData[6].substring(2,4)+value;
			}else{
				String number=formatByte(Integer.toHexString(value.length()/2),1);
				toServerMessage=TlvAnalyticalUtils.preData[0]+number+"000000000000"+value;
			}

			TlvEntity tlvEntity=new TlvEntity();
			String vString=tlvEntity.getValueLength(toServerMessage.length()/2)+toServerMessage;
			int vLength=vString.length();
			toServerMessage=TlvAnalyticalUtils.preData[8]+
					formatByte(Integer.toHexString(vLength/2+4),1)+
					"010100"+"c7"
					+vString;
			TlvAnalyticalUtils.sendToSdkLisener.sendServer(toServerMessage);

		}

	}

	private void sendBluetoothFlag() {
		getSendBlueToothTime=System.currentTimeMillis();
		isReceiveBluetoothData = false;
	}


	private  boolean speData(String message){
		if(message.startsWith("a0c0")
				||message.startsWith("a0b0")
				||message.startsWith("a0b2")
				||message.startsWith("a0f2")
				||message.startsWith("a012")){
			return true;
		}
		return false;
	}


	private static final int TELECOM=0;
	private static final int CUCC_OR_CMCC=1;
	private static final int NOT_TELECOM=-1;
	private int telType(String imsi){
		if(imsi.startsWith("46000")
				|| imsi.startsWith("46001")
				|| imsi.startsWith("46002")
				|| imsi.startsWith("46006")
				|| imsi.startsWith("46007")
				|| imsi.startsWith("46009")
				|| imsi.startsWith("46020")){
			return CUCC_OR_CMCC;
		}else if(imsi.startsWith("46003") || imsi.startsWith("46005")|| imsi.startsWith("460011")){
			return TELECOM;
		}else{
			return NOT_TELECOM;
		}
	}

	public String formatByte(String number ,int type){
		if(type==1){
			if(number.length()%4==1){
				number="000"+number;
			}else if(number.length()%4==2){
				number="00"+number;
			}else if(number.length()%4==3){
				number="0"+number;
			}
		}else if(type==2){
			if(number.length()%2==1){
				number="0"+number;
			}
		}
		return number;
	}



	public static boolean isHasPreData=false;

	private void sendToSDKAboutBluetoothInfo(final String finalMessage) {
		if (mReceiveDataframSocketService != null) {
			mReceiveDataframSocketService.sendToSdkMessage(finalMessage);
		}
	}



	private void sendToBluetoothAboutCardInfo(String msg) {
		if(TextUtils.isEmpty(msg)){
			//如果卡没有回数据就注册失败
			EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.SDK_SEND_IS_NULL);
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
			sendMessage(temp);
		} else {
			String[] messages = PacketeUtil.Separate(temp,Constant.READED_SIM_DATA);
			for (int i = 0; i < messages.length; i++) {
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
				SendCommandToBluetooth.sendMessageToBlueTooth(temp);
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
