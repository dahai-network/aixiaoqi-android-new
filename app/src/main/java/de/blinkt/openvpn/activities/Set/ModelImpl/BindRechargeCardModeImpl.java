package de.blinkt.openvpn.activities.Set.ModelImpl;

import android.util.Log;

import de.blinkt.openvpn.activities.Set.Model.BindRechargeCardMode;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by kim
 * on 2017/6/6.
 */

public class BindRechargeCardModeImpl implements BindRechargeCardMode {

    @Override
    public void BindRechargeCard(InterfaceCallback interfaceCallback, int type, String cardPsw) {

        Log.d("BindRecharge", "BindRechargeCard: "+type +"---cardPsw="+cardPsw);

        CreateHttpFactory.instanceHttp(interfaceCallback, type, cardPsw);
    }
}
