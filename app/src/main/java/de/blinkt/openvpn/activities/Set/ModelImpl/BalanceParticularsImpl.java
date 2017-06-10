package de.blinkt.openvpn.activities.Set.ModelImpl;

import android.util.Log;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.Set.Model.BalanceParticularsMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/5.
 */

public class BalanceParticularsImpl implements BalanceParticularsMode {

    @Override
    public void getAccountData(int pageNumber, int pageSize, BaseNetActivity baseNetActivity) {
        Log.d("BalanceParticularsImpl", "getAccountData: "+pageNumber+"::"+pageSize);
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_PARTICULAR, pageNumber + "", pageSize + "");

    }
}
