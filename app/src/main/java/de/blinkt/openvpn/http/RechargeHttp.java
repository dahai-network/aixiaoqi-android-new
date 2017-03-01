package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.RechargeEntity;

/**
 * Created by Administrator on 2016/9/22.
 */

public class RechargeHttp extends BaseHttp {

	//金额
	private String Amount;
	//1支付宝/2微信
	private int PaymentMethod;
	private RechargeEntity rechargeEntity;


	public RechargeEntity getRechargeEntity() {
		return rechargeEntity;
	}

	public RechargeHttp(InterfaceCallback call, int cmdType_, String Amount, int PaymentMethod) {
		super(call,cmdType_);
		this.Amount = Amount;
		this.PaymentMethod = PaymentMethod;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.RECHARGE_ORDER;
		params.put("Amount", URLEncoder.encode(Amount, "utf-8"));
		params.put("PaymentMethod", URLEncoder.encode(PaymentMethod + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		rechargeEntity = new Gson().fromJson(response, RechargeEntity.class);
	}


}
