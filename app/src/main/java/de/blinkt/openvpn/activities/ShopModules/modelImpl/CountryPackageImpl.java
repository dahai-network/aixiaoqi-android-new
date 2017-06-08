package de.blinkt.openvpn.activities.ShopModules.modelImpl;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.CountryPackageMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/7.
 */

public class CountryPackageImpl implements CountryPackageMode {
    @Override
    public void getCountryPacketData(String countryId, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_COUNTRY_PACKET, countryId);
    }
}
