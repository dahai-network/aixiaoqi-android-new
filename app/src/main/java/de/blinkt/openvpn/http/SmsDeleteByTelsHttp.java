package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class SmsDeleteByTelsHttp extends BaseHttp {

	public SmsDeleteByTelsHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
		super(interfaceCallback, cmdType_,POST_JSON,HttpConfigUrl.SMS_DELETE_BY_TELS,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Tels",valueParams[0]);
	}
}
