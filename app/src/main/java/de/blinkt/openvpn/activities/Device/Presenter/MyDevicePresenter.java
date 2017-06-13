package de.blinkt.openvpn.activities.Device.Presenter;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public interface MyDevicePresenter {
    void requestUnbindDevice();
    void requestSkyUpgrade();
    void requestDownloadUpgradePackage(String downloadUrl);
    void requestCheckDeviceIsOnline();
    void onDestory();
}
