package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/2/20.
 */

public class UpdateConnectInfoHttp extends BaseHttp {


	public UpdateConnectInfoHttp(InterfaceCallback interfaceCallback, int cmdType_, String...params ) {
		super(interfaceCallback, cmdType_, HttpConfigUrl.UPDATE_CONN_INFO,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Version", valueParams[0]);
		params.put("Power", valueParams[1]+"");
		params.put("DeviceType", valueParams[2]+"");
	}

}
