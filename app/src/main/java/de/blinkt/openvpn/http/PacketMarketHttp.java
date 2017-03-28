package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.PacketMarketEntity;

/**
 * Created by Administrator on 2016/9/13.
 */
public class PacketMarketHttp extends BaseHttp {



	public List<List<PacketMarketEntity>> getPacketMarketEntityList() {
		return packetMarketEntityList;
	}


	private List<List<PacketMarketEntity>> packetMarketEntityList;

	public PacketMarketHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call, cmdType_,GET_MODE,HttpConfigUrl.PACKET_MARKET,params);

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("pagesize", URLEncoder.encode(valueParams[0] + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		packetMarketEntityList = new Gson().fromJson(response, new TypeToken<List<List<PacketMarketEntity>>>() {
		}.getType());
	}


}
