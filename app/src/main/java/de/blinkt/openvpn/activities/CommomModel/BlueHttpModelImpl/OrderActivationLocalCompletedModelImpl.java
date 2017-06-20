package de.blinkt.openvpn.activities.CommomModel.BlueHttpModelImpl;

import de.blinkt.openvpn.activities.CommomModel.BlueHttpModel.OrderActivationLocalCompletedModel;
import de.blinkt.openvpn.activities.MyModules.ui.ActivateActivity;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class OrderActivationLocalCompletedModelImpl extends NetModelBaseImpl implements OrderActivationLocalCompletedModel{
    public OrderActivationLocalCompletedModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestOrderActivationLocalCompleted(String orderId) {
//        MyOrderDetailActivity.OrderID != null? MyOrderDetailActivity.OrderID: ActivateActivity.orderId
        createHttpRequest( HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED, orderId);
    }
}
