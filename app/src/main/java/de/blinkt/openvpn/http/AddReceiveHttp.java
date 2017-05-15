package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.FreeWorryEntity;
import de.blinkt.openvpn.model.OrderAddEntity;

/**
 * Created by Administrator on 2017/5/12.
 */

public class AddReceiveHttp extends BaseHttp {

	private FreeWorryEntity freeWorryEntity;
	private OrderAddEntity orderEntity;

	public OrderAddEntity getOrderEntity() {
		return orderEntity;
	}

	public AddReceiveHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call, cmdType_, POST_MODE, HttpConfigUrl.ADD_RECEIVE, params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("PackageID", valueParams[0]);
	}

	@Override
	protected void parseObject(String response) {
		orderEntity = new Gson().fromJson(response, OrderAddEntity.class);
	}

}
