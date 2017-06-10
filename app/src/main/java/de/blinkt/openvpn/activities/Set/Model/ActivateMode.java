package de.blinkt.openvpn.activities.Set.Model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/5.
 * 激活界面mode
 */

public interface ActivateMode {

    /**
     * 激活套餐
     *
     * @param orderId  订单id
     * @param dataTime 时间
     */
    void orderActivationHttp(String orderId, String dataTime, BaseNetActivity baseNetActivity);

    /**
     * 获取写卡数据，然后发给蓝牙写卡
     *
     * @param orderId         订单id
     * @param nullcardNumber  空卡序列号
     * @param baseNetActivity 访问网络对象
     */
    void createHttpRequest(String orderId, String nullcardNumber, BaseNetActivity baseNetActivity);

}
