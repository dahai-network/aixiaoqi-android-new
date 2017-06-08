package de.blinkt.openvpn.activities.ShopModules.model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by Administrator on 2017/6/7.
 */

public interface PackageDetailModel {
    /**
     *
     * @param id 套餐id
     * @param baseNetActivity 访问网络对象
     */
    void getPacketDetail(String id, BaseNetActivity baseNetActivity);

}
