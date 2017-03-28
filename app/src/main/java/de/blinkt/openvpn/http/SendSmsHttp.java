package de.blinkt.openvpn.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class SendSmsHttp extends BaseHttp {



	public String getSmsId() {
		return SmsId;
	}

	private String SmsId;
	public SendSmsHttp(InterfaceCallback call, int cmdType_, String ...params) {
		super(call,cmdType_,HttpConfigUrl.SEND_SMS_MESSAGE,params);

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();

		params.put("To", URLEncoder.encode(valueParams[0] + "", "utf-8"));
		params.put("SMSContent", valueParams[1]);
	}

	@Override
	protected void parseObject(String response) {
		JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
		SmsId = jsonObject.get("SMSID").getAsString();
	}


}
