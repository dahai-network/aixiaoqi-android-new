package de.blinkt.openvpn.activities.ShopModules.view;

import de.blinkt.openvpn.http.GetOrderByIdHttp;

/**
 * Created by Administrator on 2017/6/14.
 */

public interface MyOrderDetailView {
    /**
     * 加载网络成功显示界面
     * @param http
     */
    void loadSuccessShowView(GetOrderByIdHttp http);

    /**
     * 没有网络的时候显示界面
     */
    void noNetShowView();
}
