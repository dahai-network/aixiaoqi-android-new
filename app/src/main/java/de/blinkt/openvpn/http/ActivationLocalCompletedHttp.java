package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;

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

	public ActivationLocalCompletedHttp(InterfaceCallback call, int cmdType_, String OrderID) {
		super(call,cmdType_);
		this.OrderID = OrderID;

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
