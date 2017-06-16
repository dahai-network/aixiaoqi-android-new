package de.blinkt.openvpn.http;

import android.util.Log;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.OrderEntity;

/**
 * Created by Administrator on 2016/9/19.
 */
public class GetOrderByIdHttp extends BaseHttp {


	private OrderEntity orderEntity;

	public OrderEntity getOrderEntity() {
		if(orderEntity==null){
			orderEntity=new OrderEntity();
		}
		return orderEntity;
	}

	public GetOrderByIdHttp(InterfaceCallback call, int cmdType_, String... params) {
	super(call,cmdType_,GET_MODE,HttpConfigUrl.GET_USER_PACKET_BY_ID,params);

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("id", URLEncoder.encode(valueParams[0], "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		orderEntity = new Gson().fromJson(response, OrderEntity.class);
	}

}
