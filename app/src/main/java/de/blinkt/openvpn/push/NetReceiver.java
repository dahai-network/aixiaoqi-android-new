package de.blinkt.openvpn.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SocketConnection;
import com.aixiaoqi.socket.SocketConstant;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.DateUtils;

import static com.aixiaoqi.socket.EventBusUtil.registerFail;
import static com.aixiaoqi.socket.TestProvider.sendYiZhengService;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class NetReceiver extends BroadcastReceiver {

    private void connectGoip() {
        if (sendYiZhengService != null){
            registerFail(Constant.REGIST_CALLBACK_TYPE,SocketConstant.REG_STATUE_CHANGE);
            sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
        }
    }
private void restartConnect(){
    if (SocketConstant.REGISTER_STATUE_CODE == 3||SocketConstant.REGISTER_STATUE_CODE == 2) {
        SocketConstant.REGISTER_STATUE_CODE = 2;
        if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
            //从预读取数据那里重新注册
            connectGoip();
        } else {
            registerFail(Constant.REGIST_CALLBACK_TYPE, SocketConstant.RESTART_TCP);
        }

    }
}

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    restartConnect();
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager. NETWORK_TYPE_IDEN:
                            restartConnect();
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            restartConnect();
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            restartConnect();
                            break;
                        default:

                    }
                    break;
                default:
                    break;
            }

        }
    }
}
