package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.UpgradeEntity;

/**
 * Created by Administrator on 2016/10/20 0020.
 */
public class SkyUpgradeHttp extends BaseHttp {

	UpgradeEntity upgradeEntity;
	private String Version;
	private String DeviceType;

	public UpgradeEntity getUpgradeEntity() {
		return upgradeEntity;
	}

	public SkyUpgradeHttp(InterfaceCallback interfaceCallback, int cmdType_, String Version, int deviceType) {
		super(interfaceCallback, cmdType_);
		this.Version = Version;
		this.DeviceType = deviceType + "";
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.DEVICE_BRACELET_OTA;
		sendMethod_ = GET_MODE;
		params.put("Version", Version);
		params.put("DeviceType", DeviceType);
	}

	@Override
	protected void parseObject(String response) {
		upgradeEntity = new Gson().fromJson(response, UpgradeEntity.class);
	}


}
