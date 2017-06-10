package de.blinkt.openvpn.activities.MyModules.model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/7.
 */

public interface PackageMarketModel {

    /**
     *
     * @param pagesize  获取所有国家的套餐
     * @param baseNetActivity 网络访问对象
     */
    void getPackageMarketData(String pagesize, BaseNetActivity baseNetActivity);

    /**
     * 获取套餐
     * @param pageNumber
     * @param pageSize
     * @param category
     * @param baseNetActivity
     */
    void getPackageData(String pageNumber,String pageSize, String category,BaseNetActivity baseNetActivity);

}
