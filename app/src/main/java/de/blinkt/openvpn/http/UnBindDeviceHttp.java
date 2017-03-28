package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/8.
 */

public class UnBindDeviceHttp extends BaseHttp {

	public UnBindDeviceHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_,false,GET_MODE,HttpConfigUrl.UN_BIND_DEVICE);
	}




}
