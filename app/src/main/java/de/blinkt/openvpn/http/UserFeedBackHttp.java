package de.blinkt.openvpn.http;
import java.net.URLEncoder;
import java.util.HashMap;
import de.blinkt.openvpn.constant.HttpConfigUrl;
/**
 * Created by Administrator on 2016/9/10.
 */
public class UserFeedBackHttp extends BaseHttp {



	private String Model;
	private String version;
	private String Info;


	public UserFeedBackHttp(InterfaceCallback call, int cmdType_, String version, String Model, String Info) {
	super(call,cmdType_);
		this.version = version;
		this.Model = Model;
		this.Info = Info;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.USER_FEED_BACK;
		params.put("version", URLEncoder.encode(version, "utf-8"));
		params.put("Model", URLEncoder.encode(Model, "utf-8"));
		params.put("smsVerCode", URLEncoder.encode(version, "utf-8"));
		params.put("Info", Info);
	}


}
