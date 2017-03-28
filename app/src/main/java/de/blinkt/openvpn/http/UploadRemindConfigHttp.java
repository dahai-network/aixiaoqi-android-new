package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/28.
 */

public class UploadRemindConfigHttp extends BaseHttp {


	public UploadRemindConfigHttp(InterfaceCallback call, int cmdType_, String...params ) {
		super(call,cmdType_, HttpConfigUrl.UPLOAD_REMIND_CONFIG,params);

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Name", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("Status", URLEncoder.encode(valueParams[1]+"", "utf-8"));
	}
}
