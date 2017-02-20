package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

import static de.blinkt.openvpn.activities.MyOrderDetailActivity.OrderID;

/**
 * Created by Administrator on 2017/2/20.
 */

public class UpdateVersionHttp extends BaseHttp {
	private final String Version;

	public UpdateVersionHttp(InterfaceCallback interfaceCallback, int cmdType_, String Version) {
		super(interfaceCallback, cmdType_);
		this.Version = OrderID;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.UPDATE_VERSION;
		params.put("Version", Version);
	}

}
