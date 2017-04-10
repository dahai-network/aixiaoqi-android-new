package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class BlackListDeleteHttp extends BaseHttp {
    public  BlackListDeleteHttp(InterfaceCallback call,int cmdType_,String...params){
        super(call,cmdType_, HttpConfigUrl.BLACK_LIST_DELETE,params);
    }
    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        params.put("BlackNum",valueParams[0]);
    }
}
