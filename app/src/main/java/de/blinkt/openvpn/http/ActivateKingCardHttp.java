package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by wzj on 2016/12/23.
 * 激活大王卡套餐
 */

public class ActivateKingCardHttp extends BaseHttp {
	public ActivateKingCardHttp(InterfaceCallback interfaceCallback, int cmdType_, String ...params) {
		super(interfaceCallback, cmdType_,HttpConfigUrl.ACTIVATE_KINGCARD,params);

	}
	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("OrderID", valueParams[0]);
		params.put("Tel", valueParams[1]);
	}

}
