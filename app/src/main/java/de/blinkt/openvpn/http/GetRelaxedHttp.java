package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.FreeWorryEntity;

/**
 * Created by Administrator on 2017/5/12.
 */

public class GetRelaxedHttp extends BaseHttp {

	private FreeWorryEntity freeWorryEntity;

	public FreeWorryEntity getFreeWorryEntity() {
		if (freeWorryEntity == null) {
			freeWorryEntity = new FreeWorryEntity();
		}
		return freeWorryEntity;
	}

	public GetRelaxedHttp(InterfaceCallback call, int cmdType_) {
		super(call, cmdType_, false, GET_MODE, HttpConfigUrl.GET_RELAXED);
	}

	@Override
	protected void parseObject(String response) {
		try {
			freeWorryEntity = new Gson().fromJson(response, FreeWorryEntity.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
