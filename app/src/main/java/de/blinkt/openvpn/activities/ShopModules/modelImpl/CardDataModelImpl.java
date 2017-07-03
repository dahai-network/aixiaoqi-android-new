package de.blinkt.openvpn.activities.ShopModules.modelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.ShopModules.model.CardDataModel;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2017/6/29 0029.
 */

public class CardDataModelImpl extends NetModelBaseImpl implements CardDataModel {

    public  CardDataModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }

    @Override
    public void getCardDataHttp(String orderId, String nullcardNumber) {
        createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_DATA, orderId, nullcardNumber);
    }
}
