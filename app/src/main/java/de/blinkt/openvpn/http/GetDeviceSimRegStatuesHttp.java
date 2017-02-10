package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class GetDeviceSimRegStatuesHttp extends BaseHttp {
    public  GetDeviceSimRegStatuesHttp(InterfaceCallback interfaceCallback,int cmdType_){
      super(interfaceCallback,cmdType_);
        isCreateHashMap=false;
    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_ = HttpConfigUrl.GET_DEVICE_SIM_REG_STATUES;
        sendMethod_ = GET_MODE;
    }
}
