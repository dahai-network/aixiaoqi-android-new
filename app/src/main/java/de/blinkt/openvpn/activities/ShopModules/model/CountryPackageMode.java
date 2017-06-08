package de.blinkt.openvpn.activities.ShopModules.model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/7.
 */

public interface CountryPackageMode {

    /**
     * 获取国家套餐
     * @param countryId 国家id
     * @param baseNetActivity 访问网络对象
     */
    void getCountryPacketData(String countryId, BaseNetActivity baseNetActivity);
}
