package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/11/3.
 */

public class BindRechargeHttp extends BaseHttp {




	public BindRechargeHttp(InterfaceCallback call, int cmdType_, String...params) {
		super(call,cmdType_,HttpConfigUrl.BIND_RECHARGE_CARD,params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("CardPwd", URLEncoder.encode(valueParams[0], "utf-8"));
	}

}
