package de.blinkt.openvpn.http;


import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class AddNumberHttp extends BaseHttp{

    public AddNumberHttp(InterfaceCallback call, int cmdType_,String ...params) {
        super(call,cmdType_,HttpConfigUrl.ADD_NUMBER,params);

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("Tel",valueParams[0]);
        params.put("SmsVerCode",valueParams[1]);
    }
}
