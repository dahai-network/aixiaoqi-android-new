package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.net.URLEncoder;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.PacketEntity;

/**
 * Created by Administrator on 2016/10/27.
 */

public class GetPakcetHttp extends BaseHttp {
	private PacketEntity packetEntity;
	/**
	 * IsCategoryFlow（是否流量类型）
	 * <p>
	 * IsCategoryCall（是否通话类型）
	 * <p>
	 * IsCategoryDualSimStandby（是否双卡双待类型）
	 * <p>
	 * IsCategoryKingCard（是否双卡双待类型）
	 */
//	private String IsCategoryFlow;
//	private String IsCategoryCall;
//	private String IsCategoryDualSimStandby;
//	private String IsCategoryKingCard;


	public PacketEntity getPacketEntity() {
		return packetEntity;
	}

	public GetPakcetHttp(InterfaceCallback call, int cmdType_, String...params) {
		super(call, cmdType_,GET_MODE,HttpConfigUrl.PACKET_GET,params);

	}

//	//设置筛选条件
//	public void setScreenType(String IsCategoryFlow, String IsCategoryCall, String IsCategoryDualSimStandby, String IsCategoryKingCard) {
//		this.IsCategoryFlow = IsCategoryFlow;
//		this.IsCategoryCall = IsCategoryCall;
//		this.IsCategoryDualSimStandby = IsCategoryDualSimStandby;
//		this.IsCategoryKingCard = IsCategoryKingCard;
//	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("pageNumber", URLEncoder.encode(valueParams[0] + "", "utf-8"));
		params.put("pageSize", URLEncoder.encode(valueParams[1] + "", "utf-8"));
		params.put("category", URLEncoder.encode(valueParams[2] + "", "utf-8"));
//		params.put("IsCategoryFlow", URLEncoder.encode(IsCategoryFlow + "", "utf-8"));
//		params.put("IsCategoryCall", URLEncoder.encode(IsCategoryCall + "", "utf-8"));
//		params.put("IsCategoryDualSimStandby", URLEncoder.encode(IsCategoryDualSimStandby + "", "utf-8"));
//		params.put("IsCategoryKingCard", URLEncoder.encode(IsCategoryKingCard + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		packetEntity = new Gson().fromJson(response, PacketEntity.class);
	}

}
