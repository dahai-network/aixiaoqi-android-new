package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/2/20.
 */

public class UpdateConnectInfoHttp extends BaseHttp {
	private final String Version;
	private final int Power;
	private final int DeviceType;

	public UpdateConnectInfoHttp(InterfaceCallback interfaceCallback, int cmdType_, String Version, int Power, int DeviceType) {
		super(interfaceCallback, cmdType_);
		this.Version = Version;
		this.Power = Power;
		this.DeviceType = DeviceType;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.UPDATE_CONN_INFO;
		params.put("Version", Version);
		params.put("Power", Power+"");
		params.put("DeviceType", DeviceType+"");
	}

}
