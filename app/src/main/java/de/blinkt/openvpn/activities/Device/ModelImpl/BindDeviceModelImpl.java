package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.util.Log;

import com.orhanobut.logger.Logger;

import de.blinkt.openvpn.activities.Device.ui.BindDeviceActivity;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.BindDeviceModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/5 0005.
 */

public class BindDeviceModelImpl extends NetModelBaseImpl implements BindDeviceModel {
    public  BindDeviceModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }


    @Override
    public void bindDevice(String address,String  deviceType) {
        Logger.d( "bindDevice: "+address);
        createHttpRequest(HttpConfigUrl.COMTYPE_BIND_DEVICE
                ,address , deviceType);
    }
}
