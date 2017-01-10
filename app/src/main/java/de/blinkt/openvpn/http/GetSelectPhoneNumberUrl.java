package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class GetSelectPhoneNumberUrl extends BaseHttp {
    public   GetSelectPhoneNumberUrl(InterfaceCallback callback,int cmdType){
        super(callback,cmdType);
        isCreateHashMap=false;
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        sendMethod_=GET_MODE;
        slaverDomain_= HttpConfigUrl.GET_SELECT_NUMBER_URL;
    }

    @Override
    protected void parseObject(String response) {
        super.parseObject(response);
    }
}
