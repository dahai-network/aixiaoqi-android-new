package de.blinkt.openvpn.activities.Device.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.Device.Model.GetSecurityConfigModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class GetSecurityConfigModelImpl extends NetModelBaseImpl implements GetSecurityConfigModel {
    public GetSecurityConfigModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void getSecurityConfig() {
        createHttpRequest(HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        super.rightComplete(cmdType, object);
    }
}
