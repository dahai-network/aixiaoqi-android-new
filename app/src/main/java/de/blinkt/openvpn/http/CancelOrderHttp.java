package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/9/19.
 */
public class CancelOrderHttp extends BaseHttp {

	public CancelOrderHttp(InterfaceCallback call, int cmdType_, String...params) {
	super(call,cmdType_,HttpConfigUrl.CANCEL_ORDER,params);
	}
	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("OrderID", URLEncoder.encode(valueParams[0], "utf-8"));
	}

}
