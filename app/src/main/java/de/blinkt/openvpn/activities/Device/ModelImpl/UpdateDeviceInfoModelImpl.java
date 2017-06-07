package de.blinkt.openvpn.activities.Device.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.UpdateDeviceInfoModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class UpdateDeviceInfoModelImpl extends NetModelBaseImpl implements UpdateDeviceInfoModel {
    public  UpdateDeviceInfoModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void updateDeviceInfo() {
        SharedUtils utils = SharedUtils.getInstance();
        createHttpRequest(HttpConfigUrl.COMTYPE_UPDATE_CONN_INFO, utils.readString(Constant.BRACELETVERSION),
                utils.readInt(Constant.BRACELETPOWER) + "", utils.readInt(Constant.BRACELETTYPEINT) + "");
    }
}
