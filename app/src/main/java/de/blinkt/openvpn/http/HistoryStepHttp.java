package de.blinkt.openvpn.http;

import android.util.Log;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SportStepEntity;

/**
 * Created by Administrator on 2016/10/6.
 */

public class HistoryStepHttp extends BaseHttp {

	private SportStepEntity sportStepEntity;


	public HistoryStepHttp(InterfaceCallback call, int cmdType_, SportStepEntity sportStepEntity) {
		super(call,cmdType_,POST_JSON,HttpConfigUrl.SPORT_REPORT_HISTORY_STEP);
		this.sportStepEntity = sportStepEntity;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("ToDays", new Gson().toJson(sportStepEntity));
	}

}
