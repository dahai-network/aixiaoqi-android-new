package de.blinkt.openvpn.activities.Set.Model;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;

/**
 * Created by kim
 * on 2017/6/5.
 * 账单界面mode
 */

public interface BalanceParticularsMode {

    /**
     * 获取账单数据
     * @param pageNumber 第几页
     * @param pageSize 每页的数量
     */
    void getAccountData(int pageNumber, int pageSize, BaseNetActivity baseNetActivity);
}
