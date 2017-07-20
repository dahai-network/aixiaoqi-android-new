package com.aixiaoqi.socket;

import android.content.Intent;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.UserInfo.ui.LoginMainActivity;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.PacketeUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.SharedUtils;

import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;
import static com.aixiaoqi.socket.SocketConstant.TRAN_DATA_TO_SDK;
import static de.blinkt.openvpn.activities.Device.ModelImpl.HasPreDataRegisterImpl.sendYiZhengService;

/**
 * TLV的解析
 */
public class TlvAnalyticalUtils {

	private static long lastClickTime;
	private static int count = 0;
	private static MessagePackageEntity builderMessagePackage(String hexString) {
		Log.e("TlvAnalyticalUtils", hexString);
		int position = 0;
		String responeHeader = hexString.substring(position, position + 8);
		String tagString = hexString.substring(4, 6);
		int tag = Integer.parseInt(tagString, 16);
		String responeString = hexString.substring(6, 8);
		int responeCode = getResponeCode(responeString,1);
		if (dealRespone(responeCode)) return null;

		tag = tag & 127;
		position = position + 8;
		String sessionId = hexString.substring(position, position + 8);
		if(tag==4){
			SocketConstant.SESSION_ID = sessionId;
			//重新创建连接
			EventBusUtil.simRegisterStatue(SocketConstant.REGISTERING,SocketConstant.REGISTER_CHANGING);
		}
		else if (!SocketConstant.SESSION_ID.equals(sessionId) && !SocketConstant.SESSION_ID.equals(SocketConstant.SESSION_ID_TEMP)) {
//			SocketConstant.SESSION_ID = sessionId;
			return null;
		}

		position = position + 8;
		String hexStringMessageNumber = hexString.substring(position, position + 4);
		position = position + 4;
		String hexStringDatalength = hexString.substring(position, position + 4);
		position = position + 4;
		List<TlvEntity> list = builderTlvList(hexString, hexString.substring(position, hexString.length()), tag);
		MessagePackageEntity messagePackageEntity = new MessagePackageEntity(list, sessionId, hexStringMessageNumber, hexStringDatalength, responeHeader);
		return messagePackageEntity;
	}

	private static boolean dealRespone(int responeCode) {
		if (responeCode == 53||responeCode == 39) {
			//服务器错误注册失败
			EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.SERVER_IS_ERROR);
			return true;
			//会话ID不一样
		}else if(responeCode==41||responeCode==22){
			if (sendYiZhengService != null){
				ReceiveSocketService.recordStringLog(DateUtils.getCurrentDateForFileDetail() + "push service :\n" );
				sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
			}
			//过期
		}else if(responeCode==21){
			if (!CommonTools.isFastDoubleClick(1000)) {
				EventBusUtil.cancelCallService();
				if (ICSOpenVPNApplication.uartService != null)
					ICSOpenVPNApplication.uartService.disconnect();
				SharedUtils.getInstance().delete(Constant.IMEI);
				SharedUtils.getInstance().delete(Constant.BRACELETNAME);
				Intent intent = new Intent(ICSOpenVPNApplication.getContext(), LoginMainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(IntentPutKeyConstant.OTHER_DEVICE_LOGIN, ICSOpenVPNApplication.getContext().getResources().getString(R.string.token_interrupt));
				ICSOpenVPNApplication.getContext().startActivity(intent);
			}
		}
		return false;
	}

