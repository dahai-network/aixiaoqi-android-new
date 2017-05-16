package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.CheckConfirmedEntity;

/**
 * Created by Administrator on 2017/5/15.
 */

public class CheckConfirmedHttp extends BaseHttp {
	private CheckConfirmedEntity entity;

	public CheckConfirmedEntity getEntity() {
		return entity;
	}

	public CheckConfirmedHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
		super(interfaceCallback,cmdType_, GET_MODE, HttpConfigUrl.CHECK_CONFIRMED, params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("ICCID", valueParams[0]);
	}

	@Override
	protected void parseObject(String response) {
		entity = new Gson().fromJson(response, CheckConfirmedEntity.class);
	}
}
