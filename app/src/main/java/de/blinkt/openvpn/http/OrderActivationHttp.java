package de.blinkt.openvpn.http;

import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class OrderActivationHttp extends BaseHttp {

	private String OrderID;
	private String time;
	public OrderActivationHttp(InterfaceCallback call, int cmdType_, String OrderID, String time) {
		super(call,cmdType_);
		this.OrderID = OrderID;
		this.time = time;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.ORDER_ACTIVATION;
		params.put("OrderID", OrderID);
		params.put("BeginTime", time);
	}


}
