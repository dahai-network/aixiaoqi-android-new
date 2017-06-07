package de.blinkt.openvpn.activities.Device.ModelImpl;

import de.blinkt.openvpn.activities.Device.Model.CheckDeviceIsOnlineModel;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public class CheckDeviceIsOnlineModelImpl extends NetModelBaseImpl implements CheckDeviceIsOnlineModel {

    public  CheckDeviceIsOnlineModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void checkDeviceIsOnline() {
        createHttpRequest(HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES);
    }



}
