package de.blinkt.openvpn.http;

import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class AddNumberHttp extends BaseHttp{
    private String tel;
    private String code;
    public AddNumberHttp(InterfaceCallback call, int cmdType_,String tel,String code) {
        super(call,cmdType_);
        this.tel=tel;
        this.code=code;

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        slaverDomain_= HttpConfigUrl.ADD_NUMBER;
        params.put("Tel",tel);
        params.put("SmsVerCode",code);
    }
}
