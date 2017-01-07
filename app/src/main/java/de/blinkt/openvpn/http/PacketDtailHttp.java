package de.blinkt.openvpn.http;

import android.text.TextUtils;
import com.google.gson.Gson;
import java.net.URLEncoder;
import java.util.HashMap;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.PacketDtailEntity;

/**
 * 套餐详情
 * Created by Administrator on 2016/9/11.
 */
public class PacketDtailHttp extends BaseHttp {

	private String id;

	private PacketDtailEntity packetDtailEntity;

	public PacketDtailEntity getPacketDtailEntity() {
		return packetDtailEntity;
	}

	public PacketDtailHttp(InterfaceCallback call, int cmdType_, String id) {
		super(call,cmdType_);
		this.id = id;
		sendMethod_ = GET_MODE;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.PACKET_DETAIL;
		params.put("id", URLEncoder.encode(id, "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		packetDtailEntity = new Gson().fromJson(response, PacketDtailEntity.class);
	}


}
