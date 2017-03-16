package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by wzj on 2016/10/8.
 * 用于绑定蓝牙设备
 */

public class BindDeviceHttp extends BaseHttp {

	private String IMEI;
	private String Version;
	private String DeviceType;

	public BindDeviceHttp(InterfaceCallback call, int cmdType_, String IMEI, String Version, int DeviceType) {
		super(call, cmdType_);
		this.IMEI = IMEI;
		this.Version = Version;
		this.DeviceType = DeviceType + "";
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.BIND_DEVICE;
		sendMethod_ = POST_MODE;
		params.put("Version", URLEncoder.encode(Version, "utf-8"));
		params.put("IMEI", URLEncoder.encode(IMEI, "utf-8"));
		params.put("DeviceType", URLEncoder.encode(DeviceType, "utf-8"));
	}

}
