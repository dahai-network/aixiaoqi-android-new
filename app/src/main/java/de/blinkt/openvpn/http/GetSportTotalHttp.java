package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SportTotalEntity;

/**
 * Created by Administrator on 2016/10/9.
 */

public class GetSportTotalHttp extends BaseHttp {


	private SportTotalEntity sportTotalEntity;

	public SportTotalEntity getSportTotalEntity() {
		return sportTotalEntity;
	}

	public GetSportTotalHttp(InterfaceCallback call, int cmdType_) {
	super(call,cmdType_);
		isCreateHashMap=false;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_SPORT_TOTAL;
		sendMethod_ = GET_MODE;
	}

	@Override
	protected void parseObject(String response) {
		sportTotalEntity = new Gson().fromJson(response, SportTotalEntity.class);
	}


}
