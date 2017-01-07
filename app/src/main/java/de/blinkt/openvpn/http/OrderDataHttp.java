package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.OrderDataEntity;

/**
 * Created by Administrator on 2016/10/10 0010.
 */
public class OrderDataHttp extends BaseHttp {

	private String OrderID;

	private String EmptyCardSerialNumber;

	public OrderDataEntity getOrderDataEntity() {
		return orderDataEntity;
	}

	private OrderDataEntity orderDataEntity;

	public OrderDataHttp(InterfaceCallback call, int cmdType_, String OrderID , String EmptyCardSerialNumber) {
		super(call,cmdType_);
		this.OrderID = OrderID;
		this.EmptyCardSerialNumber = EmptyCardSerialNumber;

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.ORDER_DATA;
		params.put("OrderID", OrderID);
		params.put("EmptyCardSerialNumber", EmptyCardSerialNumber);
	}

	@Override
	protected void parseObject(String response) {
		orderDataEntity = new Gson().fromJson(response, OrderDataEntity.class);
	}

}
