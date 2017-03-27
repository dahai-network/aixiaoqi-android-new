package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by wzj on 2016/10/8.
 * 用于绑定蓝牙设备
 */

public class BindDeviceHttp extends BaseHttp {

//	private String IMEI;
//	private String Version;
//	private String DeviceType;

	public BindDeviceHttp(InterfaceCallback call, int cmdType_, String...params) {
		super(call, cmdType_,params);
//		this.IMEI = IMEI;
//		this.Version = Version;
//		this.DeviceType = DeviceType + "";
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.BIND_DEVICE;
		sendMethod_ = POST_MODE;
		params.put("IMEI", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("Version", URLEncoder.encode(valueParams[1], "utf-8"));
		params.put("DeviceType", URLEncoder.encode(valueParams[2]+"", "utf-8"));
	}

}
