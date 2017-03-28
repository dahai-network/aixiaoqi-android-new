package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/1/10 0010.
 */

public class GetSelectPhoneNumberUrl extends BaseHttp {
    public   GetSelectPhoneNumberUrl(InterfaceCallback callback,int cmdType){
        super(callback,cmdType,false,GET_MODE,HttpConfigUrl.GET_SELECT_NUMBER_URL);
    }

}
