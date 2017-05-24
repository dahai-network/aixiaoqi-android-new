package com.aixiaoqi.socket;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;

/**
 * Created by Administrator on 2017/1/4 0004.
 */
public class SendYiZhengService implements TlvAnalyticalUtils.SendToSdkLisener {
	ReceiveSocketService mReceiveSocketService;

	public void sendGoip(String header) {
		if (mReceiveSocketService != null)
			sendService(header);
	}

	public void initSocket(ReceiveSocketService receiveSocketService) {
		mReceiveSocketService = receiveSocketService;
		receiveSocketService.initSocket();
		if (TlvAnalyticalUtils.sendToSdkLisener == null) {
			TlvAnalyticalUtils.setListener(this);
		}

	}

	public int count = 0;

	public void sendService(String header) {
		String number = Integer.toHexString(count + 1);
		count = count + 1;
		if (number.length() % 4 == 1) {
			number = "000" + number;
		} else if (number.length() % 4 == 2) {
			number = "00" + number;
		} else if (number.length() % 4 == 3) {
			number = "0" + number;
		}
		List<TlvEntity> yiZhengTlvList = new ArrayList<>();
		if (SocketConstant.CONNECTION.equals(header)) {
			SocketConstant.SESSION_ID = SocketConstant.SESSION_ID_TEMP;
			connection(yiZhengTlvList);
		} else if (SocketConstant.PRE_DATA.equals(header)) {
			//
			sdkReturn(yiZhengTlvList);
		} else if (SocketConstant.UPDATE_CONNECTION.equals(header)) {
			updateConnection(yiZhengTlvList);
		}
		MessagePackageEntity messagePackageEntity = new MessagePackageEntity(SocketConstant.SESSION_ID, number, header, yiZhengTlvList);
		String str = messagePackageEntity.combinationPackage();
		mReceiveSocketService.sendMessage(str);
	}

	private void sdkReturn(List<TlvEntity> yiZhengTlvList) {
		TlvEntity yiZhengTlv = new TlvEntity("01", "00");
		TlvEntity yiZhengTlv1 = new TlvEntity(SocketConstant.SDK_TAG, SocketConstant.SDK_VALUE);
		yiZhengTlvList.add(yiZhengTlv);
		yiZhengTlvList.add(yiZhengTlv1);
	}

	private void updateConnection(List<TlvEntity> yiZhengTlvList) {
		TlvEntity yiZhengTlv = new TlvEntity("01", "00");
		TlvEntity yiZhengTlv1 = new TlvEntity(Integer.toHexString(101) + "", Integer.toHexString(180) + "");
		yiZhengTlvList.add(yiZhengTlv);
		yiZhengTlvList.add(yiZhengTlv1);
	}

	private void connection(List<TlvEntity> yiZhengTlvList) {
		for (int i = 0; i < SocketConstant.CONNENCT_TAG.length; i++) {
			TlvEntity yiZhengTlv = new TlvEntity(SocketConstant.CONNENCT_TAG[i], SocketConstant.CONNENCT_VALUE[i]);
			yiZhengTlvList.add(yiZhengTlv);
			Log.e("connection", "Tag" + SocketConstant.CONNENCT_TAG[i] + "\nvalue=" + SocketConstant.CONNENCT_VALUE[i]);
		}
	}


	@Override
	public void send(byte evnindex, int length, byte[] bytes) {
		Log.e("sendSDK", "sendSDK=" + HexStringExchangeBytesUtil.bytesToHexString(bytes));
		JNIUtil.getInstance().simComEvtApp2Drv((byte) 0, evnindex, length, bytes);
	}

	@Override
	public void sendServer(String hexString) {
		Log.e("TlvAnalyticalUtils", "发给一正服务器的数据=" + hexString);
		mReceiveSocketService.sendMessage(hexString);
	}


}
