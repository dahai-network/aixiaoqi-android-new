package com.aixiaoqi.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created by Administrator on 2016/12/31 0031.
 * UDP 服务
 */
public class ReceiveDataframSocketService extends Service {
    private MessageOutLisener listener;
    private final IBinder mBinder = new LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ReceiveDataframSocketService getService() {
            return ReceiveDataframSocketService.this;
        }
    }
    //初始化udp
    public void initDataframSocketService(){
        udpClient.start();
    }


    UdpClient udpClient=new UdpClient(){
        //接收到SDK的数据发送给蓝牙
        @Override
        public void sendToBluetoothMsg(String msg) {
            listener.sendToBluetoothMsg(msg);
        }
    };
    public void setListener(MessageOutLisener listener) {
        this.listener = listener;
    }
    //输出信息接口
    public interface MessageOutLisener {
        void sendToBluetoothMsg(String msg);
    }

    //向SDK发送信息
    public  void sendToSdkMessage(final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                udpClient.sendToSdkMessage(msg);
            }
        }).start();

    }


    public String getSorcketTag(){
        return  udpClient.getSorcketTag();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
//        udpClient.disconnect();
//        udpClient=null;
    }
}
