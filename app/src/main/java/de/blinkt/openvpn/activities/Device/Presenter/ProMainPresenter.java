package de.blinkt.openvpn.activities.Device.Presenter;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public interface ProMainPresenter {
    void onDestory();
    void requestGetBindDeviceInfo();
    void  requestGetSecurityConfig();
    void requestGetBasicConfig();
    void requestSkyUpdate();
}
