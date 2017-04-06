package de.blinkt.openvpn.bluetooth.util;

import android.util.Log;

import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;



/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class SendCommandToBluetooth {

    public  static final String TAG="SendCommandToBluetooth";
    public static boolean sendMessageToBlueTooth(final String message) {
        byte[] value;
        Log.d(TAG, "sendMessageToBlueTooth: "+message);
        value = HexStringExchangeBytesUtil.hexStringToBytes(message);
        Log.i("toBLue", message);
        if (ICSOpenVPNApplication.uartService != null) {
            if (ICSOpenVPNApplication.uartService.mConnectionState == UartService.STATE_CONNECTED) {
                ICSOpenVPNApplication.uartService.writeRXCharacteristic(value);
                return true;
            } else {
                return false;
            }
        } else {
          return false;
        }
    }
}
