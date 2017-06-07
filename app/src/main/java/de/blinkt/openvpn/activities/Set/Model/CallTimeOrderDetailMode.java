package de.blinkt.openvpn.activities.Set.Model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by Administrator on 2017/6/6.
 */

public interface CallTimeOrderDetailMode {

    /**
     *
     * @param orderId 订单id
     * @param baseNetActivity 访问网络对象
     */
    void getOrderDetailData(String orderId, BaseNetActivity baseNetActivity);
}
