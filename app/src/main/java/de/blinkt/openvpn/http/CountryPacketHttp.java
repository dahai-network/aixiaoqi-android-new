package de.blinkt.openvpn.http;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.CountryPacketEntity;

/**
 * Created by Administrator on 2016/9/11.
 */
public class CountryPacketHttp extends BaseHttp {

	private String CountryID;
	private List<CountryPacketEntity> countryPacketList;

	public List<CountryPacketEntity> getCountryPacketList() {
		return countryPacketList;
	}

	public CountryPacketHttp(InterfaceCallback call, int cmdType_, String CountryID) {
	super(call,cmdType_);
		this.CountryID = CountryID;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.COUNTRY_PACKET;
		sendMethod_ = GET_MODE;
		params.put("CountryID", URLEncoder.encode(CountryID + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		countryPacketList = new Gson().fromJson(response, new TypeToken<List<CountryPacketEntity>>() {
		}.getType());
	}




}
