package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/9/19.
 */
public class CancelOrderHttp extends BaseHttp {

	private String OrderID;



	public CancelOrderHttp(InterfaceCallback call, int cmdType_, String OrderID) {
	super(call,cmdType_);
		this.OrderID = OrderID;

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.CANCEL_ORDER;

		params.put("OrderID", URLEncoder.encode(OrderID, "utf-8"));
	}

}
