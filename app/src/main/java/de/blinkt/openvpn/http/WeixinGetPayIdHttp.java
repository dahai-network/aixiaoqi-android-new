package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.WeiXinResultEntity;

/**
 * Created by Administrator on 2016/9/21.
 */

public class WeixinGetPayIdHttp extends BaseHttp {


	private String orderOrPayment;
	private WeiXinResultEntity weixinResultEntity;

	public WeixinGetPayIdHttp(InterfaceCallback call, int cmdType_, String orderOrPayment) {
		super(call,cmdType_);
		this.orderOrPayment = orderOrPayment;
	}

	public WeiXinResultEntity getWeixinResultEntity() {
		return weixinResultEntity;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.WEIXIN_GETPAYID;
		params.put("orderOrPayment", URLEncoder.encode(orderOrPayment, "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		weixinResultEntity = new Gson().fromJson(response, WeiXinResultEntity.class);
	}

}
