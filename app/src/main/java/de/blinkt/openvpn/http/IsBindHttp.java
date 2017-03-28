package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.IsBindEntity;

/**
 * Created by Administrator on 2016/10/23.
 */

public class IsBindHttp extends BaseHttp {

	private IsBindEntity isBindEntity;

	public IsBindEntity getIsBindEntity() {
		return isBindEntity;
	}

	public IsBindHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call,cmdType_,GET_MODE,HttpConfigUrl.ISBIND_DEVICE,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("IMEI", URLEncoder.encode(valueParams[0], "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		isBindEntity = new Gson().fromJson(response, IsBindEntity.class);
	}

}
