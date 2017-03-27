package de.blinkt.openvpn.http;


import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class AddNumberHttp extends BaseHttp{
//    private String tel;
//    private String code;
    public AddNumberHttp(InterfaceCallback call, int cmdType_,String ...params) {
        super(call,cmdType_,params);
//        this.tel=params[0];
//        this.code=params[1];
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_= HttpConfigUrl.ADD_NUMBER;
        params.put("Tel",valueParams[0]);
        params.put("SmsVerCode",valueParams[1]);
    }
}
