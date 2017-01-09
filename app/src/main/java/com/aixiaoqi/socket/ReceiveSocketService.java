package com.aixiaoqi.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2016/12/30 0030.
 */
public class ReceiveSocketService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private int contactFailCount=1;
    private int discontactCount=1;
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public  ReceiveSocketService getService() {
            return ReceiveSocketService.this;
        }
    }
    public void initSocket(){
        tcpClient.connect();
    }

    TcpClient tcpClient =new TcpClient() {
        @Override
        public void onConnect(SocketTransceiver transceiver) {
            createSocketLisener.create();
        }

        @Override
        public void onConnectFailed() {
            if(contactFailCount<=3){
                reConnect( contactFailCount);
            }
        }

        @Override
        public void onReceive(SocketTransceiver transceiver, byte[] s,int length) {
            TlvAnalyticalUtils.builderMessagePackageList(HexStringExchangeBytesUtil.bytesToHexString(s,length));
            if(!SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID)&&count==0){
                timer.schedule(task,60000,60000);
                count=count+1;
            }
        }

        @Override
        public void onDisconnect(SocketTransceiver transceiver) {
            tcpClient.disconnect();
            JNIUtil.getInstance().reStartSDK(SharedUtils.getInstance().readString(Constant.USER_NAME));
        }


    };
    private void reConnect(int count) {
        tcpClient.disconnect();
        initSocket();
        count++;
    }
    public void sendMessage(String s){
        if(tcpClient!=null&&tcpClient.getTransceiver()!=null){
            tcpClient.getTransceiver().send(s);
        }
    }

    @Override
    public void onDestroy() {
        tcpClient.disconnect();
        super.onDestroy();
    }

    CreateSocketLisener createSocketLisener;
    public void setListener(CreateSocketLisener listener) {
        this.createSocketLisener = listener;
    }
    public interface CreateSocketLisener {
        void create();


    }

    int count=0;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            TestProvider.sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
        }
    };
}
