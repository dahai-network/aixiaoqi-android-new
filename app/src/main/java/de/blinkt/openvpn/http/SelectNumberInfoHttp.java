package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/12/1 0001.
 */
public class SelectNumberInfoHttp  extends BaseHttp{

    public SelectNumberInfoHttp(InterfaceCallback call, int cmdType_,String...params ) {
        super(call,cmdType_,HttpConfigUrl.ADD_SELECT_NUMBER_INFO,params);

    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();

        params.put("OrderByZCId",valueParams[0]);
        params.put("Name",valueParams[1]);
        params.put("IdentityNumber",valueParams[2]);
        params.put("MobileNumber",valueParams[3]);
        params.put("PaymentMethod",valueParams[4]);
    }
}
