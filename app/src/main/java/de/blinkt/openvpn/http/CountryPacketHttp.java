package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.CountryPacketEntity;

/**
 * Created by Administrator on 2016/9/11.
 */
public class CountryPacketHttp extends BaseHttp {

	private List<CountryPacketEntity> countryPacketList;

	public List<CountryPacketEntity> getCountryPacketList() {
		return countryPacketList;
	}

	public CountryPacketHttp(InterfaceCallback call, int cmdType_, String... params) {
	super(call,cmdType_,HttpConfigUrl.COUNTRY_PACKET,params);
		sendMethod_ = GET_MODE;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("CountryID", URLEncoder.encode(valueParams[0] + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		countryPacketList = new Gson().fromJson(response, new TypeToken<List<CountryPacketEntity>>() {
		}.getType());
	}




}
