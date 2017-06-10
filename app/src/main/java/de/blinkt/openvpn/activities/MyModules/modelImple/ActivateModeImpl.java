package de.blinkt.openvpn.activities.MyModules.modelImple;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.ActivateMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/5.
 */

public class ActivateModeImpl implements ActivateMode {

    /**
     *
     * @param orderId 卡的id
     * @param dataTime 时间
     * @param baseNetActivity
     */
    @Override
    public void orderActivationHttp(String orderId, String dataTime, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_ACTIVATION, orderId, dataTime);
    }

    /**
     *
     * @param orderId 卡的id
     * @param nullcardNumber 卡号
     * @param baseNetActivity 对象
     */
    @Override
    public void createHttpRequest(String orderId, String nullcardNumber, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_DATA, orderId, nullcardNumber);
    }

}
