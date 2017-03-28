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
		super(call,cmdType_,false,GET_MODE,HttpConfigUrl.SECURITY_CONFIG);

	}

	@Override
	protected void parseObject(String response) {
		securityConfig = new Gson().fromJson(response, SecurityConfig.class);
	}

}
