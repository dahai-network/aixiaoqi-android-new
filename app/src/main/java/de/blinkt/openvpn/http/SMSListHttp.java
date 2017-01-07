package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import java.util.List;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SmsEntity;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class SMSListHttp extends BaseHttp {

	private int pageNumber;
	private int pageSize;
	private List<SmsEntity> smsEntityList;

	public List<SmsEntity> getSmsEntityList() {
		return smsEntityList;
	}

	public SMSListHttp(InterfaceCallback interfaceCallback, int cmdType_, int pageNumber, int pageSize) {
		super(interfaceCallback,cmdType_);
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		sendMethod_ = GET_MODE;
		slaverDomain_ = HttpConfigUrl.GET_SMS_LIST;

		params.put("pageNumber", pageNumber + "");
		params.put("pageSize", pageSize + "");
	}

	@Override
	protected void parseObject(String response) {
		smsEntityList = new Gson().fromJson(response, new TypeToken<List<SmsEntity>>() {
		}.getType());
	}



}
