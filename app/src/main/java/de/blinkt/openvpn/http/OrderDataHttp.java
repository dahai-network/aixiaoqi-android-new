package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.OrderDataEntity;

/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class OrderDataHttp extends BaseHttp {


	public OrderDataEntity getOrderDataEntity() {
		return orderDataEntity;
	}

	private OrderDataEntity orderDataEntity;

	public OrderDataHttp(InterfaceCallback call, int cmdType_, String ...params) {
		super(call,cmdType_,HttpConfigUrl.ORDER_DATA,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("OrderID", valueParams[0]);
		params.put("EmptyCardSerialNumber", valueParams[1]);
	}

	@Override
	protected void parseObject(String response) {
		orderDataEntity = new Gson().fromJson(response, OrderDataEntity.class);
	}

}
