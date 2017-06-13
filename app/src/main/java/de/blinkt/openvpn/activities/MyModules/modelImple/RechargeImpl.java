package de.blinkt.openvpn.activities.MyModules.modelImple;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.RechargeMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/8.
 */

public class RechargeImpl implements RechargeMode {
    @Override
    public void rechargeModeBalance(String moneyAmount, String payWay, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_RECHARGE_ORDER, moneyAmount, payWay);
    }
}
