package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.UpgradeEntity;

/**
 * Created by Administrator on 2016/10/20 0020.
 */
public class SkyUpgradeHttp extends BaseHttp {

	UpgradeEntity upgradeEntity;


	public UpgradeEntity getUpgradeEntity() {
		return upgradeEntity;
	}

	public SkyUpgradeHttp(InterfaceCallback interfaceCallback, int cmdType_, String...params) {
		super(interfaceCallback, cmdType_,GET_MODE,HttpConfigUrl.DEVICE_BRACELET_OTA,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();

		params.put("Version", valueParams[0]);
		params.put("DeviceType", valueParams[1]);
	}

	@Override
	protected void parseObject(String response) {
		upgradeEntity = new Gson().fromJson(response, UpgradeEntity.class);
	}


}
