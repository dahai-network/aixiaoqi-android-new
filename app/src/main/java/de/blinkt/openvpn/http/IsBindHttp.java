package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.IsBindEntity;

/**
 * Created by Administrator on 2016/10/23.
 */

public class IsBindHttp extends BaseHttp {

	private IsBindEntity isBindEntity;
	private final String IMEI;

	public IsBindEntity getIsBindEntity() {
		return isBindEntity;
	}

	public IsBindHttp(InterfaceCallback call, int cmdType_, String IMEI) {
		super(call,cmdType_);
		this.IMEI = IMEI;
		this.sendMethod_ = GET_MODE;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.ISBIND_DEVICE;
		sendMethod_ = GET_MODE;

		params.put("IMEI", URLEncoder.encode(IMEI, "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		isBindEntity = new Gson().fromJson(response, IsBindEntity.class);
	}

}
