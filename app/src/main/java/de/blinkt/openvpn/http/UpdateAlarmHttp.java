package de.blinkt.openvpn.http;

import android.text.TextUtils;

import java.net.URLEncoder;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/27.
 */

public class UpdateAlarmHttp extends BaseHttp {


	//闹钟时间
	private final String Time;
	//重复（周一到周日重复）
	private final List<String> Repeat;
	//备注
	private final String Tag;
	//状态（0禁用1启用）
	private final String ID;
	private String  Status;

	public UpdateAlarmHttp(InterfaceCallback call, int cmdType_, String Time, List<String> Repeat, String Tag, String ID, String Status) {
	super(call,cmdType_);
		this.Time = Time;
		this.Repeat = Repeat;
		this.Tag = Tag;
		this.ID = ID;
		this.Status=Status;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.UPDATE_ALARM;
		params.put("Time", Time);

		params.put("Repeat", getRepeatString(Repeat));
		params.put("Tag", Tag);
		params.put("ID", URLEncoder.encode(ID + "", "utf-8"));
		params.put("Status", URLEncoder.encode(Status + "", "utf-8"));
	}

	private String getRepeatString(List<String> repeatList) {
		String repeatStr = "";
		for (String i : repeatList) {
			repeatStr += (i + ",");
		}
		if (!TextUtils.isEmpty(repeatStr)) {
			repeatStr = repeatStr.substring(0, repeatStr.length()-1);
		}
		return repeatStr;
	}


}
