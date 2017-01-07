package de.blinkt.openvpn.http;

import java.net.URLEncoder;
import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/10/28.
 */

public class UploadRemindConfigHttp extends BaseHttp {


	private InterfaceCallback call;
	private int cmdType_;
	private String Name;
	private int Status;


	public UploadRemindConfigHttp(InterfaceCallback call, int cmdType_, String Name, int Status) {
		super(call,cmdType_);
		this.Name = Name;
		this.Status = Status;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.UPLOAD_REMIND_CONFIG;
		params.put("Name", URLEncoder.encode(Name, "utf-8"));
		params.put("Status", URLEncoder.encode(Status+"", "utf-8"));
	}



}
