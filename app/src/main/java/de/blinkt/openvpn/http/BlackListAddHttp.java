package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class BlackListAddHttp extends BaseHttp {
    public  BlackListAddHttp(InterfaceCallback call,int cmdType_,String...params){
        super(call,cmdType_, HttpConfigUrl.BLACK_LIST_ADD,params);
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("BlackNum",valueParams[0]);
    }
}
