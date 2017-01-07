package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashMap;

import cn.com.johnson.model.BalanceEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/28.
 */

public class BalanceHttp extends BaseHttp {

	private BalanceEntity balanceEntity;

	public BalanceEntity getBalanceEntity() {
		return balanceEntity;
	}

	public BalanceHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_);
		isCreateHashMap=false;

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_BALANCE;
		sendMethod_ = GET_MODE;
	}

	@Override
	protected void parseObject(String response) {
		balanceEntity = new Gson().fromJson(response, BalanceEntity.class);
	}


}
