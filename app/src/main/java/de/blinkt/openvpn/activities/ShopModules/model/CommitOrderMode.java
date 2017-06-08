package de.blinkt.openvpn.activities.ShopModules.model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/7.
 */
public interface CommitOrderMode {

    void commitOrder(String id , String packetCount, String playMehtod, BaseNetActivity baseNetActivity);
}
