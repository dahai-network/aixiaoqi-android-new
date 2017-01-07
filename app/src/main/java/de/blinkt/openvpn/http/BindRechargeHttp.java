package de.blinkt.openvpn.http;

import java.net.URLEncoder;
import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/11/3.
 */

public class BindRechargeHttp extends BaseHttp {


	private String CardPwd;

	public BindRechargeHttp(InterfaceCallback call, int cmdType_, String CardPwd) {
		super(call,cmdType_);
		this.CardPwd = CardPwd;

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.BIND_RECHARGE_CARD;
		sendMethod_ = POST_MODE;
		params.put("CardPwd", URLEncoder.encode(CardPwd, "utf-8"));
	}

}
