package de.blinkt.openvpn.activities.MyModules.model;

import de.blinkt.openvpn.http.InterfaceCallback;

/**
 * Created by kim
 * on 2017/6/6.
 * 使用充值卡
 */

public interface BindRechargeCardMode {

    /**
     *
     * @param interfaceCallback 回调函数
     * @param type 访问类型
     * @param cardPsw 卡密
     */
    void BindRechargeCard(InterfaceCallback interfaceCallback,int type,String cardPsw);

}
