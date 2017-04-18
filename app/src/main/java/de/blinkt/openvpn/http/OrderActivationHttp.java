package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class OrderActivationHttp extends BaseHttp {

	public OrderActivationHttp(InterfaceCallback call, int cmdType_, String...params) {
		super(call,cmdType_,HttpConfigUrl.ORDER_ACTIVATION,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("OrderID", valueParams[0]);
		params.put("BeginDateTime", valueParams[1]);
	}


}
