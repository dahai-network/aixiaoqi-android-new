package com.aixiaoqi.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.SharedUtils;

import static com.aixiaoqi.socket.TlvAnalyticalUtils.sendToSdkLisener;

/**
 * Created by Administrator on 2016/12/30 0030.
 */
public class ReceiveSocketService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private int contactFailCount=1;
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
            Log.i("Blue_Chanl","正在注册GOIP");
            createSocketLisener.create();
        }

        @Override
        public void onConnectFailed() {
            if(contactFailCount<=3){
                reConnect();
            }
            contactFailCount++;
        }
        private void sendMessage() {
            byte[] value;
            value = HexStringExchangeBytesUtil.hexStringToBytes(Constant.UP_TO_POWER);
            ICSOpenVPNApplication.uartService.writeRXCharacteristic(value);
            TlvAnalyticalUtils.isOffToPower=false;
            Log.e("Blue_Chanl", "执行上电命令！");

        }
        @Override
        public void onReceive(SocketTransceiver transceiver, byte[] s,int length) {
            if(TlvAnalyticalUtils.isOffToPower){
                sendMessage();
            }
            TlvAnalyticalUtils.builderMessagePackageList(HexStringExchangeBytesUtil.bytesToHexString(s,length));
            if(!SocketConstant.SESSION_ID_TEMP.equals(SocketConstant.SESSION_ID)&&count==0){
                timer.schedule(task,60000,60000);
                count=count+1;
            }
        }

        @Override
        public void onDisconnect(SocketTransceiver transceiver) {
            Log.e("Blue_Chanl", "断开连接 - onDisconnect");

            SocketConstant.REGISTER_STATUE_CODE=2;
            sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(""));
            reConnect();

//            count=0;
//            tcpClient.disconnect();
//            JNIUtil.getInstance().reStartSDK(SharedUtils.getInstance().readString(Constant.USER_NAME));
        }


    };
    private void reConnect() {
        tcpClient.disconnect();
        initSocket();

    }
    public void sendMessage(String s){
        Log.e("sendMessage","发送到GOIPtcpClient"+(tcpClient!=null)+"\n发送到GOIPtcpClient"+(tcpClient.getTransceiver()!=null));
        if(tcpClient!=null&&tcpClient.getTransceiver()!=null){
            tcpClient.getTransceiver().send(s);
        }
    }

    @Override
    public void onDestroy() {
        count=0;
        tcpClient.disconnect();
        SocketConstant.REGISTER_STATUE_CODE=0;
        timer.cancel();
        timer=null;
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
            TestProvider.sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
        }
    };

}
