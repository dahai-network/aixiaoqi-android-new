package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;

import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Device.Model.RegisterBroadcastModel;
import de.blinkt.openvpn.activities.Device.ui.ProMainActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ui.SmsFragment;

/**
 * Created by Administrator on 2017/6/9 0009.
 */

public class RegisterBroadcastModelImpl implements RegisterBroadcastModel{
    ReceiveBLEMoveReceiver bleMoveReceiver;

    //注册蓝牙数据接收广播
    @Override
    public void registerReceiveBLEMoveReceiverBroadcast(Context context) {
        if (bleMoveReceiver == null) {
            bleMoveReceiver = new ReceiveBLEMoveReceiver();
            LocalBroadcastManager.getInstance(context).registerReceiver(bleMoveReceiver, makeGattUpdateIntentFilter());
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(UartService.FINDED_SERVICE);
        return intentFilter;
    }

//取消注册蓝牙数据接收广播
    @Override
    public void unregisterBlueChangeBroadcast(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(bleMoveReceiver);
    }
//注册蓝牙状态发生变化广播
    @Override
    public void registerBlueChangeBroadcast(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(screenoffReceive, screenoffIntentFilter());
    }
//取消注册蓝牙状态发生变化广播
    @Override
    public void unregisterReceiveBLEMoveReceiverBroadcast(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(screenoffReceive);
    }

    //接收蓝牙是否开启
    private BroadcastReceiver screenoffReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:

                        EventBusUtil.simRegisterStatue(SocketConstant.UNREGISTER, SocketConstant.BLUETOOTH_CLOSE);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };
    private  IntentFilter screenoffIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return intentFilter;
    }



}
