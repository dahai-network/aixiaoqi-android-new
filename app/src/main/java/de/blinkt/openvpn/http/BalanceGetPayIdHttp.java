package de.blinkt.openvpn.http;


import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/11/3.
 */

public class BalanceGetPayIdHttp extends BaseHttp {

	public BalanceGetPayIdHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call,cmdType_,HttpConfigUrl.BALANCE_GETPAYID,params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("OrderID", valueParams[0]);
	}

}
