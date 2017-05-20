package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.OrderAddEntity;

/**
 * Created by Administrator on 2016/9/20.
 */
public class OrderAddHttp extends BaseHttp {

	private OrderAddEntity orderEntity;
	//套餐ID
	private String PackageID;
	//数量
	private int Quantity;
	//1支付宝/2微信
	private int PaymentMethod;
	//是否余额支付
	private boolean IsPayUserAmount;

	public OrderAddEntity getOrderEntity() {
		return orderEntity;
	}//PackageID, int Quantity, int PaymentMethod, boolean IsPayUserAmount

	public OrderAddHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call, cmdType_, POST_MODE, HttpConfigUrl.CREATE_ORDER, params);
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("PackageID", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("Quantity", URLEncoder.encode(valueParams[1], "utf-8"));
		params.put("PaymentMethod", URLEncoder.encode(valueParams[2], "utf-8"));
		if (valueParams.length > 3) {
			params.put("MonthPackageFee", URLEncoder.encode(valueParams[3], "utf-8"));
			params.put("PackageAttributeId", URLEncoder.encode(valueParams[4], "utf-8"));
		}
	}

	@Override
	protected void parseObject(String response) {
		orderEntity = new Gson().fromJson(response, OrderAddEntity.class);
	}

}
