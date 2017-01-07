package de.blinkt.openvpn.http;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/9/9.
 */
public class ForgetPswHttp extends BaseHttp {


	private String tel;
	private String newPassword;
	private String smsVerCode;


	public ForgetPswHttp(InterfaceCallback call, int cmdType_, String tel, String newPassword, String smsVerCode) {
		super(call,cmdType_);
		this.tel = tel;
		this.newPassword = newPassword;
		this.smsVerCode = smsVerCode;


	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.FORGET_PSW;
		params.put("tel", URLEncoder.encode(tel, "utf-8"));
		params.put("newPassword", URLEncoder.encode(newPassword, "utf-8"));
		params.put("smsVerCode", URLEncoder.encode(smsVerCode, "utf-8"));
	}


}
