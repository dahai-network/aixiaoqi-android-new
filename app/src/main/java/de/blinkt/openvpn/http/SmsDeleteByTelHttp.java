package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class SmsDeleteByTelHttp extends BaseHttp {
  private   String Tel;
    public SmsDeleteByTelHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
        super(interfaceCallback, cmdType_);
       this.Tel=params[0];
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_= HttpConfigUrl.SMS_DELETE_BY_TEL;
        sendMethod_=POST_MODE;
        params.put("Tel",Tel);
    }
}
