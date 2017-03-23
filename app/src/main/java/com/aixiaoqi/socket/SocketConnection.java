package com.aixiaoqi.socket;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * Created by Administrator on 2017/1/6 0006.
 */
public class SocketConnection implements ServiceConnection {
	public static ReceiveSocketService mReceiveSocketService = null;
	public static ReceiveDataframSocketService mReceiveDataframSocketService = null;

	private String TAG = "toBlue";

	@Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        if(service instanceof ReceiveDataframSocketService.LocalBinder){
            mReceiveDataframSocketService = ((ReceiveDataframSocketService.LocalBinder) service)
                    .getService();
            //TODO UDP 发送给蓝牙
            Log.i(TAG,"ReceiveDataframSocketService打开");
            if(ICSOpenVPNApplication.uartService!=null){

            ProMainActivity.sdkAndBluetoothDataInchange.initReceiveDataframSocketService(mReceiveDataframSocketService, ICSOpenVPNApplication.uartService);
            }
        }else if(service instanceof ReceiveSocketService.LocalBinder ){
            Log.i(TAG,"ReceiveSocketService打开");
            mReceiveSocketService = ((ReceiveSocketService.LocalBinder) service)
                    .getService();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if("aixiaoqi.socket.ReceiveDataframSocketService".equals(name.getClassName())){
            mReceiveDataframSocketService = null;
        }else if("qixiaoqi.socket.ReceiveSocketService".equals(name.getClassName())){
            mReceiveSocketService = null;
        }

    }


}
