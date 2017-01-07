package de.blinkt.openvpn.http;

import java.net.URLEncoder;
import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class SendRetryForErrorHttp extends BaseHttp {

	private String SmsID;


	public SendRetryForErrorHttp(InterfaceCallback call, int cmdType_, String SmsID) {
	super(call,cmdType_);
		this.SmsID = SmsID;


	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.SEND_RETRY_FOR_ERROR;

		params.put("SMSID", URLEncoder.encode(SmsID + "", "utf-8"));

	}


}
