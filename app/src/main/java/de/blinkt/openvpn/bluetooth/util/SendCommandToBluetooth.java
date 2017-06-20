package de.blinkt.openvpn.bluetooth.util;

import android.util.Log;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;



/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class SendCommandToBluetooth {

    public  static final String TAG="SendCommandToBluetooth";
    //发送单包给蓝牙
    public static boolean sendMessageToBlueTooth(final String message) {
        byte[] value;
        Log.d(TAG, "sendMessageToBlueTooth: "+message);
        value = HexStringExchangeBytesUtil.hexStringToBytes(message);
        Log.i("toBLue", message);
        if (ICSOpenVPNApplication.uartService != null) {
            if (ICSOpenVPNApplication.uartService.mConnectionState == UartService.STATE_CONNECTED) {
                return  ICSOpenVPNApplication.uartService.writeRXCharacteristic(value);
            } else {
                return false;
            }
        } else {
          return false;
        }
    }
//发送多包给蓝牙
    public static void sendToBlue(String value,String type){
        Log.e("TlvAnalyticalUtils","发送给蓝牙的数据"+value);
        String[] messages = PacketeUtil.Separate(value, type);
        for (int i = 0; i < messages.length; i++) {
            byte[] valueData = HexStringExchangeBytesUtil.hexStringToBytes(messages[i]);
            ICSOpenVPNApplication.uartService.writeRXCharacteristic(valueData);

        }
    }
}
