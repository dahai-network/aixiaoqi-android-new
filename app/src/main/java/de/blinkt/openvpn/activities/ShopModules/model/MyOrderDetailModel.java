package de.blinkt.openvpn.activities.ShopModules.model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/14.
 */

public interface MyOrderDetailModel {

    /**
     * 通过id获取用户套餐
     * @param id
     * @param baseNetActivity 访问网络对象
     */
    void getUserPacketById(String id, BaseNetActivity baseNetActivity);

    void orderDataHttp(String orderId,String nullcardNumber,BaseNetActivity baseNetActivity);

}
