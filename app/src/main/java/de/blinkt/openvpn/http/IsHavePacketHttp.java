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

	/**
	 * PackageIsCategoryFlow（是否流量类型）
			* <p>
	 * PackageIsCategoryCall（是否通话类型）
			* <p>
	 * PackageIsCategoryDualSimStandby（是否双卡双待类型）
			* <p>
	 * PackageIsCategoryKingCard（是否双卡双待类型）
			*/
	private String PackageIsCategoryFlow;
	private String PackageIsCategoryCall;
	private String PackageIsCategoryDualSimStandby;
	private String PackageIsCategoryKingCard;

	public IsHavePacketHttp(InterfaceCallback call, int cmdType_,String...params) {
		super(call, cmdType_);
		this.PackageCategory = params[0];

	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.CHECK_IS_HAVE_PACKET;
		params.put("PackageCategory", PackageCategory);
//		params.put("PackageIsCategoryFlow", PackageIsCategoryFlow);
//		params.put("PackageIsCategoryCall", PackageIsCategoryCall);
//		params.put("PackageIsCategoryDualSimStandby", PackageIsCategoryDualSimStandby);
//		params.put("PackageIsCategoryKingCard", PackageIsCategoryKingCard);
	}

	//设置筛选条件
	public void setScreenType(String PackageIsCategoryFlow, String PackageIsCategoryCall, String PackageIsCategoryDualSimStandby, String PackageIsCategoryKingCard) {
		this.PackageIsCategoryFlow = PackageIsCategoryFlow;
		this.PackageIsCategoryCall = PackageIsCategoryCall;
		this.PackageIsCategoryDualSimStandby = PackageIsCategoryDualSimStandby;
		this.PackageIsCategoryKingCard = PackageIsCategoryKingCard;
	}

	@Override
	protected void parseObject(String response) {
		isHavePacketEntity = new Gson().fromJson(response, IsHavePacketEntity.class);
	}

}
