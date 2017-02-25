package de.blinkt.openvpn.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.aixiaoqi.socket.SocketConnection;
import com.aixiaoqi.socket.SocketConstant;

import static com.aixiaoqi.socket.TestProvider.sendYiZhengService;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class NetReceiver extends BroadcastReceiver {
    private String TAG="NetReceiver";
    private void connectGoip() {
        if (sendYiZhengService != null){
            SocketConnection.mReceiveSocketService.disconnect();
            sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case ConnectivityManager.TYPE_WIFI:

                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (ni.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager. NETWORK_TYPE_IDEN:
                            connectGoip();
                            Log.e(TAG,"2G");
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
                            Log.e(TAG,"3G");
                            connectGoip();
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            Log.e(TAG,"4G");
                            connectGoip();
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
