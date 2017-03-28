package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class SmsDeleteHttp extends BaseHttp {

    public SmsDeleteHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
        super(interfaceCallback, cmdType_, HttpConfigUrl.SMS_DELETE,params);

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("Id",valueParams[0]);
    }
}
