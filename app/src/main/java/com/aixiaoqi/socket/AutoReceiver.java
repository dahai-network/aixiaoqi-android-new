package com.aixiaoqi.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.aixiaoqi.socket.SocketConstant.HEARTBEAT_PACKET_TIMER;

/**
 * Created by Administrator on 2017/1/20 0020.
 */

public class AutoReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("onReceive", "定时器进来了");
        if (intent.getAction().equals(HEARTBEAT_PACKET_TIMER)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TestProvider.sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
                }
            }).start();
        }
    }
}
