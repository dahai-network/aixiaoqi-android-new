package de.blinkt.openvpn.http;

import android.util.Log;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/2/20.
 */

public class UpdateVersionHttp extends BaseHttp {
	private final String Version;
	private final String TAG = "UpdateVersionHttp";

	public UpdateVersionHttp(InterfaceCallback interfaceCallback, int cmdType_, String Version) {
		super(interfaceCallback, cmdType_);
		Log.i(TAG, "接口进入设备号：" + Version);
		this.Version = Version;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.UPDATE_VERSION;
		params.put("Version", Version);
	}

}
