package de.blinkt.openvpn.activities.MyModules.model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/9.
 */

public interface MyPackageMode {

    /**
     * @param baseNetActivity 访问网络对象
     * @param pageNumber      页的号码
     * @param pageSize        页的数量
     * @param type            套餐类型
     */
    void getOrder(BaseNetActivity baseNetActivity, String pageNumber, String pageSize, String type);
}
