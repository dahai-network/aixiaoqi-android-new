package de.blinkt.openvpn.activities.MyModules.modelImple;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.PaySuccessMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/8.
 */

public class PaySuccessImpl  implements PaySuccessMode{
    @Override
    public void isHavePacket(String type, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET, "3");
    }
}
