package de.blinkt.openvpn.activities.Device.Model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public interface IsBindDeviceModel {
    void isBindDevice(String address);

    /**
     *获取该设备的绑定状态
     * @param addresss device address
     */
    void getDeviceState(ArrayList<String> addresss);
}
