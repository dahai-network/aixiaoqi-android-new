package de.blinkt.openvpn.http;

import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.LoginEntity;

/**
 * import java.io.IOException;
 * Created by Administrator on 2016/9/6 0006.
 */
public class LoginHttp extends BaseHttp {
	private String tel;
	private String password;
	LoginEntity loginEntity;

	public LoginEntity getLoginModel() {
		return loginEntity;
	}

	public LoginHttp(InterfaceCallback call, int cmdType_, String tel, String password) {
		super(call,cmdType_);
		this.tel = tel;
		this.password = password;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.LOGIN;
		params.put("tel", URLEncoder.encode(tel, "utf-8"));
		params.put("password", URLEncoder.encode(password, "utf-8"));
		params.put("loginTerminal", URLEncoder.encode(Build.MODEL, "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		loginEntity = new Gson().fromJson(response, LoginEntity.class);
	}


}
