package de.blinkt.openvpn.http;
import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
/**
 * Created by Administrator on 2016/9/10.
 */
public class UserFeedBackHttp extends BaseHttp {






	public UserFeedBackHttp(InterfaceCallback call, int cmdType_,String...params) {
	super(call,cmdType_,HttpConfigUrl.USER_FEED_BACK,params);
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("version", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("Model", URLEncoder.encode(valueParams[1], "utf-8"));
		params.put("smsVerCode", URLEncoder.encode(valueParams[0], "utf-8"));
		params.put("Info", valueParams[2]);
	}


}
