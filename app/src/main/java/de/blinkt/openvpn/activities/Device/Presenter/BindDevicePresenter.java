package de.blinkt.openvpn.activities.Device.Presenter;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public interface BindDevicePresenter {
    void requestIsBindDevice();
    void requestUpdateDeviceInfo();
    void onDestory();
    void requestBindDevice(String deviceType);
}
