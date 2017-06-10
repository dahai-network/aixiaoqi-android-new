package de.blinkt.openvpn.activities.MyModules.modelImple;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.PackageMarketModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/7.
 */
public class PackageMarketImpl implements PackageMarketModel {
    @Override
    public void getPackageMarketData(String pageSize, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_MARKET, pageSize);
    }

    @Override
    public void getPackageData(String pageNumber, String pageSize, String category, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_GET, pageNumber, pageSize, category);
    }
}
