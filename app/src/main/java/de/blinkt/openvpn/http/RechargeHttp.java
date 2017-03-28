package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.RechargeEntity;

/**
 * Created by Administrator on 2016/9/22.
 */

public class RechargeHttp extends BaseHttp {


	private RechargeEntity rechargeEntity;


	public RechargeEntity getRechargeEntity() {
		return rechargeEntity;
	}

	public RechargeHttp(InterfaceCallback call, int cmdType_, String ...params) {
		super(call,cmdType_,HttpConfigUrl.RECHARGE_ORDER,params);
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Amount", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("PaymentMethod", URLEncoder.encode(valueParams[1] + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		rechargeEntity = new Gson().fromJson(response, RechargeEntity.class);
	}


}
