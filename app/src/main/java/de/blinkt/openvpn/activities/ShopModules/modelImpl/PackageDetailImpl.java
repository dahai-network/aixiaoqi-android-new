package de.blinkt.openvpn.activities.ShopModules.modelImpl;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.PackageDetailModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/7.
 */

public class PackageDetailImpl implements PackageDetailModel {
    @Override
    public void getPacketDetail(String id, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_DETAIL, id);
    }
}
