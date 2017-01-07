package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by wzj on 2016/9/28.
 * 推出登陆
 */

public class ExitHttp extends BaseHttp {


	public ExitHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_);
		isCreateHashMap=false;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.EXIT;
		sendMethod_ = GET_MODE;
	}

}
