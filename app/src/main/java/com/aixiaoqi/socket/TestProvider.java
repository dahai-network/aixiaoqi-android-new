package com.aixiaoqi.socket;

import android.util.Log;

public class TestProvider   {

	static PreDataEntity preDataEntity=new PreDataEntity();
	static IccidEntity iccidEntity=new IccidEntity();
	static SendYiZhengService  sendYiZhengService;
	public static void getCardInfo(String info) {
		String indexString=info.substring(2,4);

		if (null == info) {
			return;
		}
		if(sendYiZhengService==null){
			sendYiZhengService=new SendYiZhengService();
		}
		if(SocketConstant.EN_APPEVT_PRDATA.equals(indexString)|| SocketConstant.EN_APPEVT_SIMDATA.equals(indexString)){
			preDataSplit(info);
		}else if(SocketConstant.EN_APPEVT_SIMINFO.equals(indexString)){
			iccidDataSplit(info);
		}
	}
	private static boolean isCreate=false;
	private static boolean isIccid=false;



	private static  void iccidDataSplit(String item){
		iccidEntity.setChnString(item.substring(0,2));
		iccidEntity.setEvtIndex(item.substring(2,4));
		iccidEntity.setLenString(item.substring(4,8));
		String iccid=	RadixAsciiChange.convertHexToString(item.substring(8,item.length()));
		String [] iccidArray=iccid.split(";");
		for(int i=0;i<iccidArray.length;i++){
			String [] iccidArray1=iccidArray[i].split(":");
			if(i==0){
				iccidEntity.setIccid(iccidArray1[1]);
			}else{
				iccidEntity.setImmsi(iccidArray1[1]);
			}
		}
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length-5]=RadixAsciiChange.convertStringToHex(iccidEntity.getImmsi());
		SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length-6]=RadixAsciiChange.convertStringToHex(iccidEntity.getIccid());
		isIccid=true;
		if(isCreate&&isIccid) {
			Log.e("preDataSplit","isCreate1"+isCreate+"isIccid1"+isIccid);
			sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
		}
	}
	private static  void preDataSplit(String item){

		SocketConnection.mReceiveSocketService.setListener(new ReceiveSocketService.CreateSocketLisener() {
			@Override
			public void create() {
				isCreate=true;
				Log.e("preDataSplit","isCreate"+isCreate+"isIccid"+isIccid);
				if(isCreate&&isIccid){
					sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
				}
			}

		});
		preDataEntity.setChnString(item.substring(0,2));
		preDataEntity.setEvtIndex(item.substring(2,4));
		preDataEntity.setLenString(item.substring(4,8));
		preDataEntity.setPreDataString(item.substring(8,item.length()));
		String hex=preDataEntity.getPreDataString();
		if(SocketConstant.EN_APPEVT_PRDATA.equals(preDataEntity.getEvtIndex())){
			SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length-1]=hex;
			SocketConstant.CONNENCT_VALUE[SocketConstant.CONNENCT_VALUE.length-2]=preDataEntity.getLenString();
			sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);

		}else if(SocketConstant.EN_APPEVT_SIMDATA.equals(preDataEntity.getEvtIndex())){
			SocketConstant.SDK_VALUE=hex;
			sendYiZhengService.sendGoip(SocketConstant.PRE_DATA);

		}
	}



}
