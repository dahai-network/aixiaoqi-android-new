package de.blinkt.openvpn.activities.Device.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * Created by Administrator on 2017/6/6 0006.
 */

public abstract  class BluetoothBaseActivity extends BaseActivity {
    private BluetoothManager mBluetoothManager;
    protected BluetoothAdapter mBluetoothAdapter;
    protected UartService mService = ICSOpenVPNApplication.uartService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }
    public static final int REQUEST_ENABLE_BT = 2;
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.

        if (! ICSOpenVPNApplication.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {//蓝牙不支持低功耗蓝牙
            finish();
            return false;
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) ICSOpenVPNApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                finish();
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            finish();
            return false;
        }
        return true;
    }

    public void scanNotFindDevice(){

    }

    public  void scanLeDevice(boolean enable) {
        if (enable) {
            scanNotFindDevice();
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }
    abstract  void findDevices(BluetoothDevice device,int rssi,byte[] scanRecord);;

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device,final  int rssi,final  byte[] scanRecord) {
                    e("device="+device.getAddress());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findDevices(device,rssi,scanRecord);
                        }
                    });

                }
            };

    public void connect(String macAddress){
        if(mService!=null){
            if(mService.mBluetoothAdapter==null){
                mService.initialize();
            }
            mService.connect(macAddress);
        }
    }

    public void disconnect(){
        if(mService!=null){
            mService.disconnect();
        }
    }



    public boolean bluetoothIsOpen() {

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        return mBluetoothAdapter.isEnabled();
    }
}
