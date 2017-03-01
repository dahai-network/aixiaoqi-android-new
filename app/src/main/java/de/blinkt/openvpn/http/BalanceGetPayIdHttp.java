package de.blinkt.openvpn.http;


import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/11/3.
 */

public class BalanceGetPayIdHttp extends BaseHttp {

	private String OrderID;

	public BalanceGetPayIdHttp(InterfaceCallback call, int cmdType_, String OrderID) {
		super(call,cmdType_);
		this.OrderID = OrderID;

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.BALANCE_GETPAYID;
		params.put("OrderID", OrderID);
	}

}
