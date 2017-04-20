package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import cn.com.johnson.model.BalanceEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/28.
 */

public class BalanceHttp extends BaseHttp {

	private BalanceEntity balanceEntity;

	public BalanceEntity getBalanceEntity() {
		if (balanceEntity == null) {
			balanceEntity = new BalanceEntity();
		}
		return balanceEntity;
	}

	public BalanceHttp(InterfaceCallback call, int cmdType_) {
		super(call, cmdType_, false, GET_MODE, HttpConfigUrl.GET_BALANCE);
	}


	@Override
	protected void parseObject(String response) {
		try {
			balanceEntity = new Gson().fromJson(response, BalanceEntity.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
