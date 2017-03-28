package de.blinkt.openvpn.http;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/27.
 */

public class UpdateAlarmHttp extends BaseHttp {



	public UpdateAlarmHttp(InterfaceCallback call, int cmdType_, String...params) {
	super(call,cmdType_,HttpConfigUrl.UPDATE_ALARM,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Time", valueParams[0]);
		params.put("Repeat", valueParams[1]);
		params.put("Tag", valueParams[2]);
		params.put("ID", URLEncoder.encode(valueParams[3] , "utf-8"));
		params.put("Status", URLEncoder.encode(valueParams[4] , "utf-8"));
	}




}
