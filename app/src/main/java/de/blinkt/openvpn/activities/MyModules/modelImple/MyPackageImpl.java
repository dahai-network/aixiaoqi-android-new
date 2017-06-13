package de.blinkt.openvpn.activities.MyModules.modelImple;

import android.util.Log;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.MyPackageMode;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by kim
 * on 2017/6/9.
 */

public class MyPackageImpl implements MyPackageMode {
    @Override
    public void getOrder(BaseNetActivity baseNetActivity, String pageNumber, String pageSize, String type) {
        Log.d("MyPackageImpl", "getOrder: "+pageNumber +pageSize+type);
        CreateHttpFactory.instanceHttp(baseNetActivity, HttpConfigUrl.COMTYPE_GET_ORDER, pageNumber, pageSize, type);
    }
}
