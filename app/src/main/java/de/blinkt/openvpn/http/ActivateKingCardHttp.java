package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by wzj on 2016/12/23.
 * 激活大王卡套餐
 */

public class ActivateKingCardHttp extends BaseHttp {

	private final String OrderID;
	private final String Tel;

	public ActivateKingCardHttp(InterfaceCallback interfaceCallback, int cmdType_, String ...params) {
		super(interfaceCallback, cmdType_);
		this.OrderID = params[0];
		this.Tel = params[1];
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.ACTIVATE_KINGCARD;
		params.put("OrderID", OrderID);
		params.put("Tel", Tel);
	}

}
