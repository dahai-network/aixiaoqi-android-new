package com.aixiaoqi.socket;

import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.IsSuccessEntity;

import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;


public class TestProvider {

	static PreDataEntity preDataEntity = new PreDataEntity();
	static IccidEntity iccidEntity = new IccidEntity();
	public static SendYiZhengService sendYiZhengService;
	private static void offToPower() {
		byte[] value;
		value = HexStringExchangeBytesUtil.hexStringToBytes(Constant.OFF_TO_POWER);
		ICSOpenVPNApplication.uartService.writeRXCharacteristic(value);
	}
	public static void getCardInfo(String info) {

		String indexString = info.substring(2, 4);

		if (null == info) {
			return;
		}
		if (sendYiZhengService == null) {
			sendYiZhengService = new SendYiZhengService();
		}
		if (SocketConstant.EN_APPEVT_PRDATA.equals(indexString) || SocketConstant.EN_APPEVT_SIMDATA.equals(indexString)) {
			offToPower();
			preDataSplit(info);
		} else if (SocketConstant.EN_APPEVT_SIMINFO.equals(indexString)) {
			iccidDataSplit(info);
		}
	}

	public static boolean isCreate = false;
	public static boolean isIccid = false;


	private static void iccidDataSplit(String item) {
		iccidEntity.setChnString(item.substring(0, 2));
		iccidEntity.setEvtIndex(item.substring(2, 4));
		iccidEntity.setLenString(item.substring(4, 8));
		String iccid = RadixAsciiChange.convertHexToString(item.substring(8, item.length()));
		String[] iccidArray = iccid.split(";");
		for (int i = 0; i < iccidArray.length; i++) {
			String[] iccidArray1 = iccidArray[i].split(":");
			if (i == 0) {
				iccidEntity.setIccid(iccidArray1[1]);
			} else {
				iccidEntity.setImmsi(iccidArray1[1]);
			}
		}
		String imsi = iccidEntity.getImmsi().trim();
		Log.e("preDataSplit", "ICCID:" + iccidEntity.getIccid() + "\nIMMSI:" + iccidEntity.getImmsi());
		if (!TextUtils.isEmpty(imsi)) {
			if (imsi.startsWith("46000") || imsi.startsWith("46001") || imsi.startsWith("46002") || imsi.startsWith("46003") || imsi.startsWith("46007")) {//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
				SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5] = RadixAsciiChange.convertStringToHex(iccidEntity.getImmsi());
				SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6] = RadixAsciiChange.convertStringToHex(iccidEntity.getIccid());
				isIccid = true;
				if (isCreate && isIccid) {
					sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
				}
			} else {
				IsSuccessEntity entity = new IsSuccessEntity();
				entity.setType(Constant.REGIST_CALLBACK_TYPE);
				entity.setFailType(SocketConstant.REGISTER_FAIL_IMSI_IS_NULL);
				entity.setSuccess(false);
				EventBus.getDefault().post(entity);
				return;
			}
		} else {
			IsSuccessEntity entity = new IsSuccessEntity();
			entity.setType(Constant.REGIST_CALLBACK_TYPE);
			entity.setFailType(SocketConstant.REGISTER_FAIL_IMSI_IS_NULL);
			entity.setSuccess(false);
			EventBus.getDefault().post(entity);
			return;
		}
	}

	private static void preDataSplit(String item) {

		SocketConnection.mReceiveSocketService.setListener(new ReceiveSocketService.CreateSocketLisener() {
			@Override
			public void create() {
				isCreate = true;
				Log.e("preDataSplit", "isCreate" + isCreate + "isIccid" + isIccid);
				if (isCreate && isIccid) {
					sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
				}
			}

		});
		preDataEntity.setChnString(item.substring(0, 2));
		preDataEntity.setEvtIndex(item.substring(2, 4));
		preDataEntity.setLenString(item.substring(4, 8));
		preDataEntity.setPreDataString(item.substring(8, item.length()));
		String hex = preDataEntity.getPreDataString();
		if (SocketConstant.EN_APPEVT_PRDATA.equals(preDataEntity.getEvtIndex())) {
			SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 1] = hex;
			SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 2] = preDataEntity.getLenString();
			sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);
			REGISTER_STATUE_CODE = 2;
		} else if (SocketConstant.EN_APPEVT_SIMDATA.equals(preDataEntity.getEvtIndex())) {
			SocketConstant.SDK_VALUE = hex;
			sendYiZhengService.sendGoip(SocketConstant.PRE_DATA);

		}
	}

	public static  void  clearData(){
		sendYiZhengService=null;
		isCreate=false;
		isIccid=false;
	}

}
