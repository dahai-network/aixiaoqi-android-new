package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class SmsDeleteHttp extends BaseHttp {
  private   String Id;
    public SmsDeleteHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
        super(interfaceCallback, cmdType_);
       this.Id=params[0];
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_= HttpConfigUrl.SMS_DELETE;
        sendMethod_=POST_MODE;
        params.put("Id",Id);
    }
}
