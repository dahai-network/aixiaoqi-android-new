package de.blinkt.openvpn.activities.UserInfo.ModelImpl;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.UserInfo.Model.VertifyPhoneNumModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/8.
 */

public class VertifyPhoneNumImpl implements VertifyPhoneNumModel {
    @Override
    public void confirmedICCID(String phoneNumEditText, String ICCID, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_CONFIRMED, phoneNumEditText,
                ICCID);
    }
}
