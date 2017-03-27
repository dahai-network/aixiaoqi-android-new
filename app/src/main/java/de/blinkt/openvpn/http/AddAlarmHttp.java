package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.AlarmClockEntity;

/**
 * Created by Administrator on 2016/10/27.
 */

public class AddAlarmHttp extends BaseHttp {

	private AlarmClockEntity alarmClockEntity;
	public AlarmClockEntity getAlarmClockEntity() {
		return alarmClockEntity;
	}

	public AddAlarmHttp(InterfaceCallback call, int cmdType_ , String ...params) {
		super(call,cmdType_,params);
	}
	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.ADD_ALARM;
		params.put("Time", valueParams[0]);
		params.put("Repeat",  valueParams[1]);
		params.put("Tag",  valueParams[2]);
		params.put("Status", URLEncoder.encode( valueParams[3] + "", "utf-8"));
	}



	@Override
	protected void parseObject(String response) {
		alarmClockEntity = new Gson().fromJson(response, AlarmClockEntity.class);
		alarmClockEntity.setTime(valueParams[0] + "");
		alarmClockEntity.setRepeat(valueParams[1] + "");
		alarmClockEntity.setTag(valueParams[2] + "");
		alarmClockEntity.setStatus(valueParams[3] + "");
	}



}
