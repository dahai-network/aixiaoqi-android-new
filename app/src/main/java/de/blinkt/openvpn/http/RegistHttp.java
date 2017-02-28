package de.blinkt.openvpn.http;



import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/9/9.
 */
public class RegistHttp extends BaseHttp {


	private String tel;

	private String password;
	private String smsVerCode;


	public RegistHttp(InterfaceCallback call, int cmdType_, String tel, String password, String smsVerCode) {
		super(call,cmdType_);
		this.tel = tel;
		this.password = password;
		this.smsVerCode = smsVerCode;


	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.REGIST;

		params.put("tel", URLEncoder.encode(tel, "utf-8"));
		params.put("password", URLEncoder.encode(password, "utf-8"));
		params.put("smsVerCode", URLEncoder.encode(smsVerCode, "utf-8"));
	}


}
