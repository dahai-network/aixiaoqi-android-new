package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.model.*;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/5/15.
 */

public class GetCurrentHttp extends BaseHttp {

	private GetCurrentEntity getCurrentEntity;

	public GetCurrentEntity getEntity() {
		if (getCurrentEntity == null) {
			getCurrentEntity = new GetCurrentEntity();
		}
		return getCurrentEntity;
	}

	public GetCurrentHttp(InterfaceCallback interfaceCallback, int cmdType_) {
		super(interfaceCallback, cmdType_, GET_MODE, HttpConfigUrl.GETCURRENT);
	}

	@Override
	protected void parseObject(String response) {
		getCurrentEntity = new Gson().fromJson(response, GetCurrentEntity.class);
	}
}
