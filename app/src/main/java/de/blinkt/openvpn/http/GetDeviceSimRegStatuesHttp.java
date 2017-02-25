package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.RegSuccessEntity;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class GetDeviceSimRegStatuesHttp extends BaseHttp {
	private RegSuccessEntity regSuccessEntity;

	public RegSuccessEntity getRegSuccessEntity() {
		return regSuccessEntity;
	}

	public GetDeviceSimRegStatuesHttp(InterfaceCallback interfaceCallback, int cmdType_) {
		super(interfaceCallback, cmdType_);
		isCreateHashMap = false;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_DEVICE_SIM_REG_STATUES;
		sendMethod_ = GET_MODE;
	}

	@Override
	protected void parseObject(String response) {
		regSuccessEntity = new Gson().fromJson(response, RegSuccessEntity.class);
	}
}
