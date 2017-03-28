package com.aixiaoqi.socket;

import android.text.TextUtils;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.database.DBHelp;
import de.blinkt.openvpn.model.PreReadEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/1/5 0005.
 */
public class SdkAndBluetoothDataInchange {
	public static final String TAG = "Blue_Chanl";
	UartService mService;
	ReceiveDataframSocketService mReceiveDataframSocketService;
	private String saveBluetoothData;
	long getSendBlueToothTime;
	private int countMessage = 0;
	private int notCanReceiveBluetoothDataCount = 0;
	private String finalTemp;//保存上一次发给蓝牙的数据，以免出错，需要重发
	private boolean isReceiveBluetoothData = true;//判断5s内是否接收完成，没有完成则重新发送
	public int count =0;
	private String[] IccidCommand={"a0a40000022fe2","a0c000000f","a0b000000a"};
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
		EventBusUtil.simRegisterStatue(SocketConstant.NOT_CAN_RECEVIE_BLUETOOTH_DATA);
	}

	public void sendToSDKAboutBluetoothInfo(ArrayList<String> messages) {

		synchronized (this){

			if(ProMainActivity.isGetIccid){
				simRegisterStatue=null;
				getIccid(messages);
			}else if(isHasPreData){
				if (simRegisterStatue == null) {
					simRegisterStatue = new SimRegisterStatue();
				}
				int percent=simRegisterStatue.getProgressCount()+1;
				eventPercent(percent);
				registerGoip(messages);
			}else if(ProMainActivity.isStartSdk) {
//				startTimer();
				if (simRegisterStatue == null) {
					simRegisterStatue = new SimRegisterStatue();
				}
				int percent = Integer.parseInt(TextUtils.isEmpty(mReceiveDataframSocketService.getSorcketTag()) ? "-1" : mReceiveDataframSocketService.getSorcketTag().substring(mReceiveDataframSocketService.getSorcketTag().length() - 4, mReceiveDataframSocketService.getSorcketTag().length() - 1));
				eventPercent(percent);
				isReceiveBluetoothData = true;
				notCanReceiveBluetoothDataCount = 0;
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
	}

	private void eventPercent(int percent) {
		simRegisterStatue.setRigsterSimStatue(SocketConstant.REGISTER_CHANGING);
		simRegisterStatue.setProgressCount(percent);
		EventBus.getDefault().post(simRegisterStatue);
	}

	private void registerGoip(ArrayList<String> messages) {
		count=count+1;
		if(count+1==Integer.parseInt(TlvAnalyticalUtils.preData[7])&&TlvAnalyticalUtils.preData[6].startsWith("a088")){

			//判断是否是电信还是联通的卡
			saveBluetoothData= PacketeUtil.Combination(messages);
			String imsi=	RadixAsciiChange.convertHexToString(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5]);
			if (telType(imsi)==CUCC_OR_CMCC){//移动和联通
				TlvAnalyticalUtils.sendToBlue("a0c000000c");
			}else if( telType(imsi)==TELECOM){//电信
				TlvAnalyticalUtils.sendToBlue("a0c0000003");
			}

		}else if(count+1==Integer.parseInt(TlvAnalyticalUtils.preData[7])&&!TlvAnalyticalUtils.preData[6].startsWith("a088")){
			TlvAnalyticalUtils.sendToBlue(TlvAnalyticalUtils.preData[6]);
		}else if(count+1<Integer.parseInt(TlvAnalyticalUtils.preData[7])){
			if(count+2==Integer.parseInt(TlvAnalyticalUtils.preData[7])&&TlvAnalyticalUtils.preData[6].startsWith("a088")){
				TlvAnalyticalUtils.sendToBlue(TlvAnalyticalUtils.preData[6]);
				return;
			}
			TlvAnalyticalUtils.sendToBlue(TlvAnalyticalUtils.preData[count+2]);

		}else{
			// 组数据
			SendCommandToBluetooth.sendMessageToBlueTooth(Constant.OFF_TO_POWER);
			count=0;
			String  toServerMessage="";
			String value=PacketeUtil.Combination(messages);
			if(TlvAnalyticalUtils.preData[6].startsWith("a088")){
				String number=formatByte(Integer.toHexString(value.length()/2+1+saveBluetoothData.length()/2),1);
				String subNumber=formatByte(Integer.toHexString(value.length()/2+1),2);
				String imsi=	RadixAsciiChange.convertHexToString(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5]);
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




	private void getIccid(ArrayList<String> messages) {

		count=count+1;
		Log.e(TAG,"count="+count);
		if(count<4){
			TlvAnalyticalUtils.sendToBlue(IccidCommand[count-1]);
		}else if(count==4){
			ProMainActivity.isGetIccid=false;
			String value= PacketeUtil.Combination(messages);
			StringBuilder stringBuilder=new StringBuilder();
			for(int i=0;i<value.length()-4;i=i+2){
				stringBuilder.append(value.charAt(i+1));
				stringBuilder.append(value.charAt(i+0));
			}

			PreReadEntity preReadEntity = getPreReadEntity(stringBuilder);
			if(preReadEntity!=null){
				//发送直接从预读取数据开始注册
				count=0;
				isHasPreData=true;
				String token= SharedUtils.getInstance().readString(Constant.TOKEN);
				if(TextUtils.isEmpty(token)){
					EventBusUtil.simRegisterStatue(SocketConstant.TOKEN_IS_NULL);
				}else{
					EventBusUtil.simRegisterType(Constant.REGISTER_SIM_PRE_DATA);
					initPre(preReadEntity, token);
				}
			}else{
				//发送启动SDK
				isHasPreData=false;
				EventBusUtil.simRegisterType(Constant.REGISTER_SIM_NOT_PRE_DATA);
			}
		}
	}

	private PreReadEntity getPreReadEntity(StringBuilder stringBuilder) {
		DBHelp dbHelp=new DBHelp(ProMainActivity.instance);
		PreReadEntity preReadEntity= dbHelp.getPreReadEntity(RadixAsciiChange.convertStringToHex(stringBuilder.toString()));
		dbHelp.close();
		return preReadEntity;
	}

	private void initPre(PreReadEntity preReadEntity, String token) {
		SocketConstant.REGISTER_STATUE_CODE = 2;
		SocketConstant.CONNENCT_VALUE[3] = RadixAsciiChange.convertStringToHex(token);
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 1] = preReadEntity.getPreReadData();
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 2] = preReadEntity.getDataLength();
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5] = preReadEntity.getImsi();
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6] = preReadEntity.getIccid();
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
	SimRegisterStatue simRegisterStatue;

	private void sendToSDKAboutBluetoothInfo(final String finalMessage) {
		if (mReceiveDataframSocketService != null) {
			mReceiveDataframSocketService.sendToSdkMessage(finalMessage);
		}
	}



	private void sendToBluetoothAboutCardInfo(String msg) {
		if(TextUtils.isEmpty(msg)){
			EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL);
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
