package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.GetHostAndPortEntity;

/**
 * Created by wzj on 2016/10/8.
 * 用于绑定蓝牙设备
 */

public class GetHostAndPortHttp extends BaseHttp {

	private GetHostAndPortEntity getHostAndPortEntity;

	public GetHostAndPortEntity getGetHostAndPortEntity() {
		return getHostAndPortEntity;
	}

	public GetHostAndPortHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_);
		isCreateHashMap=false;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_SECURITY_CONFIG;
		sendMethod_ = GET_MODE;
	}

	@Override
	protected void parseObject(String response) {
		getHostAndPortEntity = new Gson().fromJson(response, GetHostAndPortEntity.class);
	}
}
