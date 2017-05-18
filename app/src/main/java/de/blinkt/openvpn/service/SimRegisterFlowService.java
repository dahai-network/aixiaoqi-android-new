package de.blinkt.openvpn.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class SimRegisterFlowService extends Service implements InterfaceCallback{
    private UartService mService = null;
    private   ReceiveBLEMoveReceiver bleMoveReceiver;
    private String deviceAddress;
    @Override
    public void onCreate() {
        super.onCreate();

        //本地是否保存有设备地址和设备类型
        if(TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))||TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))){
            //如果没有保存设备地址和设备类型则去请求
            CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
        }else{
            //有绑定过，则搜索设备，没有搜索到，就用通知栏的方式提示用户
            BluetoothConstant.IS_BIND = true;
            //搜索到设备，则连接设备。
            initUartServices();

        }
    }

    public void initUartServices() {
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(UartService.class.getName())) {
            Intent bindIntent = new Intent(this, UartService.class);
            try {
                bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            ICSOpenVPNApplication.uartService=  mService = ((UartService.LocalBinder) rawBinder).getService();
            //存在Application供全局使用
            initBrocast();
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {

                            if (device.getName() == null) {
                                return;
                            }
                            if (deviceAddress.equalsIgnoreCase(device.getAddress())) {
                                scanLeDevice(false);
                                mService.connect(deviceAddress);

                            }

                }
            };


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mService.mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mService.mBluetoothAdapter.stopLeScan(mLeScanCallback);
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
    private void initBrocast() {
        if (bleMoveReceiver == null) {
            bleMoveReceiver = new ReceiveBLEMoveReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(bleMoveReceiver, makeGattUpdateIntentFilter());
        }
    }
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        //如果没有绑定过则所有的操作结束。否则继续下面的操作
        if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
            GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
            if (object.getStatus() == 1) {
                if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
                    if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
                        deviceAddress = getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI();
                        if (deviceAddress != null) {
                            deviceAddress = deviceAddress.toUpperCase();
                            BluetoothConstant.IS_BIND = true;
                        }
                        SharedUtils utils = SharedUtils.getInstance();
                        utils.writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI().toUpperCase());
                        //按MAC地址保存版本号
                        if (!TextUtils.isEmpty(deviceAddress))
                            utils.writeString(deviceAddress, getBindDeviceHttp.getBlueToothDeviceEntityity().getVersion());
                        //防止返回“”或者null
                        String deviceTypeStr = getBindDeviceHttp.getBlueToothDeviceEntityity().getDeviceType();
                        if (!TextUtils.isEmpty(deviceTypeStr)) {
                            int deviceType = Integer.parseInt(deviceTypeStr);
                            if (deviceType == 0) {
                                utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNITOYS);
                            } else {
                                utils.writeString(Constant.BRACELETNAME, MyDeviceActivity.UNIBOX);
                            }
                        }
                        initUartServices();
                    }
                }
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {

    }

    @Override
    public void noNet() {

    }
}
