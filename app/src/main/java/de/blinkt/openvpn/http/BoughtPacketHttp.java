package de.blinkt.openvpn.http;


import com.google.gson.Gson;

import java.net.URLEncoder;

import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/28.
 */

public class BoughtPacketHttp extends BaseHttp {

	private BoughtPackageEntity boughtPackageEntity;
	/**
	 * PackageIsCategoryFlow（是否流量类型）
	 * <p>
	 * PackageIsCategoryCall（是否通话类型）
	 * <p>
	 * PackageIsCategoryDualSimStandby（是否双卡双待类型）
	 * <p>
	 * PackageIsCategoryKingCard（是否双卡双待类型）
	 */
	public BoughtPackageEntity getBoughtPackageEntity() {
		return boughtPackageEntity;
	}

	public BoughtPacketHttp(InterfaceCallback call, int cmdType_, String ...params) {
		super(call, cmdType_,params);

	}
	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_ORDER;
		sendMethod_ = GET_MODE;
		params.put("PageNumber", URLEncoder.encode(valueParams[0] + "", "utf-8"));
		params.put("PageSize", URLEncoder.encode(valueParams[1] + "", "utf-8"));

		if (!"-1".equals(valueParams[2]))
			params.put("PackageCategory", URLEncoder.encode(valueParams[2] + "", "utf-8"));

	}

	@Override
	protected void parseObject(String response) {
		boughtPackageEntity = new Gson().fromJson(response, BoughtPackageEntity.class);
	}


}
