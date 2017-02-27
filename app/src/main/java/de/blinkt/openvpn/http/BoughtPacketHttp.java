package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.HashMap;

import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/28.
 */

public class BoughtPacketHttp extends BaseHttp {

	private final int PageNumber;
	private final int PageSize;
	private BoughtPackageEntity boughtPackageEntity;


	public void setType(int type) {
		this.type = type;
	}

	private int type=-1;
	public BoughtPackageEntity getBoughtPackageEntity() {
		return boughtPackageEntity;
	}

	public BoughtPacketHttp(InterfaceCallback call, int cmdType_, int PageNumber, int PageSize) {
		super(call,cmdType_);
		this.PageNumber = PageNumber;
		this.PageSize = PageSize;

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_ORDER;
		sendMethod_ = GET_MODE;
		params.put("PageNumber", URLEncoder.encode(PageNumber + "", "utf-8"));
		params.put("PageSize", URLEncoder.encode(PageSize + "", "utf-8"));
		if(type!=-1)
		params.put("PackageCategory", URLEncoder.encode(type + "", "utf-8"));

	}

	@Override
	protected void parseObject(String response) {
		boughtPackageEntity = new Gson().fromJson(response, BoughtPackageEntity.class);
	}



}
