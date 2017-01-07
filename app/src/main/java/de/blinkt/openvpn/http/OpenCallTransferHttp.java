package de.blinkt.openvpn.http;


import java.util.HashMap;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class OpenCallTransferHttp extends BaseHttp {

	private String iccId;

	public OpenCallTransferHttp(InterfaceCallback call, int cmdType_, String iccId) {
		super(call,cmdType_);
		this.iccId = iccId;
	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.OPEN_CALL_TRANSFER;
		params.put("IccId", iccId);
	}

}
