package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.text.TextUtils;
import android.view.View;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.SkyUpgradeModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.SkyUpgradeHttp;
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

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
    if (cmdType == HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA) {
            SkyUpgradeHttp skyUpgradeHttp = (SkyUpgradeHttp) object;
            if (skyUpgradeHttp.getStatus() == 1) {
                String braceletVersion = SharedUtils.getInstance().readString(Constant.BRACELETVERSION);
                if (!TextUtils.isEmpty(braceletVersion) && skyUpgradeHttp.getUpgradeEntity().getVersion() > Float.parseFloat(braceletVersion)) {
                    SharedUtils.getInstance().writeBoolean(Constant.HAS_DEVICE_NEED_UPGRADE,true);
                }
            }
            onLoadFinishListener.rightLoad( cmdType,  object);
        }


    }
}
