package de.blinkt.openvpn.activities.Set.ModelImpl;

import android.util.Log;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.Set.Model.CallTimeOrderDetailMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/6.
 */

public class CallTimeOrderDetailImpl implements CallTimeOrderDetailMode {
    @Override
    public void getOrderDetailData(String orderId, BaseNetActivity baseNetActivity) {

        Log.d("CallTimeOrder", "getOrderDetailData: "+orderId);
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID, orderId);
    }
}
