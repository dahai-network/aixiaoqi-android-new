package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class SendRetryForErrorHttp extends BaseHttp {



	public SendRetryForErrorHttp(InterfaceCallback call, int cmdType_, String...params ) {
	super(call,cmdType_,HttpConfigUrl.SEND_RETRY_FOR_ERROR,params);

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();

		params.put("SMSID", URLEncoder.encode(valueParams[0] + "", "utf-8"));

	}


}
