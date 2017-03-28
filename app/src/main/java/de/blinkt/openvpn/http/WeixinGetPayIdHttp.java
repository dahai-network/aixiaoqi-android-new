package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.WeiXinResultEntity;

/**
 * Created by Administrator on 2016/9/21.
 */

public class WeixinGetPayIdHttp extends BaseHttp {



	private WeiXinResultEntity weixinResultEntity;

	public WeixinGetPayIdHttp(InterfaceCallback call, int cmdType_, String...params  ) {
		super(call,cmdType_,HttpConfigUrl.WEIXIN_GETPAYID,params);

	}

	public WeiXinResultEntity getWeixinResultEntity() {
		return weixinResultEntity;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("orderOrPayment", URLEncoder.encode(valueParams[0], "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		weixinResultEntity = new Gson().fromJson(response, WeiXinResultEntity.class);
	}

}
