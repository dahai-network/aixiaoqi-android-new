package com.aixiaoqi.socket;

import android.text.TextUtils;
import android.util.Log;

import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.DBHelp;
import de.blinkt.openvpn.model.PreReadEntity;
import de.blinkt.openvpn.util.SharedUtils;

import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;
import static de.blinkt.openvpn.activities.Device.ModelImpl.HasPreDataRegisterImpl.sendYiZhengService;


public class TestProvider {

	static PreDataEntity preDataEntity = new PreDataEntity();
	static IccidEntity iccidEntity = new IccidEntity();


	public static void getCardInfo(String info) {

		String indexString = info.substring(2, 4);

		if (null == info) {
			return;
		}

		if (SocketConstant.EN_APPEVT_PRDATA.equals(indexString) || SocketConstant.EN_APPEVT_SIMDATA.equals(indexString)) {
			SendCommandToBluetooth.sendMessageToBlueTooth(Constant.OFF_TO_POWER);
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
		if (TextUtils.isEmpty(iccidEntity.getImmsi())){
			EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.REGISTER_FAIL_IMSI_IS_NULL);
			return;
		}
		Log.e("preDataSplit", "ICCID:" + iccidEntity.getIccid() + "\nIMMSI:" + iccidEntity.getImmsi());
		createTcp();
	}

	private static void createTcp() {
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5] = RadixAsciiChange.convertStringToHex(iccidEntity.getImmsi());
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6] = RadixAsciiChange.convertStringToHex(iccidEntity.getIccid());
		String token = SharedUtils.getInstance().readString(Constant.TOKEN);
		if (TextUtils.isEmpty(token)) {
			EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.TOKEN_IS_NULL);
		} else {
			SocketConstant.CONNENCT_VALUE[3] = RadixAsciiChange.convertStringToHex(token);
			REGISTER_STATUE_CODE = 2;
			isIccid = true;
			savePreData();
			if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_SUCCEED) {
				sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
			} else if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_FAIL) {
				SocketConnection.mReceiveSocketService.disconnect();
				connectTcp();
			} else {
				connectTcp();
			}

		}

	}

	private static void connectTcp() {
		sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);
		if (isCreate && isIccid) {
			sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
		}
	}

	private static void savePreData() {
		DBHelp db = new DBHelp(ICSOpenVPNApplication.getContext());
		PreReadEntity preReadEntity = new PreReadEntity();
		preReadEntity.setIccid(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6]);
		preReadEntity.setImsi(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5]);
		preReadEntity.setPreReadData(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 1]);
		preReadEntity.setDataLength(SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 2]);
		db.insertPreData(preReadEntity);
	}

	private static void preDataSplit(String item) {

		createTcpSucceedAndConnectionGOIP();
		preDataEntity.setChnString(item.substring(0, 2));
		preDataEntity.setEvtIndex(item.substring(2, 4));
		preDataEntity.setLenString(item.substring(4, 8));
		preDataEntity.setPreDataString(item.substring(8, item.length()));
		String hex = preDataEntity.getPreDataString();
		Log.e("preDataSplit", "evt:" + preDataEntity.getEvtIndex() + "\nchn:" + preDataEntity.getChnString() + "\nlen:" + preDataEntity.getLenString() + "\npreData:" + preDataEntity.getPreDataString());
		if (SocketConstant.EN_APPEVT_PRDATA.equals(preDataEntity.getEvtIndex())) {
			SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 1] = hex;
			SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 2] = preDataEntity.getLenString();
		} else if (SocketConstant.EN_APPEVT_SIMDATA.equals(preDataEntity.getEvtIndex())) {
			SocketConstant.SDK_VALUE = hex;
			sendYiZhengService.sendGoip(SocketConstant.PRE_DATA);
		}
	}

	private static void createTcpSucceedAndConnectionGOIP() {
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
	}

	public static void clearData() {
		sendYiZhengService = null;
		isCreate = false;
		isIccid = false;
	}

}
