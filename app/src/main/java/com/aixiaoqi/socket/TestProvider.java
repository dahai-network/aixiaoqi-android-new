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
		if("04".equals(indexString)||"05".equals(indexString)){
			preDataSplit(info);
		}else if("06".equals(indexString)){
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
		Contant.CONNENCT_VALUE[Contant.CONNENCT_VALUE.length-5]=RadixAsciiChange.convertStringToHex(iccidEntity.getImmsi());
		Contant.CONNENCT_VALUE[Contant.CONNENCT_VALUE.length-6]=RadixAsciiChange.convertStringToHex(iccidEntity.getIccid());
		isIccid=true;
		if(isCreate&&isIccid) {
			Log.e("preDataSplit","isCreate1"+isCreate+"isIccid1"+isIccid);
			sendYiZhengService.sendGoip(Contant.CONNECTION);
		}
	}
	private static  void preDataSplit(String item){

		SocketConnection.mReceiveSocketService.setListener(new ReceiveSocketService.CreateSocketLisener() {
			@Override
			public void create() {
				isCreate=true;
				Log.e("preDataSplit","isCreate"+isCreate+"isIccid"+isIccid);
				if(isCreate&&isIccid){
					sendYiZhengService.sendGoip(Contant.CONNECTION);
				}
			}

		});
		preDataEntity.setChnString(item.substring(0,2));
		preDataEntity.setEvtIndex(item.substring(2,4));
		preDataEntity.setLenString(item.substring(4,8));
		preDataEntity.setPreDataString(item.substring(8,item.length()));
		String hex=preDataEntity.getPreDataString();
		if("04".equals(preDataEntity.getEvtIndex())){
			Contant.CONNENCT_VALUE[Contant.CONNENCT_VALUE.length-1]=hex;
			Contant.CONNENCT_VALUE[Contant.CONNENCT_VALUE.length-2]=preDataEntity.getLenString();
			sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);

		}else if("05".equals(preDataEntity.getEvtIndex())){
			Contant.SDK_VALUE=hex;
			sendYiZhengService.sendGoip(Contant.PRE_DATA);

		}
	}



}
