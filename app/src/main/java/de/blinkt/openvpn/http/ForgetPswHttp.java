package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/9/9.
 */
public class ForgetPswHttp extends BaseHttp {



	public ForgetPswHttp(InterfaceCallback call, int cmdType_, String ...params) {
		super(call,cmdType_,HttpConfigUrl.FORGET_PSW,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("tel", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("newPassword", URLEncoder.encode(valueParams[1], "utf-8"));
		params.put("smsVerCode", URLEncoder.encode(valueParams[2], "utf-8"));
	}


}
