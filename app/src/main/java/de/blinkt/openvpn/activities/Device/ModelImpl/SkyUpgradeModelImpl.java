package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.text.TextUtils;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.SkyUpgradeModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/1 0001.
 */

public class SkyUpgradeModelImpl extends NetModelBaseImpl implements SkyUpgradeModel {

    public  SkyUpgradeModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void skyUpgrade( ) {
        if(SharedUtils.getInstance().readBoolean(Constant.HAS_DEVICE_NEED_UPGRADE)){
            return ;
        }
        int DeviceType;
        String braceletname = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
        if (!TextUtils.isEmpty(braceletname)) {
            if (braceletname.contains(Constant.UNITOYS)) {
                //手环固件
                DeviceType = 0;
            } else if (braceletname.contains(Constant.UNIBOX)) {
                //钥匙扣固件
                DeviceType = 1;
            } else {
                return;
            }
        } else {
            return;
        }
        createHttpRequest(HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA, SharedUtils.getInstance().readString(Constant.BRACELETVERSION), DeviceType + "");
    }


}
