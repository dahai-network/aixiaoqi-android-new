package de.blinkt.openvpn.http;



import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class CancelCallTransferHttp extends BaseHttp {


	public CancelCallTransferHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_);
		isCreateHashMap=false;
	}


	@Override
	protected void BuildParams() throws Exception {
		slaverDomain_ = HttpConfigUrl.CANCEL_CALL_TRANSFER;
	}

}
