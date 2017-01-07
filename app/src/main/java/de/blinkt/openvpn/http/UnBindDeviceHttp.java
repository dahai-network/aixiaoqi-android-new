package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/8.
 */

public class UnBindDeviceHttp extends BaseHttp {

	public UnBindDeviceHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_);
		isCreateHashMap=false;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.UN_BIND_DEVICE;
		sendMethod_ = GET_MODE;
	}


}
