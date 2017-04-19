package com.aixiaoqi.socket;

import android.util.Log;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

import static com.aixiaoqi.socket.SocketConstant.EN_APPEVT_CMD_SETRST;
import static com.aixiaoqi.socket.SocketConstant.TRAN_DATA_TO_SDK;
import static com.aixiaoqi.socket.TlvAnalyticalUtils.sendToSdkLisener;

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
		switch (SocketConstant.REGISTER_STATUE_CODE) {
			case 0:
				if (jniUtil != null)
					phoneAddressAndStartSDK();
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

	private static void phoneAddressAndStartSDK() {

		try {
			Log.e("SocketConstant.SIM_TYPE","SocketConstant.SIM_TYPE="+SocketConstant.SIM_TYPE);
			if(SocketConstant.SIM_TYPE==1||SocketConstant.SIM_TYPE==2){
				jniUtil.main((byte) 1);
			}else if(SocketConstant.SIM_TYPE==3){
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


}
