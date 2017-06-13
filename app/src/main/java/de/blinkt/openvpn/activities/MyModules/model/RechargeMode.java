package de.blinkt.openvpn.activities.MyModules.model;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/8.
 */

public interface RechargeMode {

   // createHttpRequest(HttpConfigUrl.COMTYPE_RECHARGE_ORDER, moneyAmount + "", payWay+"");

    /**
     *
     * @param moneyAmount 支付金额
     * @param payWay 支付方式
     * @param baseNetActivity 访问网络对象
     */
    void rechargeModeBalance(String moneyAmount, String payWay, BaseNetActivity baseNetActivity);
}
