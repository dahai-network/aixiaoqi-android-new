package de.blinkt.openvpn.http;

import android.text.TextUtils;
import com.google.gson.Gson;
import java.net.URLEncoder;
import java.util.HashMap;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.ParticularEntity;

/**
 * Created by Administrator on 2016/9/23.
 */

public class ParticularHttp extends BaseHttp {


	private int pageNumber;
	private int pageSize;

	private ParticularEntity particularEntity;

	public ParticularEntity getParticularEntity() {
		return particularEntity;
	}

	public ParticularHttp(InterfaceCallback call, int cmdType_, int pageNumber, int pageSize) {
super(call,cmdType_);
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.sendMethod_ = GET_MODE;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.PARTICULAR;

		params.put("pageSize", URLEncoder.encode(pageSize + "", "utf-8"));
		params.put("pageNumber", URLEncoder.encode(pageNumber + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		particularEntity = new Gson().fromJson(response, ParticularEntity.class);
	}

}
