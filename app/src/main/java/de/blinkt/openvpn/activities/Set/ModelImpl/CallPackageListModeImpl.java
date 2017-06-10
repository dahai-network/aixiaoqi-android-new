package de.blinkt.openvpn.activities.Set.ModelImpl;

import android.util.Log;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.Set.Model.CallPackageListMode;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by kim
 * on 2017/6/6.
 */

public class CallPackageListModeImpl implements CallPackageListMode {
    @Override
    public void getPackageListData(String pageNumber, String pageSize, String category, BaseNetActivity baseNetActivity) {
        Log.d("CallPackageList", "getPackageListData: "+pageNumber +"pageSize="+pageSize+"--category"+category);
        baseNetActivity.createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_GET, pageNumber + "", Constant.PAGESIZE + "", category + "");
    }
}
