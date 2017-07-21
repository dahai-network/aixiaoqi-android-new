package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.content.Context;
import android.content.Intent;

import com.aixiaoqi.socket.JNIUtil;
import com.aixiaoqi.socket.ReceiveDataframSocketService;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SendYiZhengService;
import com.aixiaoqi.socket.SocketConnection;
import com.orhanobut.logger.Logger;

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
        Logger.d("开启so库");
        JNIUtil.getInstance().startSDK(0);
    }
private boolean isBind=false;
    private void startDataframService() {
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())) {
            Intent receiveSdkIntent = new Intent(context, ReceiveDataframSocketService.class);
            isBind = context.bindService(receiveSdkIntent, socketUdpConnection, Context.BIND_AUTO_CREATE);
        }

    }

    public void unbindUdpService(){
        if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveDataframSocketService.class.getName())) {
            if(isBind&&socketUdpConnection!=null) {
                context.unbindService(socketUdpConnection);
                isBind=false;
            }
            if (SocketConnection.mReceiveDataframSocketService != null) {
                SocketConnection.mReceiveDataframSocketService.stopSelf();
            }
        }
    }
}
