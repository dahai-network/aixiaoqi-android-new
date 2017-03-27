package de.blinkt.openvpn.http;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by wzj on 2016/9/28.
 * 检查token是否过期
 */

public class CheckTokenHttp extends BaseHttp {

	public String getUpdateConfigTime() {
		return updateConfigTime;
	}

	String updateConfigTime;
	public CheckTokenHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_,false,GET_MODE,HttpConfigUrl.CHECKTOKEN);
	}
	@Override
	protected void parseObject(String response) {
		JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
		updateConfigTime = jsonObject.get("updateConfigTime").getAsString();
	}

}
