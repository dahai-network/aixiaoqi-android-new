package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.text.TextUtils;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.GetBindDeviceInfoModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class GetBindDeviceInfoModelImpl extends NetModelBaseImpl implements GetBindDeviceInfoModel {
    public GetBindDeviceInfoModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }

    @Override
    public void getBindDeviceinfo() {

        if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI)) || TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))) {
            createHttpRequest(HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
        }
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
            GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
            if (object.getStatus() == 1) {
                if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
                    SharedUtils utils = SharedUtils.getInstance();
                    if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
                     String   deviceAddress = getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI();
                        if (!TextUtils.isEmpty(deviceAddress)) {
                            deviceAddress = deviceAddress.toUpperCase();
                            utils.writeString(deviceAddress, getBindDeviceHttp.getBlueToothDeviceEntityity().getVersion());
                            SharedUtils.getInstance().writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI());
                            String deviceTypeStr = getBindDeviceHttp.getBlueToothDeviceEntityity().getDeviceType();
                            if ("0".equals(deviceTypeStr)) {
                                SharedUtils.getInstance().writeString(Constant.BRACELETNAME, Constant.UNITOYS);
                            } else if ("1".equals(deviceTypeStr)) {
                                SharedUtils.getInstance().writeString(Constant.BRACELETNAME, Constant.UNIBOX);
                            }
                        }

                    }
                }
            }
            onLoadFinishListener.rightLoad(cmdType,object);
        }
    }
}
