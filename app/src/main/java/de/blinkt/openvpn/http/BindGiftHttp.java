package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.BindCardModel;

/**
 * Created by Administrator on 2016/12/23.
 */

public class BindGiftHttp extends BaseHttp {

	private String CardPwd = null;
	private BindCardModel data ;

	private BindCardModel getBindCardModel()
	{
		return data;
	}
	public BindGiftHttp(InterfaceCallback interfaceCallback, int cmdType_, String...params ) {
		super(interfaceCallback, cmdType_,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.BIND_GIFT;
		params.put("CardPwd", URLEncoder.encode(valueParams[0], "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		super.parseObject(response);
		data = new Gson().fromJson(response,BindCardModel.class);
	}
}
