package de.blinkt.openvpn.activities.Device.Model;

import android.content.Context;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public interface RegisterBroadcastModel {
    void registerReceiveBLEMoveReceiverBroadcast(Context context);
    void unregisterReceiveBLEMoveReceiverBroadcast(Context context);
    void registerBlueChangeBroadcast(Context context);
    void unregisterBlueChangeBroadcast(Context context);
}
