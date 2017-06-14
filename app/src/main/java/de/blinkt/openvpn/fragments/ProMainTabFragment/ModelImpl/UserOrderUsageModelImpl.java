package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Model.UserOrderUsageModel;
import de.blinkt.openvpn.http.CreateHttpFactory;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class UserOrderUsageModelImpl extends NetModelBaseImpl implements UserOrderUsageModel {
    public  UserOrderUsageModelImpl(OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
    }
    @Override
    public void requestUserPackage() {
      createHttpRequest( HttpConfigUrl.COMTYPE_GET_USER_ORDER_USAGE_REMAINING);
    }
}
