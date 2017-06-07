package de.blinkt.openvpn.activities.Device.View;

import de.blinkt.openvpn.activities.CommomView.Toast;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public interface BindDeviceView extends Toast{
    void finishView();
    void searchBluetoothText(int searchId);
    void setFindedImageView(int IsVisible);
    void SetUniImageViewBackground(int sourceId);
    void tipSearchText(int tipText);
    String getDeviceName();
    void toActivity();
    void showNotSearchDeviceDialog();
    void connect(String macAddress);
    void scanLeDevice(boolean enable);
    void afterConnDevice();
}
