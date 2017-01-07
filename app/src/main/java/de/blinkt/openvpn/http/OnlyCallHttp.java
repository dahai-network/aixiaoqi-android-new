package de.blinkt.openvpn.http;

import android.text.TextUtils;
import com.google.gson.Gson;
import cn.com.johnson.model.OnlyCallModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class OnlyCallHttp extends BaseHttp {

	private OnlyCallModel model;

	public OnlyCallModel getOnlyCallModel() {
		return model;
	}

	public OnlyCallHttp(InterfaceCallback callback, int cmdType) {
		super(callback,cmdType);
		isCreateHashMap=false;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		sendMethod_ = GET_MODE;
		slaverDomain_ = HttpConfigUrl.GET_MAX_PHONE_CALL_TIME;
	}

	@Override
	protected void parseObject(String response) {
		model = new Gson().fromJson(response, OnlyCallModel.class);
	}


}
