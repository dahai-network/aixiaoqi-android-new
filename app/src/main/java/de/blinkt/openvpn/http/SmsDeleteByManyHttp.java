package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/4/7.
 */

public class SmsDeleteByManyHttp extends BaseHttp {

	public SmsDeleteByManyHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
		super(interfaceCallback, cmdType_,POST_JSON, HttpConfigUrl.SMS_DELETE_SMSs,params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Ids",valueParams[0]);
	}
}
