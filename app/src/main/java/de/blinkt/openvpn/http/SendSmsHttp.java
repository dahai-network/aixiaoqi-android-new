package de.blinkt.openvpn.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class SendSmsHttp extends BaseHttp {

	private String ToNum;
	private String smsContent;

	public String getSmsId() {
		return SmsId;
	}

	private String SmsId;
	public SendSmsHttp(InterfaceCallback call, int cmdType_, String ToNum, String smsContent) {
		super(call,cmdType_);
		this.ToNum = ToNum;
		this.smsContent = smsContent;

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.SEND_SMS_MESSAGE;

		params.put("To", URLEncoder.encode(ToNum + "", "utf-8"));
		params.put("SMSContent", smsContent);
	}

	@Override
	protected void parseObject(String response) {
		JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
		SmsId = jsonObject.get("SMSID").getAsString();
	}


}
