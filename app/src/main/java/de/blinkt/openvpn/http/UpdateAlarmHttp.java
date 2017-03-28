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
	private  String Time;
	//重复（周一到周日重复）
	private String Repeat;
	//备注
	private  String Tag;
	//状态（0禁用1启用）
	private  String ID;
	private String  Status;

	public UpdateAlarmHttp(InterfaceCallback call, int cmdType_, String...params) {
	super(call,cmdType_);
		this.Time = params[0];
		this.Repeat =  params[1];
		this.Tag =  params[2];
		this.ID =  params[3];
		this.Status= params[4];;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.UPDATE_ALARM;
		params.put("Time", Time);
		params.put("Repeat", Repeat);
		params.put("Tag", Tag);
		params.put("ID", URLEncoder.encode(ID + "", "utf-8"));
		params.put("Status", URLEncoder.encode(Status + "", "utf-8"));
	}




}
