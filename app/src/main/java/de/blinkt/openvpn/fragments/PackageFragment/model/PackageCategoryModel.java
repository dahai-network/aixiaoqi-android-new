package de.blinkt.openvpn.fragments.PackageFragment.model;

import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by kim
 * on 2017/6/15.
 */

public interface PackageCategoryModel {

    void getOrder(InterfaceCallback interfaceCallback, int page, int pageSize, int type,String channel_id);

}
