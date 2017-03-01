package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.BlueToothDeviceEntity;

/**
 * Created by Administrator on 2016/10/8.
 */

public class GetBindDeviceHttp extends BaseHttp  {

	private BlueToothDeviceEntity blueToothDeviceEntityity;

	public BlueToothDeviceEntity getBlueToothDeviceEntityity() {
		return blueToothDeviceEntityity;
	}

	public GetBindDeviceHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_);
		isCreateHashMap=false;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_BIND_DEVICE;
		sendMethod_ = GET_MODE;
	}

	@Override
	protected void parseObject(String response) {
		blueToothDeviceEntityity = new Gson().fromJson(response, BlueToothDeviceEntity.class);
	}


}
