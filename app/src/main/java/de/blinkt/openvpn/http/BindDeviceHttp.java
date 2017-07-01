package de.blinkt.openvpn.http;

import android.util.Log;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by wzj on 2016/10/8.
 * 用于绑定蓝牙设备
 */

public class BindDeviceHttp extends BaseHttp {

	public BindDeviceHttp(InterfaceCallback call, int cmdType_, String...params) {
		super(call, cmdType_,HttpConfigUrl.BIND_DEVICE,params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		Log.d("BindDeviceHttp", "---valueParams="+valueParams[0]);
		params.put("IMEI", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("DeviceType", URLEncoder.encode(valueParams[1]+"", "utf-8"));
	}

}
