package de.blinkt.openvpn.http;



import com.google.gson.Gson;
import cn.com.johnson.model.SecurityConfig;
import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class SecurityConfigHttp extends BaseHttp {


	SecurityConfig securityConfig;

	public SecurityConfig getSecurityConfig() {
		return securityConfig;
	}

	public SecurityConfigHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_);
		isCreateHashMap=false;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.SECURITY_CONFIG;
		sendMethod_ = GET_MODE;

	}

	@Override
	protected void parseObject(String response) {
		securityConfig = new Gson().fromJson(response, SecurityConfig.class);
	}

}