	public static void builderMessagePackageList(String hexString) {
		String dataLength = hexString.substring(20, 24);
		int index = Integer.parseInt(dataLength, 16) * 2;
		if (index + 24 < hexString.length()) {
			builderMessagePackage(hexString.substring(0, index + 24));
			builderMessagePackageList(hexString.substring(index + 24));
		} else {
			builderMessagePackage(hexString);
		}
	}
	public static	String[] preData;
	private static List<TlvEntity> builderTlvList(String orData, String hexString, int tag) {
		int position = 0;
		String tempTag = "";
		List<TlvEntity> tlvs = new ArrayList<>();
		while (position < hexString.length()) {
			String _hexTag = getTag(hexString, position);
			position += _hexTag.length();
			LPositon lPositon = getLengthAndPosition(hexString, position);

			int vl = lPositon.getvL();
			position = lPositon.getposition();
			String value;
			if (position + vl * 2 <= hexString.length()) {
				value = hexString.substring(position, position + vl * 2);
			} else {
				position = position + vl * 2;
				continue;
			}
			position = position + value.length();
			int typeParams = Integer.parseInt(_hexTag, 16);
			if (tag == 4) {
				value = getConnectResultString(tag, value, typeParams);
			} else if (tag == 16) {
				if (typeParams == 1) {
					tempTag = value;
					if ("01".equals(value)) {
						orData = orData.replace("8a1000", "8a9000");
					}
				}
				if ("00".equals(tempTag)) {
					if (typeParams == 199) {
						SendCommandToBluetooth.sendMessageToBlueTooth(Constant.UP_TO_POWER_NO_RESPONSE);
						if(SdkAndBluetoothDataInchange.isHasPreData) {
							Logger.d("有鉴权数据进行处理");
							hasPreData(orData, value);
						}else{
							Logger.d("没有鉴权数据进行处理");
							byte[] bytes = HexStringExchangeBytesUtil.hexStringToBytes(value);
							sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_SIMDATA), vl, bytes);
						}
					}
				} else if (typeParams == 199) {
					if(!SdkAndBluetoothDataInchange.isHasPreData)
						sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
					orData = dealDataAddZeroSendServer(orData, vl, value);
				}
				if (value.length() >= 2) {
					value = RadixAsciiChange.convertHexToString(value.substring(0, value.length() - 2));
				}
			} else if (tag == 15) {
				disConnect(orData, tag);
			} else if (tag == 5) {
				simStatue(value, typeParams);
			}

