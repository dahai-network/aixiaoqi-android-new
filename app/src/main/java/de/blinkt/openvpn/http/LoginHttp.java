package de.blinkt.openvpn.http;

import android.os.Build;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.LoginEntity;

/**
 * import java.io.IOException;
 * Created by Administrator on 2016/9/6 0006.
 */
public class LoginHttp extends BaseHttp {
	LoginEntity loginEntity;

	public LoginEntity getLoginModel() {
		return loginEntity;
	}

	public LoginHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call,cmdType_, HttpConfigUrl.LOGIN,params);

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("tel", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("password", URLEncoder.encode(valueParams[1], "utf-8"));
		params.put("loginTerminal", URLEncoder.encode(Build.MODEL, "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		loginEntity = new Gson().fromJson(response, LoginEntity.class);
	}


}
