package de.blinkt.openvpn.fragments.PackageFragment.modelImpl;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.PackageFragment.model.PackageCategoryModel;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by kim
 * on 2017/6/15.
 */

public class PackageCategoryImpl implements PackageCategoryModel {
    @Override
    public void getOrder(InterfaceCallback interfaceCallback, int page, int pageSize,int type, String channel_id) {
        CreateHttpFactory.instanceHttp(interfaceCallback, HttpConfigUrl.COMTYPE_GET_ORDER, page + "", Constant.PAGESIZE + "", type+"", channel_id);
    }
}
