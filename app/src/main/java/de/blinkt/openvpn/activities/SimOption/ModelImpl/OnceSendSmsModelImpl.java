package de.blinkt.openvpn.activities.SimOption.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.SimOption.Model.OnceSendSmsModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class OnceSendSmsModelImpl extends NetModelBaseImpl implements OnceSendSmsModel {
    public OnceSendSmsModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestOnceSendSms(String smsID) {
        createHttpRequest(HttpConfigUrl.COMTYPE_SEND_RETRY_FOR_ERROR, smsID);
    }
}
