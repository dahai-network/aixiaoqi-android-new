package de.blinkt.openvpn.http;


import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.OrderDataEntity;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class ActivationLocalCompletedHttp extends BaseHttp {

	private String OrderID;

	public OrderDataEntity getOrderDataEntity() {
		return orderDataEntity;
	}

	private OrderDataEntity orderDataEntity;

	public ActivationLocalCompletedHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call,cmdType_);
		this.OrderID = params[0];

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.ORDER_ACTIVATION_LOCAL_COMPLETED;
		params.put("OrderID", OrderID);
	}



	@Override
	protected void parseObject(String response) {
		orderDataEntity = new Gson().fromJson(response, OrderDataEntity.class);
	}



}
