package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.IsHavePacketEntity;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class IsHavePacketHttp extends BaseHttp {

	private String PackageCategory;

	private IsHavePacketEntity isHavePacketEntity;

	public IsHavePacketEntity getOrderDataEntity() {
		return isHavePacketEntity;
	}

	public IsHavePacketHttp(InterfaceCallback call, int cmdType_, String PackageCategory) {
		super(call,cmdType_);
		this.PackageCategory = PackageCategory;

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.CHECK_IS_HAVE_PACKET;
		params.put("PackageCategory", PackageCategory);
	}



	@Override
	protected void parseObject(String response) {
		isHavePacketEntity = new Gson().fromJson(response, IsHavePacketEntity.class);
	}

}
