package de.blinkt.openvpn.activities.Device.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.IsBindDeviceModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class IsBindDeviceModelImpl extends NetModelBaseImpl implements IsBindDeviceModel {
    public  IsBindDeviceModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void isBindDevice(String address) {
        createHttpRequest(HttpConfigUrl.COMTYPE_ISBIND_DEVICE, address);
    }
}
