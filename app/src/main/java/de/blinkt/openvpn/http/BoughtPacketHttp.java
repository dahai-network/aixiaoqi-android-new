package de.blinkt.openvpn.http;


import com.google.gson.Gson;

import java.net.URLEncoder;

import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/28.
 */

public class BoughtPacketHttp extends BaseHttp {

	private final int PageNumber;
	private final int PageSize;
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
	private String PackageIsCategoryFlow;
	private String PackageIsCategoryCall;
	private String PackageIsCategoryDualSimStandby;
	private String PackageIsCategoryKingCard;

	public void setType(int type) {
		this.type = type;
	}

	private int type = -1;

	public BoughtPackageEntity getBoughtPackageEntity() {
		return boughtPackageEntity;
	}

	public BoughtPacketHttp(InterfaceCallback call, int cmdType_, int PageNumber, int PageSize) {
		super(call, cmdType_);
		this.PageNumber = PageNumber;
		this.PageSize = PageSize;
	}

	//设置筛选条件
	public void setScreenType(String PackageIsCategoryFlow, String PackageIsCategoryCall, String PackageIsCategoryDualSimStandby, String PackageIsCategoryKingCard) {
		this.PackageIsCategoryFlow = PackageIsCategoryFlow;
		this.PackageIsCategoryCall = PackageIsCategoryCall;
		this.PackageIsCategoryDualSimStandby = PackageIsCategoryDualSimStandby;
		this.PackageIsCategoryKingCard = PackageIsCategoryKingCard;
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_ORDER;
		sendMethod_ = GET_MODE;
		params.put("PageNumber", URLEncoder.encode(PageNumber + "", "utf-8"));
		params.put("PageSize", URLEncoder.encode(PageSize + "", "utf-8"));
//		params.put("PackageIsCategoryFlow", URLEncoder.encode(PackageIsCategoryFlow + "", "utf-8"));
//		params.put("PackageIsCategoryCall", URLEncoder.encode(PackageIsCategoryCall + "", "utf-8"));
//		params.put("PackageIsCategoryDualSimStandby", URLEncoder.encode(PackageIsCategoryDualSimStandby + "", "utf-8"));
//		params.put("PackageIsCategoryKingCard", URLEncoder.encode(PackageIsCategoryKingCard + "", "utf-8"));
		if (type != -1)
			params.put("PackageCategory", URLEncoder.encode(type + "", "utf-8"));

	}

	@Override
	protected void parseObject(String response) {
		boughtPackageEntity = new Gson().fromJson(response, BoughtPackageEntity.class);
	}


}
