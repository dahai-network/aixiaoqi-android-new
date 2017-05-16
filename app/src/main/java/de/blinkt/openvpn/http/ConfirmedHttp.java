package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/5/15.
 */

public class ConfirmedHttp extends BaseHttp {


	public ConfirmedHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
		super(interfaceCallback, cmdType_, HttpConfigUrl.CONFIRMED, params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Tel", valueParams[0]);
		params.put("ICCID", valueParams[1]);
	}
}
