package de.blinkt.openvpn.activities.ShopModules.modelImpl;

import android.util.Log;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.ShopModules.model.MyOrderDetailModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/14.
 */

public class MyOrderDetailImple implements MyOrderDetailModel {
    @Override
    public void getUserPacketById(String id, BaseNetActivity baseNetActivity) {
        Log.d("MyOrderDetailImple", "getUserPacketById: "+id);
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, id);
    }

    @Override
    public void orderDataHttp(String orderId, String nullcardNumber, BaseNetActivity baseNetActivity) {
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_DATA, orderId, nullcardNumber);
    }
}
