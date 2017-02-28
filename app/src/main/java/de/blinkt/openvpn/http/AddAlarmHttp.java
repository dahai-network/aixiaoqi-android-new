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

	//闹钟时间
	private final String Time;
	//重复（周一到周日重复）
	private final List<String> Repeat;
	//备注
	private final String Tag;
	//状态（0禁用1启用）
	private final int Status;

	private AlarmClockEntity alarmClockEntity;

	public AlarmClockEntity getAlarmClockEntity() {
		return alarmClockEntity;
	}

	public AddAlarmHttp(InterfaceCallback call, int cmdType_ , String Time, List<String> Repeat, String Tag, int Status) {
		super(call,cmdType_);

		this.Time = Time;
		this.Repeat = Repeat;
		this.Tag = Tag;
		this.Status = Status;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.ADD_ALARM;
		params.put("Time", Time);
		params.put("Repeat", getRepeatString(Repeat));
		params.put("Tag", Tag);
		params.put("Status", URLEncoder.encode(Status + "", "utf-8"));
	}

	private String getRepeatString(List<String> repeatList) {
		StringBuilder repeatStrB = new StringBuilder();
		for (String i : repeatList) {
			repeatStrB.append(i + ",");
		}
		String repeatStr=repeatStrB.toString();
		if (!TextUtils.isEmpty(repeatStr)) {
			repeatStr = repeatStr.substring(0, repeatStr.length() - 1);
		}
		return repeatStr;
	}

	@Override
	protected void parseObject(String response) {
		alarmClockEntity = new Gson().fromJson(response, AlarmClockEntity.class);
		alarmClockEntity.setTime(Time + "");
		alarmClockEntity.setRepeat(getRepeatString(Repeat) + "");
		alarmClockEntity.setTag(Tag + "");
		alarmClockEntity.setStatus(Status + "");
	}



}
