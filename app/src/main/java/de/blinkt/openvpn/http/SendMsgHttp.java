package de.blinkt.openvpn.http;



import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by wzj on 2016/9/9.
 */
public class SendMsgHttp extends BaseHttp {



	public SendMsgHttp(InterfaceCallback call, int cmdType_, String ...params ) {
		super(call,cmdType_, HttpConfigUrl.SEND_SMS,params);


	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("ToNum", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("Type", URLEncoder.encode(valueParams[1] + "", "utf-8"));
	}


}