			tlvs.add(new TlvEntity(_hexTag, vl + "", value));
		}
		return tlvs;
	}

	//处理数据，发送给服务器
	private static String dealDataAddZeroSendServer(String orData, int vl, String value) {
		String rpValue = "000100163b9f94801fc78031e073fe211b573786609b30800119";
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(rpValue);
		if (rpValue.length() < vl * 2) {
			for (int i = rpValue.length(); i < vl * 2; i++) {
				stringBuilder.append("0");
			}
		}
		orData = orData.replace(value, stringBuilder.toString());
		sendToSdkLisener.sendServer(orData);
		return orData;
	}

	//有预读取数据，获取卡指令，进行解析
	private static void hasPreData(String orData, String value) {
		if(preData==null){
			preData= new String[9];
		}
		String messageSeq = value.substring(0, 4);
		preData[0]=messageSeq;
		String preNumber = value.substring(14, 16);
		preData[1]=preNumber;
		int responeCode = getResponeCode(preNumber,1);
		preData(value, preData);
		String valideData = value.substring(32, value.length());
		preData[6]=valideData;
		int preCode=getResponeCode(preData[2].substring(preData[2].length()-4,preData[2].length()),2);
		if(responeCode==0&&preCode==0){
			getOrderNumber(0);
			SendCommandToBluetooth.sendToBlue(preData[6],Constant.READED_SIM_DATA);
		}else if(responeCode==0&&preCode!=0){
			for(int i=0;i<4;i++){
				int	responeC=getResponeCode(preData[i+2].substring(preData[i+2].length()-4,preData[i+2].length()),2);
				if(responeC==0){
					break;
				}
				getOrderNumber(i+1);
			}
			SendCommandToBluetooth.sendToBlue(preData[2],Constant.READED_SIM_DATA);
		}else{
			getOrderNumber(responeCode);
			SendCommandToBluetooth.sendToBlue(preData[2],Constant.READED_SIM_DATA);
		}
		preData[8]=orData.replace("8a1000", "8a9000").substring(0,20);
		for(int i=0;i<9;i++){
			Logger.d("分离服务器发过来的数据"+preData[i]);
		}
	}

	//sim卡注册状态
	private static void simStatue(String value, int typeParams) {
		if (typeParams == 162) {
			if (Integer.parseInt(value, 16) == 3) {
				if(SdkAndBluetoothDataInchange.isHasPreData){
					SdkAndBluetoothDataInchange.PERCENT=0;
				}
				REGISTER_STATUE_CODE = 3;
				EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_SUCCESS);
			} else if (Integer.parseInt(value, 16) > 4) {
				REGISTER_STATUE_CODE = 2;
				EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.SERVER_IS_ERROR);
			}
		}
	}

	//服务器断开连接，发送0f过来，5分钟之内只重连三次，不无限重连。
	private static void disConnect(String orData, int tag) {
		if (System.currentTimeMillis() - lastClickTime > 365 * 24 * 60 * 60 * 1000L || isFast(5 * 60 * 1000)) {
			if (!isFast(5 * 60 * 1000)) {
				isFast(5 * 60 * 1000);
			}
			count++;
		} else {
			count = 0;
		}
		if (count <= 3) {
			REGISTER_STATUE_CODE = 2;
			reRegistering(orData, tag);
		}
	}

	private static String getConnectResultString(int tag, String value, int typeParams) {
		if (typeParams == 160) {
			value = RadixAsciiChange.convertHexToString(value.substring(0, value.length() - 2));
			SocketConstant.REGISTER_REMOTE_ADDRESS = value.substring(value.indexOf("_") + 1, value.lastIndexOf("."));
			SocketConstant.REGISTER_ROMOTE_PORT = value.substring(value.lastIndexOf(".") + 1, value.length());

		}else if(typeParams==101){
			TCP_HEART_TIME=Integer.parseInt(value,16);
			TCP_HEART_TIME=TCP_HEART_TIME/2-30;
			Log.e("TlvAnalytical","TCP_HEART_TIME"+TCP_HEART_TIME+"\nvalue"+value+"\ntag"+tag);
		}
		return value;
	}

	public static int TCP_HEART_TIME;
	private static void getOrderNumber(int responeCode) {
		if(preData[6].startsWith("a088")){
			preData[7]=(responeCode+2)+"";
		}else{
			preData[7]=(responeCode+1)+"";
		}

	}

	private static int getResponeCode(String preNumber,int type) {
		int responeCode = Integer.parseInt(preNumber, 16);
		if(type==1)
			responeCode = responeCode & 127;
		else if(type==2){
			responeCode = responeCode & 65535;
		}
		return responeCode;
	}

	private static void preData(String value, String[] preData) {
		for(int i=16;i<32;i=i+4){
			int index=(i-16)/4;
			String cmd=	value.substring(i,i+4);
			preData[index+2]="a0a4000002"+cmd;
		}
	}

	/**
	 * 注册中不成功再次注册
	 */
	public static void reRegistering(String orData, int tag) {
		if(!SdkAndBluetoothDataInchange.isHasPreData)
			sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));//重置SDK
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(orData);
		stringBuilder.replace(4, 6, Integer.toHexString(tag | 0x80));
		stringBuilder.replace(6, 8, "00");
		sendToSdkLisener.sendServer(stringBuilder.toString());
		CommonTools.delayTime(2000);
		if (sendYiZhengService != null){
			SocketConstant.SESSION_ID=SocketConstant.SESSION_ID_TEMP;
			sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
		}
	}

	private static boolean isFast(int maxTime) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < maxTime) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static SendToSdkLisener sendToSdkLisener;

	public static void setListener(SendToSdkLisener listener) {
		sendToSdkLisener = listener;
	}

	public interface SendToSdkLisener {
		void send(byte evnindex, int length, byte[] bytes);//发送给so库
		void sendServer(String hexString);//发送给服务器

	}

	/**
	 * 返回最后的Value的长度
	 *
	 * @param hexString
	 * @param position
	 * @return
	 */
	private static LPositon getLengthAndPosition(String hexString, int position) {
		String firstByteString = hexString.substring(position, position + 2);
		int i = Integer.parseInt(firstByteString, 16);
		String hexLength;
		if (((i >>> 7) & 1) == 0) {
			hexLength = hexString.substring(position, position + 2);
			position = position + 2;
		} else {
			hexLength = hexString.substring(position + 1, position + 4);
			position = position + 4;
		}
		return new LPositon(Integer.parseInt(hexLength, 16), position);

	}

	private static String getTag(String hexString, int position) {
		return hexString.substring(position, position + 2);
	}

	public static void clearData() {
		lastClickTime = 0;
		count = 0;
	}

}
