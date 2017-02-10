package com.aixiaoqi.socket;

import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.IsSuccessEntity;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;

import static com.aixiaoqi.socket.EventBusUtil.registerFail;
import static com.aixiaoqi.socket.SocketConstant.REGISTER_STATUE_CODE;


public class TestProvider {

	static PreDataEntity preDataEntity = new PreDataEntity();
	static IccidEntity iccidEntity = new IccidEntity();
	public static SendYiZhengService sendYiZhengService;

	public static void getCardInfo(String info) {

		String indexString = info.substring(2, 4);

		if (null == info) {
			return;
		}
		if (sendYiZhengService == null) {
			sendYiZhengService = new SendYiZhengService();
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
		Log.e("preDataSplit", "ICCID:" + item);
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
		String imsi;
		if(!TextUtils.isEmpty(iccidEntity.getImmsi()))
			imsi = iccidEntity.getImmsi().trim();
		else{
			notifiUI(SocketConstant.REGISTER_FAIL_IMSI_IS_NULL);
			return;
		}
		Log.e("preDataSplit", "ICCID:" + iccidEntity.getIccid() + "\nIMMSI:" + iccidEntity.getImmsi());
		if (!TextUtils.isEmpty(imsi)) {
			if (imsi.startsWith("46000") || imsi.startsWith("46001") || imsi.startsWith("46002") || imsi.startsWith("46003") || imsi.startsWith("46007")) {//因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
				SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 5] = RadixAsciiChange.convertStringToHex(iccidEntity.getImmsi());
				SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length - 6] = RadixAsciiChange.convertStringToHex(iccidEntity.getIccid());
				String token=SharedUtils.getInstance().readString(Constant.TOKEN);
				if(TextUtils.isEmpty(token)){
					notifiUI(SocketConstant.TOKEN_IS_NULL);
				}else{
					SocketConstant.CONNENCT_VALUE[3] =RadixAsciiChange.convertStringToHex(token);
					REGISTER_STATUE_CODE = 2;
					isIccid = true;
					if(NetworkUtils.isNetworkAvailable(ICSOpenVPNApplication.getContext())){
						sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);
						if (isCreate && isIccid) {
							sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
						}
					}else{
						notifiUI(SocketConstant.NOT_NETWORK);
					}
				}
			} else {
				notifiUI(SocketConstant.REGISTER_FAIL_IMSI_IS_ERROR);
			}
		} else {
			notifiUI(SocketConstant.REGISTER_FAIL_IMSI_IS_NULL);

		}
	}

	private static void notifiUI(int type) {
		registerFail(Constant.REGIST_CALLBACK_TYPE,type);
	}

	private static void preDataSplit(String item) {

		createTcpSucceedAndConnectionGOIP();
		preDataEntity.setChnString(item.substring(0, 2));
		preDataEntity.setEvtIndex(item.substring(2, 4));
		preDataEntity.setLenString(item.substring(4, 8));
		preDataEntity.setPreDataString(item.substring(8, item.length()));
		String hex = preDataEntity.getPreDataString();
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

	public static  void  clearData(){
		sendYiZhengService=null;
		isCreate=false;
		isIccid=false;
	}

}
