package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SmsEntity;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class SMSListHttp extends BaseHttp {


	private List<SmsEntity> smsEntityList;

	public List<SmsEntity> getSmsEntityList() {
		return smsEntityList;
	}

	public SMSListHttp(InterfaceCallback interfaceCallback, int cmdType_,String... params ) {
		super(interfaceCallback,cmdType_,GET_MODE, HttpConfigUrl.GET_SMS_LIST,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("pageNumber", valueParams[0]);
		params.put("pageSize", valueParams[1] );
	}

	@Override
	protected void parseObject(String response) {
		smsEntityList = new Gson().fromJson(response, new TypeToken<List<SmsEntity>>() {
		}.getType());
	}



}
