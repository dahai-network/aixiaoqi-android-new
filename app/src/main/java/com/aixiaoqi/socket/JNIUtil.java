package com.aixiaoqi.socket;

import android.util.Log;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

import static com.aixiaoqi.socket.SocketConstant.EN_APPEVT_CMD_SETRST;
import static com.aixiaoqi.socket.SocketConstant.TRAN_DATA_TO_SDK;
import static com.aixiaoqi.socket.TlvAnalyticalUtils.sendToSdkLisener;
import static de.blinkt.openvpn.constant.Constant.IS_TEXT_SIM;

/**
 * Created by Administrator on 2016/12/27 0027.
 * 跟SDK打交道
 */
public class JNIUtil {
	static JNIUtil jniUtil;
	private static final String libSoName = "aixiaoqi";

	public native void getCardInfo();

	public native void main(byte simType);

	public native void simComEvtApp2Drv(byte chn, byte index, int length, byte[] pData);

	static {
		System.loadLibrary(libSoName);
	}

	public static JNIUtil getInstance() {

		if (jniUtil == null) {
			synchronized (JNIUtil.class) {
				if (jniUtil == null) {
					jniUtil = new JNIUtil();
				}
			}
		}

		return jniUtil;
	}

	/**
	 * 开启SDK，重启SDK。
	 *
	 * @param reconnectType 0,默认为0，表示正常情况。1表示蓝牙断开,2接收不到蓝牙数据。3表示Tcp断开
	 */
	public static void startSDK(int reconnectType) {
//		if (!IS_TEXT_SIM) return;

		Log.e("Blue_Chanl", "启动startSDK - REGISTER_STATUE_CODE=" + SocketConstant.REGISTER_STATUE_CODE);
		String phoneNumber = SharedUtils.getInstance().readString(Constant.USER_NAME);
		switch (SocketConstant.REGISTER_STATUE_CODE) {
			case 0:
				if (jniUtil != null)
					phoneAddressAndStartSDK(phoneNumber);
				break;
			case 1:
				reStartSDK();
				break;
			case 2:
				if (reconnectType == 1 || reconnectType == 2) {
					reSDK();
				}

				break;
			case 3:
				if (reconnectType == 2) {
					reSDK();
				}

				break;
			default:
				Log.e("ReconnectBluebooth", "RegisterSucceed");
				break;
		}

	}

	private static void reSDK() {
		if (sendToSdkLisener != null)
			sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
	}

	private static void phoneAddressAndStartSDK(String phonenumber) {
		Log.e("phoneAddress", "phoneAddress");
		try {
			if (matchesPhoneNumber(phonenumber) == 1 || matchesPhoneNumber(phonenumber) == 2)
				jniUtil.main((byte) 1);
			else if (matchesPhoneNumber(phonenumber) == 3) {
				jniUtil.main((byte) 2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reStartSDK() {
		if (jniUtil != null)
			jniUtil.simComEvtApp2Drv((byte) 0, Byte.parseByte(EN_APPEVT_CMD_SETRST), 0, HexStringExchangeBytesUtil.hexStringToBytes(TRAN_DATA_TO_SDK));
		//重置记录标签
		UdpClient.tag = null;
	}

	public static int matchesPhoneNumber(String phone_number) {

		String cm = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}$";//中国移动
		String cu = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5-6]))\\d{8}$";//中国联通
		String ct = "^((133)|(153)|(1700)|(18[0-1,9]))\\d{8}$";//中国电信

		int flag = 0;
		if (phone_number.matches(cm)) {
			flag = 1;
		} else if (phone_number.matches(cu)) {
			flag = 2;
		} else if (phone_number.matches(ct)) {
			flag = 3;
		} else {
			flag = 4;
		}
		return flag;

	}
}
