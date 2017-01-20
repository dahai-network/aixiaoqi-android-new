package com.aixiaoqi.socket;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.IsSuccessEntity;

import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;
import static com.aixiaoqi.socket.SocketConstant.TRAN_DATA_TO_SDK;

/**
 * TLV的解析
 */
public class TlvAnalyticalUtils {



	public static MessagePackageEntity builderMessagePackage(String hexString) {
		Log.e("TlvAnalyticalUtils", hexString);
		int position = 0;
		String responeHeader = hexString.substring(position, position + 8);
		String tagString = hexString.substring(4, 6);
		int tag = Integer.parseInt(tagString, 16);
		String responeString = hexString.substring(6, 8);
		int responeCode = Integer.parseInt(responeString, 16);
		responeCode = responeCode & 127;
		if (responeCode == 41) {
			registerFail();
			return null;
		} else if (responeCode == 39) {
			registerFail();
			return null;
		}
		tag = tag & 127;
		if (tag != 5) {
			if (System.currentTimeMillis() - registerSimTime > 5 * 60 * 1000 && isRegisterSucceed) {
				sendMessageToBlueTooth(Constant.OFF_TO_POWER);

			}
			registerSimTime = System.currentTimeMillis();
		}
		position = position + 8;
		String sessionId = hexString.substring(position, position + 8);
		if(!SocketConstant.SESSION_ID.equals(sessionId)&&!SocketConstant.SESSION_ID.equals(SocketConstant.SESSION_ID_TEMP)){
			SocketConstant.SESSION_ID = sessionId;
			return null;
		}
		SocketConstant.SESSION_ID = sessionId;
		position = position + 8;
		String hexStringMessageNumber = hexString.substring(position, position + 4);
		position = position + 4;
		String hexStringDatalength = hexString.substring(position, position + 4);

		position = position + 4;
		List<TlvEntity> list = builderTlvList(hexString, hexString.substring(position, hexString.length()), tag);
		MessagePackageEntity messagePackageEntity = new MessagePackageEntity(list, sessionId, hexStringMessageNumber, hexStringDatalength, responeHeader);

		return messagePackageEntity;
	}

	private static void sendMessageToBlueTooth(final String message) {
		byte[] value;
		value = HexStringExchangeBytesUtil.hexStringToBytes(message);
		UartService mService = ICSOpenVPNApplication.uartService;
		if (mService != null) {
			if (mService.mConnectionState == UartService.STATE_CONNECTED) {
				mService.writeRXCharacteristic(value);
			}
		}
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
			String value = "";
			if (position + vl * 2 <= hexString.length()) {
				value = hexString.substring(position, position + vl * 2);
			} else {
				position = position + vl * 2;
				continue;
			}
			position = position + value.length();
			int typeParams = Integer.parseInt(_hexTag, 16);
			if (tag == 4) {

				if (typeParams == 160) {
					value = RadixAsciiChange.convertHexToString(value.substring(0, value.length() - 2));
					SocketConstant.REGISTER_REMOTE_ADDRESS=value.substring(value.indexOf("_")+1,value.lastIndexOf("."));
					SocketConstant.REGISTER_ROMOTE_PORT=value.substring(value.lastIndexOf(".")+1,value.length());
					Log.e("RemoteAddress","REGISTER_REMOTE_ADDRESS="+SocketConstant.REGISTER_REMOTE_ADDRESS+"\nREGISTER_ROMOTE_PORT"+SocketConstant.REGISTER_ROMOTE_PORT);
				}
			} else if (tag == 16) {
				if (typeParams == 1) {
					tempTag = value;
					if ("01".equals(value)) {
						orData = orData.replace("8a1000", "8a9000");
					}

				}
				if ("00".equals(tempTag)) {
					if (typeParams == 199) {
						upToPower();

						byte[] bytes = HexStringExchangeBytesUtil.hexStringToBytes(value);
						sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_SIMDATA), vl, bytes);
					}
				} else if (typeParams == 199) {
					if(REGISTER_STATUE_CODE==2){//第一次是010101的时候不去复位SDK,第二次的时候才对SDK进行复位
						sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
					}
					SocketConstant.REGISTER_STATUE_CODE = 2;
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
				}
				if (value.length() >= 2) {
					value = RadixAsciiChange.convertHexToString(value.substring(0, value.length() - 2));
				}
			} else if (tag == 15) {
				if (System.currentTimeMillis() - lastClickTime > 365 * 24 * 60 * 60 * 1000l || isFast(90 * 60 * 1000)) {
					if (!isFast(90 * 60 * 1000)) {
						isFast(90 * 60 * 1000);
					}
					count++;
				} else {
					count = 0;
				}
				if (count <= 3) {
					reRegistering(orData, tag);
				}
			} else if (tag == 5) {
				if (typeParams == 162) {
					if (Integer.parseInt(value, 16) == 3) {
						REGISTER_STATUE_CODE = 3;
						IsSuccessEntity entity = new IsSuccessEntity();
						entity.setType(Constant.REGIST_CALLBACK_TYPE);
						entity.setSuccess(true);
						EventBus.getDefault().post(entity);
						registerSimTime = System.currentTimeMillis();
						registerOrTime = System.currentTimeMillis();
						isRegisterSucceed = true;
					} else if (Integer.parseInt(value, 16) > 4) {
						REGISTER_STATUE_CODE = 0;
						registerFail();
					}
				}
			}

			tlvs.add(new TlvEntity(_hexTag, vl + "", value));
		}
		return tlvs;
	}

	private static void registerFail() {
		IsSuccessEntity entity = new IsSuccessEntity();
		entity.setType(Constant.REGIST_CALLBACK_TYPE);
		entity.setFailType(SocketConstant.REGISTER_FAIL);
		entity.setSuccess(false);
		EventBus.getDefault().post(entity);
	}

	public static void upToPower() {
		byte[] value;
		value = HexStringExchangeBytesUtil.hexStringToBytes(Constant.UP_TO_POWER);
		ICSOpenVPNApplication.uartService.writeRXCharacteristic(value);
	}
	/**
	 * 注册中不成功再次注册
	 */
	public static void reRegistering(String orData, int tag) {
		sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));//重置SDK
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(orData);
		stringBuilder.replace(4, 6, Integer.toHexString(tag | 0x80));
		stringBuilder.replace(6, 8, "00");
		sendToSdkLisener.sendServer(stringBuilder.toString());
		if(TestProvider.sendYiZhengService!=null)
			TestProvider.sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
	}

	public static boolean isRegisterSucceed = false;
	public static long registerSimTime;
	public static long registerOrTime;
	private static long lastClickTime;
	private static int count = 0;

	public static boolean isFast(int maxTime) {
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
		void send(byte evnindex, int length, byte[] bytes);

		void sendServer(String hexString);

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
		String hexLength = "";
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
		isRegisterSucceed = false;
		registerSimTime = 0;
		registerOrTime = 0;
		lastClickTime = 0;
		count = 0;
	}

}
