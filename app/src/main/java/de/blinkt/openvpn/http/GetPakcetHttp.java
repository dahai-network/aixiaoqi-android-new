package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.PacketEntity;

/**
 * Created by Administrator on 2016/10/27.
 */

public class GetPakcetHttp extends BaseHttp {
	private final int PageNumber;
	private final int PageSize;
	private final int category;
	private PacketEntity packetEntity;


	public PacketEntity getPacketEntity() {
		return packetEntity;
	}

	public GetPakcetHttp(InterfaceCallback call, int cmdType_, int PageNumber, int PageSize , int category) {
		super(call,cmdType_);
		this.PageNumber = PageNumber;
		this.PageSize = PageSize;
		this.category = category;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.PACKET_GET;
		sendMethod_ = GET_MODE;
		params.put("pageNumber", URLEncoder.encode(PageNumber + "", "utf-8"));
		params.put("pageSize", URLEncoder.encode(PageSize + "", "utf-8"));
		params.put("category", URLEncoder.encode(category + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		packetEntity = new Gson().fromJson(response, PacketEntity.class);
	}

}
