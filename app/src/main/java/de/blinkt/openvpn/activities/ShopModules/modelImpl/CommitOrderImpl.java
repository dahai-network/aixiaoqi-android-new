package de.blinkt.openvpn.activities.ShopModules.modelImpl;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.CommitOrderMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/7.
 */

public class CommitOrderImpl implements CommitOrderMode {
    @Override
    public void commitOrder(String id, String packetCount, String playMehtod, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_CREATE_ORDER, id, packetCount, playMehtod);
    }
}
