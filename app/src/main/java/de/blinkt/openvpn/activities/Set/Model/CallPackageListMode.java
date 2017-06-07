package de.blinkt.openvpn.activities.Set.Model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/6.
 */

public interface CallPackageListMode {

    /**
     *
     * @param pageNumber 页码
     * @param pageSize   页数
     * @param category  0流量/1通话/2大王卡/3双卡双待
     */
    void getPackageListData(String pageNumber, String pageSize, String category, BaseNetActivity baseNetActivity);
}
