package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.OrderEntity;

/**
 * Created by Administrator on 2016/9/19.
 */
public class GetOrderByIdHttp extends BaseHttp {

	private String id;
	private OrderEntity orderEntity;

	public OrderEntity getOrderEntity() {
		if(orderEntity==null){
			orderEntity=new OrderEntity();
		}
		return orderEntity;
	}

	public GetOrderByIdHttp(InterfaceCallback call, int cmdType_, String id) {
	super(call,cmdType_);
		this.id = id;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_USER_PACKET_BY_ID;
		sendMethod_ = GET_MODE;

		params.put("id", URLEncoder.encode(id, "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		orderEntity = new Gson().fromJson(response, OrderEntity.class);
	}

}
