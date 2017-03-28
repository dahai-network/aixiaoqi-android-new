package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.PacketDtailEntity;

/**
 * 套餐详情
 * Created by Administrator on 2016/9/11.
 */
public class PacketDtailHttp extends BaseHttp {

	private PacketDtailEntity packetDtailEntity;

	public PacketDtailEntity getPacketDtailEntity() {
		return packetDtailEntity;
	}

	public PacketDtailHttp(InterfaceCallback call, int cmdType_, String...params) {
		super(call,cmdType_,GET_MODE,HttpConfigUrl.PACKET_DETAIL,params);

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("id", URLEncoder.encode(valueParams[0], "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		packetDtailEntity = new Gson().fromJson(response, PacketDtailEntity.class);
	}


}
