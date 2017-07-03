package de.blinkt.openvpn.activities.Device.Presenter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public interface BindDevicePresenter {
    void requestUpdateDeviceInfo();
    void onDestory();
    void requestBindDevice(String deviceType);
    void requestBindDeviceList(ArrayList<String> addresss);
}
