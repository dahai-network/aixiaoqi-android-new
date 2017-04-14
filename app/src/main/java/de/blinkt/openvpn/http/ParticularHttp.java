package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.ParticularEntity;

/**
 * Created by Administrator on 2016/9/23.
 */

public class ParticularHttp extends BaseHttp {



	private ParticularEntity particularEntity;

	public ParticularEntity getParticularEntity() {
		return particularEntity;
	}

	public ParticularHttp(InterfaceCallback call, int cmdType_, String...params) {
		super(call,cmdType_,GET_MODE,HttpConfigUrl.PARTICULAR,params);


	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();


		params.put("pageSize", URLEncoder.encode(valueParams[1] + "", "utf-8"));
		params.put("pageNumber", URLEncoder.encode(valueParams[0] + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		particularEntity = new Gson().fromJson(response, ParticularEntity.class);
	}

}
