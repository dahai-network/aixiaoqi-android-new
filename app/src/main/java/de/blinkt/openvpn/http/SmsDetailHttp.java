package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SmsDetailEntity;


/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class SmsDetailHttp extends BaseHttp {


	private List<SmsDetailEntity> smsEntityList;

	public List<SmsDetailEntity> getSmsDetailEntityList() {
		return smsEntityList;
	}

	public SmsDetailHttp(InterfaceCallback interfaceCallback, int cmdType_, String...params ) {
	super(interfaceCallback,cmdType_,GET_MODE,HttpConfigUrl.GET_SMS_DETAIL,params);

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("Tel", valueParams[0]);
		params.put("pageNumber", valueParams[1] );
		params.put("pageSize", valueParams[2] );
	}

	@Override
	protected void parseObject(String response) {
		smsEntityList = new Gson().fromJson(response, new TypeToken<List<SmsDetailEntity>>() {
		}.getType());
	}

}
