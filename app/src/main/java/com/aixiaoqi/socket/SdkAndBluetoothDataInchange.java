package com.aixiaoqi.socket;

import android.util.Log;

import java.util.ArrayList;

import de.blinkt.openvpn.bluetooth.service.UartService;

/**
 * Created by Administrator on 2017/1/5 0005.
 */
public class SdkAndBluetoothDataInchange {
    public static final String TAG = "nRFUART";
    UartService mService;
    ArrayList<String> messages ;
    ReceiveDataframSocketService mReceiveDataframSocketService;
    public void initReceiveDataframSocketService(ReceiveDataframSocketService receiveDataframSocketService,UartService mService){
        receiveDataframSocketService.setListener(new ReceiveDataframSocketService.MessageOutLisener() {
                                                     @Override
                                                     public void sendToBluetoothMsg(final String msg) {
                                                         //SDK接收到消息发送给蓝牙消息的方法
                                                         //TODO
                                                         Log.e(TAG,"&&& server temp:" + msg);
                                                         sendToBluetoothAboutCardInfo(msg);


                                                     }

                                                 }
        );
        //TODO 初始化UDPsocket
        receiveDataframSocketService.initDataframSocketService();
        this.mReceiveDataframSocketService=receiveDataframSocketService;
        this.mService=mService;
    }
    private String socketTag="0";
    private String sendToOneServerTemp;
    private  String mStrSimPowerOnPacket = "";
    byte num=0;
    public void sendToSDKAboutBluetoothInfo(String temp,byte []txValue){
        num++;
        //Log.e("Bluetooth","num="+num+"\ntxValue[4]="+txValue[4]);
        if(num!=txValue[4]){
            num=0;
            Log.e("BlueError","蓝牙数据出错重发="+finalTemp);
            sendToBluetoothAboutCardInfo(finalTemp);
            return;
        }
        if(messages ==null){
            messages = new ArrayList<>();
        }
        messages.add(temp);
        if (txValue[3] == txValue[4]) {
            mStrSimPowerOnPacket = PacketeUtil.Combination(messages);

            // 接收到一个完整的数据包,发送到SDK
            int length = (txValue[2] & 0xff);
            String sendToOnService = null;
            Log.e("Bluetooth","从蓝牙发出的完整数据 mStrSimPowerOnPacket:" + mStrSimPowerOnPacket.length() + "; \n"
                    + mStrSimPowerOnPacket+"\nlength="+length);
            if (mStrSimPowerOnPacket.length() >= length) {
                try{
                    sendToOnService = mStrSimPowerOnPacket.substring(0, length * 2);
                }catch (StringIndexOutOfBoundsException e ){
                    Log.e("Bluetooth","catch socketTag:" + socketTag+ "; \n"
                            + sendToOneServerTemp);
                    sendToBluetoothAboutCardInfo(finalTemp);
                    return ;
                }
            } else {
                Log.e("Bluetooth","catch else:" + socketTag+ "; \n"
                        + sendToOneServerTemp);
                sendToBluetoothAboutCardInfo(finalTemp);
                return ;
            }
            socketTag=    mReceiveDataframSocketService.getSorcketTag();
            sendToOneServerTemp  =sendToOnService;
            Log.e("Bluetooth","从蓝牙发出的完整数据 socketTag:" + socketTag+ "; \n"
                    + sendToOneServerTemp);
            //TODO 从蓝牙发出的数据到SDK

            sendToSDKAboutBluetoothInfo( socketTag+sendToOneServerTemp );
            num=0;
            Log.e("Bluetooth", "从蓝牙发出的数据" + socketTag+ sendToOneServerTemp);

        }
    }


    private void sendToSDKAboutBluetoothInfo(final String finalMessage) {
        //TODO 从蓝牙发出的数据到SDK
        if (mReceiveDataframSocketService != null) {
            mReceiveDataframSocketService.sendToSdkMessage(finalMessage);
        }
        messages.clear();
    }
    private String finalTemp;
    private void sendToBluetoothAboutCardInfo(String msg) {
        //TODO 从SDK发出的数据到蓝牙
        String temp = "";
        if (msg.length() > 7) {
            temp = msg.substring(7);
        } else {
            temp = msg;
        }
        finalTemp=temp;
        if (temp.contains("0x0000")) {
            sendMessage(temp);
        } else {
            Log.e(TAG,"&&& server temp:" + temp);
            String[] messages = PacketeUtil.Separate(temp);

            for (int i = 0; i < messages.length; i++) {
                Log.e(TAG,"&&& server  message: " + messages[i].toString());
                Log.e("Bluetooth", "发送到蓝牙的数据" + socketTag+ sendToOneServerTemp);
                sendMessage(messages[i]);
            }
        }
    }

    private void sendMessage(String temp){
        if (temp.contains("0x0000")) {
            byte[] value;
            value = HexStringExchangeBytesUtil.hexStringToBytes("AADB040174");
            mService.writeRXCharacteristic(value);
            Log.e("BLUETOOTH", "SIM POWER UP");
        } else {
            byte[] value = HexStringExchangeBytesUtil.hexStringToBytes(temp);
            mService.writeRXCharacteristic(value);
        }
    }
}
