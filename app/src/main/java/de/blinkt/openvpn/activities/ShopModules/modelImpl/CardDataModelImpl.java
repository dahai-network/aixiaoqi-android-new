package de.blinkt.openvpn.activities.ShopModules.modelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.activities.ShopModules.model.CardDataModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/29 0029.
 */

public class CardDataModelImpl extends NetModelBaseImpl implements CardDataModel {

    public  CardDataModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }

    @Override
    public void getCardDataHttp(String orderId, String nullcardNumber) {
        if (SharedUtils.getInstance().readBoolean(Constant.IS_NEW_SIM_CARD)) {
            nullcardNumber="";
        }
        createHttpRequest(HttpConfigUrl.COMTYPE_ORDER_DATA, orderId, nullcardNumber);
    }
}
