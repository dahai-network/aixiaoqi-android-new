package de.blinkt.openvpn.activities.CommomModel.BlueTooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * Created by Administrator on 2017/6/3 0003.
 */

public  class BlueToothModel {
    private BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    BluetoothListener bluetoothListener;
    Context context;

    public interface  BluetoothListener{
        void   scanNotFindDevice();
        void findDevices(final BluetoothDevice device, final int rssi, byte[] scanRecord);
    }
    public BlueToothModel(BluetoothListener bluetoothListener,Context context){
        this.bluetoothListener=bluetoothListener;
        this.context=context;
    }

    public static final int REQUEST_ENABLE_BT = 2;
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.

        if (! ICSOpenVPNApplication.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {//蓝牙不支持低功耗蓝牙
            return false;
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) ICSOpenVPNApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }
                mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            return false;
        }
        return true;
    }




    public  void scanLeDevice(final boolean enable) {
        if (enable) {
            if(bluetoothListener!=null)
                bluetoothListener.scanNotFindDevice();
            mBluetoothAdapter.startLeScan(mLeScanCallback);

        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device,  int rssi, byte[] scanRecord) {
                    if(bluetoothListener!=null)
                        bluetoothListener.findDevices(device,rssi,scanRecord);
                }
            };

    public void connect(String macAddress){
        if(ICSOpenVPNApplication.uartService!=null){
            ICSOpenVPNApplication.uartService.connect(macAddress);
        }
    }

    public void disconnect(){
        if(ICSOpenVPNApplication.uartService!=null){
            ICSOpenVPNApplication.uartService.disconnect();
        }
    }



    public boolean bluetoothIsOpen(Context context) {
        if(!(context instanceof  Activity)){
            throw new IllegalArgumentException("context is must Activity");
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        return mBluetoothAdapter.isEnabled();
    }
}
