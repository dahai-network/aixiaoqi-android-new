package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.content.Context;
import android.content.Intent;

import com.aixiaoqi.socket.JNIUtil;
import com.aixiaoqi.socket.ReceiveDataframSocketService;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SendYiZhengService;
import com.aixiaoqi.socket.SocketConnection;

import de.blinkt.openvpn.activities.Device.Model.NoPreDataRegisterModel;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class NoPreDataRegisterModelImpl implements NoPreDataRegisterModel {
    public static boolean isStartSdk = false;
    Context context;
    SocketConnection socketUdpConnection;

    public NoPreDataRegisterModelImpl( Context context){
        this.context=context;
        if(socketUdpConnection==null)
            socketUdpConnection = new SocketConnection();


    }
    @Override
    public void noPreDataStartSDKSimRegister() {
        isStartSdk = true;
        startDataframService();
        CommonTools.delayTime(5000);
        JNIUtil.getInstance().startSDK(1);
    }

    private void startDataframService() {
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())) {
            Intent receiveSdkIntent = new Intent(context, ReceiveDataframSocketService.class);
            context.bindService(receiveSdkIntent, socketUdpConnection, Context.BIND_AUTO_CREATE);
        }

    }

    public void unbindUdpService(){
        if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())) {
            context.unbindService(socketUdpConnection);
            if (SocketConnection.mReceiveDataframSocketService != null) {
                SocketConnection.mReceiveDataframSocketService.stopSelf();
            }
        }
    }
}
