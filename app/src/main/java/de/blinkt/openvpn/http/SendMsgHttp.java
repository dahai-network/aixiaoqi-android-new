package de.blinkt.openvpn.http;



import java.net.URLEncoder;
import java.util.HashMap;
import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by wzj on 2016/9/9.
 */
public class SendMsgHttp extends BaseHttp {

	private String ToNum;
	private int type;

	public SendMsgHttp(InterfaceCallback call, int cmdType_, String ToNum, int type) {
super(call,cmdType_);
		this.ToNum = ToNum;
		this.type = type;

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.SEND_SMS;
		params.put("ToNum", URLEncoder.encode(ToNum, "utf-8"));
		params.put("Type", URLEncoder.encode(type + "", "utf-8"));
	}


}
