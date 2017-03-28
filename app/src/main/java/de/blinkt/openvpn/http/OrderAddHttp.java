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
	}

	public OrderAddHttp(InterfaceCallback call, int cmdType_, String PackageID, int Quantity, int PaymentMethod, boolean IsPayUserAmount) {
		super(call, cmdType_,HttpConfigUrl.CREATE_ORDER);
		this.PackageID = PackageID;
		this.Quantity = Quantity;
		this.PaymentMethod = PaymentMethod;
		this.IsPayUserAmount = IsPayUserAmount;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("PackageID", URLEncoder.encode(PackageID, "utf-8"));
		params.put("Quantity", URLEncoder.encode(Quantity + "", "utf-8"));
		params.put("PayUserAmount", URLEncoder.encode(0 + "", "utf-8"));
		params.put("PaymentMethod", URLEncoder.encode(PaymentMethod + "", "utf-8"));
		//是否使用余额支付，暂时不使用，所以是false,否则true
		params.put("IsPayUserAmount", URLEncoder.encode(IsPayUserAmount + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		orderEntity = new Gson().fromJson(response, OrderAddEntity.class);
	}

}
